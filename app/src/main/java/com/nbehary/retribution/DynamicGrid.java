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

import android.appwidget.AppWidgetHostView;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.LayoutDirection;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.nbehary.retribution.R;
import com.nbehary.retribution.backup.BackupProtos;
import com.nbehary.retribution.preference.PreferencesProvider;


class DeviceProfileQuery {
    float widthDps;
    float heightDps;
    float value;
    PointF dimens;

    DeviceProfileQuery(float w, float h, float v) {
        widthDps = w;
        heightDps = h;
        value = v;
        dimens = new PointF(w, h);
    }
}

class DeviceProfile {
    String name;
    float minWidthDps;
    float minHeightDps;
    float numRows;
    float numRowsCalc;
    float numRowsDevice;
    float numColumns;
    float numColumnsCalc;
    float numColumnsDevice;
    float iconSize;
    float iconSizeCalc;
    float iconSizeDevice;
    float iconTextSize;
    float iconTextSizeCalc;
    float iconTextSizeDevice;
    float numHotseatIcons;
    float hotseatIconSize;
    float hotseatIconSizeCalc;

    boolean isLandscape;
    boolean isTablet;
    boolean isLargeTablet;
    boolean transposeLayoutWithOrientation;

    int desiredWorkspaceLeftRightMarginPx;
    int edgeMarginPx;
    Rect defaultWidgetPadding;

    int widthPx;
    int heightPx;
    int availableWidthPx;
    int availableHeightPx;
    int iconSizePx;
    int iconSizePxDevice;
    int iconTextSizePx;

    int cellWidthPx;
    int cellWidthPxDevice;
    int cellHeightPx;
    int folderBackgroundOffset;
    int folderIconSizePx;
    int folderCellWidthPx;
    int folderCellHeightPx;
    int hotseatCellWidthPx;
    int hotseatCellHeightPx;
    int hotseatIconSizePx;
    int hotseatBarHeightPx;
    int hotseatBarHeightPxDefault;
    int hotseatAllAppsRank;
    int allAppsNumRows;
    int allAppsNumCols;
    int searchBarSpaceWidthPx;
    int searchBarSpaceMaxWidthPx;
    int searchBarSpaceHeightPx;
    int searchBarHeightPx;
    int searchBarSpaceWidthPxDefault;
    int searchBarSpaceMaxWidthPxDefault;
    int searchBarSpaceHeightPxDefault;
    int searchBarHeightPxDefault;
    int pageIndicatorHeightPx;
    boolean hideHotseat;
    boolean hideQSB;
    boolean hideLabels;
    boolean allowLandscape;
    boolean autoHotseat;


    FrameLayout.LayoutParams mSearchBarLayoutForDrag;

    Resources mResources;
    Context mContext;

    DeviceProfile(String n, float w, float h, float r, float c,
                  float is, float its, float hs, float his) {
        // Ensure that we have an odd number of hotseat items (since we need to place all apps)
        if (!AppsCustomizePagedView.DISABLE_ALL_APPS && hs % 2 == 0) {
            throw new RuntimeException("All Device Profiles must have an odd number of hotseat spaces");
        }

        name = n;
        minWidthDps = w;
        minHeightDps = h;
        numRows = r;
        numColumns = c;
        iconSize = is;
        iconTextSize = its;
        numHotseatIcons = hs;
        hotseatIconSize = his;
    }

