package com.nbehary.retribution;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


import com.nbehary.retribution.preference.PreferencesProvider;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


public class GridEditor extends AppCompatActivity
        implements GridFragment.OnRowColDockChangedListener,
        GridFragment.OnLandscapeListener,
        GridIconFragment.OnCalculatedChangeListener,
        GridIconValuesFragment.OnValuesInteractionListener,
        GridIconPercentFragment.OnPercentInteractionListener{
    DeviceProfile mProfile;
    Context mContext;

    private ViewPager mViewPager;
    private LinearLayout mFrame;

    private GridPagerAdapter mPagerAdapter;

    private static int NUM_VIEWS = 20;
    private ActionBar mActionBar;
    private GridIconFragment mIconFrag;
    private String mChanging;
    private boolean landscapeChanged;
    private Toolbar mToolbar;
    SlidingTabLayout mTabs;
    CharSequence Titles[]={"Desktop Grid","Icon Sizes"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_editor);
        mProfile = new DeviceProfile(LauncherAppState.getInstance().getDynamicGrid().getDeviceProfile());
        mContext = this;
        mChanging = "Desktop";
        landscapeChanged = false;
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
      //  mActionBar = getSupportActionBar();
        //mActionBar.setDisplayShowHomeEnabled(false);
        //mActionBar.setDisplayShowTitleEnabled(false);
        //LayoutInflater mInflater = LayoutInflater.from(this);
        //View mCustomView = mInflater.inflate(R.layout.actionbar_set_grid, null);
        //mActionBar.setCustomView(mCustomView);
        //mActionBar.setDisplayShowCustomEnabled(true);
/*
        mActionBar.getCustomView().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LauncherAppState.getInstance().getDynamicGrid().setDeviceProfile(mProfile);
                        PreferencesProvider.Interface.General.setIconSize(mContext, mProfile.iconSize);
                        PreferencesProvider.Interface.General.setIconSizeCalc(mContext, mProfile.iconSizeCalc);
                        PreferencesProvider.Interface.General.setIconTextSize(mContext, mProfile.iconTextSize);
                        PreferencesProvider.Interface.General.setIconTextSizeCalc(mContext, mProfile.iconTextSizeCalc);
                        PreferencesProvider.Interface.General.setHotseatIcons(mContext, mProfile.numHotseatIcons);
                        PreferencesProvider.Interface.General.setWorkspaceColumns(mContext, (int) mProfile.numColumns);
                        PreferencesProvider.Interface.General.setWorkspaceRows(mContext, (int) mProfile.numRows);
                        PreferencesProvider.Interface.General.setHideHotSeat(mContext, mProfile.hideHotseat);
                        PreferencesProvider.Interface.General.setHideQSB(mContext, mProfile.hideQSB);
                        //PreferencesProvider.Interface.General.setHideLabels(mContext, mProfile.hideLabels);
                        if (landscapeChanged){
                            PreferencesProvider.Interface.General.setAllowLand(mContext, mProfile.allowLandscape);
                        }
                        LauncherAppState.getInstance().getDynamicGrid().setDeviceProfile(mProfile);
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                });
*/
        if (savedInstanceState == null) {
            if (findViewById(R.id.grid_editor) instanceof ViewPager) {
                setupPager();
            } else {
                setupFrames();
            }
        } else {
            Log.d("GridEditor", "Saved instance.....figure out why this is here someday.....");
        }

        if (!PreferencesProvider.Interface.General.getGridFirstRun()) {
            showHelp();
            PreferencesProvider.Interface.General.setGridFirstRun(this,true);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.grid_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_help) {
            showHelp();
            return true;
        }
        if (id == R.id.action_done) {
            LauncherAppState.getInstance().getDynamicGrid().setDeviceProfile(mProfile);
            PreferencesProvider.Interface.General.setIconSize(mContext, mProfile.iconSize);
            PreferencesProvider.Interface.General.setIconSizeCalc(mContext, mProfile.iconSizeCalc);
            PreferencesProvider.Interface.General.setIconTextSize(mContext, mProfile.iconTextSize);
            PreferencesProvider.Interface.General.setIconTextSizeCalc(mContext, mProfile.iconTextSizeCalc);
            PreferencesProvider.Interface.General.setHotseatIcons(mContext, mProfile.numHotseatIcons);
            PreferencesProvider.Interface.General.setWorkspaceColumns(mContext, (int) mProfile.numColumns);
            PreferencesProvider.Interface.General.setWorkspaceRows(mContext, (int) mProfile.numRows);
            PreferencesProvider.Interface.General.setHideHotSeat(mContext, mProfile.hideHotseat);
            PreferencesProvider.Interface.General.setHideQSB(mContext, mProfile.hideQSB);
            //PreferencesProvider.Interface.General.setHideLabels(mContext, mProfile.hideLabels);
            if (landscapeChanged){
                PreferencesProvider.Interface.General.setAllowLand(mContext, mProfile.allowLandscape);
            }
            LauncherAppState.getInstance().getDynamicGrid().setDeviceProfile(mProfile);
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (LauncherAppState.getInstance().getProVersion()) {
            builder.setMessage(R.string.grid_help_pro);
        } else {
            builder.setMessage(R.string.grid_help_pro);
        }


        builder.setTitle(R.string.grid_about_dialog_title);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupFrames(){
        Fragment gridFragment = GridFragment.newInstance("grid_frgment");
        Fragment iconFragment = GridIconFragment.newInstance("icon_fragment", "");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.grid_grid_frame, gridFragment,"grid_fragment")
                .add(R.id.grid_icon_frame, iconFragment, "icon_fragment").commit();
        // .add(R.id.grid_icon_frame, mIconFrag, "icon_fragment")
        // .add(R.id.grid_icon_percent_frame,percentFragment,"percent_fragment").commit();
        //  mIconPager = (ViewPager) findViewById(R.id.grid_icon_frame);
        //   mIconPagerAdapter = new IconPagerAdapter(getSupportFragmentManager());
        //   mIconPager.setAdapter(mIconPagerAdapter);
    }

    private void setupPager() {
        mViewPager = (ViewPager) findViewById(R.id.grid_editor);
        mPagerAdapter = new GridPagerAdapter(getSupportFragmentManager(),Titles,2);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getSupportActionBar().setSelectedNavigationItem(position);
                    }
                });
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the mTabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
      /*  mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
*/
        // Setting the ViewPager For the SlidingTabsLayout
        mTabs.setViewPager(mViewPager);
        //mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
      /*  mToolbar.TabListener tabListener = new ActionBar.TabListener() {

            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
                mViewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        mActionBar.addTab(mActionBar.newTab()
                .setText("Grid")
                .setTabListener(tabListener));
        mActionBar.addTab(mActionBar.newTab()
                .setText("Icons Values")
                .setTabListener(tabListener));

*/
    }

    public void onLandscapeChanged() {
        landscapeChanged = true;
        if (mProfile.allowLandscape == true) {
            //mProfile.allowLandscape = false;
            Log.d("nbehary121","False!!!");
        } else {
            //mProfile.allowLandscape = true;
            Log.d("nbehary121","True!!!");
        }
        LauncherAppState.getInstance().getDynamicGrid().setDeviceProfile(mProfile);
    }

    public void onRowColDockChanged(DeviceProfile profile) {
        GridIconValuesFragment iconFrag;
        GridIconPercentFragment percentFragment;
        GridIconFragment iconContainer;
        if (mViewPager == null) {
            iconFrag = (GridIconValuesFragment) getSupportFragmentManager().findFragmentByTag("values_fragment");
            percentFragment = (GridIconPercentFragment)getSupportFragmentManager().findFragmentByTag("percent_fragment");
            iconContainer = (GridIconFragment) getSupportFragmentManager().findFragmentByTag("icon_fragment");
        }else {
            //iconFrag = (GridIconValuesFragment) mPagerAdapter.getFragment("values_fragment");
            //percentFragment = (GridIconPercentFragment) mPagerAdapter.getFragment("percent_fragment");
            iconFrag = (GridIconValuesFragment) getSupportFragmentManager().findFragmentByTag("values_fragment");
            percentFragment = (GridIconPercentFragment)getSupportFragmentManager().findFragmentByTag("percent_fragment");
            iconContainer = (GridIconFragment) getSupportFragmentManager().findFragmentByTag("icon_fragment");
        }

        if (iconFrag != null) {
            iconFrag.updateViews(profile);

        }

        if (iconContainer != null) {
            iconContainer.updateCalculated();
        }

        if (percentFragment != null) {
            percentFragment.updateViews(profile);
        }

        LauncherAppState.getInstance().getDynamicGrid().setDeviceProfile(profile);

    }

    public void onCalculatedChangedInteraction(DeviceProfile profile) {
        onRowColDockChanged(profile);
    }

    public void onValuesInteraction() {
        GridIconFragment iconFrag;
        if (mViewPager == null) {
            iconFrag = (GridIconFragment) getSupportFragmentManager().findFragmentByTag("icon_fragment");
        }else {
            iconFrag = (GridIconFragment) mPagerAdapter.getFragment("icon_fragment");
            //percentFragment = (GridIconPercentFragment) mPagerAdapter.getFragment("percent_fragment");
            //iconFrag = (GridIconFragment) getSupportFragmentManager().findFragmentByTag("icon_fragment");
        }

        if (iconFrag != null) {
            iconFrag.updateCalculated();
        }
    }

    public void onPercentInteraction() {
        onValuesInteraction();
    }



    public DeviceProfile getmProfile() {return mProfile;}

    public String getmChanging() {return mChanging;}

    public void setmChanging(String changing) {mChanging = changing;}

    private class IconPagerAdapter extends FragmentPagerAdapter {
        private Map<String, WeakReference<Fragment>> mPageReferenceMap = new HashMap<String, WeakReference<Fragment>>();

        public IconPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int pos) {
            Fragment fragment;
            switch(pos) {

                case 0:
                    fragment = GridIconFragment.newInstance("icon_fragment","");
                    mPageReferenceMap.put("icon_fragment", new WeakReference<Fragment>(fragment));
                    return fragment;
                case 1:
                    fragment = GridIconPercentFragment.newInstance("percent_fragment","");
                    mPageReferenceMap.put("percent_fragment", new WeakReference<Fragment>(fragment));
                    return fragment;
                default:
                    fragment = GridIconFragment.newInstance("icon_fragment","");
                    mPageReferenceMap.put("icon_fragment", new WeakReference<Fragment>(fragment));
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        public Fragment getFragment(String key) {

            WeakReference<Fragment> weakReference = mPageReferenceMap.get(key);

            if(null != weakReference) {

                return weakReference.get();
            }
            else {

                return null;
            }
        }
    }
    public class GridPagerAdapterNo extends FragmentPagerAdapter {

        CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
        int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


        // Build a Constructor and assign the passed Values to appropriate values in the class
        public GridPagerAdapterNo(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
            super(fm);

            this.Titles = mTitles;
            this.NumbOfTabs = mNumbOfTabsumb;

        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {
            Fragment fragment;

            if(position == 0) // if the position is 0 we are returning the First tab
            {
                fragment = GridFragment.newInstance("grid_fragment");
                return fragment;
            }
            else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
            {
                fragment = GridFragment.newInstance("icon_fragment");
                return fragment;
            }


        }

        // This method return the titles for the Tabs in the Tab Strip

        @Override
        public CharSequence getPageTitle(int position) {
            return Titles[position];
        }

        // This method return the Number of tabs for the tabs Strip

        @Override
        public int getCount() {
            return NumbOfTabs;
        }
    }

    private class GridPagerAdapter extends FragmentPagerAdapter {
        CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
        int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


        private Map<String, WeakReference<Fragment>> mPageReferenceMap = new HashMap<String, WeakReference<Fragment>>();

        public GridPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabs) {
            super(fm);

            this.Titles = mTitles;
            this.NumbOfTabs = mNumbOfTabs;
        }


        @Override
        public Fragment getItem(int pos) {
            Fragment fragment;
            switch(pos) {

                case 0:
                    fragment = GridFragment.newInstance("grid_fragment");
                    mPageReferenceMap.put("grid_fragment", new WeakReference<Fragment>(fragment));
                    return fragment;
                case 1:
                    fragment = GridIconFragment.newInstance("icon_fragment","");
                    mPageReferenceMap.put("icon_fragment", new WeakReference<Fragment>(fragment));
                    return fragment;

                default:
                    fragment = GridFragment.newInstance("grid_fragment");
                    mPageReferenceMap.put("grid_fragment", new WeakReference<Fragment>(fragment));
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        public Fragment getFragment(String key) {

            WeakReference<Fragment> weakReference = mPageReferenceMap.get(key);

            if(null != weakReference) {

                return weakReference.get();
            }
            else {

                return null;
            }
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return Titles[position];
        }

        // This method return the Number of tabs for the tabs Strip



    }




    /**
     * A placeholder fragment containing a simple view.
     */


}