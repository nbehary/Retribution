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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nate on 2/13/14.
 */
public class GroupsDialogFragment extends DialogFragment {

    private LinearLayout mRootView;

    private OnGroupsChangeListener mListener;

    private String mCategory;


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mRootView = (LinearLayout) inflater.inflate(R.layout.groups_dialog,null);
        AllAppsList apps = ((Launcher)getActivity()).getModel().getAllApps();
        final LauncherModel model = ((Launcher) getActivity()).getModel();
        final AppCategories categories = model.getCategories();
        final RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.group_list2);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        final GroupsAdapter adapter = new GroupsAdapter(apps, (Launcher)getActivity());
        recyclerView.setAdapter(adapter);
        final Spinner spinner = (Spinner) mRootView.findViewById(R.id.groups_spinner);
        final ArrayList<String> cats = ((Launcher) getActivity()).getModel().getCategories().getCategories();
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,
                cats);
        mCategory = ((Launcher) getActivity()).getAllAppsGroup();
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(spinnerAdapter.getPosition(mCategory));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                adapter.setApps( ((Launcher) getActivity()).getModel().getCategories().getCategoryList(selected),selected);
                adapter.notifyDataSetChanged();
                mCategory = selected;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button newButton = (Button) mRootView.findViewById(R.id.groups_new_button);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                LinearLayout dialogView = (LinearLayout) inflater.inflate(R.layout.group_new_dialog, null);
                final EditText editText = (EditText) dialogView.findViewById(R.id.groups_new_name);
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(dialogView);
                builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        String newCategory = editText.getText().toString();
                        categories.addEmptyCategory(newCategory);
                        model.updateCategoryInDatabase(getActivity(),"",newCategory);
                        adapter.setApps(categories.getCategoryList(newCategory),newCategory);
                        adapter.notifyDataSetChanged();
                        spinnerAdapter.add(newCategory);
                        spinnerAdapter.notifyDataSetChanged();
                        spinner.invalidate();
                        spinner.setSelection(spinnerAdapter.getPosition(newCategory));
                        mCategory = newCategory;
                    }
                });
                builder.setNegativeButton(R.string.cancel_action, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });

        builder.setPositiveButton(R.string.dialog_done, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                boolean newCat = false;
                String result = ((Launcher) getActivity()).getModel().getCategories().clearEmptyCategories();
                if (!result.equals("")) {
                    model.deleteCategoryFromAppInDatabse(getActivity(), "", result);
                    newCat = true;
                }
                categories.getCategoryList(mCategory);
                adapter.notifyDataSetChanged();

                mListener.onGroupsChange(mCategory,newCat);
            }
        });

        builder.setView(mRootView);

        return builder.create();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGroupsChangeListener) activity;
        } catch (ClassCastException e) {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    public interface OnGroupsChangeListener {
        // TODO: Update argument type and name
        void onGroupsChange(String category, boolean newCat);
    }
}


class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder> {

    public AllAppsList mAllAppsList;
    public AllAppsList mCatList;
    public Launcher mLauncher;

    private String category;

    public GroupsAdapter(AllAppsList apps, Launcher launcher){

        mLauncher = launcher;
        mAllAppsList = launcher.getModel().getAllApps();
        category = "All Apps";
        mCatList = apps;
    }

    @Override
    public GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from (parent.getContext()).
                inflate(R.layout.groups_list_item, parent, false);
        return new GroupsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupsViewHolder holder, final int position) {
        holder.iconView.setImageBitmap(mAllAppsList.get(position).iconBitmap);
        holder.nameView.setText(mAllAppsList.get(position).title);
        if (mCatList.data.contains(mAllAppsList.get(position))) {
            holder.selectView.setChecked(true);
        } else {
            holder.selectView.setChecked(false);
        }
//        if (mFolderInfo.titles.contains(mAllAppsList.get(position).title)) {
//            holder.selectView.setChecked(true);
//        } else {
//            holder.selectView.setChecked(false);
//        }
        holder.selectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                Context context = mLauncher;
                LauncherModel model = ((Launcher) context).getModel();
                AppCategories cats = model.getCategories();
                String packageName = mAllAppsList.get(position).componentName.getPackageName();
                if (checkBox.isChecked()) {
                    cats.addAppToCategory(mAllAppsList.get(position), category);
                    model.updateCategoryInDatabase(context, packageName, category);
                } else {
                    cats.removeAppFromCategory(mAllAppsList.get(position), category);
                    model.deleteCategoryFromAppInDatabse(context, packageName, category);
                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return mAllAppsList.size();
    }

    public void setApps(AllAppsList apps, String cat){
        mCatList = apps;
        category = cat;
    }

    class GroupsViewHolder extends RecyclerView.ViewHolder {

        public ImageView iconView;
        public TextView nameView;
        public CheckBox selectView;

        public GroupsViewHolder(View itemView) {
            super(itemView);
            iconView = (ImageView) itemView.findViewById(R.id.icon);
            nameView = (TextView) itemView.findViewById(R.id.label);
            selectView = (CheckBox) itemView.findViewById(R.id.checkbox);

        }
    }
}
