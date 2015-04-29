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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GridIconPercentFragment.OnPercentInteractionListener} interface
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

    private DeviceProfile mTempProfile;
    private LinearLayout mRootView;
    private TextView mIconCurrent;
    private TextView mIconFull;
    private SeekBar mPercentBar;

    private static GridIconPercentFragment instance;

    private OnPercentInteractionListener mListener;

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

        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher,null);
        mIconFull = (TextView) mRootView.findViewById(R.id.icon_full);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = DynamicGrid.pxFromDp(mTempProfile.iconSizeCalc,dm);
        icon.setBounds(0,0,size,size);
        mIconFull.setCompoundDrawables(null,icon,null,null);
        mIconCurrent = (TextView) mRootView.findViewById(R.id.icon_current);
        Drawable icon2 = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher,null);
        icon2.setBounds(0,0,mTempProfile.iconSizePx,mTempProfile.iconSizePx);
        mIconCurrent.setCompoundDrawables(null,icon2,null,null);

        int percent = (int)(mTempProfile.iconSize/mTempProfile.iconSizeCalc*100f);
        mIconCurrent.setText(String.format("%d",percent)+'%');
        mPercentBar = (SeekBar) mRootView.findViewById(R.id.icon_percent_bar);
        mPercentBar.setProgress(percent-75);
        mPercentBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                DisplayMetrics dm = getResources().getDisplayMetrics();
                mTempProfile.iconSize = mTempProfile.iconSizeCalc * (progress+75)/100f;
                mTempProfile.iconSizePx = DynamicGrid.pxFromDp(mTempProfile.iconSize,dm);
                Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher,null);
                icon.setBounds(0,0,mTempProfile.iconSizePx,mTempProfile.iconSizePx);
                mIconCurrent.setCompoundDrawables(null,icon,null,null);
                mIconCurrent.setText(String.format("%d",progress+75)+'%');
                mTempProfile.setCellHotSeatAndFolders();
                mListener.onPercentInteraction();

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



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPercentInteractionListener) activity;
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
    public interface OnPercentInteractionListener {
        // TODO: Update argument type and name
        void onPercentInteraction();
    }

    public void updateViews(DeviceProfile profile) {
        mTempProfile = profile;
        int percent = (int)(mTempProfile.iconSize/mTempProfile.iconSizeCalc*100f);
        mIconCurrent.setText(String.format("%d",percent)+'%');
        mPercentBar.setProgress(percent-75);

        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher,null);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = DynamicGrid.pxFromDp(mTempProfile.iconSizeCalc,dm);
        icon.setBounds(0, 0, size, size);
        mIconFull.setCompoundDrawables(null,icon,null,null);
        Drawable icon2 = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher,null);
        icon2.setBounds(0,0,mTempProfile.iconSizePx,mTempProfile.iconSizePx);
        mIconCurrent.setCompoundDrawables(null,icon2,null,null);
        
    }

}
