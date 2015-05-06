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

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;



import com.nbehary.retribution.preference.PreferencesProvider;


public class ColorThemeActivity extends AppCompatActivity {

    private Context mContext;
    ColorTheme mColorTheme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getString(R.string.screen_type).equals("phone")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mColorTheme = LauncherAppState.getInstance().getColorTheme();

        mColorTheme.readFromFolderColors();

        mContext = this;
        //No colors set.  Default to black text on white.  (sort of, but not the default)
        if (mColorTheme.mFolderBack == 0 && mColorTheme.mFolderLabels == 0 && mColorTheme.mFolderName == 0) {
            mColorTheme.mFolderBack = -109145601;
            mColorTheme.mFolderLabels = -4038656;
            mColorTheme.mFolderName = -847872;
            //TODO: Write to ColorTheme.  Once it is being saved.  (if this still exists)
            mColorTheme.writeToPrefs();
            PreferencesProvider.Interface.General.setDefaultFolderBG(mContext, true);
        }
        setContentView(R.layout.activity_folder_colors);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_set_folder_colors, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.getCustomView().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mColorTheme.writeToPrefs();
                        PreferencesProvider.Interface.General.setDefaultFolderBG(mContext, false);//TODO: Fixme
                        LauncherAppState.getInstance().getModel().startLoader(true, -1);
                        finish();
                        //do stuff
                    }

                });

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ColorThemeFragment())
                    .commit();
        }

    }


    void setmNameColor(int mNameColor) {
        this.mColorTheme.mFolderName = mNameColor;
    }

    void setmIconColor(int mIconColor) {
        this.mColorTheme.mFolderLabels = mIconColor;
    }

    void setmBgColor(int color) {
        this.mColorTheme.mFolderBack = color;
    }


    void setmTintIcon(boolean mTintIcon) {
        this.mColorTheme.mFolderIconTint = mTintIcon;
    }

    void setmFolderType(int mFolderType) {
        this.mColorTheme.mFolderType = mFolderType;
    }

    void setmWallTint(boolean wall) {
        this.mColorTheme.mWallTint = wall;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void finish() {
        Intent intent = new Intent();
        //intent.putExtra("resetWorkspace", true);
        setResult(RESULT_OK, intent);

        super.finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
}