package com.nbehary.retribution;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.ref.PhantomReference;
import java.text.DecimalFormat;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.nbehary.retribution.GridIconFragment.OnCalculatedChangeListener} interface
 * to handle interaction events.
 * Use the {@link GridIconFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class GridIconFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnCalculatedChangeListener mListener;

    DeviceProfile mTempProfile;
    ImageView mPreviewImage;
    TextView mIconSize, mFontSize, mFontLabel;
    ImageButton mUpFontButton,mDownFontButton;
    DisplayMetrics mDisplayMetrics;
    String mChanging;
    Switch mUseCalculated, mChangeValues;
    DecimalFormat df;
    View mRootView;
    TextView mFolderWarning, mDockWarning;

    private static GridIconFragment instance;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GridIconFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GridIconFragment newInstance(String param1, String param2) {
        GridIconFragment fragment = new GridIconFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public GridIconFragment() {
        // Required empty public constructor
    }

    public static GridIconFragment getInstance()
    {
        if (instance == null)
            instance = new GridIconFragment();
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
     //   if (mRootView != null){
     //       return mRootView;
     //   }
        df= new DecimalFormat("0.##");
        mTempProfile = ((GridEditor) getActivity()).getmProfile();
        mDisplayMetrics = getActivity().getResources().getDisplayMetrics();
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_grid_icon, container, false);
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentById(R.id.icon_fragment_container) == null) {
            //GridIconPercentFragment percentFragment = (GridIconPercentFragment) fm.findFragmentById(R.id.icon_fragment_container);
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.icon_fragment_container, GridIconPercentFragment.newInstance("percent_fragment", ""),"percent_fragment").commit();

        }

        mChangeValues = (Switch) mRootView.findViewById(R.id.icon_percent_values);
        mChangeValues.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                if (isChecked){
                    ft.replace(R.id.icon_fragment_container, GridIconValuesFragment.newInstance("values_fragment", ""),"values_fragment")
                    .commit();
                } else {
                    ft.replace(R.id.icon_fragment_container, GridIconPercentFragment.newInstance("percent_fragment", ""),"percent_fragment")
                            .commit();
                }
            }
        });
        if (!LauncherAppState.getInstance().getProVersion()) {
            mChangeValues.setVisibility(View.GONE);
        }



        mUseCalculated = (Switch) mRootView.findViewById(R.id.grid_use_calc);
        mUseCalculated.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mTempProfile.iconSize =  mTempProfile.iconSizeCalc;
                    mTempProfile.hotseatIconSize = mTempProfile.hotseatIconSizeCalc;
                    mTempProfile.iconTextSize = mTempProfile.iconTextSizeCalc;
                    mTempProfile.setCellHotSeatAndFolders();
                    mListener.onCalculatedChangedInteraction(mTempProfile);


                }
            }
        });

        if ((mTempProfile.iconSize != mTempProfile.iconSizeCalc) || (mTempProfile.iconTextSize != mTempProfile.iconTextSizeCalc)){
            mUseCalculated.setChecked(false);
        }

        mFolderWarning = (TextView) mRootView.findViewById(R.id.folder_warning);
        if (mTempProfile.iconSize > 72) {
            mFolderWarning.setVisibility(View.VISIBLE);
        } else {
            mFolderWarning.setVisibility(View.GONE);
        }

        mDockWarning = (TextView) mRootView.findViewById(R.id.dock_warning);
        if (mTempProfile.iconSize < 48) {
            mDockWarning.setVisibility(View.VISIBLE);
        } else {
            mDockWarning.setVisibility(View.GONE);
        }

/* TODO:Next Version......
        CheckBox hideLabels = (CheckBox) mRootView.findViewById(R.id.grid_hide_labels);
        hideLabels.setChecked(mTempProfile.hideLabels);
        hideLabels.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTempProfile.hideLabels = true;

                } else {
                    mTempProfile.hideLabels = false;
                }
                mTempProfile.setIconLabels();
                mTempProfile.adjustSizesAuto(getResources());
            }
        });
*/
        return mRootView;
    }

    public void updateCalculated() {
       // if (mUseCalculated.isChecked()) {
       //     mUseCalculated.toggle();
       // }
        if ((mTempProfile.iconSize != mTempProfile.iconSizeCalc) || (mTempProfile.iconTextSize != mTempProfile.iconTextSizeCalc)){
            mUseCalculated.setChecked(false);
        }
        if ((mTempProfile.iconSize > 72)  && (!mTempProfile.autoHotseat)) {
            mFolderWarning.setVisibility(View.VISIBLE);
        } else {
            mFolderWarning.setVisibility(View.GONE);
        }

        if ((mTempProfile.iconSize < 48) && (!mTempProfile.autoHotseat)) {
            mDockWarning.setVisibility(View.VISIBLE);
        } else {
            mDockWarning.setVisibility(View.GONE);
        }
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCalculatedChangeListener) activity;
        } catch (ClassCastException e) {
          //  throw new ClassCastException(activity.toString()
          //          + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroyView() {
        super.onDetach();
        Log.d("nbehary110", "GridIconFragment OnDestroyView");


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
    public interface OnCalculatedChangeListener {
        // TODO: Update argument type and name
        public void onCalculatedChangedInteraction(DeviceProfile profile);
    }




}
