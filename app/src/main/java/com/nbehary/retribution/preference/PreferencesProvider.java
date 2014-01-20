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

    public static boolean checkKey(Context ctx, String key) {
        SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCES_KEY, 0);
        return preferences.contains(key);
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

    private static float getFloat(String key, float def) {
        return sKeyValues.containsKey(key) && sKeyValues.get(key) instanceof Float ?
                (Float) sKeyValues.get(key) : def;
    }

    private static void setFloat(Context ctx, String key, float value) {
        SharedPreferences preferences = ctx.getSharedPreferences(PREFERENCES_KEY, 0);
        Editor editor = preferences.edit();
        editor.putFloat(key, value);
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

            public static void setWorkspaceColumns(Context ctx, int cols){
                setInt(ctx,"pref_workspace_cols",cols);
            }

            public static void setWorkspaceRows(Context ctx, int rows){
                setInt(ctx,"pref_workspace_rows",rows);
            }

            public static int getWorkspaceColumns() {
                return getInt("pref_workspace_cols",0);
            }

            public static int getWorkspaceRows() {
                return getInt("pref_workspace_rows",0);
            }

            public static void setHotseatIcons(Context ctx, float icons){
                setFloat(ctx, "pref_hotseat_icons", icons);
            }

            public static float getHotseatIcons() {
                return getFloat("pref_hotseat_icons", 0);
            }

            public static void setIconSize(Context ctx, float size) {
                setFloat(ctx,"pref_grid_icon_size",size);
            }

            public static float getIconSize() {
                return getFloat("pref_grid_icon_size", 0);
            }

            public static void setIconSizeCalc(Context ctx, float size) {
                setFloat(ctx,"pref_grid_icon_size_calculated",size);
            }

            public static float getIconSizeCalc() {
                return getFloat("pref_grid_icon_size_calculated", 0);
            }

            public static void setIconTextSize(Context ctx, float size) {
                setFloat(ctx,"pref_grid_icon_text_size",size);
            }

            public static float getIconTextSize() {
                return getFloat("pref_grid_icon_text_size",0);
            }

            public static void setIconTextSizeCalc(Context ctx, float size) {
                setFloat(ctx,"pref_grid_icon_text_size_calculated",size);
            }

            public static float getIconTextSizeCalc() {
                return getFloat("pref_grid_icon_text_size_calculated",0);
            }

            public static void setHotseatIconSize(Context ctx, float size) {
                setFloat(ctx,"pref_hotseat_icon_size",size);
            }

            public static float getHotseatIconSize() {
                return getFloat("pref_hotseat_icon_size",0);
            }

            public static void setHideHotSeat(Context ctx,boolean hideHotSeat) {
                setBoolean(ctx,"pref_hide_hotseat",hideHotSeat);
            }

            public static boolean getHideHotSeat() {
                return getBoolean("pref_hide_hotseat",false);
            }

            public static void setHideQSB(Context ctx,boolean hideQSB) {
                setBoolean(ctx,"pref_hide_qsb",hideQSB);
            }

            public static boolean getHideQSB() {
                return getBoolean("pref_hide_qsb",false);
            }

            public static void setHideLabels(Context ctx,boolean hideLabels) {
                setBoolean(ctx,"pref_hide_labels",hideLabels);
            }

            public static boolean getHideLabels() {
                return getBoolean("pref_hide_labels",false);
            }

            public static void setAllowLand(Context ctx,boolean allowLand) {
                setBoolean(ctx,"pref_allow_land",allowLand);
            }

            public static boolean getAllowLand() {
                return getBoolean("pref_allow_land",false);
            }

            public static void setAutoHotseat(Context ctx,boolean autoHotseat) {
                setBoolean(ctx,"pref_auto_hotseat",autoHotseat);
            }

            public static boolean getAutoHotseat() {
                return getBoolean("pref_auto_hotseat",false);
            }
        }
    }

    public static class Application {

    }
}
