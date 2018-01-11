package com.sanganan.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sanganan.app.interfaces.ToolbarListner;


/**
 * Created by aksoni on 1/6/2016.
 */
public class BaseFragment extends Fragment {
    public ToolbarListner activityCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCallback = (ToolbarListner) activity;
        }   catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ToolbarListener");
        }
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            // mfragmentButtonClickListner = (FragmentButtonClickListner)
            // getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FragmentButtonClickListner");
        }
    }
}