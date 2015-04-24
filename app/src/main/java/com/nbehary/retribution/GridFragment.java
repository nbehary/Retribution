package com.nbehary.retribution;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by nate on 1/15/14.
 */
public class GridFragment extends Fragment {

    DeviceProfile mProfile,mTempProfile;
    DisplayMetrics mDisplayMetrics;
    String mChanging;
    NumberPicker mColsPicker, mRowsPicker, mDockPicker;
    DecimalFormat df;
    OnRowColDockChangedListener mCallback;
    OnLandscapeListener mLandCallback;
    boolean landscapeChanged;
    CheckBox mHideHotseat, mHideQSB;
    TextView mHotseatNotice,mLandscapeNotice,mSearchNotice;
    View mRootView;

    private static GridFragment instance;

    public interface OnRowColDockChangedListener {
        public void onRowColDockChanged(DeviceProfile profile);
    }

    public interface OnLandscapeListener {
        public void onLandscapeChanged();
    }

    public GridFragment() {
        mProfile = LauncherAppState.getInstance().getDynamicGrid().getDeviceProfile();


    }

    public static GridFragment newInstance(String text){
        GridFragment f = new GridFragment();
        Bundle b = new Bundle();
        b.putString("TAG", text);

        f.setArguments(b);

        return f;

    }

    public static GridFragment getInstance()
    {
        if (instance == null)
            instance = new GridFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     //   if (mRootView != null){
    //        return mRootView;
     //   }
        df= new DecimalFormat("0.##");
        mTempProfile = ((GridEditor) getActivity()).getmProfile();
        mChanging = "Desktop";
        landscapeChanged = false;
        mDisplayMetrics = getActivity().getResources().getDisplayMetrics();
        mRootView = getActivity().getLayoutInflater(). inflate(R.layout.fragment_grid_editor, container, false);
        LinearLayout ui = (LinearLayout) mRootView.findViewById(R.id.grid_editor_ui);
        //ui.setBackgroundColor(Color.argb(177, 0, 0, 0));
        mHotseatNotice = (TextView) mRootView.findViewById(R.id.grid_auto_hotseat_notice);
        mLandscapeNotice = (TextView) mRootView.findViewById(R.id.grid_landscape_notice);
        if (!mTempProfile.isTablet() && mTempProfile.allowLandscape) {
            mLandscapeNotice.setVisibility(View.VISIBLE);
        }
        mSearchNotice = (TextView) mRootView.findViewById(R.id.grid_search_notice);

        mColsPicker = (NumberPicker) mRootView.findViewById(R.id.grid_cols_picker);
        mColsPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        if (LauncherAppState.getInstance().getProVersion()) {
            mColsPicker.setMaxValue(12);
            mColsPicker.setMinValue(2);
        } else {
            mColsPicker.setMinValue((int)mTempProfile.numColumnsDevice -1);
            mColsPicker.setMaxValue((int)mTempProfile.numColumnsDevice +1);
        }
        mColsPicker.setValue((int)mTempProfile.numColumns);
        mColsPicker.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mTempProfile.numColumns = picker.getValue();
                boolean hotseatChange = mTempProfile.adjustSizesAuto(getResources());
                if ((hotseatChange) && mTempProfile.autoHotseat){
                    //hotseat was hidden.
                    mHideHotseat.setChecked(true);
                    mHideHotseat.setVisibility(View.GONE);
                    mHotseatNotice.setVisibility(View.VISIBLE);
                } else if (hotseatChange){
                    //hotseat unhidden.
                    mHideHotseat.setVisibility(View.VISIBLE);
                    mHideHotseat.setChecked(false);
                    mHotseatNotice.setVisibility(View.GONE);
                }
                //mFontSize.setText(df.format(mTempProfile.iconTextSize));
                //mIconSize.setText(df.format(mTempProfile.iconSize));
                //mChanging = "Desktop";
               // mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,true));
                mCallback.onRowColDockChanged(mTempProfile);
            }
        });

        mRowsPicker = (NumberPicker) mRootView.findViewById(R.id.grid_rows_picker);
        mRowsPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        if (LauncherAppState.getInstance().getProVersion()) {
            mRowsPicker.setMaxValue(12);
            mRowsPicker.setMinValue(2);
        } else {
            mRowsPicker.setMinValue((int)mTempProfile.numRowsDevice -1);
            mRowsPicker.setMaxValue((int)mTempProfile.numRowsDevice +1);
        }
        mRowsPicker.setValue((int)mTempProfile.numRows);
        mRowsPicker.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mTempProfile.numRows = picker.getValue();
                boolean hotseatChange = mTempProfile.adjustSizesAuto(getResources());
                if ((hotseatChange) && mTempProfile.autoHotseat){
                    //hotseat was hidden.
                    mHideHotseat.setChecked(true);
                    mHideHotseat.setVisibility(View.GONE);
                    mHotseatNotice.setVisibility(View.VISIBLE);
                } else if (hotseatChange){
                    //hotseat unhidden.
                    mHideHotseat.setVisibility(View.VISIBLE);
                    mHideHotseat.setChecked(false);
                    mHotseatNotice.setVisibility(View.GONE);
                }


                //mFontSize.setText(df.format(mTempProfile.iconTextSize));
                //mIconSize.setText(df.format(mTempProfile.iconSize));
               // mChanging = "Desktop";
               // mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,true));
               mCallback.onRowColDockChanged(mTempProfile);
            }
        });


        Button defaultButton = (Button) mRootView.findViewById(R.id.grid_reset_button);
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mTempProfile = new DeviceProfile(LauncherAppState.getInstance().getDynamicGrid().getCalculatedProfile());
                mTempProfile.iconSize = mTempProfile.iconSizeDevice;
                mTempProfile.iconSizeCalc = mTempProfile.iconSizeDevice;
                mTempProfile.iconTextSize = mTempProfile.iconTextSizeDevice;
                mTempProfile.iconTextSizeCalc = mTempProfile.iconTextSizeDevice;
                mTempProfile.iconSizePx = mTempProfile.iconSizePxDevice;
                mTempProfile.numRows = mTempProfile.numRowsDevice;
                mTempProfile.numColumns = mTempProfile.numColumnsDevice;
                //mProfile = mTempProfile;
               // mFontSize.setText(df.format(mTempProfile.iconTextSize));
              //  mIconSize.setText(df.format(mTempProfile.iconSize));
              //  mChanging = "Desktop";
              //  mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,true));
                mCallback.onRowColDockChanged(mTempProfile);
                //mDockPicker.setValue((int) mTempProfile.numHotseatIcons/2);
                mRowsPicker.setValue((int)mTempProfile.numRows);
                mColsPicker.setValue((int)mTempProfile.numColumns);
                //The hotseat should never be invalid on default.
                if (mHideHotseat.getVisibility() == View.GONE) {
                    mHideHotseat.setChecked(false);
                    mHideHotseat.setVisibility(View.VISIBLE);
                    mHotseatNotice.setVisibility(View.GONE);
                }

            }
        });

        mHideHotseat = (CheckBox) mRootView.findViewById(R.id.grid_hide_hotseat);
        mHideHotseat.setChecked(mTempProfile.hideHotseat);
        mHideHotseat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTempProfile.hideHotseat = true;

                } else {
                    mTempProfile.hideHotseat = false;
                }
                mTempProfile.adjustSizesAuto(getResources());
            }
        });

        if (mTempProfile.autoHotseat){
            mHotseatNotice.setVisibility(View.VISIBLE);
            mHideHotseat.setVisibility(View.GONE);
        }

        mHideQSB = (CheckBox) mRootView.findViewById(R.id.grid_hide_qsb);
        mHideQSB.setChecked(mTempProfile.hideQSB);
        mHideQSB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTempProfile.hideQSB = true;

                } else {
                    mTempProfile.hideQSB = false;
                }
                mTempProfile.adjustSizesAuto(getResources());
            }
        });
