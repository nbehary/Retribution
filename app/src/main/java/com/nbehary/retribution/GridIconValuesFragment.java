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
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DecimalFormat;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GridIconValuesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GridIconValuesFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class GridIconValuesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "TAG";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnValuesInteractionListener mListener;

    private DeviceProfile mTempProfile;
    private TextView mPreviewImage;
    private TextView mIconSize;
    private TextView mFontSize;
    private TextView mFontLabel;
    private ImageButton mUpFontButton;
    private ImageButton mDownFontButton;
    private DisplayMetrics mDisplayMetrics;
    private String mChanging;
    Switch mUseCalculated;
    private DecimalFormat df;
    private View mRootView;

    private static GridIconValuesFragment instance;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment GridIconValuesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GridIconValuesFragment newInstance(String param1) {
        GridIconValuesFragment fragment = new GridIconValuesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, "values_fragment");
        args.putString(ARG_PARAM2, "");
        fragment.setArguments(args);
        return fragment;
    }
    public GridIconValuesFragment() {
        // Required empty public constructor
    }

    public static GridIconValuesFragment getInstance()
    {
        if (instance == null)
            instance = new GridIconValuesFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView !=null) {
            return mRootView;
        }
        df= new DecimalFormat("0.##");
        mTempProfile = ((GridEditor) getActivity()).getmProfile();
        mDisplayMetrics = getActivity().getResources().getDisplayMetrics();
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_grid_icon_values, container, false);
        //mPreviewImage = (ImageView) mRootView.findViewById(R.id.grid_icon_preview);
        //mPreviewImage.setImageBitmap(generateIconPreview(getResources(),mTempProfile.iconSize,mTempProfile,true));
        mPreviewImage = (TextView) mRootView.findViewById(R.id.grid_icon_preview);
        updatePreview();

        mIconSize = (TextView) mRootView.findViewById(R.id.grid_icon_size);
        mIconSize.setText(df.format(mTempProfile.iconSize));

        mFontSize = (TextView) mRootView.findViewById(R.id.grid_font_size);
        mFontSize.setText(df.format(mTempProfile.iconTextSize));

        mFontLabel = (TextView) mRootView.findViewById(R.id.grid_font_label);

        ImageButton downSizeButton = (ImageButton) mRootView.findViewById(R.id.grid_icon_down);
        downSizeButton.setOnTouchListener(new RepeatListener(400, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean showText = true;
                if (((GridEditor) getActivity()).getmChanging().equals("Desktop")) {
                    mTempProfile.iconSize -= 0.5f;
                    if (mTempProfile.iconSize < 1.0) {
                        mTempProfile.iconSize = 1.0f;
                    }
                    mTempProfile.iconSizePx = DynamicGrid.pxFromDp(mTempProfile.iconSize, mDisplayMetrics);
                    mIconSize.setText(df.format(mTempProfile.iconSize));
                    showText = true;
                } else if (((GridEditor) getActivity()).getmChanging().equals("Dock")) {
                    mTempProfile.hotseatIconSize -=0.5f;
                    mTempProfile.hotseatIconSizePx = DynamicGrid.pxFromDp(mTempProfile.hotseatIconSize, mDisplayMetrics);
                    mIconSize.setText(df.format(mTempProfile.hotseatIconSize));
                    showText =false;
                } else {

                }

               // mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,showText));
                updatePreview();
                mTempProfile.setCellHotSeatAndFolders();
                mListener.onValuesInteraction();
            //    if (mUseCalculated.isChecked()) {
             //       mUseCalculated.toggle();
             //   }
            }
        }));

        ImageButton upSizeButton = (ImageButton) mRootView.findViewById(R.id.grid_icon_up);
        upSizeButton.setOnTouchListener(new RepeatListener(400, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean showText = true;
                if (((GridEditor) getActivity()).getmChanging().equals("Desktop")) {
                    mTempProfile.iconSize += 0.5f;
                    mTempProfile.iconSizePx = DynamicGrid.pxFromDp(mTempProfile.iconSize, mDisplayMetrics);
                    mIconSize.setText(df.format(mTempProfile.iconSize));
                    showText = true;
                } else if (((GridEditor) getActivity()).getmChanging().equals("Dock")) {
                    mTempProfile.hotseatIconSize +=0.5f;
                    mTempProfile.hotseatIconSizePx = DynamicGrid.pxFromDp(mTempProfile.hotseatIconSize, mDisplayMetrics);
                    mIconSize.setText(df.format(mTempProfile.hotseatIconSize));
                    showText =false;
                }
                //mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,showText));
                updatePreview();
                mTempProfile.setCellHotSeatAndFolders();
                mListener.onValuesInteraction();
            //    if (mUseCalculated.isChecked()) {
             //       mUseCalculated.toggle();
              //  }
            }
        }));



        mDownFontButton = (ImageButton) mRootView.findViewById(R.id.grid_font_down);
        mDownFontButton.setOnTouchListener(new RepeatListener(400, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTempProfile.iconTextSize -= 0.1f;
                if (mTempProfile.iconTextSize < 1.0) {
                    mTempProfile.iconTextSize = 1.0f;
                }
                mTempProfile.iconTextSizePx = DynamicGrid.pxFromSp(mTempProfile.iconTextSize, mDisplayMetrics);
                //mPreviewImage.setImageBitmap(generateIconPreview(getResources(),mTempProfile.iconSize,mTempProfile,true));
                updatePreview();
                mFontSize.setText(df.format(mTempProfile.iconTextSize));
                mTempProfile.setCellHotSeatAndFolders();
                mListener.onValuesInteraction();
               // if (mUseCalculated.isChecked()) {
             //       mUseCalculated.toggle();
              //  }
            }
        }));

        mUpFontButton = (ImageButton) mRootView.findViewById(R.id.grid_font_up);
        mUpFontButton.setOnTouchListener(new RepeatListener(400, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTempProfile.iconTextSize += 0.1f;
                mTempProfile.iconTextSizePx = DynamicGrid.pxFromSp(mTempProfile.iconTextSize, mDisplayMetrics);
                //mPreviewImage.setImageBitmap(generateIconPreview(getResources(),mTempProfile.iconSize,mTempProfile,true));
                updatePreview();
                mFontSize.setText(df.format(mTempProfile.iconTextSize));
                mTempProfile.setCellHotSeatAndFolders();
                mListener.onValuesInteraction();
              //  if (mUseCalculated.isChecked()) {
             //       mUseCalculated.toggle();
             //   }

            }
        }));






        return mRootView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnValuesInteractionListener) activity;
        } catch (ClassCastException e) {
          //  throw new ClassCastException(activity.toString()
           //         + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnValuesInteractionListener {
        // TODO: Update argument type and name
        void onValuesInteraction();
    }

    public void updateViews(DeviceProfile profile) {
        mTempProfile = profile;
        mFontSize.setText(df.format(mTempProfile.iconTextSize));
        mIconSize.setText(df.format(mTempProfile.iconSize));
        mChanging = "Desktop";
        //mPreviewImage.setImageBitmap(generateIconPreview(getResources(), mTempProfile.iconSize, mTempProfile,true));
        updatePreview();
    }

    private void updatePreview() {
        Drawable icon2 = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher,null);
        icon2.setBounds(0,0,mTempProfile.iconSizePx,mTempProfile.iconSizePx);
        mPreviewImage.setCompoundDrawables(null,icon2,null,null);
        mPreviewImage.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTempProfile.iconTextSize);
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
        Drawable icon = ResourcesCompat.getDrawable(res,R.drawable.ic_launcher,null);

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

    private static void renderDrawableToBitmap(
            Drawable d, Bitmap bitmap, int x, int w, int h) {
        renderDrawableToBitmap(d, bitmap, 0, 0, w, h);
    }

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
