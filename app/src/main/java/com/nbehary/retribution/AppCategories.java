package com.nbehary.retribution;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.nbehary.retribution.preference.PreferencesProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by nate on 2/11/14.
 */
public class AppCategories  {

    private ArrayList<CategoryEntry> mList2;

    private IconCache mIconCache;

    private AppFilter mAppFilter;

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
