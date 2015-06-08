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



    private Launcher mLauncher;


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mRootView = (LinearLayout) inflater.inflate(R.layout.folder_add_dialog,null);
        mLauncher = ((Launcher)getActivity());

        AllAppsList apps = ((Launcher)getActivity()).getModel().getAllApps();
        final RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.group_list2);
        recyclerView.setHasFixedSize(true);
        final LauncherModel model = ((Launcher) getActivity()).getModel();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        final FolderAddAdapter adapter = new FolderAddAdapter(apps, mFolderInfo, mLauncher,mFolder);
        recyclerView.setAdapter(adapter);
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
                    adapter.setCategoryAndReload(((Launcher) getActivity()).getModel().getCategories().getCategoryList(selected));
                } else {
                    adapter.setCategoryAndReload(((Launcher) getActivity()).getModel().getAllApps());
                }
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
                } else {
                    int index2 = mFolderInfo.titles.indexOf(mAllAppsList.get(position).title);
                    ShortcutInfo item = mFolderInfo.contents.get(index2);
                    mFolderInfo.remove(item);
                    LauncherModel.deleteItemFromDatabase(
                            mLauncher, item);
                    if (mFolderInfo.contents.size() == 1) {
                        mFolder.animateClosed();
                    }
                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return mAllAppsList.size();
    }

    public void setCategoryAndReload(AllAppsList apps){
        this.mAllAppsList = apps;
        notifyDataSetChanged();
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


