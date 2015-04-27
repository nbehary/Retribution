/*
        * Copyright (C) 2013 Nathan A. Behary
        *
        * Licensed under the Apache License, Version 2.0 (the "License");
        * you may not use this file except in compliance with the License.
        * You may obtain a copy of the License at
        *
        *      http://www.apache.org/licenses/LICENSE-2.0
        *
        * Unless required by applicable law or agreed to in writing, software
        * distributed under the License is distributed on an "AS IS" BASIS,
        * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        * See the License for the specific language governing permissions and
        * limitations under the License.
        */

package com.nbehary.retribution;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 *
 *
 */
public class SettingsActivity extends Activity implements AdapterView.OnItemSelectedListener {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private String mFolderColorStart;
    private String mFolderIconStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFolderColorStart = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_folder_color", "1");
        mFolderIconStart = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_folder_icon", "1");

        // Display the fragment as the main content.
       // if (LauncherAppState.getInstance().getProVersion()){

      //  } else {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
       // }
     /*     setContentView(R.layout.fragment_folder_colors);
        Spinner spinner = (Spinner) findViewById(R.id.folder_colors_item_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.folder_colors_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        FrameLayout preview = (FrameLayout) findViewById(R.id.folder_color_preview);

        Log.d("nbehary546","debug placeholder");

*/
    }

    @Override
    public void finish() {
        String folderColor = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_folder_color", "1");
        String folderIcon = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_folder_icon", "1");

        Intent intent = new Intent();
        if ((folderColor != mFolderColorStart) || (folderIcon != mFolderIconStart)) {
            intent.putExtra("resetWorkspace", true);
        } else {
            intent.putExtra("resetWorkspace", false);
        }
        setResult(RESULT_OK, intent);
        super.finish();

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String item = (String) parent.getItemAtPosition(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
