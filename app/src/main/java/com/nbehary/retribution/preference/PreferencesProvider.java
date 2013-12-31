/*
 * Copyright (C) 2011 The CyanogenMod Project
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

package com.nbehary.retribution.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.nbehary.retribution.Workspace;
import com.nbehary.retribution.AppsCustomizePagedView;

import java.util.Map;

public final class PreferencesProvider {
    public static final String PREFERENCES_KEY = "com.nbehary.retribution_preferences";

    public static final String PREFERENCES_CHANGED = "preferences_changed";

    private static Map<String, Object> sKeyValues;

    @SuppressWarnings("unchecked")
    public static void load(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_KEY, 0);
        sKeyValues = (Map<String, Object>)preferences.getAll();
    }

    private static int getInt(String key, int def) {
        return sKeyValues.containsKey(key) && sKeyValues.get(key) instanceof Integer ?
                (Integer) sKeyValues.get(key) : def;
    }

    private static void setInt(Context ctx, String key, int value) {
        SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCES_KEY, 0);
        Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply(); // For better performance
        sKeyValues.put(key, value);
    }

    private static boolean getBoolean(String key, boolean def) {
        return sKeyValues.containsKey(key) && sKeyValues.get(key) instanceof Boolean ?
                (Boolean) sKeyValues.get(key) : def;
    }

    private static void setBoolean(Context ctx, String key, boolean value) {
        SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCES_KEY, 0);
        Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply(); // For better performance
        sKeyValues.put(key, Boolean.valueOf(value));
    }

    private static String getString(String key, String def) {
        return sKeyValues.containsKey(key) && sKeyValues.get(key) instanceof String ?
                (String) sKeyValues.get(key) : def;
    }

    private static void setString(Context ctx, String key, String value) {
        SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCES_KEY, 0);
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply(); // For better performance
        sKeyValues.put(key, value);
    }

    public static class Interface {

        public static class Icons {

        }

        public static class General {

            public static String getIconPack() {
                return getString("ui_general_iconpack", "Default");
            }

            public static void setIconPack(Context ctx, String packageName) {
                setString(ctx, "ui_general_iconpack", packageName);
            }

            public static int getFolderBackColor() {
                return getInt("pref_folder_bg_color", 0);
            }

            public static void setFolderBackColor(Context ctx, int color) {
                setInt(ctx,"pref_folder_bg_color",color);
            }

            public static int getFolderIconColor() {
                return getInt("pref_folder_icon_color", 0);
            }

            public static void setFolderIconColor(Context ctx, int color) {
                setInt(ctx,"pref_folder_icon_color",color);
            }

            public static int getFolderNameColor() {
                return getInt("pref_folder_name_color", 0);
            }

            public static void setFolderNameColor(Context ctx, int color) {
                setInt(ctx,"pref_folder_name_color",color);
            }

            public static boolean getDefaultFolderBG() {
                return getBoolean("pref_folder_default_bg", true);
            }

            public static void setDefaultFolderBG(Context ctx, boolean defBG){
                setBoolean(ctx,"pref_folder_default_bg",defBG);
            }

            public static void setFolderColor(Context ctx, String color) {
                setString(ctx,"pref_folder_color",color);
            }

            public static String getFolderColor() {
                return getString("pref_folder_color", "1");
            }
        }
    }

    public static class Application {

    }
}