    DeviceProfile(Context context,
                  ArrayList<DeviceProfile> profiles,
                  float minWidth, float minHeight,
                  int wPx, int hPx,
                  int awPx, int ahPx,
                  Resources resources) {
        boolean isProVersion = LauncherAppState.getInstance().getProVersion();
        DisplayMetrics dm = resources.getDisplayMetrics();
        ArrayList<DeviceProfileQuery> points =
                new ArrayList<DeviceProfileQuery>();
        transposeLayoutWithOrientation =
                resources.getBoolean(R.bool.hotseat_transpose_layout_with_orientation);
        minWidthDps = minWidth;
        minHeightDps = minHeight;
        mResources = resources;
        mContext = context;

        ComponentName cn = new ComponentName(context.getPackageName(),
                this.getClass().getName());
        defaultWidgetPadding = AppWidgetHostView.getDefaultPaddingForWidget(context, cn, null);
        edgeMarginPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_edge_margin);
        desiredWorkspaceLeftRightMarginPx = 2 * edgeMarginPx;
        pageIndicatorHeightPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_page_indicator_height);

        // Interpolate the rows
        for (DeviceProfile p : profiles) {
            points.add(new DeviceProfileQuery(p.minWidthDps, p.minHeightDps, p.numRows));
        }
        numRows = Math.round(invDistWeightedInterpolate(minWidth, minHeight, points));
        numRowsDevice = numRows;
        numRowsCalc= numRows;
        numRows = PreferencesProvider.Interface.General.getWorkspaceRows();
        if ( (numRows==0)|| ((!isProVersion) && (( numRows > numRowsCalc+1) || (numRows < numRowsCalc-1))) ) {
            numRows = numRowsCalc;
        }

        // Interpolate the columns
        points.clear();
        for (DeviceProfile p : profiles) {
            points.add(new DeviceProfileQuery(p.minWidthDps, p.minHeightDps, p.numColumns));
        }
        numColumns = Math.round(invDistWeightedInterpolate(minWidth, minHeight, points));
        numColumnsDevice = numColumnsCalc = numColumns;
        numColumns = PreferencesProvider.Interface.General.getWorkspaceColumns();
        if ( (numColumns==0) || ((!isProVersion) && (( numColumns > numColumnsCalc+1) || (numColumns < numColumnsCalc-1))) ) {
            numColumns = numColumnsCalc;
        }
        // Interpolate the icon size
        points.clear();
        for (DeviceProfile p : profiles) {
            points.add(new DeviceProfileQuery(p.minWidthDps, p.minHeightDps, p.iconSize));
        }

        iconSize = invDistWeightedInterpolate(minWidth, minHeight, points);
        iconSizeCalc = iconSize;
        iconSizeDevice = iconSize;

            iconSizeCalc = PreferencesProvider.Interface.General.getIconSizeCalc();
            if (iconSizeCalc == 0){
                iconSizeCalc = iconSize;
            }
            iconSize = PreferencesProvider.Interface.General.getIconSize();

        if (iconSize==0) {
            iconSize = iconSizeCalc;
        }
        iconSizePx = DynamicGrid.pxFromDp(iconSize, dm);
        iconSizePxDevice = DynamicGrid.pxFromDp(iconSizeDevice,dm);

        points.clear();
        for (DeviceProfile p : profiles) {
            points.add(new DeviceProfileQuery(p.minWidthDps, p.minHeightDps, p.iconTextSize));
        }
        iconTextSizeDevice = invDistWeightedInterpolate(minWidth, minHeight, points);

        //hideLabels = PreferencesProvider.Interface.General.getHideLabels();
        hideLabels = false;
        setIconLabels();

        hideHotseat = PreferencesProvider.Interface.General.getHideHotSeat();
        // Interpolate the hotseat size
        points.clear();
        for (DeviceProfile p : profiles) {
            points.add(new DeviceProfileQuery(p.minWidthDps, p.minHeightDps, p.numHotseatIcons));
        }
        float temp = numHotseatIcons = Math.round(invDistWeightedInterpolate(minWidth, minHeight, points));
        if (isProVersion) {
            numHotseatIcons = PreferencesProvider.Interface.General.getHotseatIcons();
        }
        if  (numHotseatIcons==0) {
            numHotseatIcons = temp;
        }
        // Interpolate the hotseat icon size
        points.clear();
        for (DeviceProfile p : profiles) {
            points.add(new DeviceProfileQuery(p.minWidthDps, p.minHeightDps, p.hotseatIconSize));
        }
        // Hotseat
        hotseatIconSize = hotseatIconSizeCalc = invDistWeightedInterpolate(minWidth, minHeight, points);
        if (isProVersion) {
            hotseatIconSize = PreferencesProvider.Interface.General.getHotseatIconSize();
        }
        if  (hotseatIconSize==0) {
            hotseatIconSize = hotseatIconSizeCalc;
        }
        hotseatIconSizePx = DynamicGrid.pxFromDp(hotseatIconSize, dm);
        hotseatBarHeightPxDefault = DynamicGrid.pxFromDp(hotseatIconSizeCalc,dm);
        hotseatAllAppsRank = (int) (numHotseatIcons / 2);

        // Calculate other vars based on Configuration
        updateFromConfiguration(resources, wPx, hPx, awPx, ahPx);

        // Search Bar
        searchBarSpaceMaxWidthPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_search_bar_max_width);
        hideQSB = PreferencesProvider.Interface.General.getHideQSB();
        //hideQSB = false;
        if (hideQSB) {
            searchBarHeightPx = 0;
            searchBarSpaceWidthPx = 0;
            searchBarSpaceHeightPx = 0;
        } else {
            searchBarHeightPx = mResources.getDimensionPixelSize(R.dimen.dynamic_grid_search_bar_height);
            searchBarSpaceWidthPx = Math.min(searchBarSpaceMaxWidthPx, widthPx);
            searchBarSpaceHeightPx = searchBarHeightPx + 2 * edgeMarginPx;
        }
        searchBarSpaceMaxWidthPxDefault = resources.getDimensionPixelSize(R.dimen.dynamic_grid_search_bar_max_width);
        searchBarHeightPxDefault = mResources.getDimensionPixelSize(R.dimen.dynamic_grid_search_bar_height);
        searchBarSpaceWidthPxDefault = Math.min(searchBarSpaceMaxWidthPxDefault, widthPx);
        searchBarSpaceHeightPxDefault = searchBarHeightPxDefault + 2 * edgeMarginPx;


        if (PreferencesProvider.checkKey(mContext,"pref_allow_land")) {
            allowLandscape = PreferencesProvider.Interface.General.getAllowLand();
        } else {

            if (isTablet()) {
                allowLandscape = true;
            }else {
                allowLandscape = false;
            }

        }

        autoHotseat = PreferencesProvider.Interface.General.getAutoHotseat();
        setCellHotSeatAndFolders();
        if (iconSizeDevice > 72) {
            //Restrict icon sizes to <72, and don't shrink wrap if they were.....
            iconSize = iconSizeDevice = iconSizeCalc = 71.5f;
        } else {
            //Shrink-Wrap!!
            //Only if custom sizes not present. Already ran if they are.  (and it kills them)
            if ((iconSize == iconSizeDevice) && (iconTextSize == iconTextSizeDevice)) {
                adjustSizesAuto(resources);
            }
        }
    }

    public DeviceProfile(DeviceProfile profile) {
        this.name = profile.name;
        this.minWidthDps = profile.minWidthDps;
        this.minHeightDps = profile.minHeightDps;
        this.numRows = profile.numRows;
        this.numRowsCalc = profile.numRowsCalc;
        this.numRowsDevice = profile.numRowsDevice;
        this.numColumns = profile.numColumns;
        this.numColumnsCalc = profile.numColumnsCalc;
        this.numColumnsDevice = profile.numColumnsDevice;
        this.iconSize = profile.iconSize;
        this.iconSizeCalc = profile.iconSizeCalc;
        this.iconSizeDevice = profile.iconSizeDevice;
        this.iconTextSize = profile.iconTextSize;
        this.iconTextSizeCalc = profile.iconTextSizeCalc;
        this.iconTextSizeDevice = profile.iconTextSizeDevice;
        this.numHotseatIcons = profile.numHotseatIcons;
        this.hotseatIconSize = profile.hotseatIconSize;
        this.hotseatIconSizeCalc =profile.hotseatIconSizeCalc;

        this.isLandscape = profile.isLandscape;
        this.isTablet = profile.isTablet();
        this.isLargeTablet = profile.isLargeTablet();
        this.transposeLayoutWithOrientation = profile.transposeLayoutWithOrientation;

        this.desiredWorkspaceLeftRightMarginPx = profile.desiredWorkspaceLeftRightMarginPx;
        this.edgeMarginPx = profile.edgeMarginPx;
        this.defaultWidgetPadding = profile.defaultWidgetPadding;

        this.widthPx = profile.widthPx;
        this.heightPx = profile.heightPx;
        this.availableWidthPx = profile.availableWidthPx;
        this.availableHeightPx = profile.availableHeightPx;
        this.iconSizePx = profile.iconSizePx;
        this.iconSizePxDevice = profile.iconSizePxDevice;
        this.iconTextSizePx = profile.iconTextSizePx;
        this.cellWidthPx = profile.cellWidthPx;
        this.cellHeightPx = profile.cellHeightPx;
        this.cellWidthPxDevice = profile.cellWidthPxDevice;
        this.folderBackgroundOffset = profile.folderBackgroundOffset;
        this.folderIconSizePx = profile.folderIconSizePx;
        this.folderCellWidthPx = profile.folderCellWidthPx;
        this.folderCellHeightPx = profile.folderCellHeightPx;
        this.hotseatCellWidthPx = profile.hotseatCellWidthPx;
        this.hotseatCellHeightPx = profile.hotseatCellHeightPx;
        this.hotseatIconSizePx = profile.hotseatIconSizePx;
        this.hotseatBarHeightPx = profile.hotseatBarHeightPx;
        this.hotseatBarHeightPxDefault = profile.hotseatBarHeightPxDefault;
        this.hotseatAllAppsRank = profile.hotseatAllAppsRank;
        this.allAppsNumRows = profile.allAppsNumCols;
        this.allAppsNumCols = profile.allAppsNumRows;
        this.searchBarSpaceWidthPx = profile.searchBarSpaceWidthPx;
        this.searchBarSpaceMaxWidthPx = profile.searchBarSpaceMaxWidthPx;
        this.searchBarSpaceHeightPx = profile.searchBarSpaceHeightPx;
        this.searchBarHeightPx = profile.searchBarHeightPx;
        this.searchBarSpaceWidthPxDefault = profile.searchBarSpaceWidthPxDefault;
        this.searchBarSpaceMaxWidthPxDefault = profile.searchBarSpaceMaxWidthPxDefault;
        this.searchBarSpaceHeightPxDefault = profile.searchBarSpaceHeightPxDefault;
        this.searchBarHeightPxDefault = profile.searchBarHeightPxDefault;
        this.pageIndicatorHeightPx = profile.pageIndicatorHeightPx;
        this.hideHotseat = profile.hideHotseat;
        this.hideQSB = profile.hideQSB;
        this.hideLabels = profile.hideLabels;
        this.mResources = profile.mResources;
        this.mContext = profile.mContext;
        this.allowLandscape = profile.allowLandscape;
        this.autoHotseat = profile.autoHotseat;
    }

    void updateFromConfiguration(Resources resources, int wPx, int hPx,
                                 int awPx, int ahPx) {

        isLandscape = (resources.getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE);
        isTablet = resources.getBoolean(R.bool.is_tablet);
        isLargeTablet = resources.getBoolean(R.bool.is_large_tablet);
        widthPx = wPx;
        heightPx = hPx;
        availableWidthPx = awPx;
        availableHeightPx = ahPx;

        Rect padding = getWorkspacePadding(isLandscape ?
                CellLayout.LANDSCAPE : CellLayout.PORTRAIT);
/*        int pageIndicatorOffset =
            resources.getDimensionPixelSize(R.dimen.apps_customize_page_indicator_offset);

        if (isLandscape) {
            allAppsNumRows = (availableHeightPx - pageIndicatorOffset - 4 * edgeMarginPx) /
                    (iconSizePx + iconTextSizePx + 2 * edgeMarginPx);
        } else {
            allAppsNumRows = (int) numRows + 1;
        }
        allAppsNumCols = (availableWidthPx - padding.left - padding.right - 2 * edgeMarginPx) /
                (iconSizePx + 2 * edgeMarginPx);


*/
        if (isLandscape) {

            allAppsNumCols = (availableWidthPx - padding.left - padding.right - 2 * edgeMarginPx) /
                    (iconSizePxDevice + 2 * edgeMarginPx);
            //Restrict AllApps columns to 7 or less
            if (allAppsNumCols > 7) {
                allAppsNumCols = 7;
            }
            allAppsNumRows = (allAppsNumCols/2) + 1;

        } else {
            allAppsNumRows = (int) numRowsDevice + 1;
            allAppsNumCols = allAppsNumRows - 1;

        }


    }

    void setIconLabels() {
        DisplayMetrics dm = mResources.getDisplayMetrics();
        if (!hideLabels) {
            iconTextSize = iconTextSizeDevice;
            iconTextSizeCalc = iconTextSize;



            iconTextSizeCalc = PreferencesProvider.Interface.General.getIconTextSizeCalc();
            if (iconTextSizeCalc == 0){
                iconTextSizeCalc = iconTextSize;
            }
            iconTextSize = PreferencesProvider.Interface.General.getIconTextSize();

            if  (iconTextSize==0) {
                iconTextSize = iconTextSizeCalc;
            }
            iconTextSizePx = DynamicGrid.pxFromSp(iconTextSize, dm);
        } else {
            iconTextSize = 0.001f;
            iconTextSizeCalc = 0;
            iconTextSizePx = DynamicGrid.pxFromSp(iconTextSize, dm);

        }
    }

    void setCellHotSeatAndFolders() {

        // Calculate the actual text height
        Paint textPaint = new Paint();
        textPaint.setTextSize(iconTextSizePx);
        FontMetrics fm = textPaint.getFontMetrics();
        cellWidthPx = iconSizePx;
        cellHeightPx = iconSizePx + (int)(  Math.ceil(fm.bottom - fm.top) +10);
        if (hideQSB) {
            searchBarHeightPx = 0;
            searchBarSpaceWidthPx = 0;
            searchBarSpaceHeightPx = 0;//2 * edgeMarginPx;
        } else {
            searchBarHeightPx = mResources.getDimensionPixelSize(R.dimen.dynamic_grid_search_bar_height);
            searchBarSpaceWidthPx = Math.min(searchBarSpaceMaxWidthPx, widthPx);
            searchBarSpaceHeightPx = searchBarHeightPx + 2 * edgeMarginPx;
        }
        // Hotseat
        if (hideHotseat) {
            hotseatBarHeightPx = 0;

            hotseatCellWidthPx = 0;
            hotseatCellHeightPx = 0;
            hotseatAllAppsRank = 0;

        } else {

            hotseatBarHeightPx = hotseatIconSizePx + 4 * edgeMarginPx;
            hotseatCellWidthPx = hotseatIconSizePx;
            hotseatCellHeightPx =  hotseatIconSizePx;
            hotseatAllAppsRank = (int) (numHotseatIcons / 2);

        }
        // Folder
        folderCellWidthPx = cellWidthPx + 3 * edgeMarginPx;
        folderCellHeightPx = cellHeightPx + (int) ((3f/2f) * edgeMarginPx);
        folderBackgroundOffset = -edgeMarginPx;
        folderIconSizePx = iconSizePx + 2 * -folderBackgroundOffset;
    }

    private float dist(PointF p0, PointF p1) {
        return (float) Math.sqrt((p1.x - p0.x)*(p1.x-p0.x) +
                (p1.y-p0.y)*(p1.y-p0.y));
    }

    private float weight(PointF a, PointF b,
                        float pow) {
        float d = dist(a, b);
        if (d == 0f) {
            return Float.POSITIVE_INFINITY;
        }
        return (float) (1f / Math.pow(d, pow));
    }

    private float invDistWeightedInterpolate(float width, float height,
                ArrayList<DeviceProfileQuery> points) {
        float sum = 0;
        float weights = 0;
        float pow = 5;
        float kNearestNeighbors = 3;
        final PointF xy = new PointF(width, height);

        ArrayList<DeviceProfileQuery> pointsByNearness = points;
        Collections.sort(pointsByNearness, new Comparator<DeviceProfileQuery>() {
            public int compare(DeviceProfileQuery a, DeviceProfileQuery b) {
                return (int) (dist(xy, a.dimens) - dist(xy, b.dimens));
            }
        });

        for (int i = 0; i < pointsByNearness.size(); ++i) {
            DeviceProfileQuery p = pointsByNearness.get(i);
            if (i < kNearestNeighbors) {
                float w = weight(xy, p.dimens, pow);
                if (w == Float.POSITIVE_INFINITY) {
                    return p.value;
                }
                weights += w;
            }
        }

        for (int i = 0; i < pointsByNearness.size(); ++i) {
            DeviceProfileQuery p = pointsByNearness.get(i);
            if (i < kNearestNeighbors) {
                float w = weight(xy, p.dimens, pow);
                sum += w * p.value / weights;
            }
        }

        return sum;
    }

    Rect getWorkspacePadding(int orientation) {
        Rect padding = new Rect();
        int left = mResources.getDimensionPixelSize(R.dimen.dynamic_grid_search_bar_height);
        if (orientation == CellLayout.LANDSCAPE &&
                transposeLayoutWithOrientation) {
            // Pad the left and right of the workspace with search/hotseat bar sizes
            if ((hideHotseat) && (hideQSB)) {
                padding.set(0,edgeMarginPx,0,edgeMarginPx);

            } else if (hideHotseat) {
                padding.set(left, edgeMarginPx,
                        hotseatBarHeightPxDefault, edgeMarginPx);
            }
            else {

                padding.set(left, edgeMarginPx,
                        hotseatBarHeightPx, edgeMarginPx);
            }


        } else {
            if (isTablet()) {
                // Pad the left and right of the workspace to ensure consistent spacing
                // between all icons
                int width = (orientation == CellLayout.LANDSCAPE)
                        ? Math.max(widthPx, heightPx)
                        : Math.min(widthPx, heightPx);
                // XXX: If the icon size changes across orientations, we will have to take
                //      that into account here too.
                int gap = (int) ((width - 2 * edgeMarginPx -
                        (numColumnsDevice * cellWidthPx)) / (2 * (numColumnsDevice + 1)));
                padding.set(edgeMarginPx + gap,
                        searchBarSpaceHeightPx,
                        edgeMarginPx + gap,
                        hotseatBarHeightPx + pageIndicatorHeightPx);
            } else {
                // Pad the top and bottom of the workspace with search/hotseat bar sizes
                padding.set(desiredWorkspaceLeftRightMarginPx - defaultWidgetPadding.left,
                        searchBarSpaceHeightPx,
                        desiredWorkspaceLeftRightMarginPx - defaultWidgetPadding.right,
                        hotseatBarHeightPx + pageIndicatorHeightPx);
            }
        }
        return padding;
    }

    // The rect returned will be extended to below the system ui that covers the workspace
    Rect getHotseatRect() {
        if (isVerticalBarLayout()) {
            return new Rect(availableWidthPx - hotseatBarHeightPx, 0,
                    Integer.MAX_VALUE, availableHeightPx);
        } else {
            return new Rect(0, availableHeightPx - hotseatBarHeightPx,
                    availableWidthPx, Integer.MAX_VALUE);
        }
    }

    int calculateCellWidth(int width, int countX) {
        return width / countX;
    }
    int calculateCellHeight(int height, int countY) {
        return height / countY;
    }

    boolean isPhone() {
        return !isTablet && !isLargeTablet;
    }
    boolean isTablet() {
        return isTablet;
    }
    boolean isLargeTablet() {
        return isLargeTablet;
    }

    boolean isVerticalBarLayout() {
        return isLandscape && transposeLayoutWithOrientation;
    }

    public FrameLayout.LayoutParams getSearchBarLayoutAfterDrag(View searchBar) {
        FrameLayout.LayoutParams lp;
        boolean hasVerticalBarLayout = isVerticalBarLayout();
        lp = (FrameLayout.LayoutParams) searchBar.getLayoutParams();
        if (hasVerticalBarLayout) {
            // Vertical search bar
            lp.gravity = Gravity.TOP | Gravity.LEFT;
            lp.width = searchBarSpaceHeightPx;
            lp.height = LayoutParams.MATCH_PARENT;
            if (!hideQSB) {
                searchBar.setPadding(
                        0, 2 * edgeMarginPx, 0,
                        2 * edgeMarginPx);
            } else {
                searchBar.setPadding(0,0,0,0);
            }
        } else {
            // Horizontal search bar
            lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            lp.width = searchBarSpaceWidthPx;
            lp.height = searchBarSpaceHeightPx;
            searchBar.setPadding(
                    2 * edgeMarginPx,
                    2 * edgeMarginPx,
                    2 * edgeMarginPx, 0);
        }
        return lp;

    }

    public FrameLayout.LayoutParams getSearchBarLayoutForDrag(View searchBar) {
        FrameLayout.LayoutParams lp;
        boolean hasVerticalBarLayout = isVerticalBarLayout();
        lp = (FrameLayout.LayoutParams) searchBar.getLayoutParams();
        if (hasVerticalBarLayout) {
            // Vertical search bar
            lp.gravity = Gravity.TOP | Gravity.LEFT;
            lp.width = searchBarSpaceHeightPxDefault;
            lp.height = LayoutParams.MATCH_PARENT;
            if (!hideQSB) {
                searchBar.setPadding(
                        0, 2 * edgeMarginPx, 0,
                        2 * edgeMarginPx);
            } else {
                searchBar.setPadding(0,0,0,0);
            }
        } else {
            // Horizontal search bar
            lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            lp.width = searchBarSpaceWidthPxDefault;
            lp.height = searchBarSpaceHeightPxDefault;
            searchBar.setPadding(
                    2 * edgeMarginPx,
                    2 * edgeMarginPx,
                    2 * edgeMarginPx, 0);
        }
        return lp;

    }

    public void layout(Launcher launcher) {
        FrameLayout.LayoutParams lp;
        //Resources res = launcher.getResources();
        boolean hasVerticalBarLayout = isVerticalBarLayout();

        // Layout the search bar space
        View searchBar = launcher.getSearchBar();
        lp = (FrameLayout.LayoutParams) searchBar.getLayoutParams();
        if ((hasVerticalBarLayout) ) {//&& (!hideQSB)) {
            // Vertical search bar
            lp.gravity =  Gravity.LEFT;
            lp.width = searchBarSpaceHeightPxDefault;
            lp.height = LayoutParams.MATCH_PARENT;
            if (!hideQSB) {
                searchBar.setPadding(
                    0, 2 * edgeMarginPx, 0,
                    2 * edgeMarginPx);
            } else {
                searchBar.setPadding(0,0,0,0);
            }
        } else {
            // Horizontal search bar
            lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            lp.width = searchBarSpaceWidthPxDefault;
            lp.height = searchBarSpaceHeightPxDefault;

            searchBar.setPadding(
                    2 * edgeMarginPx,
                    2 * edgeMarginPx,
                    2 * edgeMarginPx, 0);
        }
        searchBar.setLayoutParams(lp);
        searchBar.setVisibility(View.VISIBLE);

        // Layout the search bar
        View qsbBar = launcher.getQsbBar();
        LayoutParams vglp = qsbBar.getLayoutParams();
        vglp.width = LayoutParams.MATCH_PARENT;
        vglp.height = LayoutParams.MATCH_PARENT;
        qsbBar.setLayoutParams(vglp);

        // Layout the voice proxy
        View voiceButtonProxy = launcher.findViewById(R.id.voice_button_proxy);
        if (voiceButtonProxy != null) {
            if (hasVerticalBarLayout) {
                // TODO: MOVE THIS INTO SEARCH BAR MEASURE
            } else {
                lp = (FrameLayout.LayoutParams) voiceButtonProxy.getLayoutParams();
                lp.gravity = Gravity.TOP | Gravity.END;
                lp.width = (widthPx - searchBarSpaceWidthPx) / 2 +
                        2 * iconSizePx;
                lp.height = searchBarSpaceHeightPx;
            }
        }

        // Layout the workspace
        View workspace = launcher.findViewById(R.id.workspace);
        lp = (FrameLayout.LayoutParams) workspace.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        Rect padding = getWorkspacePadding(isLandscape
                ? CellLayout.LANDSCAPE
                : CellLayout.PORTRAIT);
        workspace.setPadding(padding.left, padding.top,
                padding.right, padding.bottom);
        workspace.setLayoutParams(lp);

        // Layout the hotseat
        View hotseat = launcher.findViewById(R.id.hotseat);
        lp = (FrameLayout.LayoutParams) hotseat.getLayoutParams();
        if (hasVerticalBarLayout) {
            // Vertical hotseat
            lp.gravity = Gravity.RIGHT;
            lp.width = hotseatBarHeightPx;
            lp.height = LayoutParams.MATCH_PARENT;
            hotseat.setPadding(0, 2 * edgeMarginPx,
                    2 * edgeMarginPx, 2 * edgeMarginPx);
        } else if (isTablet()) {
            //here!!
            // Pad the hotseat with the grid gap calculated above
            int gridGap = (int) ((widthPx - 2 * edgeMarginPx -
                    (numColumnsDevice * cellWidthPx)) / (2 * (numColumnsDevice + 1)));
            int gridWidth = (int) ((numColumnsDevice * cellWidthPx) +
                    ((numColumnsDevice - 1) * gridGap));
            int hotseatGap = (int) Math.max(0,
                    (gridWidth - (numHotseatIcons * hotseatCellWidthPx))
                            / (numHotseatIcons - 1));
            lp.gravity = Gravity.BOTTOM;
            lp.width = LayoutParams.MATCH_PARENT;
            lp.height = hotseatBarHeightPx;
            hotseat.setPadding(2 * edgeMarginPx + gridGap + hotseatGap, 0,
                    2 * edgeMarginPx + gridGap + hotseatGap,
                    2 * edgeMarginPx);
        } else {
            // For phones, layout the hotseat without any bottom margin
            // to ensure that we have space for the folders
            lp.gravity = Gravity.BOTTOM;
            lp.width = LayoutParams.MATCH_PARENT;
            lp.height = hotseatBarHeightPx;
            hotseat.findViewById(R.id.layout).setPadding(2 * edgeMarginPx, 0,
                    2 * edgeMarginPx, 0);
        }
        hotseat.setLayoutParams(lp);

        // Layout the page indicators
        View pageIndicator = launcher.findViewById(R.id.page_indicator);
        if (pageIndicator != null) {
            if (hasVerticalBarLayout) {
                // Hide the page indicators when we have vertical search/hotseat
                //pageIndicator.setVisibility(View.GONE);
            } else {
                // Put the page indicators above the hotseat
                lp = (FrameLayout.LayoutParams) pageIndicator.getLayoutParams();
                lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                lp.width = LayoutParams.WRAP_CONTENT;
                lp.height = LayoutParams.WRAP_CONTENT;
                lp.bottomMargin = hotseatBarHeightPx;
                pageIndicator.setLayoutParams(lp);
            }
        }
    }

    public float getNumRowsCalc() {
        return numRowsCalc;
    }

    public float getNumColumnsCalc() {
        return numColumnsCalc;
    }



    public boolean adjustSizesAuto(Resources res) {
        DisplayMetrics dm = res.getDisplayMetrics();
        int minWidth = DynamicGrid.pxFromDp(minWidthDps,dm);
        int minHeight = DynamicGrid.pxFromDp(minHeightDps,dm) - hotseatBarHeightPx - searchBarHeightPx;
      //  if (folderCellHeightPx*numRows > minWidth) {
        int fontArea = 0;

        Paint textPaint = new Paint();
        //iconTextSizePx= DynamicGrid.pxFromSp(iconTextSizeDevice,dm); //get default text size for device.
        textPaint.setTextSize(iconTextSizePx);
        FontMetrics fm = textPaint.getFontMetrics();
        fontArea = (int)(  Math.ceil(fm.bottom - fm.top))+10;
        int maxCellHeight = 0;
        if (allowLandscape) {
            //too high
            maxCellHeight = minWidth/(int)numRows;
        } else {

            maxCellHeight = minHeight/(int)numRows;
        }
        float overUnderRows = numRows - numRowsDevice;

        iconSizePx = maxCellHeight - fontArea - (int) ((3f/2f) * edgeMarginPx);
        iconSize = DynamicGrid.dpiFromPx(iconSizePx,dm);
        cellWidthPx = iconSizePx;
        folderCellWidthPx = cellWidthPx + 3 * edgeMarginPx;
        if(folderCellWidthPx*numColumns > minWidth) {
            //Here!!
            int iconSizePxTemp = (minWidth/(int)numColumns) - (int) ((2f/3f) * edgeMarginPx);
            if (!(iconSizePxTemp > iconSizePx)) {
                iconSizePx = iconSizePxTemp;
                iconSize = DynamicGrid.dpiFromPx(iconSizePx,dm);

            }

        }

        iconSizeCalc = iconSize;
        setCellHotSeatAndFolders();
        if ((iconSize < 28) ) {
            //Don't allow the hotseat at icon sizes lower than 48dp.
            autoHotseat = true;
            PreferencesProvider.Interface.General.setAutoHotseat(mContext,autoHotseat);
            hideHotseat = true;
            return true;
        }
        if (((iconSize >= 28) && PreferencesProvider.Interface.General.getAutoHotseat())) {
            //Hotseat valid and was hidden automatically.
            autoHotseat = false;
            hideHotseat = false;
            PreferencesProvider.Interface.General.setAutoHotseat(mContext,autoHotseat);
            return true;
        }

        //Cap the calculated icon size at 72dp
        if (iconSize >72) {
            iconSize  = 72;
            iconSizeCalc = iconSize;
            iconSizePx = DynamicGrid.pxFromDp(iconSize,dm);
        }

        return false;
    }
}

