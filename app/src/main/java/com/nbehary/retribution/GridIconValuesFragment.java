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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    DeviceProfile mTempProfile;
    ImageView mPreviewImage;
    TextView mIconSize, mFontSize, mFontLabel;
    ImageButton mUpFontButton,mDownFontButton;
    DisplayMetrics mDisplayMetrics;
    String mChanging;
    Switch mUseCalculated;
    DecimalFormat df;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GridIconValuesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GridIconValuesFragment newInstance(String param1, String param2) {
        GridIconValuesFragment fragment = new GridIconValuesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public GridIconValuesFragment() {
        // Required empty public constructor
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
        df= new DecimalFormat("0.##");
        mTempProfile = ((GridEditor) getActivity()).getmProfile();
        mDisplayMetrics = getActivity().getResources().getDisplayMetrics();
        // Inflate the layout for this fragment
        View rootView = (FrameLayout) inflater.inflate(R.layout.fragment_grid_icon, container, false);
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


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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
