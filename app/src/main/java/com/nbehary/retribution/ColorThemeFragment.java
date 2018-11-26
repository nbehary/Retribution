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

import android.app.Fragment;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.palette.graphics.Palette;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


public class ColorThemeFragment extends Fragment {
    String mChanging;
    ImageView mPreviewImage;

    ColorPickerView mPicker;
    ColorThemeRepo mColorThemeRepo;
    View rootView;
    LinearLayout preview;
    TextView labelText;
    TextView nameText;
    Drawable previewBackground;
    ColorThemeActivity mParent;



    public ColorThemeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mParent = (ColorThemeActivity) getActivity();
        mColorThemeRepo = mParent.mColorThemeRepo;







        mChanging = "Backgrounds";
        //No colors set.  Default to black text on white.  (sort of, but not the default)
        if (mColorThemeRepo.mFolderBack == 0 && mColorThemeRepo.mFolderLabels == 0 && mColorThemeRepo.mFolderName == 0) {
            mColorThemeRepo.mFolderBack = -109145601;
            mColorThemeRepo.mFolderLabels = -4038656;
            mColorThemeRepo.mFolderName = -847872;
        }


        View ui_pane;

        final View rootView = inflater.inflate(R.layout.fragment_folder_colors, container, false);
        ui_pane = rootView.findViewById(R.id.folder_colors_ui);
        ui_pane.setBackgroundColor(Color.argb(128, 0, 0, 0));



        final Spinner spinner = (Spinner) rootView.findViewById(R.id.folder_colors_item_spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mParent,
                R.array.folder_colors_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mChanging = (String) parent.getItemAtPosition(position);
                switch (mChanging) {
                    case "Backgrounds":
                        mPicker.setColor(mColorThemeRepo.mFolderBack);
                        break;
                    case "Titles (ie. Folder Names)":
                        if (mColorThemeRepo.mFolderName != 0) {
                            mPicker.setColor(mColorThemeRepo.mFolderName);
                        }
                        break;
                    default:
                        if (mColorThemeRepo.mFolderLabels != 0) {
                            mPicker.setColor(mColorThemeRepo.mFolderLabels);
                        }

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button reset = (Button) rootView.findViewById(R.id.folder_color_reset);
        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: this sets mDefaultBG.  Should ColorThemeRepo replace that?
                if (rootView.getTag().equals("big")) {
                    mPreviewImage.setImageBitmap(generateFolderPreview(getResources(), mColorThemeRepo.mFolderBack, mColorThemeRepo.mFolderLabels, mColorThemeRepo.mFolderName));
                }else {
                    setColorsMini();
                }

            }
        });

//

        Button wallBox = (Button) rootView.findViewById(R.id.folder_colors_wall);
        wallBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mColorThemeRepo.mWallTint = isChecked;
                ColorThemeActivity parent = (ColorThemeActivity) getActivity();

//                if (isChecked) {
                    Palette.Swatch swatch = Utilities.swatchFromWallpaper(mParent);
                    int back = swatch.getRgb();
                    int title = swatch.getTitleTextColor();
                    int icon = swatch.getBodyTextColor();
                    mChanging = "Backgrounds";
                    adapter.notifyDataSetChanged();
                    mPicker.setColor(back);
                    mColorThemeRepo.mFolderBack = back;
                    mColorThemeRepo.mFolderLabels = icon;
                    mColorThemeRepo.mFolderName = title;
                    if (rootView.getTag().equals("big")) {
                        mPreviewImage.setImageBitmap(generateFolderPreview(getResources(), mColorThemeRepo.mFolderBack, mColorThemeRepo.mFolderLabels, mColorThemeRepo.mFolderName));
                    }else {
                        setColorsMini();
                    }
                    //parent.setmBgColor(back);
                   // parent.setmIconColor(icon);
                   // parent.setmNameColor(title);
