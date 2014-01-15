package com.nbehary.retribution;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.effect.EffectUpdateListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;


import com.nbehary.retribution.preference.PreferencesProvider;

import java.text.DecimalFormat;

public class GridEditor extends Activity {
    DeviceProfile mProfile;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_editor);
        mProfile = new DeviceProfile(LauncherAppState.getInstance().getDynamicGrid().getDeviceProfile());
        mContext = this;
        final ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.actionbar_set_grid);
        actionBar.getCustomView().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LauncherAppState.getInstance().getDynamicGrid().setDeviceProfile(mProfile);
                        PreferencesProvider.Interface.General.setIconSize(mContext, mProfile.iconSize);
                        PreferencesProvider.Interface.General.setIconTextSize(mContext, mProfile.iconTextSize);
                        PreferencesProvider.Interface.General.setHotseatIcons(mContext, mProfile.numHotseatIcons);
                        PreferencesProvider.Interface.General.setWorkspaceColumns(mContext, (int) mProfile.numColumns);
                        PreferencesProvider.Interface.General.setWorkspaceRows(mContext, (int) mProfile.numRows);


                        finish();
                        //do stuff
                    }

                });

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setmProfile(DeviceProfile profile) {mProfile = profile;}

    public DeviceProfile getmProfile() {return mProfile;}

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        DeviceProfile mProfile,mTempProfile;
        ImageView mPreviewImage;
        TextView mIconSize, mFontSize, mFontLabel;
        ImageButton mUpFontButton,mDownFontButton;
        DisplayMetrics mDisplayMetrics;
        String mChanging;
        NumberPicker mColsPicker, mRowsPicker, mDockPicker;
        Switch mUseCalculated;
        DecimalFormat df;


        public PlaceholderFragment() {
            mProfile = LauncherAppState.getInstance().getDynamicGrid().getDeviceProfile();


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
            ui.setBackgroundColor(Color.argb(177, 0, 0, 0));

            mColsPicker = (NumberPicker) rootView.findViewById(R.id.grid_cols_picker);
            mColsPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            mColsPicker.setMaxValue(12);
            mColsPicker.setMinValue(2);
            mColsPicker.setValue((int)mTempProfile.numColumns);
            mColsPicker.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    mTempProfile.numColumns = picker.getValue();
               //     if (oldVal < newVal) {
                 //       mTempProfile.adjustWidthToFitGridDown(getResources());
                 //   } else{
                 //       mTempProfile.adjustWidthToFitGridUp(getResources());
                 //   }
                    mTempProfile.adjustSizesAuto(getResources());
                    mFontSize.setText(df.format(mTempProfile.iconTextSize));
                    mIconSize.setText(df.format(mTempProfile.iconSize));
                    mChanging = "Desktop";
                    mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,true));

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
                //    if (oldVal < newVal) {
                //        mTempProfile.adjustHeightToFitGridDown(getResources());
                //    } else{
                //        mTempProfile.adjustHeightToFitGridUp(getResources());
                //    }
                    mTempProfile.adjustSizesAuto(getResources());
                    mFontSize.setText(df.format(mTempProfile.iconTextSize));
                    mIconSize.setText(df.format(mTempProfile.iconSize));
                    mChanging = "Desktop";
                    mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,true));

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
                    Log.d("nbehary10x",String.format("num: %d,%d",(picker.getValue()*2)+1,newVal));
                }
            });

            mPreviewImage = (ImageView) rootView.findViewById(R.id.grid_icon_preview);
            mPreviewImage.setImageBitmap(generateIconPreview(getResources(),mTempProfile.iconSize,mTempProfile,true));

            mIconSize = (TextView) rootView.findViewById(R.id.grid_icon_size);
            mIconSize.setText(df.format(mTempProfile.iconSize));

            mFontSize = (TextView) rootView.findViewById(R.id.grid_font_size);
            mFontSize.setText(df.format(mTempProfile.iconTextSize));

            mFontLabel = (TextView) rootView.findViewById(R.id.grid_font_label);

            ImageButton downSizeButton = (ImageButton) rootView.findViewById(R.id.grid_icon_down);
            downSizeButton.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean showText = true;
                    if (mChanging.equals("Desktop")) {
                        mTempProfile.iconSize -= 0.5f;
                        mTempProfile.iconSizePx = DynamicGrid.pxFromDp(mTempProfile.iconSize, mDisplayMetrics);
                        mIconSize.setText(df.format(mTempProfile.iconSize));
                        showText = true;
                    } else if (mChanging.equals("Dock")) {
                        mTempProfile.hotseatIconSize -=0.5f;
                        mTempProfile.hotseatIconSizePx = DynamicGrid.pxFromDp(mTempProfile.hotseatIconSize, mDisplayMetrics);
                        mIconSize.setText(df.format(mTempProfile.hotseatIconSize));
                        showText =false;
                    } else {

                    }

                    mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,showText));

                    mTempProfile.setCellHotSeatAndFolders();
                    if (mUseCalculated.isChecked()) {
                        mUseCalculated.toggle();
                    }
                }
            }));

            ImageButton upSizeButton = (ImageButton) rootView.findViewById(R.id.grid_icon_up);
            upSizeButton.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean showText = true;
                    if (mChanging.equals("Desktop")) {
                        mTempProfile.iconSize += 0.5f;
                        mTempProfile.iconSizePx = DynamicGrid.pxFromDp(mTempProfile.iconSize, mDisplayMetrics);
                        mIconSize.setText(df.format(mTempProfile.iconSize));
                        showText = true;
                    } else if (mChanging.equals("Dock")) {
                        mTempProfile.hotseatIconSize +=0.5f;
                        mTempProfile.hotseatIconSizePx = DynamicGrid.pxFromDp(mTempProfile.hotseatIconSize, mDisplayMetrics);
                        mIconSize.setText(df.format(mTempProfile.hotseatIconSize));
                        showText =false;
                    }
                    mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,showText));

                    mTempProfile.setCellHotSeatAndFolders();
                    if (mUseCalculated.isChecked()) {
                        mUseCalculated.toggle();
                    }
                }
            }));



            mDownFontButton = (ImageButton) rootView.findViewById(R.id.grid_font_down);
            mDownFontButton.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTempProfile.iconTextSize -= 0.1f;
                    mTempProfile.iconTextSizePx = DynamicGrid.pxFromSp(mTempProfile.iconTextSize, mDisplayMetrics);
                    mPreviewImage.setImageBitmap(generateIconPreview(getResources(),mTempProfile.iconSize,mTempProfile,true));
                    mFontSize.setText(df.format(mTempProfile.iconTextSize));
                    mTempProfile.setCellHotSeatAndFolders();
                    if (mUseCalculated.isChecked()) {
                        mUseCalculated.toggle();
                    }
                }
            }));

            mUpFontButton = (ImageButton) rootView.findViewById(R.id.grid_font_up);
            mUpFontButton.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTempProfile.iconTextSize += 0.1f;
                    mTempProfile.iconTextSizePx = DynamicGrid.pxFromSp(mTempProfile.iconTextSize, mDisplayMetrics);
                    mPreviewImage.setImageBitmap(generateIconPreview(getResources(),mTempProfile.iconSize,mTempProfile,true));
                    mFontSize.setText(df.format(mTempProfile.iconTextSize));
                    mTempProfile.setCellHotSeatAndFolders();
                    if (mUseCalculated.isChecked()) {
                        mUseCalculated.toggle();
                    }

                }
            }));



            Spinner spinner = (Spinner) rootView.findViewById(R.id.grid_change_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.grid_choices_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mChanging = (String) parent.getItemAtPosition(position);
                    if (mChanging.equals("Desktop")) {
                        mFontLabel.setVisibility(View.VISIBLE);
                        mUpFontButton.setVisibility(View.VISIBLE);
                        mDownFontButton.setVisibility(View.VISIBLE);
                        mFontSize.setVisibility(View.VISIBLE);
                        mIconSize.setText(df.format(mTempProfile.iconSize));
                        mPreviewImage.setImageBitmap(generateIconPreview(getResources(),mTempProfile.iconSize,mTempProfile,false));

                    } else if (mChanging.equals("Dock")) {
                        mFontLabel.setVisibility(View.GONE);
                        mUpFontButton.setVisibility(View.GONE);
                        mDownFontButton.setVisibility(View.GONE);
                        mFontSize.setVisibility(View.GONE);
                        mIconSize.setText(df.format(mTempProfile.hotseatIconSize));
                        mPreviewImage.setImageBitmap(generateIconPreview(getResources(),mTempProfile.iconSize,mTempProfile,false));

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {mFontSize.setText(df.format(mTempProfile.iconTextSize));

                }
            });

            Button defaultButton = (Button) rootView.findViewById(R.id.grid_reset_button);
            defaultButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("nbehary10x","Reset!");
                    mTempProfile = new DeviceProfile(LauncherAppState.getInstance().getDynamicGrid().getCalculatedProfile());
                    //mProfile = mTempProfile;
                    mFontSize.setText(df.format(mTempProfile.iconTextSize));
                    mIconSize.setText(df.format(mTempProfile.iconSize));
                    mChanging = "Desktop";
                    mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,true));
                    mDockPicker.setValue((int) mTempProfile.numHotseatIcons/2);
                    mRowsPicker.setValue((int)mTempProfile.numRows);
                    mColsPicker.setValue((int)mTempProfile.numColumns);
                    Log.d("nbehary10x",String.format("R:%d,C:%d,D:%d",mRowsPicker.getValue(),mColsPicker.getValue(), mDockPicker.getValue()));

                }
            });

            mUseCalculated = (Switch) rootView.findViewById(R.id.grid_use_calc);
            mUseCalculated.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){

                            //mUseCalculated.toggle();
                        //DeviceProfile calculated = LauncherAppState.getInstance().getDynamicGrid().getCalculatedProfile();
                        mTempProfile.iconSize =  mTempProfile.iconSizeCalc;
                        mTempProfile.hotseatIconSize = mTempProfile.hotseatIconSizeCalc;
                        mTempProfile.iconTextSize = mTempProfile.iconTextSizeCalc;
                        mTempProfile.setCellHotSeatAndFolders();
                        mFontSize.setText(df.format(mTempProfile.iconTextSize));
                        mIconSize.setText(df.format(mTempProfile.iconSize));
                        mChanging = "Desktop";
                        //TODO:Using calculated below shouldn't be necessary......
                        mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,true));
                    }
                }
            });


            return rootView;
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
