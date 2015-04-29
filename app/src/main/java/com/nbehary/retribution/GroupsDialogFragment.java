/*
 * Copyright (c) 2015. Nathan A. Behary
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

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_multiple_choice, GENRES);



        AllAppsList apps = ((Launcher)getActivity()).getModel().getAllApps();
        ArrayList<String> strings = new ArrayList<String>();
        final GroupsArrayAdapter adapter = new GroupsArrayAdapter(getActivity(),strings,apps,"All Apps");
        final ListView listView = (ListView) mRootView.findViewById(R.id.group_list);
        final LauncherModel model = ((Launcher) getActivity()).getModel();
        final AppCategories categories = model.getCategories();

        listView.setAdapter(adapter);

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);



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
                    //((Launcher) getActivity()).getModel().deleteCategoryInDatabase(getActivity(),result);
                    model.deleteCategoryFromAppInDatabse(getActivity(), "", result);
                    newCat = true;
                }
//                adapter.setApps(((Launcher) getActivity()).getModel().getCategories().getCategoryList(result),result);
//                adapter.notifyDataSetChanged();
//                spinnerAdapter.add(result);
//                spinnerAdapter.notifyDataSetChanged();
//                spinner.setSelection(spinner.getLastVisiblePosition());
                adapter.setApps(categories.getCategoryList(mCategory),mCategory);
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
            //  throw new ClassCastException(activity.toString()
            //         + " must implement OnFragmentInteractionListener");
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

 class GroupsArrayAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> values;
    private AllAppsList apps;
     private final AllAppsList allApps;
    private String category;

    public GroupsArrayAdapter(Context context, ArrayList<String>values,AllAppsList apps, String category) {
        super(context, R.layout.groups_list_item, values);
        this.context = context;
        this.apps = apps;
        this.category = category;
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
        Bitmap b = this.allApps.get(position).iconBitmap;
        imageView.setImageBitmap(b);
        final int index = position;

        final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);

        if (this.apps.data.contains(this.allApps.get(position))) {
            checkBox.setChecked(true);
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LauncherModel model = ((Launcher) context).getModel();
                AppCategories cats = model.getCategories();
                String packageName = allApps.get(index).componentName.getPackageName();
                if (!checkBox.isChecked()) {
                    cats.addAppToCategory(allApps.get(index), category);
                    model.updateCategoryInDatabase(context, packageName, category);
                    checkBox.setChecked(true);
                } else {
                    cats.removeAppFromCategory(allApps.get(index), category);
                    model.deleteCategoryFromAppInDatabse(context, packageName, category);
                    checkBox.setChecked(false);
                }
            }
        });
        return rowView;

    }

    public void setApps(AllAppsList apps, String category){
        this.apps = apps;
        this.category = category;
    }
}
