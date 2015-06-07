/*
 *
 *   Copyright (c) 2015. Nathan A. Behary
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *
 */

package com.nbehary.retribution;


import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.view.View;

import com.nbehary.retribution.preference.PreferencesProvider;

public class ColorTheme {

    //This class is a work in progress.  It is in no way guaranteed to look or function like this in the future.


    int id;
    String name;
    int mFolderBack;
    int mFolderLabels;
    int mFolderName;
    boolean mFolderIconTint;
    int mFolderType;

    int mAllAppsOuter;
    int mAllAppsInner;



    int mSearchBarBack;
    int mSearchBarGlass;
    int mSearchBarMic;
    //TODO: Maybe.  Split the PagedView items into separate ones (Apps, Widgets, IconPacks) where applicable.
    int mPagedViewBack;
    int mPagedViewCards;
    int mPagedViewLabels;

    boolean mWallTint; //TODO: I'm temporary.  Tint from wallpaper will be a named theme.'
    private Context mContext; //TODO: Not sure we need this. Only keep if used more than just readFromFolderColors.

    public ColorTheme(Context ctx){
        mContext = ctx;
        readFromFolderColors();
    }



    /* This is called by the constructor the first time a ColorTheme is ever created. (TODO:eventually, always now)
       It reads the Values set to FolderColors items and builds a theme based on it.

       When done, it removes the FolderColors items from SharedPrefs and sets "pref_folder_colors_import" to true.
       (TODO: again, eventually.  Once there are no reads of those items left, and ColorTheme is savable.)
       TODO too: This gets to be public until we don't rely on Prefs anymore....make it private.
     */
    public void readFromFolderColors() {
        //This is a template for how fromWallpaper will work.
        //These 3 correspond to,respectively, getRgb(), getBodyTextColor(), and getTitleColor() from Palette.Swatch.
        //How they are assigned to others are how the others match to those.
        mFolderBack = PreferencesProvider.Interface.General.getFolderBackColor();
        mFolderLabels = PreferencesProvider.Interface.General.getFolderIconColor();
        mFolderName = PreferencesProvider.Interface.General.getFolderNameColor();

        //Uses getRgb().
        mPagedViewBack = mFolderBack;
        mSearchBarBack = mFolderBack;

        //Uses getBodyTextColor()
        mPagedViewLabels = mFolderLabels;
        mSearchBarGlass = mFolderLabels;
        mSearchBarMic = mFolderLabels;

        //Uses getTitleColor()
        mPagedViewCards = mFolderName;

        //Custom
        //TODO: AllAppsButton is very difficult to auto-color well.  For now, Inner is Body Text, Outer is getRgb.
        mAllAppsInner = mFolderLabels;
        mAllAppsOuter = mFolderBack;


        mFolderIconTint = PreferencesProvider.Interface.General.getFolderIconTint();

    }

    //TODO: This is VERY temporary for the conversion from Folder Colors to Color Theme. DO NOT ADD others.
    //  Color Themes will be saved to the database.

    public void writeToPrefs() {
        PreferencesProvider.Interface.General.setFolderBackColor(mContext, mFolderBack);
        PreferencesProvider.Interface.General.setFolderNameColor(mContext, mFolderName);
        PreferencesProvider.Interface.General.setFolderIconColor(mContext, mFolderLabels);
        PreferencesProvider.Interface.General.setDefaultFolderBG(mContext, false);//TODO: I'm probably useless

        PreferencesProvider.Interface.General.setFolderIconTint(mContext, mFolderIconTint);
        PreferencesProvider.Interface.General.setFolderType(mContext, 0); //TODO: I'm probably useless
        PreferencesProvider.Interface.General.setWallpaperTint(mContext, mWallTint);
    }

    public int getPagedViewLabels() {
        return mPagedViewLabels;
    }

    public void setPagedViewLabels(int mPagedViewLabels) {
        this.mPagedViewLabels = mPagedViewLabels;
    }

    public int getPagedViewCards() {
        return mPagedViewCards;
    }

    public void setPagedViewCards(int mPagedViewCards) {
        this.mPagedViewCards = mPagedViewCards;
    }