public class DynamicGrid {
    @SuppressWarnings("unused")
    private static final String TAG = "DynamicGrid";

    private DeviceProfile mProfile;
    private DeviceProfile mCalculatedProfile;
    private float mMinWidth;
    private float mMinHeight;

    public static float dpiFromPx(int size, DisplayMetrics metrics){
        float densityRatio = (float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT;
        return (size / densityRatio);
    }
    public static int pxFromDp(float size, DisplayMetrics metrics) {
        return (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                size, metrics));
    }
    public static int pxFromSp(float size, DisplayMetrics metrics) {
        return (int) Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                size, metrics));
    }

    public DynamicGrid(Context context, Resources resources,
                       int minWidthPx, int minHeightPx,
                       int widthPx, int heightPx,
                       int awPx, int ahPx) {
        DisplayMetrics dm = resources.getDisplayMetrics();
        ArrayList<DeviceProfile> deviceProfiles =
                new ArrayList<DeviceProfile>();
        boolean hasAA = !AppsCustomizePagedView.DISABLE_ALL_APPS;
        // Our phone profiles include the bar sizes in each orientation
        deviceProfiles.add(new DeviceProfile("Super Short Stubby",
                255, 300,  2, 3,  48, 13, (hasAA ? 5 : 4), 48));
        deviceProfiles.add(new DeviceProfile("Shorter Stubby",
                255, 400,  3, 3,  48, 13, (hasAA ? 5 : 4), 48));
        deviceProfiles.add(new DeviceProfile("Short Stubby",
                275, 420,  3, 4,  48, 13, (hasAA ? 5 : 4), 48));
        deviceProfiles.add(new DeviceProfile("Stubby",
                255, 450,  3, 4,  48, 13, (hasAA ? 5 : 4), 48));
        deviceProfiles.add(new DeviceProfile("Nexus S",
                296, 491.33f,  4, 4,  48, 13, (hasAA ? 5 : 4), 48));
        deviceProfiles.add(new DeviceProfile("Nexus 4",
                359, 518,  4, 4,  60, 13, (hasAA ? 5 : 4), 56));
        deviceProfiles.add(new DeviceProfile("test",
                294.666666f, 544,  4, 5,  48, 12, 5 , 48));
        // The tablet profile is odd in that the landscape orientation
        // also includes the nav bar on the side

        deviceProfiles.add(new DeviceProfile("Nexus 7",
                575, 904,  5, 6,  72, 14.4f,  7, 64));
       // //  TODO: This is what AOSP has: 575, 904,  6, 6,  72, 14.4f,  7, 60));

        // Larger tablet profiles always have system bars on the top & bottom
        deviceProfiles.add(new DeviceProfile("Nexus 10",
                727, 1207,  5, 8,  77, 14f,  9, 64));
        /*
        deviceProfiles.add(new DeviceProfile("Nexus 7",
                600, 960,  5, 5,  72, 14.4f,  5, 60));*/
       // deviceProfiles.add(new DeviceProfile("Nexus 10",
       //         800, 1280,  5, 5,  80, 14.4f, (hasAA ? 7 : 6), 64));

   //     deviceProfiles.add(new DeviceProfile("20-inch Tablet",
  //              1527, 2527,  7, 7,  100, 20,  7, 72));
        mMinWidth = dpiFromPx(minWidthPx, dm);
        mMinHeight = dpiFromPx(minHeightPx, dm);
        mProfile = new DeviceProfile(context, deviceProfiles,
                mMinWidth, mMinHeight,
                widthPx, heightPx,
                awPx, ahPx,
                resources);
        mCalculatedProfile = new DeviceProfile(mProfile);

    }

    DeviceProfile getDeviceProfile() {
        return mProfile;
    }

    DeviceProfile getCalculatedProfile() { return mCalculatedProfile; }

    public void setDeviceProfile(DeviceProfile profile) { mProfile = profile; }

    public String toString() {
        return "-------- DYNAMIC GRID ------- \n" +
                "Wd: " + mProfile.minWidthDps + ", Hd: " + mProfile.minHeightDps +
                ", W: " + mProfile.widthPx + ", H: " + mProfile.heightPx +
                " [r: " + mProfile.numRows + ", c: " + mProfile.numColumns +
                ", is: " + mProfile.iconSizePx + ", its: " + mProfile.iconTextSize +
                ", cw: " + mProfile.cellWidthPx + ", ch: " + mProfile.cellHeightPx +
                ", hc: " + mProfile.numHotseatIcons + ", his: " + mProfile.hotseatIconSizePx + "]";
    }
}
