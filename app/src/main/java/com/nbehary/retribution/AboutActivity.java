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
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_about, container, false);
            rootView.setBackgroundColor(Color.argb(128,0,0,0));
            TextView versionText = (TextView) rootView.findViewById(R.id.about_version_text);
            LauncherAppState appState = LauncherAppState.getInstance();
            String versionName = appState.mVersionName;
            //String versionName = appState.internalVersion;
            int versionCode = appState.mVersionCode;
            versionText.setText(String.format("Version: %s (%d)", versionName,versionCode));
            final Button button = (Button) rootView.findViewById(R.id.os_button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DialogFragment dialog = new OpenSourceDialog();
                    dialog.show(getFragmentManager(), "NoticeDialogFragment");

                }
            });
            final Button playButton = (Button) rootView.findViewById(R.id.play_button);


            return rootView;
        }
    }

}
