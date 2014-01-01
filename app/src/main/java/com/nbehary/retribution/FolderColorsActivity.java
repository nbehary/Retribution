package com.nbehary.retribution;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.nbehary.retribution.preference.PreferencesProvider;

import java.util.zip.Inflater;

public class FolderColorsActivity extends Activity {

    int mBgColor;
    int mIconColor;
    int mNameColor;
    boolean mDefaultBG;
    Context mContext;
    String mFreeColor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        mBgColor = PreferencesProvider.Interface.General.getFolderBackColor();
        mIconColor = PreferencesProvider.Interface.General.getFolderIconColor();
        mNameColor = PreferencesProvider.Interface.General.getFolderNameColor();
        mDefaultBG = PreferencesProvider.Interface.General.getDefaultFolderBG();
        mFreeColor = PreferencesProvider.Interface.General.getFolderColor();
        mContext = this;
        //No colors set.  Default to black text on white.  (sort of, but not the default)
        if (mBgColor==0 && mIconColor==0 && mNameColor==0) {
            mBgColor = -109145601;
            mIconColor = -4038656;
            mNameColor = -847872;
            PreferencesProvider.Interface.General.setFolderBackColor(mContext, mBgColor);
            PreferencesProvider.Interface.General.setFolderNameColor(mContext, mNameColor);
            PreferencesProvider.Interface.General.setFolderIconColor(mContext, mIconColor);
            PreferencesProvider.Interface.General.setDefaultFolderBG(mContext, true);
        }
        setContentView(R.layout.activity_folder_colors);

