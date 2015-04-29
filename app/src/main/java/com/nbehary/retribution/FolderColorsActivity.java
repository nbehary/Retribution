/*
 * Copyright (c) 2015. Nathan A. Behary
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.nbehary.retribution;

import android.content.pm.ActivityInfo;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;


import com.nbehary.retribution.preference.PreferencesProvider;


public class FolderColorsActivity extends AppCompatActivity {
    //TODO: This is really ugly.  It can probably be done better.

    private int mBgColor;
    private int mIconColor;
    private int mNameColor;
    private boolean mDefaultBG;
    private Context mContext;
    private String mFreeColor;
    private boolean mTintIcon;
    private int mFolderType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getString(R.string.screen_type).equals("phone")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Log.d("nbehary345","BORK!");
        }

        mBgColor = PreferencesProvider.Interface.General.getFolderBackColor();
        mIconColor = PreferencesProvider.Interface.General.getFolderIconColor();
        mNameColor = PreferencesProvider.Interface.General.getFolderNameColor();
        mDefaultBG = PreferencesProvider.Interface.General.getDefaultFolderBG();
        mFreeColor = PreferencesProvider.Interface.General.getFolderColor();
        mTintIcon = PreferencesProvider.Interface.General.getFolderIconTint();
        mFolderType = PreferencesProvider.Interface.General.getFolderType();
        mContext = this;
        //No colors set.  Default to black text on white.  (sort of, but not the default)
        if (mBgColor == 0 && mIconColor == 0 && mNameColor == 0) {
            mBgColor = -109145601;
            mIconColor = -4038656;
            mNameColor = -847872;
            PreferencesProvider.Interface.General.setFolderBackColor(mContext, mBgColor);
            PreferencesProvider.Interface.General.setFolderNameColor(mContext, mNameColor);
            PreferencesProvider.Interface.General.setFolderIconColor(mContext, mIconColor);
            PreferencesProvider.Interface.General.setDefaultFolderBG(mContext, true);
        }
        setContentView(R.layout.activity_folder_colors);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_set_folder_colors, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.getCustomView().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PreferencesProvider.Interface.General.setFolderBackColor(mContext, mBgColor);
                        PreferencesProvider.Interface.General.setFolderNameColor(mContext, mNameColor);
                        PreferencesProvider.Interface.General.setFolderIconColor(mContext, mIconColor);
                        PreferencesProvider.Interface.General.setDefaultFolderBG(mContext, mDefaultBG);
                        PreferencesProvider.Interface.General.setFolderColor(mContext, mFreeColor);
                        PreferencesProvider.Interface.General.setFolderIconTint(mContext, mTintIcon);
                        PreferencesProvider.Interface.General.setFolderType(mContext, mFolderType);
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

    private void setmDefaultBG(boolean mDefaultBG) {
        this.mDefaultBG = mDefaultBG;
    }

    private void setmNameColor(int mNameColor) {
        this.mNameColor = mNameColor;
    }

    private void setmIconColor(int mIconColor) {
        this.mIconColor = mIconColor;
    }

    private void setmBgColor(int mBgColor) {
        this.mBgColor = mBgColor;
    }

    private void setmFreeColor(String mFreeColor) {
        this.mFreeColor = mFreeColor;
    }

    private void setmTintIcon(boolean mTintIcon) {
        this.mTintIcon = mTintIcon;
    }

    private void setmFolderType(int mFolderType) {
        this.mFolderType = mFolderType;
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        String mChanging;
        ImageView mPreviewImage;
        Context mContext;
        ColorPickerView mPicker;

        int mBgColor;
        int mIconColor;
        int mNameColor;
        String mFreeColor;
        boolean mDefaultBG;
        boolean mTintIcon;
        int mFolderType;


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            mContext = getActivity();
            mBgColor = PreferencesProvider.Interface.General.getFolderBackColor();
            mIconColor = PreferencesProvider.Interface.General.getFolderIconColor();
            mNameColor = PreferencesProvider.Interface.General.getFolderNameColor();
            mDefaultBG = PreferencesProvider.Interface.General.getDefaultFolderBG();
            mTintIcon = PreferencesProvider.Interface.General.getFolderIconTint();
            mFolderType = PreferencesProvider.Interface.General.getFolderType();

            mChanging = "Background";
            //No colors set.  Default to black text on white.  (sort of, but not the default)
            if (mBgColor == 0 && mIconColor == 0 && mNameColor == 0) {
                mBgColor = -109145601;
                mIconColor = -4038656;
                mNameColor = -847872;
            }

            View rootView;

            rootView = inflater.inflate(R.layout.fragment_folder_colors, container, false);
            LinearLayout ui_pane = (LinearLayout) rootView.findViewById(R.id.folder_colors_ui);
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
                    ((FolderColorsActivity) getActivity()).setmDefaultBG(true);
                    mPreviewImage.setImageBitmap(generateFolderPreview(getResources(), mBgColor, mIconColor, mNameColor, mDefaultBG));
                }
            });

            Button wall = (Button) rootView.findViewById(R.id.folder_colors_wall);
            wall.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int color = Utilities.colorFromWallpaper(mContext,0);
                    mPicker.setColor(color);
                    mBgColor = color;
                    mPreviewImage.setImageBitmap(generateFolderPreview(getResources(), mBgColor, mIconColor, mNameColor, mDefaultBG));
                    FolderColorsActivity parent = (FolderColorsActivity) getActivity();
                    parent.setmBgColor(color);
                }
            });


            FrameLayout preview_pane = (FrameLayout) rootView.findViewById(R.id.folder_color_preview);
            preview_pane.setBackgroundColor(Color.argb(0, 0, 0, 0));

            mPreviewImage.setImageBitmap(generateFolderPreview(getResources(), mBgColor, mIconColor, mNameColor, mDefaultBG));

            mPicker = (ColorPickerView) rootView.findViewById(R.id.picker);
            mPicker.setAlphaSliderVisible(false);

            mPicker.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
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
                    mPreviewImage.setImageBitmap(generateFolderPreview(getResources(), mBgColor, mIconColor, mNameColor, mDefaultBG));

                }
            });
            mPicker.setColor(mBgColor);


            Spinner iconSpinner = (Spinner) rootView.findViewById(R.id.folder_colors_icon_spinner);
            ArrayAdapter<CharSequence> iconAdapter = ArrayAdapter.createFromResource(mContext,
                    R.array.folder_icons_array_pro, android.R.layout.simple_spinner_item);
            iconAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            iconSpinner.setAdapter(iconAdapter);
            if (!mTintIcon && (mFolderType != 2)) {
                iconSpinner.setSelection(0);
            } else if (mTintIcon) {
                iconSpinner.setSelection(1);
            } else {
                iconSpinner.setSelection(2);
            }
            iconSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String iconType = (String) parent.getItemAtPosition(position);
                    FolderColorsActivity activity = (FolderColorsActivity) getActivity();
                    if (iconType.equals("Default (White)")) {
                        mTintIcon = false;
                        activity.setmTintIcon(false);
                        mFolderType = 0;
                        activity.setmFolderType(0);
                    } else if (iconType.equals("Tint with Background")) {
                        mTintIcon = true;
                        activity.setmTintIcon(true);
                        mFolderType = 0;
                        activity.setmFolderType(0);

                    } else {
                        mTintIcon = false;
                        activity.setmTintIcon(false);
                        mFolderType = 2;
                        activity.setmFolderType(2);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            return rootView;
        }


        private static Bitmap generateFolderPreview(Resources resources, int back, int iconText, int nameText, boolean bg) {

            Drawable previewDefaultBG;

            LauncherAppState app = LauncherAppState.getInstance();
            DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();

            Drawable icon = app.getIconCache().getFullResDefaultActivityIcon();

            int folderWidth = grid.folderCellWidthPx * 2;
            int folderHeight = (int) (grid.folderCellHeightPx + (grid.folderCellHeightPx / 2) * 1.2);

            Bitmap previewBitmap = Bitmap.createBitmap(folderWidth, folderHeight,
                    Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(previewBitmap);
            // new antialised Paint
            previewDefaultBG = ResourcesCompat.getDrawable(resources, R.drawable.portal_container_holo, null);
            if (!bg) {
                ColorFilter filter = new LightingColorFilter(back, back);
                previewDefaultBG.setColorFilter(filter);
            }
            //      previewDefaultBG.setColorFilter(back, PorterDuff.Mode.MULTIPLY);

            //  } else {
            //canvas.drawColor(back);
            //    previewDefaultBG = resources.getDrawable(R.drawable.portal_container_custom);
            //      ;
            // }
            renderDrawableToBitmap(previewDefaultBG, previewBitmap, 0, 0, folderWidth, folderHeight);
            renderDrawableToBitmap(icon, previewBitmap, 20, 20, grid.iconSizePx, grid.iconSizePx);

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

            paint.getTextBounds("Icon Text", 0, 9, bounds);
            int width = bounds.width();
            int x = ((grid.folderCellWidthPx - width) / 2);
            canvas.drawText("Icon Text", x, grid.iconSizePx + 50, paint);

            if (!bg) {
                paint.setColor(nameText);
            } else {
                paint.setColor(Color.rgb(61, 61, 61));
            }

            paint.getTextBounds("Folder Name", 0, 11, bounds);
            width = bounds.width();
            x = (canvas.getWidth() - width) / 2;
            canvas.drawText("Folder Name", x, grid.folderCellHeightPx + 50, paint);
            previewBitmap = getRoundedCornerBitmap(previewBitmap, 2);
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

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, (float) pixels, (float) pixels, paint);

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
