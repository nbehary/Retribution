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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by nate on 2/11/14.
 */
public class AppCategories  {

    private final ArrayList<CategoryEntry> mList2;

    private final IconCache mIconCache;

    private final AppFilter mAppFilter;

    private class CategoryEntry {
        private AppInfo info;
        private String category;

        public CategoryEntry(AppInfo info, String category){
            this.info = info;
            this.category = category;
        }

        public void put(AppInfo info, String category){
            this.info = info;
            this.category = category;
        }

        public AppInfo getInfo() {return info;}

        public String getCategory() {return category;}
    }

    public AppCategories(IconCache iconCache, AppFilter appFilter) {

        mList2 = new ArrayList<CategoryEntry>();
        mIconCache = iconCache;
        mAppFilter = appFilter;
    }

    public void addAppToCategory(AppInfo info, String category){
        CategoryEntry entry = new CategoryEntry(info,category);
        mList2.add(entry);
    }

    public void addEmptyCategory(String category) {
        AppInfo info = new AppInfo();
        info.title = "Retribution Launcher Not an app at all thingy.";
        mList2.add(new CategoryEntry(info, category));

    }

    public void removeAppFromCategory(AppInfo info, String category){
        Iterator it = mList2.iterator();
        while (it.hasNext()){
            CategoryEntry entry = (CategoryEntry) it.next();
            if ( entry.getCategory().equals(category) && entry.getInfo().equals(info)){
                mList2.remove(entry);
                break;
            }
        }

    }

    public String clearEmptyCategories(){
        Iterator it = mList2.iterator();
        while (it.hasNext()){
            CategoryEntry entry = (CategoryEntry) it.next();
            if ( entry.getInfo().title.equals("Retribution Launcher Not an app at all thingy.")){
                mList2.remove(entry);
                return entry.getCategory();
            }
        }
        return "";
    }

    public ArrayList<String> getCategories() {
        ArrayList<String> categories = new ArrayList<String>();
        Iterator it = mList2.iterator();
        while (it.hasNext()) {
            CategoryEntry entry = (CategoryEntry) it.next();
            if (!categories.contains(entry.getCategory())) {
                categories.add(entry.getCategory());
            }
        }
        return categories;
    }

    public AllAppsList getCategoryList(String category) {
        //TODO: This is probably terrible......
        AllAppsList list = new AllAppsList(mIconCache,mAppFilter);
        Iterator it = mList2.iterator();
        while (it.hasNext()) {
            CategoryEntry entry = (CategoryEntry) it.next();
            if (entry.getCategory().equals(category)) {
                list.add(entry.getInfo());
            }

        }
        return list;

    }


}
