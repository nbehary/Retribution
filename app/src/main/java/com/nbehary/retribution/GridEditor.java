package com.nbehary.retribution;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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


public class GridEditor extends ActionBarActivity
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_editor);
        mProfile = new DeviceProfile(LauncherAppState.getInstance().getDynamicGrid().getDeviceProfile());
        mContext = this;
        mChanging = "Desktop";
        landscapeChanged = false;
        mActionBar = getSupportActionBar();
        mActionBar.setCustomView(R.layout.actionbar_set_grid);
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
                        Intent resultIntent = new Intent();
// TODO Add extras or a data URI to this intent as appropriate.
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                });
        if (savedInstanceState == null) {
            Log.d("nbehary110", "No Saved instance");
            if (findViewById(R.id.grid_editor) instanceof ViewPager) {
                setupPager();
            } else {
                setupFrames();
            }
        } else {
            Log.d("nbehary110", "Saved instance");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.grid_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        mPagerAdapter = new GridPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });

        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

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


    }

    public void onLandscapeChanged() {
        landscapeChanged = true;
    }

    public void onRowColDockChanged(DeviceProfile profile) {
        Log.d("nbehary10x", "Something changed.");
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
            iconContainer.updateCalculated();
        }

        if (percentFragment != null) {
            percentFragment.updateViews(profile);
        }

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
                    Log.d("nbehary110","test");
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

    private class GridPagerAdapter extends FragmentPagerAdapter {

        private Map<String, WeakReference<Fragment>> mPageReferenceMap = new HashMap<String, WeakReference<Fragment>>();

        public GridPagerAdapter(FragmentManager fm) {
            super(fm);
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


    }




    /**
     * A placeholder fragment containing a simple view.
     */


}
