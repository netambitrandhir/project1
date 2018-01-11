package com.sanganan.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanganan.app.R;

/**
 * Created by pranav on 3/8/16.
 */
public class ResidentDirectory extends BaseFragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.resident_directory, container, false);
        initializeVariables();


        TextView title = (TextView)getActivity().findViewById(R.id.toolbar_title);
        title.setText("RESIDENT DIRECTORY");

        return view;
    }

    private void initializeVariables() {
    }
}