    public int getPagedViewBack() {
        return mPagedViewBack;
    }

    public void setPagedViewBack(int mPagedViewBack) {
        this.mPagedViewBack = mPagedViewBack;
    }

    public int getAllAppsOuter() {
        return mAllAppsOuter;
    }

    public void setAllAppsOuter(int mAllAppsButton) {
        this.mAllAppsOuter = mAllAppsButton;
    }

    public int getAllAppsInner() {
        return mAllAppsInner;
    }

    public void setAllAppsInner(int mAllAppsInner) {
        this.mAllAppsInner = mAllAppsInner;
    }

    public int getFolderName() {
        return mFolderName;
    }

    public void setFolderName(int mFolderName) {
        this.mFolderName = mFolderName;
    }

    public int getFolderLabel() {
        return mFolderLabels;
    }

    public void setFolderIcon(int mFolderIcon) {
        this.mFolderLabels = mFolderIcon;
    }

    public int getFolderBack() {
        return mFolderBack;
    }

    public void setFolderBack(int mFolderBack) {
        this.mFolderBack = mFolderBack;
    }

    public int getSearchBarBack() {
        return mSearchBarBack;
    }

    public void setSearchBarBack(int mSearchBar) {
        this.mSearchBarBack = mSearchBar;
    }

    public int getSearchBarGlass() {
        return mSearchBarGlass;
    }

    public void setSearchBarGlass(int mSearchBarGlass) {
        this.mSearchBarGlass = mSearchBarGlass;
    }

    public int getSearchBarMic() {
        return mSearchBarMic;
    }

    public void setSearchBarMic(int mSearchBarMic) {
        this.mSearchBarMic = mSearchBarMic;
    }

    public boolean getFolderIconTint() {
        return mFolderIconTint;
    }

    public void setFolderIconTint(boolean mFolderIconTint) {
        this.mFolderIconTint = mFolderIconTint;
    }

    public boolean ismallTint() {
        return mWallTint;
    }

    public void setWallTint(boolean mWallTint) {
        this.mWallTint = mWallTint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFolderType() {
        return mFolderType;
    }

    public void setFolderType(int mFolderType) {
        this.mFolderType = mFolderType;
    }

    static public Palette.Swatch swatchFromWallpaper(Context ctx) {

        View launcherView = LauncherAppState.getInstance().getmLauncherView();
        Bitmap bmp;
        if (WallpaperManager.getInstance(ctx).getWallpaperInfo() != null) {
            //TODO: We don't actually want to base this on the thumbnail, but it is the best thing for now (maybe ever).
            //wallpaperDrawable = WallpaperManager.getInstance(ctx).getWallpaperInfo().loadThumbnail(pm);
            launcherView.setDrawingCacheEnabled(true);
            bmp = launcherView.getDrawingCache();
        } else {
            Drawable wallpaperDrawable = WallpaperManager.getInstance(ctx).getDrawable();
            bmp = Utilities.drawableToBitmap(wallpaperDrawable);
        }

        Palette pal = Palette.from(bmp).generate();
        //Todo: Better?
        if (pal.getVibrantSwatch()!=null) {
            return pal.getVibrantSwatch();
        }
        if (pal.getMutedSwatch()!=null) {
            return pal.getMutedSwatch();
        }

        return pal.getMutedSwatch();
    }

    static Drawable tintedAppsButtonDrawable(Context ctx, int outerColor, int innerColor){
        Resources res = ctx.getResources();
/*        BitmapDrawable inner = (BitmapDrawable) res.getDrawable(R.drawable.ic_allapps_inner);
        inner.setGravity(Gravity.CENTER);
        DrawableCompat.setTint(DrawableCompat.wrap(inner), innerColor);
 */
        BitmapDrawable outer = (BitmapDrawable) res.getDrawable(R.drawable.ic_allapps_new);
/*        outer.setGravity(Gravity.CENTER);
        DrawableCompat.setTint(DrawableCompat.wrap(outer), outerColor);


        Drawable drawableArray[]= new Drawable[]{outer,inner};
        LayerDrawable layerDraw = new LayerDrawable(drawableArray);
        return layerDraw;
*/
        return outer;
    }
}
