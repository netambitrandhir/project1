package com.sanganan.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanganan.app.R;

/**
 * Created by pranav on 3/8/16.
 */
public class FromMembers extends BaseFragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.from_members, container, false);
        initializeVariables();

        return view;
    }

    private void initializeVariables() {
    }
}
