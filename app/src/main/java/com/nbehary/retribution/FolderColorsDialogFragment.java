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


import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.nbehary.retribution.preference.PreferencesProvider;


public class FolderColorsDialogFragment extends DialogFragment implements ColorPickerView.OnColorChangedListener {

    private Folder mFolder;
    private GridLayout mRootView;
    private Spinner mChangeSpinner;
    Spinner mIconSpinner;
    private ColorPickerView mPicker;
    private TextView mLabelText;
    private TextView mNameText;
    private LinearLayout mPreview;
    private Drawable mPreviewBackground;
    private int mCustomColors;
    private int mIconColor;
    private int mNameColor;
    private int mBgColor;
    private int mLabelColor;
    private boolean mDefaultBG;
    boolean mTintIcon;
    int mFolderType;
    private String mChanging;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        getColors();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mRootView = (GridLayout) inflater.inflate(R.layout.folder_colors_dialog,null);

        mChangeSpinner = (Spinner) mRootView.findViewById(R.id.folder_colors_dialog_change);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.folder_colors_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mChangeSpinner.setAdapter(adapter);
        mChangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mChanging = (String) parent.getItemAtPosition(position);
                if (mChanging.equals("Background")) {
                    mPicker.setColor(mBgColor);
                } else if (mChanging.equals("Folder Name")) {
                    if (mNameColor != 0) {
                        mPicker.setColor(mNameColor);
                    }
                } else {
                    if (mIconColor != 0) {
                        mPicker.setColor(mIconColor);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
/*
                mIconSpinner = (Spinner) mRootView.findViewById(R.id.folder_colors_dialog_icon);
        ArrayAdapter<CharSequence> iconAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.folder_icons_array_pro, android.R.layout.simple_spinner_item);
        iconAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mIconSpinner.setAdapter(iconAdapter);
*/
        mPicker = (ColorPickerView) mRootView.findViewById(R.id.picker);
        mPicker.setOnColorChangedListener(this);

        mPreview = (LinearLayout) mRootView.findViewById(R.id.folder_color_preview);
        mPreviewBackground = mPreview.getBackground();
        mLabelText = (TextView) mRootView.findViewById(R.id.folder_colors_icon_text);
        mNameText = (TextView) mRootView.findViewById(R.id.folder_colors_label_text);
        Button resetButton = (Button) mRootView.findViewById(R.id.folder_colors_reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mCustomColors = 0;
                mBgColor = PreferencesProvider.Interface.General.getFolderBackColor();
                mLabelColor = PreferencesProvider.Interface.General.getFolderIconColor();
                mNameColor = PreferencesProvider.Interface.General.getFolderNameColor();
                setColors();

            }
       });    



        setColors();

        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mFolder.mInfo.customColors = mCustomColors;
                mFolder.mInfo.labelColor = mLabelColor;
                mFolder.mInfo.nameColor = mNameColor;
                mFolder.mInfo.backColor = mBgColor;
                LauncherModel.updateItemInDatabase(mFolder.mLauncher, mFolder.mInfo);
                mFolder.mLauncher.getModel().startLoader(true,-1);
                mFolder.mLauncher.onResume();
                mFolder.animateClosed();
                //mModel.startLoader(true, -1);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setView(mRootView);

        return builder.create();
    }



    public void setmFolder(Folder folder) {mFolder=folder;}

    private void getColors() {
        FolderInfo info = mFolder.mInfo;
        mDefaultBG = PreferencesProvider.Interface.General.getDefaultFolderBG();
        if (info.customColors == 1) {
            mCustomColors = info.customColors;
            mBgColor = info.backColor;
            mLabelColor = info.labelColor;
            mNameColor = info.nameColor;
            mIconColor = info.iconColor;
        } else if (!mDefaultBG) {
            mBgColor = PreferencesProvider.Interface.General.getFolderBackColor();
            mLabelColor = PreferencesProvider.Interface.General.getFolderIconColor();
            mNameColor = PreferencesProvider.Interface.General.getFolderNameColor();
            mCustomColors = 0;
        } else {
            mBgColor = Color.WHITE;
            mNameColor = Color.BLACK;
            mLabelColor = Color.BLACK;
            mCustomColors = 0;
        }

        //mTintIcon = PreferencesProvider.Interface.General.getFolderIconTint();
        //mFolderType = PreferencesProvider.Interface.General.getFolderType();
    }

    private void setColors() {
        ColorFilter filter = new LightingColorFilter( mBgColor, mBgColor);
        mPreviewBackground.setColorFilter(filter);
        mLabelText.setTextColor(mLabelColor);
        mNameText.setTextColor(mNameColor);
    }

    @Override
    public void onColorChanged(int color) {
        if (mChanging.equals("Background")) {
            mBgColor = color;
        } else if (mChanging.equals("Folder Name")) {
            mNameColor = color;
        } else {
            mLabelColor = color;
        }
        if (mDefaultBG) {
            mDefaultBG = false;
        }
        mCustomColors = 1;
        setColors();

    }
}
