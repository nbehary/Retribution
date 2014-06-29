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

import android.app.SearchManager;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;

import java.lang.ref.WeakReference;

import com.nbehary.retribution.R;

import com.nbehary.retribution.pro_key.IRemoteService;

public class LauncherAppState {
    private static final String TAG = "LauncherAppState";
    private static final String SHARED_PREFERENCES_KEY = "com.nbehary.retribution_preferences";

    private LauncherModel mModel;
    private IconCache mIconCache;
    private AppFilter mAppFilter;
    private WidgetPreviewLoader.CacheDb mWidgetPreviewCacheDb;
    private IconPackPreviewLoader.CacheDb mIconPackPreviewCacheDb;
    private boolean mIsScreenLarge;
    private float mScreenDensity;
    private int mLongPressTimeout = 300;

    private static WeakReference<LauncherProvider> sLauncherProvider;
    private static Context sContext;

    private static LauncherAppState INSTANCE;

    private DynamicGrid mDynamicGrid;

    public String mVersionName;
    public int mVersionCode;

    public String internalVersion = "1.3.1";

    private boolean mProVersion;

    IRemoteService mRemoteService;

    public static LauncherAppState getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LauncherAppState();
        }
        return INSTANCE;
    }

    public static LauncherAppState getInstanceNoCreate() {
        return INSTANCE;
    }

    public Context getContext() {
        return sContext;
    }

    public static void setApplicationContext(Context context) {
        if (sContext != null) {
            Log.w(Launcher.TAG, "setApplicationContext called twice! old=" + sContext + " new=" + context);
        }
        sContext = context.getApplicationContext();
    }

    private LauncherAppState() {
        if (sContext == null) {
            throw new IllegalStateException("LauncherAppState inited before app context set");
        }

        Log.v(Launcher.TAG, "LauncherAppState inited");

        if (sContext.getResources().getBoolean(R.bool.debug_memory_enabled)) {
            MemoryTracker.startTrackingMe(sContext, "L");
        }

        // set sIsScreenXLarge and mScreenDensity *before* creating icon cache
        mIsScreenLarge = isScreenLarge(sContext.getResources());
        mScreenDensity = sContext.getResources().getDisplayMetrics().density;

        mWidgetPreviewCacheDb = new WidgetPreviewLoader.CacheDb(sContext);
        mIconPackPreviewCacheDb = new IconPackPreviewLoader.CacheDb(sContext);
        mIconCache = new IconCache(sContext);

        mAppFilter = AppFilter.loadByName(sContext.getString(R.string.app_filter_class));
        mModel = new LauncherModel(this, mIconCache, mAppFilter);

        // Register intent receivers
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        sContext.registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        sContext.registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(SearchManager.INTENT_GLOBAL_SEARCH_ACTIVITY_CHANGED);
        sContext.registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(SearchManager.INTENT_ACTION_SEARCHABLES_CHANGED);
        sContext.registerReceiver(mModel, filter);

        // Register for changes to the favorites
        ContentResolver resolver = sContext.getContentResolver();
        resolver.registerContentObserver(LauncherSettings.Favorites.CONTENT_URI, true,
                mFavoritesObserver);

        PackageInfo pInfo = null;

        try {
            pInfo = sContext.getPackageManager().getPackageInfo(sContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mVersionName = pInfo.versionName;
        mVersionCode = pInfo.versionCode;
        mProVersion = false;

        checkProVersion();

    }

    /**
     * Call from Application.onTerminate(), which is not guaranteed to ever be called.
     */
    public void onTerminate() {
        sContext.unregisterReceiver(mModel);

        ContentResolver resolver = sContext.getContentResolver();
        resolver.unregisterContentObserver(mFavoritesObserver);
    }

    /**
     * Receives notifications whenever the user favorites have changed.
     */
    private final ContentObserver mFavoritesObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // If the database has ever changed, then we really need to force a reload of the
            // workspace on the next load
            mModel.resetLoadedState(false, true);
            mModel.startLoaderFromBackground();
        }
    };



    LauncherModel setLauncher(Launcher launcher) {
        if (mModel == null) {
            throw new IllegalStateException("setLauncher() called before init()");
        }
        mModel.initialize(launcher);
        mModel.setLauncher(launcher);
        return mModel;
    }

    IconCache getIconCache() {
        return mIconCache;
    }

    LauncherModel getModel() {
        return mModel;
    }

    boolean shouldShowAppOrWidgetProvider(ComponentName componentName) {
        return mAppFilter == null || mAppFilter.shouldShowApp(componentName);
    }

    WidgetPreviewLoader.CacheDb getWidgetPreviewCacheDb() {
        return mWidgetPreviewCacheDb;
    }

    IconPackPreviewLoader.CacheDb getIconPackPreviewCacheDb() {
        return mIconPackPreviewCacheDb;
    }

    String getVersionName() {return mVersionName;}

    int getVersionCode() {return mVersionCode;}

    boolean getProVersion() { return mProVersion; }

    static void setLauncherProvider(LauncherProvider provider) {
        sLauncherProvider = new WeakReference<LauncherProvider>(provider);
    }

    static LauncherProvider getLauncherProvider() {
        return sLauncherProvider.get();
    }

    public static String getSharedPreferencesKey() {
        return SHARED_PREFERENCES_KEY;
    }

    DeviceProfile initDynamicGrid(Context context, int minWidth, int minHeight,
                                  int width, int height,
                                  int availableWidth, int availableHeight) {
        if (mDynamicGrid == null) {
            mDynamicGrid = new DynamicGrid(context,
                    context.getResources(),
                    minWidth, minHeight, width, height,
                    availableWidth, availableHeight);
        }

        // Update the icon size
        DeviceProfile grid = mDynamicGrid.getDeviceProfile();

        Utilities.setIconSize(grid.iconSizePx);
        grid.updateFromConfiguration(context.getResources(), width, height,
                availableWidth, availableHeight);
        return grid;
    }
    DynamicGrid getDynamicGrid() {
        return mDynamicGrid;
    }

    public void checkProVersion() {
        PackageManager manager = sContext.getPackageManager();
        mProVersion = false;
        boolean isDebuggable =  ( 0 != ( sContext.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );
        if (isDebuggable) {
            mProVersion= true;
            return;
        }

        if ((manager.checkSignatures("com.nbehary.retribution", "com.nbehary.retribution.pro_key")
                == PackageManager.SIGNATURE_MATCH) &&
                (manager.getInstallerPackageName("com.nbehary.retribution.pro_key").equals("com.android.vending"))){
          //  Intent serviceIntent=new Intent();
          //  serviceIntent.setClassName("com.nbehary.retribution.pro_key", "com.nbehary.retribution.pro_key.LicenseCheckService");
          //  mProVersion=sContext.bindService(serviceIntent, mServiceConnection,Context.BIND_AUTO_CREATE);
                mProVersion = true;
        }

        if ((mVersionName.contains("Beta"))||(mVersionName.contains("Dev")) || (mVersionName.contains("RC"))) {
            if ((manager.checkSignatures("com.nbehary.retribution", "com.nbehary.retribution.key.beta")
                    == PackageManager.SIGNATURE_MATCH) &&
                    (manager.getInstallerPackageName("com.nbehary.retribution.key.beta").equals("com.android.vending"))){
                mProVersion = true;
            }
        }





    }

    private ServiceConnection mServiceConnection=new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // get instance of the aidl binder
            mRemoteService = IRemoteService.Stub.asInterface(service);
            try {
                String message = "";
                if (mRemoteService.checkLicense()) {
                    message=mRemoteService.sayHello("Mina");
                }
                Log.v("nbehary445", message);
            } catch (RemoteException e) {
                Log.e("nbehary445", "RemoteException: "+e.toString());
            }

        }
    };

    public boolean isScreenLarge() {
        return mIsScreenLarge;
    }

    // Need a version that doesn't require an instance of LauncherAppState for the wallpaper picker
    public static boolean isScreenLarge(Resources res) {
        return res.getBoolean(R.bool.is_large_tablet);
    }

    public static boolean isScreenLandscape(Context context) {
        return context.getResources().getConfiguration().orientation ==
            Configuration.ORIENTATION_LANDSCAPE;
    }

    public float getScreenDensity() {
        return mScreenDensity;
    }

    public int getLongPressTimeout() {
        return mLongPressTimeout;
    }
}
