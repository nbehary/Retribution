/*
 * Copyright (C) 2013 The Android Open Source Project
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

import android.app.Application;
import android.content.pm.ApplicationInfo;

import com.nbehary.retribution.preference.PreferencesProvider;
//import com.squareup.leakcanary.LeakCanary;

public class LauncherApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Load all preferences
//        LeakCanary.install(this);
        PreferencesProvider.load(this);
        LauncherAppState.setApplicationContext(this);
        LauncherAppState.getInstance();

        boolean isDebuggable =  ( 0 != ( getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );


    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LauncherAppState.getInstance().onTerminate();
    }
}