        final ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.actionbar_set_folder_colors);
        actionBar.getCustomView().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PreferencesProvider.Interface.General.setFolderBackColor(mContext, mBgColor);
                        PreferencesProvider.Interface.General.setFolderNameColor(mContext, mNameColor);
                        PreferencesProvider.Interface.General.setFolderIconColor(mContext, mIconColor);
                        PreferencesProvider.Interface.General.setDefaultFolderBG(mContext, mDefaultBG);
                        PreferencesProvider.Interface.General.setFolderColor(mContext, mFreeColor);
                        LauncherAppState.getInstance().getModel().startLoader(true, -1);
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

    public void setmDefaultBG(boolean mDefaultBG) {
        this.mDefaultBG = mDefaultBG;
    }

    public void setmNameColor(int mNameColor) {
        this.mNameColor = mNameColor;
    }

    public void setmIconColor(int mIconColor) {
        this.mIconColor = mIconColor;
    }

    public void setmBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
    }

    public void setmFreeColor(String mFreeColor) {
        this.mFreeColor = mFreeColor;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        //intent.putExtra("resetWorkspace", true);
        setResult(RESULT_OK, intent);

        super.finish();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        String mChanging;
        ImageView mPreviewImage;
        Context mContext;
        ColorPicker mPicker;

        int mBgColor;
        int mIconColor;
        int mNameColor;
        String mFreeColor;
        boolean mDefaultBG;


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

 /*           int orientation = getActivity().getRequestedOrientation();
            int rotation = ((WindowManager) getActivity().getSystemService(
                    Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
            }
            getActivity().setRequestedOrientation(orientation);
*/
            mContext = getActivity();
            mBgColor = PreferencesProvider.Interface.General.getFolderBackColor();
            mIconColor = PreferencesProvider.Interface.General.getFolderIconColor();
            mNameColor = PreferencesProvider.Interface.General.getFolderNameColor();
            mDefaultBG = PreferencesProvider.Interface.General.getDefaultFolderBG();

            mChanging = "Background";
            //No colors set.  Default to black text on white.  (sort of, but not the default)
            if (mBgColor==0 && mIconColor==0 && mNameColor==0) {
                mBgColor = -109145601;
                mIconColor = -4038656;
                mNameColor = -847872;
            }

            View rootView;
            if (LauncherAppState.getInstance().getProVersion()) {
                rootView = inflater.inflate(R.layout.fragment_folder_colors, container, false);
                FrameLayout ui_pane = (FrameLayout) rootView.findViewById(R.id.folder_colors_ui);
                ui_pane.setBackgroundColor(Color.argb(128, 0, 0, 0));
                mPreviewImage = (ImageView) rootView.findViewById(R.id.folder_color_preview_image);

                Spinner spinner = (Spinner) rootView.findViewById(R.id.folder_colors_item_spinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                        R.array.folder_colors_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mChanging = (String) parent.getItemAtPosition(position);
                        if (mChanging.equals("Background")) {
                            mPicker.setColor(mBgColor);
                        } else if (mChanging.equals("Folder Name")) {
                            if (mNameColor != 0) {
                                mPicker.setColor(mNameColor);
                            }
                        } else {
                            if (mIconColor != 0) {
                                mPicker.setColor(mIconColor);
                            }

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                Button reset = (Button) rootView.findViewById(R.id.folder_color_reset);
                reset.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        mDefaultBG = true;
                        ((FolderColorsActivity)getActivity()).setmDefaultBG(true);
                        mPreviewImage.setImageBitmap(generateFolderPreview(getResources(), mBgColor, mIconColor, mNameColor, mDefaultBG));
                    }
                });


                FrameLayout preview_pane = (FrameLayout) rootView.findViewById(R.id.folder_color_preview);
                preview_pane.setBackgroundColor(Color.argb(0,0, 0, 0));

                mPreviewImage.setImageBitmap(generateFolderPreview(getResources(),mBgColor,mIconColor,mNameColor,mDefaultBG));

                mPicker = (ColorPicker) rootView.findViewById(R.id.picker);
                SVBar svBar = (SVBar) rootView.findViewById(R.id.svbar);
                OpacityBar opacityBar = (OpacityBar) rootView.findViewById(R.id.opacitybar);


                mPicker.addSVBar(svBar);
                mPicker.addOpacityBar(opacityBar);

                mPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int i) {
                        FolderColorsActivity parent = (FolderColorsActivity) getActivity();
                        if (mChanging.equals("Background")) {
                            mBgColor = i;
                            parent.setmBgColor(i);
                        } else if (mChanging.equals("Folder Name")) {
                            mNameColor = i;
                            parent.setmNameColor(i);
                        } else {
                            mIconColor = i;
                            parent.setmIconColor(i);
                        }
                        if (mDefaultBG) {
                            mDefaultBG = false;
                            parent.setmDefaultBG(false);
                        }
                        mPreviewImage.setImageBitmap(generateFolderPreview(getResources(),mBgColor,mIconColor,mNameColor,mDefaultBG));

                    }
                });
                mPicker.setColor(mBgColor);
            } else {
                rootView = inflater.inflate(R.layout.fragment_folder_colors_free, container, false);
                FrameLayout ui_pane = (FrameLayout) rootView.findViewById(R.id.folder_colors_ui_free);
                ui_pane.setBackgroundColor(Color.argb(128, 0, 0, 0));
                mPreviewImage = (ImageView) rootView.findViewById(R.id.folder_color_preview_image_free);
                RadioGroup rGroup = (RadioGroup)rootView.findViewById(R.id.radioGroup1);

                int color = Integer.parseInt(PreferencesProvider.Interface.General.getFolderColor());
                switch (color) {
                    case 1:  //White(Default)
                        mPreviewImage.setImageBitmap(generateFolderPreview(getResources(),
                                Color.argb(255,84,84,84),
                                Color.rgb(255,255,255),
                                Color.rgb(255,255,255),
                                true));
                        rGroup.check(R.id.radioWhite);
                        break;
                    case 2:  //Grey (Transparent)
                        //folder.setBackgroundColor(Color.argb(128,84,84,84));
                        mPreviewImage.setImageBitmap(generateFolderPreview(getResources(),
                                Color.argb(128, 84, 84, 84),
                                Color.rgb(255, 255, 255),
                                Color.rgb(255, 255, 255),
                                false));
                        rGroup.check(R.id.radioGreyT);
                        break;
                    case 3:  //Black (Transparent)
                        //folder.setBackgroundColor(Color.argb(128,0,0,0));
                        mPreviewImage.setImageBitmap(generateFolderPreview(getResources(),
                                Color.argb(128, 0, 0, 0),
                                Color.rgb(255, 255, 255),
                                Color.rgb(255, 255, 255),
                                false));
                        rGroup.check(R.id.radioBlackT);
                        break;
                    case 4:  //Grey (Opaque)
                        //folder.setBackgroundColor(Color.argb(255,84,84,84));
                        mPreviewImage.setImageBitmap(generateFolderPreview(getResources(),
                                Color.argb(255, 84, 84, 84),
                                Color.rgb(255, 255, 255),
                                Color.rgb(255, 255, 255),
                                false));
                        rGroup.check(R.id.radioGreyO);
                        break;
                    case 5:  //Black (Opaque)
                        //folder.setBackgroundColor(Color.argb(255,0,0,0));
                        mPreviewImage.setImageBitmap(generateFolderPreview(getResources(),
                                Color.argb(255, 0, 0, 0),
                                Color.rgb(255, 255, 255),
                                Color.rgb(255, 255, 255),
                                false));
                        rGroup.check(R.id.radioBlackO);
                        break;
                }
                rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    public void onCheckedChanged(RadioGroup rGroup, int checkedId)
                    {
                        FolderColorsActivity parent = (FolderColorsActivity) getActivity();
                        // This will get the radiobutton that has changed in its check state
                        RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(checkedId);
                        // This puts the value (true/false) into the variable
                        boolean isChecked = checkedRadioButton.isChecked();
                        // If the radiobutton that has changed in check state is now checked...
                        if (isChecked)
                        {mPreviewImage.setImageBitmap(generateFolderPreview(getResources(),
                                            Color.argb(128, 0, 0, 0),
                                            Color.rgb(255, 255, 255),
                                            Color.rgb(255, 255, 255),
                                            false));
                            // Changes the textview's text to "Checked: example radiobutton text"
                            switch (checkedId) {
                                case R.id.radioWhite:
                                    Log.d("nbehary999","White");
                                    parent.setmFreeColor("1");
                                    mPreviewImage.setImageBitmap(generateFolderPreview(getResources(),
                                            Color.argb(255, 84, 84, 84),
                                            Color.rgb(255, 255, 255),
                                            Color.rgb(255, 255, 255),
                                            true));
                                    break;
                                case R.id.radioGreyO:
                                    parent.setmFreeColor("4");
                                    mPreviewImage.setImageBitmap(generateFolderPreview(getResources(),
                                            Color.argb(255, 84, 84, 84),
                                            Color.rgb(255, 255, 255),
                                            Color.rgb(255, 255, 255),
                                            false));
                                    break;
                                case R.id.radioGreyT:
                                    parent.setmFreeColor("2");
                                    mPreviewImage.setImageBitmap(generateFolderPreview(getResources(),
                                            Color.argb(128, 84, 84, 84),
                                            Color.rgb(255, 255, 255),
                                            Color.rgb(255, 255, 255),
                                            false));
                                    break;
                                case R.id.radioBlackO:
                                    parent.setmFreeColor("5");
                                    mPreviewImage.setImageBitmap(generateFolderPreview(getResources(),
                                            Color.argb(255, 0, 0, 0),
                                            Color.rgb(255, 255, 255),
                                            Color.rgb(255, 255, 255),
                                            false));
                                    break;
                                case R.id.radioBlackT:
                                    Log.d("nbehary999","BlackT");
                                    parent.setmFreeColor("3");
                                    mPreviewImage.setImageBitmap(generateFolderPreview(getResources(),
                                            Color.argb(128, 0, 0, 0),
                                            Color.rgb(255, 255, 255),
                                            Color.rgb(255, 255, 255),
                                            false));
                                    break;
                            }
                        }
                    }
                });
            }

            return rootView;
        }


        private static Bitmap generateFolderPreview(Resources resources, int back, int iconText, int nameText, boolean bg) {

            Drawable previewDefaultBG = resources.getDrawable(R.drawable.portal_container_holo);

            LauncherAppState app = LauncherAppState.getInstance();
            DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();

            Drawable icon =  app.getIconCache().getFullResDefaultActivityIcon();

            int folderWidth = grid.folderCellWidthPx *2;
            int folderHeight = (int) (grid.folderCellHeightPx + (grid.folderCellHeightPx/2)*1.2);

            Bitmap previewBitmap = Bitmap.createBitmap(folderWidth,folderHeight,
                    Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(previewBitmap);
            // new antialised Paint
            if (bg){
                renderDrawableToBitmap(previewDefaultBG,previewBitmap ,0,0,folderWidth,folderHeight);
            } else {
                //canvas.drawColor(back);
                previewBitmap.eraseColor(back);
            }
            renderDrawableToBitmap(icon,previewBitmap,20,20,grid.iconSizePx,grid.iconSizePx);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            // text color - #3D3D3D


            paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    14, resources.getDisplayMetrics()));
            paint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
            Rect bounds = new Rect();
            if (!bg) {
                paint.setColor(iconText);
            } else {
                paint.setColor(Color.rgb(61, 61, 61));
            }

            paint.getTextBounds("Icon Text",0,9,bounds);
            int width = bounds.width();
            int x = ((grid.folderCellWidthPx-width)/2 );
            canvas.drawText("Icon Text", x, grid.iconSizePx + 50, paint);

            if (!bg) {
                paint.setColor(nameText);
            } else {
                paint.setColor(Color.rgb(61, 61, 61));
            }

            paint.getTextBounds("Folder Name",0,11,bounds);
            width = bounds.width();
            x = (canvas.getWidth()-width)/2;
            canvas.drawText("Folder Name", x, grid.folderCellHeightPx + 50,paint);
            previewBitmap = getRoundedCornerBitmap(previewBitmap,2);
            return previewBitmap;
        }

        public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            //final int color = 0xff424242;
            final int color = 0xff000000;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = pixels;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
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





}
