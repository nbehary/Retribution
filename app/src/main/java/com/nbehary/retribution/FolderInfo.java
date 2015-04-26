/*
 * Copyright (C) 2008 The Android Open Source Project
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

import java.util.ArrayList;

import android.content.ContentValues;

/**
 * Represents a folder containing shortcuts or apps.
 */
class FolderInfo extends ItemInfo {

    /**
     * Whether this folder has been opened
     */
    boolean opened;

    /**
     * The apps and shortcuts
     */
    final ArrayList<ShortcutInfo> contents = new ArrayList<ShortcutInfo>();

    private final ArrayList<FolderListener> listeners = new ArrayList<FolderListener>();

    final ArrayList<CharSequence> titles = new ArrayList<CharSequence>();

    /**
     * Colors and sorting
     */

    int customColors;
    int nameColor;
    int labelColor;
    int iconColor;
    int backColor;

    int sortItems;
    int sortType;

    FolderInfo() {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_FOLDER;
    }

    /**
     * Add an app or shortcut
     *
     * @param item
     */
    public void add(ShortcutInfo item) {
        contents.add(item);
        titles.add(item.title);
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onAdd(item);
        }
        itemsChanged();
    }

    /**
     * Remove an app or shortcut. Does not change the DB.
     *
     * @param item
     */
    public void remove(ShortcutInfo item) {
        contents.remove(item);
        titles.remove(item.title);
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onRemove(item);
        }
        itemsChanged();
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onTitleChanged(title);
        }
    }

    @Override
    void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);
        values.put(LauncherSettings.Favorites.TITLE, title.toString());
        values.put(LauncherSettings.Favorites.FOLDER_CUSTOM_COLORS,customColors);
        values.put(LauncherSettings.Favorites.FOLDER_NAME_COLOR,nameColor);
        values.put(LauncherSettings.Favorites.FOLDER_ICON_COLOR,iconColor);
        values.put(LauncherSettings.Favorites.FOLDER_LABEL_COLOR,labelColor);
        values.put(LauncherSettings.Favorites.FOLDER_BACK_COLOR,backColor);
        values.put(LauncherSettings.Favorites.FOLDER_SORT,sortItems);
        values.put(LauncherSettings.Favorites.FOLDER_SORT_TYPE,sortType);
    }

    void addListener(FolderListener listener) {
        listeners.add(listener);
    }

    void removeListener(FolderListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    private void itemsChanged() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onItemsChanged();
        }
    }

    @Override
    void unbind() {
        super.unbind();
        listeners.clear();
    }

    interface FolderListener {
        void onAdd(ShortcutInfo item);
        void onRemove(ShortcutInfo item);
        void onTitleChanged(CharSequence title);
        void onItemsChanged();
    }

    @Override
    public String toString() {
        return "FolderInfo(id=" + this.id + " type=" + this.itemType
                + " container=" + this.container + " screen=" + screenId
                + " cellX=" + cellX + " cellY=" + cellY + " spanX=" + spanX
                + " spanY=" + spanY + " dropPos=" + dropPos + ")";
    }
}