//                }
               // parent.setmWallTint(mColorThemeRepo.mWallTint);
            }
        });
        //Only setup the full size Preview on larger screens (or landscape...)
        if (rootView.getTag().equals("big")) {
            mPreviewImage = (ImageView) rootView.findViewById(R.id.folder_color_preview_image);
            FrameLayout preview_pane = (FrameLayout) rootView.findViewById(R.id.folder_color_preview);
            preview_pane.setBackgroundColor(Color.argb(0, 0, 0, 0));
            mPreviewImage.setImageBitmap(generateFolderPreview(getResources(), mColorThemeRepo.mFolderBack, mColorThemeRepo.mFolderLabels, mColorThemeRepo.mFolderName));

        }else {//Mini-Preview
            preview = (LinearLayout) rootView.findViewById(R.id.folder_color_preview);
            labelText = (TextView) rootView.findViewById(R.id.folder_colors_icon_text);
            nameText = (TextView) rootView.findViewById(R.id.folder_colors_label_text);
            previewBackground = preview.getBackground();
            DrawableCompat.setTint(DrawableCompat.wrap(previewBackground), mColorThemeRepo.mFolderBack);
            labelText.setTextColor(mColorThemeRepo.mFolderLabels);
            nameText.setTextColor(mColorThemeRepo.mFolderName);
        }

        mPicker = (ColorPickerView) rootView.findViewById(R.id.picker);
        mPicker.setAlphaSliderVisible();

        mPicker.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int i) {
                ColorThemeActivity parent = (ColorThemeActivity) getActivity();
                switch (mChanging) {
                    case "Backgrounds":
                        mColorThemeRepo.mFolderBack = i;
                        //parent.setmBgColor(i);
                        break;
                    case "Titles (ie. Folder Names)":
                        mColorThemeRepo.mFolderName = i;
                        //parent.setmNameColor(i);
                        break;
                    case "Body Text (ie. Icon Labels)":

                        mColorThemeRepo.mFolderLabels = i;
                        //parent.setmIconColor(i);
                        break;
                }
                if (rootView.getTag().equals("big")) {
                    Resources res = getResources();
                    mPreviewImage.setImageBitmap(generateFolderPreview(res, mColorThemeRepo.mFolderBack, mColorThemeRepo.mFolderLabels, mColorThemeRepo.mFolderName));
                }else {
                    setColorsMini();
                }

            }
        });
        mPicker.setColor(mColorThemeRepo.mFolderBack);


        Spinner iconSpinner = (Spinner) rootView.findViewById(R.id.folder_colors_icon_spinner);
        ArrayAdapter<CharSequence> iconAdapter = ArrayAdapter.createFromResource(mParent,
                R.array.folder_icons_array_pro, android.R.layout.simple_spinner_item);
        iconAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        iconSpinner.setAdapter(iconAdapter);
        if (!mColorThemeRepo.mFolderIconTint && (mColorThemeRepo.mFolderType != 2)) {
            iconSpinner.setSelection(0);
        } else if (mColorThemeRepo.mFolderIconTint) {
            iconSpinner.setSelection(1);
        } else {
            iconSpinner.setSelection(2);
        }
        iconSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String iconType = (String) parent.getItemAtPosition(position);
                ColorThemeActivity activity = (ColorThemeActivity) getActivity();
                switch (iconType) {
                    case "Default (White)":
                        mColorThemeRepo.mFolderIconTint = false;
                        //activity.setmTintIcon(false);
                        mColorThemeRepo.mFolderType = 0;
                        //activity.setmFolderType(0);
                        break;
                    case "Tint with Background":
                        mColorThemeRepo.mFolderIconTint = true;
                        //activity.setmTintIcon(true);
                        mColorThemeRepo.mFolderType = 0;
                        //activity.setmFolderType(0);
                        break;
                    default:
                        mColorThemeRepo.mFolderIconTint = false;
                        //activity.setmTintIcon(false);
                        mColorThemeRepo.mFolderType = 2;
                        //activity.setmFolderType(2);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return rootView;
    }

    private void setColorsMini() {
        previewBackground = preview.getBackground();
        DrawableCompat.setTint(DrawableCompat.wrap(previewBackground), mColorThemeRepo.mFolderBack);
        preview.invalidate();
        labelText.setTextColor(mColorThemeRepo.mFolderLabels);
        nameText.setTextColor(mColorThemeRepo.mFolderName);
    }

    private static Bitmap generateFolderPreview(Resources resources, int back, int iconText, int nameText) {

        Drawable previewDefaultBG;

        LauncherAppState app = LauncherAppState.getInstance();
        DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();

        Drawable icon = app.getIconCache().getFullResDefaultActivityIcon();

        int folderWidth = grid.folderCellWidthPx * 2;
        int folderHeight = (int) (grid.folderCellHeightPx + (grid.folderCellHeightPx / 2) * 1.2);

        Bitmap previewBitmap = Bitmap.createBitmap(folderWidth, folderHeight,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(previewBitmap);

        previewDefaultBG = ResourcesCompat.getDrawable(resources, R.drawable.portal_container_holo, null);

        DrawableCompat.setTint(DrawableCompat.wrap(previewDefaultBG), back);

        renderDrawableToBitmap(previewDefaultBG, previewBitmap, 0, 0, folderWidth, folderHeight);
        renderDrawableToBitmap(icon, previewBitmap, 20, 20, grid.iconSizePx, grid.iconSizePx);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);




        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                14, resources.getDisplayMetrics()));
        paint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        Rect bounds = new Rect();

        paint.setColor(iconText);

        //paint.setColor(Color.rgb(61, 61, 61));


        paint.getTextBounds("Icon Text", 0, 9, bounds);
        int width = bounds.width();
        int x = ((grid.folderCellWidthPx - width) / 2);
        canvas.drawText("Icon Text", x, grid.iconSizePx + 50, paint);


        paint.setColor(nameText);


        paint.getTextBounds("Folder Name", 0, 11, bounds);
        width = bounds.width();
        x = (canvas.getWidth() - width) / 2;
        canvas.drawText("Folder Name", x, grid.folderCellHeightPx + 50, paint);
        previewBitmap = getRoundedCornerBitmap(previewBitmap);
        return previewBitmap;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
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
        canvas.drawRoundRect(rectF, (float) 2, (float) 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    //TODO: Kill this.  (It is in Utilities)
    private static void renderDrawableToBitmap(
            Drawable d, Bitmap bitmap, int x, int y, int w, int h) {
        if (bitmap != null) {
            Canvas c = new Canvas(bitmap);
            c.scale(1f, 1f);
            Rect oldBounds = d.copyBounds();
            d.setBounds(x, y, x + w, y + h);
            d.draw(c);
            d.setBounds(oldBounds); // Restore the bounds
            c.setBitmap(null);
        }
    }
}




