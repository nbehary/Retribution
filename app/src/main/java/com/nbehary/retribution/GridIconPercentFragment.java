package com.nbehary.retribution;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GridIconPercentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GridIconPercentFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class GridIconPercentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DeviceProfile mTempProfile;
    LinearLayout mRootView;
    TextView mIconCurrent;
    TextView mIconFull;
    SeekBar mPercentBar;

    private static GridIconPercentFragment instance;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GridIconPercentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GridIconPercentFragment newInstance(String param1, String param2) {
        GridIconPercentFragment fragment = new GridIconPercentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public GridIconPercentFragment() {
        // Required empty public constructor
    }

    public static GridIconPercentFragment getInstance()
    {
        if (instance == null)
            instance = new GridIconPercentFragment();
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
        // Inflate the layout for this fragment
        if (mRootView !=null) {
            return mRootView;
        }
        mRootView = (LinearLayout) inflater.inflate(R.layout.fragment_grid_icon_percent, container, false);
        mTempProfile = ((GridEditor) getActivity()).getmProfile();

        Drawable icon = getResources().getDrawable(R.drawable.ic_launcher);
        mIconFull = (TextView) mRootView.findViewById(R.id.icon_full);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = DynamicGrid.pxFromDp(mTempProfile.iconSizeCalc,dm);
        icon.setBounds(0,0,size,size);
        Log.d("nbehary110", String.format("IconSizeCalc: %f. IconSize:%f",mTempProfile.iconSizeCalc,mTempProfile.iconSize));
        mIconFull.setCompoundDrawables(null,icon,null,null);
        mIconCurrent = (TextView) mRootView.findViewById(R.id.icon_current);
        Drawable icon2 = getResources().getDrawable(R.drawable.ic_launcher);
        icon2.setBounds(0,0,mTempProfile.iconSizePx,mTempProfile.iconSizePx);
        mIconCurrent.setCompoundDrawables(null,icon2,null,null);

        int percent = (int)(mTempProfile.iconSizeCalc/mTempProfile.iconSize*100f);
        mIconCurrent.setText(String.format("%d",percent)+'%');
        mPercentBar = (SeekBar) mRootView.findViewById(R.id.icon_percent_bar);
        mPercentBar.setProgress(percent-75);
        mPercentBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                DisplayMetrics dm = getResources().getDisplayMetrics();
                mTempProfile.iconSize = mTempProfile.iconSizeCalc * (float)((progress+75)/100f);

                Log.d("nbehary110", String.format("IconSize: %f, progress: %d",mTempProfile.iconSize,progress));
                mTempProfile.iconSizePx = DynamicGrid.pxFromDp(mTempProfile.iconSize,dm);
                Drawable icon = getResources().getDrawable(R.drawable.ic_launcher);
                icon.setBounds(0,0,mTempProfile.iconSizePx,mTempProfile.iconSizePx);
                mIconCurrent.setCompoundDrawables(null,icon,null,null);
                mIconCurrent.setText(String.format("%d",progress+75)+'%');
                mTempProfile.setCellHotSeatAndFolders();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return mRootView;
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
           // throw new ClassCastException(activity.toString()
             //       + " must implement OnFragmentInteractionListener");
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

    public void updateViews(DeviceProfile profile) {
        mTempProfile = profile;
        int percent = (int)(mTempProfile.iconTextSizeCalc/mTempProfile.iconSize*100f);
        mIconCurrent.setText(String.format("%d",percent+'%'));
        mPercentBar.setProgress(percent-75);

        Drawable icon = getResources().getDrawable(R.drawable.ic_launcher);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = DynamicGrid.pxFromDp(mTempProfile.iconSizeCalc,dm);
        icon.setBounds(0, 0, size, size);
        mIconFull.setCompoundDrawables(null,icon,null,null);
        Drawable icon2 = getResources().getDrawable(R.drawable.ic_launcher);
        icon2.setBounds(0,0,mTempProfile.iconSizePx,mTempProfile.iconSizePx);
        mIconCurrent.setCompoundDrawables(null,icon2,null,null);
        
    }

}
