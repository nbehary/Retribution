/*
 * Copyright (c) 2014-2015. Nathan A. Behary
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.nbehary.retribution;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nate on 4/21/14.
 */
public class FolderAddDialogFragment extends DialogFragment {

    private LinearLayout mRootView;

    private OnFolderShortcutsAddedListener mListener;

    private String mCategory;

    private Folder mFolder;
    private FolderInfo mFolderInfo;

    private FolderAddArrayAdapter mListAdapter;

    private Launcher mLauncher;


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mRootView = (LinearLayout) inflater.inflate(R.layout.folder_add_dialog,null);
        mLauncher = ((Launcher)getActivity());

        AllAppsList apps = ((Launcher)getActivity()).getModel().getAllApps();
        ArrayList<String> strings = new ArrayList<String>();
        mListAdapter = new FolderAddArrayAdapter(getActivity(),strings,apps, mFolderInfo, mLauncher,mFolder);
//        final ListView listView = (ListView) mRootView.findViewById(R.id.group_list);
        final RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.group_list2);
        recyclerView.setHasFixedSize(true);
        final LauncherModel model = ((Launcher) getActivity()).getModel();
        final AppCategories categories = model.getCategories();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        FolderAddAdapter adapter = new FolderAddAdapter(apps, mFolderInfo, mLauncher,mFolder);
        recyclerView.setAdapter(adapter);
//        listView.setAdapter(mListAdapter);

//        listView.setItemsCanFocus(false);
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);



        final Spinner spinner = (Spinner) mRootView.findViewById(R.id.groups_spinner);
        final ArrayList<String> cats = ((Launcher) getActivity()).getModel().getCategories().getCategories();
        cats.add(0,"All Apps");
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,
                cats);
        mCategory = ((Launcher) getActivity()).getAllAppsGroup();
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(spinnerAdapter.getPosition(mCategory));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                if (!selected.equals("All Apps")) {
                    mListAdapter.setCategoryAndReload(((Launcher) getActivity()).getModel().getCategories().getCategoryList(selected), selected);
                } else {
                    mListAdapter.setCategoryAndReload(((Launcher)getActivity()).getModel().getAllApps(),selected);
                }
                mListAdapter.notifyDataSetChanged();
                mCategory = selected;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        builder.setPositiveButton(R.string.dialog_done, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String result = ((Launcher) getActivity()).getModel().getCategories().clearEmptyCategories();
                if (!result.equals("")) {
                    model.deleteCategoryFromAppInDatabse(getActivity(), "", result);
                }
               spinner.setSelection(spinner.getLastVisiblePosition());
                mListAdapter.setApps(categories.getCategoryList(mCategory),mCategory);
                mListAdapter.notifyDataSetChanged();

                //mListener.onGroupsChange(mCategory,newCat);
            }
        });
        builder.setView(mRootView);
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFolderShortcutsAddedListener) activity;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    public interface OnFolderShortcutsAddedListener {
        // TODO: Update argument type and name
        void onFolderShortcutsAdded();
    }

    public void setFolder(Folder folder) {
        mFolder = folder;
        mFolderInfo = folder.getInfo();
        //mFolderInfo.contents
    }
}

class FolderAddAdapter extends RecyclerView.Adapter<FolderAddAdapter.ViewHolder> {

    public AllAppsList mAllAppsList;
    public FolderInfo mFolderInfo;
    public Launcher mLauncher;
    public Folder mFolder;

    public FolderAddAdapter(AllAppsList list,FolderInfo info, Launcher launcher, Folder folder){
        mAllAppsList = list;
        mFolderInfo = info;
        mLauncher = launcher;
        mFolder = folder;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from (parent.getContext()).
                inflate(R.layout.groups_list_item, parent, false);
//        ViewHolder vh = new ViewHolder(itemView);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.iconView.setImageBitmap(mAllAppsList.get(position).iconBitmap);
        holder.nameView.setText(mAllAppsList.get(position).title);
        if (mFolderInfo.titles.contains(mAllAppsList.get(position).title)) {
            holder.selectView.setChecked(true);
        } else {
            holder.selectView.setChecked(false);
        }
        holder.selectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    mFolderInfo.add(new ShortcutInfo(mAllAppsList.get(position)));
//                    checkBox.setChecked(true);
                } else {
                    int index2 = mFolderInfo.titles.indexOf(mAllAppsList.get(position).title);
                    ShortcutInfo item = mFolderInfo.contents.get(index2);
                    mFolderInfo.remove(item);
                    LauncherModel.deleteItemFromDatabase(
                            mLauncher, item);
                    if (mFolderInfo.contents.size() == 1) {
                        mFolder.animateClosed();
                    }
//                    checkBox.setChecked(false);
                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return mAllAppsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iconView;
        public TextView nameView;
        public CheckBox selectView;



        public ViewHolder(View itemView) {
            super(itemView);
            iconView = (ImageView) itemView.findViewById(R.id.icon);
            nameView = (TextView) itemView.findViewById(R.id.label);
            selectView = (CheckBox) itemView.findViewById(R.id.checkbox);

        }



    }
}

 class FolderAddArrayAdapter extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<String> values;
    private AllAppsList apps;
    private final AllAppsList allApps;

    private final FolderInfo folderInfo;
    private final Launcher mLauncher;
    private final Folder mFolder;

    public FolderAddArrayAdapter(Context context, ArrayList<String> values, AllAppsList apps, FolderInfo folderInfo, Launcher launcher, Folder folder) {
        super(context, R.layout.groups_list_item, values);
        this.context = context;
        this.apps = apps;
        mLauncher =launcher;
        mFolder = folder;
        this.folderInfo = folderInfo;
        this.allApps = ((Launcher)context).getModel().getAllApps();
        this.clear();
        this.values = new ArrayList<String>();
        for (AppInfo info: allApps.data){
            this.add((String)info.title);
            this.values.add((String)info.title);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout rowView = (LinearLayout) inflater.inflate(R.layout.groups_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values.get(position));
        Bitmap b = this.apps.get(position).iconBitmap;

        imageView.setImageBitmap(b);
        final int index = position;

        final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);

        if (this.folderInfo.titles.contains(this.apps.get(position).title)) {
            checkBox.setChecked(true);
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    folderInfo.add(new ShortcutInfo(allApps.get(index)));
                    checkBox.setChecked(true);
                } else {
                    int index2 = folderInfo.titles.indexOf(allApps.get(index).title);
                    ShortcutInfo item = folderInfo.contents.get(index2);
                    folderInfo.remove(item);
                    LauncherModel.deleteItemFromDatabase(
                            mLauncher, item);
                    if (folderInfo.contents.size() == 1){
                        mFolder.animateClosed();
                    }
                    checkBox.setChecked(false);
                }
            }
        });
        return rowView;

    }

    public void setApps(AllAppsList apps, String category){
        this.apps = apps;
    }

    public void setCategoryAndReload(AllAppsList apps,String category) {
        this.clear();
        this.apps = apps;
        this.values = new ArrayList<String>();
        for (AppInfo info: apps.data){
            this.add((String)info.title);
            this.values.add((String)info.title);
        }
    }
}
