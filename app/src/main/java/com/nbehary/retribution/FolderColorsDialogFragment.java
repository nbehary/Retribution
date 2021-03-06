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


import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
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
        mRootView = (GridLayout) inflater.inflate(R.layout.folder_colors_dialog, null);

        mChangeSpinner = (Spinner) mRootView.findViewById(R.id.folder_colors_dialog_change);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.folder_colors_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mChangeSpinner.setAdapter(adapter);
        mChangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mChanging = (String) parent.getItemAtPosition(position);
                switch (mChanging) {
                    case "Backgrounds":
                        mPicker.setColor(mBgColor);
                        break;
                    case "Folder Name":
                        if (mNameColor != 0) {
                            mPicker.setColor(mNameColor);
                        }
                        break;
                    default:
                        if (mIconColor != 0) {
                            mPicker.setColor(mIconColor);
                        }

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                ColorThemeRepo theme = LauncherAppState.getInstance().getColorTheme();
                mBgColor = theme.getmFolderBack();
                mLabelColor = theme.getmFolderLabel();
                mNameColor = theme.getmFolderName();
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
            ColorThemeRepo theme = LauncherAppState.getInstance().getColorTheme();
            mBgColor = theme.getmFolderBack();
            mLabelColor = theme.getmFolderLabel();
            mNameColor = theme.getmFolderName();
            mCustomColors = 0;
        } else {
            mBgColor = Color.WHITE;
            mNameColor = Color.BLACK;
            mLabelColor = Color.BLACK;
            mCustomColors = 0;
        }

    }

    private void setColors() {
        DrawableCompat.setTint(DrawableCompat.wrap(mPreviewBackground),mBgColor);
        mPreview.invalidate();
        mLabelText.setTextColor(mLabelColor);
        mNameText.setTextColor(mNameColor);
    }

    @Override
    public void onColorChanged(int color) {
        switch (mChanging) {
            case "Backgrounds":
                mBgColor = color;
                break;
            case "Folder Name":
                mNameColor = color;
                break;
            default:
                mLabelColor = color;
                break;
        }
        if (mDefaultBG) {
            mDefaultBG = false;
        }
        mCustomColors = 1;
        setColors();

    }
}