/*        if (!mTempProfile.allowLandscape){
            mHideQSB.setVisibility(View.VISIBLE);
            mSearchNotice.setVisibility(View.GONE);
        } else {
            mHideQSB.setVisibility(View.GONE);
            mSearchNotice.setVisibility(View.VISIBLE);
        }
*/


        CheckBox allowLand = (CheckBox) mRootView.findViewById(R.id.grid_allow_land);
        allowLand.setChecked(mTempProfile.allowLandscape);
        allowLand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    mTempProfile.allowLandscape = true;
                    if (!mTempProfile.isTablet()) {
                        mLandscapeNotice.setVisibility(View.VISIBLE);
                    }
                  //  mHideQSB.setVisibility(View.GONE);
                  //  mSearchNotice.setVisibility(View.VISIBLE);
                  //  mHideQSB.setChecked(false);
                  //  mTempProfile.hideQSB = false;
                  //  mTempProfile.adjustSizesAuto(getResources());


                } else {
                    mTempProfile.allowLandscape = false;
                    if (!mTempProfile.isTablet()) {
                        mLandscapeNotice.setVisibility(View.GONE);
                    }
                  //  mHideQSB.setVisibility(View.VISIBLE);
                  //  mSearchNotice.setVisibility(View.GONE);


                }
                mLandCallback.onLandscapeChanged();
                mTempProfile.adjustSizesAuto(getResources());
                mCallback.onRowColDockChanged(mTempProfile);

                //mTempProfile.setCellHotSeatAndFolders();
            }
        });




        return mRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnRowColDockChangedListener) activity;
            mLandCallback = (OnLandscapeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


}
