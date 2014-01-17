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

    private static GridFragment instance;

    public interface OnRowColDockChangedListener {
        public void onRowColDockChanged(DeviceProfile profile);
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
        df= new DecimalFormat("0.##");
        mTempProfile = ((GridEditor) getActivity()).getmProfile();
        mChanging = "Desktop";
        mDisplayMetrics = getActivity().getResources().getDisplayMetrics();
        View rootView = inflater.inflate(R.layout.fragment_grid_editor, container, false);
        LinearLayout ui = (LinearLayout) rootView.findViewById(R.id.grid_editor_ui);
        //ui.setBackgroundColor(Color.argb(177, 0, 0, 0));

        mColsPicker = (NumberPicker) rootView.findViewById(R.id.grid_cols_picker);
        mColsPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mColsPicker.setMaxValue(12);
        mColsPicker.setMinValue(2);
        mColsPicker.setValue((int)mTempProfile.numColumns);
        mColsPicker.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mTempProfile.numColumns = picker.getValue();
                mTempProfile.adjustSizesAuto(getResources());
                //mFontSize.setText(df.format(mTempProfile.iconTextSize));
                //mIconSize.setText(df.format(mTempProfile.iconSize));
                //mChanging = "Desktop";
               // mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,true));
                mCallback.onRowColDockChanged(mTempProfile);
            }
        });

        mRowsPicker = (NumberPicker) rootView.findViewById(R.id.grid_rows_picker);
        mRowsPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        mRowsPicker.setMaxValue(12);
        mRowsPicker.setMinValue(2);
        mRowsPicker.setValue((int)mTempProfile.numRows);
        mRowsPicker.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mTempProfile.numRows = picker.getValue();
                mTempProfile.adjustSizesAuto(getResources());
                //mFontSize.setText(df.format(mTempProfile.iconTextSize));
                //mIconSize.setText(df.format(mTempProfile.iconSize));
               // mChanging = "Desktop";
               // mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,true));
                mCallback.onRowColDockChanged(mTempProfile);
            }
        });

        mDockPicker = (NumberPicker) rootView.findViewById(R.id.grid_dock_picker);
        mDockPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        String[] stringArray = new String[5];
        int n=1;
        for(int i=0; i<5; i++){
            stringArray[i] = Integer.toString(n);
            n+=2;
        }
        mDockPicker.setMaxValue(stringArray.length-1);
        mDockPicker.setMinValue(1);
        mDockPicker.setValue((int) mTempProfile.numHotseatIcons/2);
        mDockPicker.setDisplayedValues(stringArray);
        mDockPicker.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mTempProfile.numHotseatIcons = (picker.getValue()*2)+1;
                Log.d("nbehary10x", String.format("num: %d,%d", (picker.getValue() * 2) + 1, newVal));
            }
        });

        Button defaultButton = (Button) rootView.findViewById(R.id.grid_reset_button);
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("nbehary10x","Reset!");
                mTempProfile = new DeviceProfile(LauncherAppState.getInstance().getDynamicGrid().getCalculatedProfile());
                //mProfile = mTempProfile;
               // mFontSize.setText(df.format(mTempProfile.iconTextSize));
              //  mIconSize.setText(df.format(mTempProfile.iconSize));
              //  mChanging = "Desktop";
              //  mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,true));
                mCallback.onRowColDockChanged(mTempProfile);
                mDockPicker.setValue((int) mTempProfile.numHotseatIcons/2);
                mRowsPicker.setValue((int)mTempProfile.numRows);
                mColsPicker.setValue((int)mTempProfile.numColumns);
                Log.d("nbehary10x",String.format("R:%d,C:%d,D:%d",mRowsPicker.getValue(),mColsPicker.getValue(), mDockPicker.getValue()));

            }
        });




        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnRowColDockChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    private static Bitmap generateIconPreview(Resources res, float size, DeviceProfile grid, boolean showText) {

        Drawable previewDefaultBG;

        LauncherAppState app = LauncherAppState.getInstance();

        DisplayMetrics dm = res.getDisplayMetrics();

        Paint textPaint = new Paint();
        textPaint.setTextSize(grid.iconTextSizePx);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        int iconSizePx;
        if (showText){
            iconSizePx = grid.iconSizePx;
        } else{
            iconSizePx = grid.hotseatIconSizePx;
        }


        // Drawable icon =  app.getIconCache().getFullResDefaultActivityIcon();
        Drawable icon = res.getDrawable(R.drawable.ic_launcher);

        int cellWidthPx = iconSizePx;
        int cellHeightPx = iconSizePx + (int)(  Math.ceil(fm.bottom - fm.top));

        Bitmap previewBitmap = Bitmap.createBitmap(cellWidthPx,cellHeightPx,
                Bitmap.Config.ARGB_8888);
        renderDrawableToBitmap(icon,previewBitmap,0,0,iconSizePx,iconSizePx);
        if (showText) {
            Canvas canvas = new Canvas(previewBitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    grid.iconTextSize, res.getDisplayMetrics()));
            paint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
            Rect bounds = new Rect();
            paint.setColor(Color.WHITE);


            paint.getTextBounds("Icon Text",0,9,bounds);
            int width = bounds.width();
            int x = ((cellWidthPx-width)/2 );
            canvas.drawText("Icon Text", x, cellHeightPx, paint);
        }
        return previewBitmap;
    }

    public static void renderDrawableToBitmap(
            Drawable d, Bitmap bitmap, int x, int y, int w, int h) {
        renderDrawableToBitmap(d, bitmap, x, y, w, h, 1f);
    }

    private static void renderDrawableToBitmap(
            Drawable d, Bitmap bitmap, int x, int y, int w, int h,
            float scale) {
        if (bitmap != null) {
            Canvas c = new Canvas(bitmap);
            c.scale(scale, scale);
            Rect oldBounds = d.copyBounds();
            d.setBounds(x, y, x + w, y + h);
            d.draw(c);
            d.setBounds(oldBounds); // Restore the bounds
            c.setBitmap(null);
        }
    }
}
