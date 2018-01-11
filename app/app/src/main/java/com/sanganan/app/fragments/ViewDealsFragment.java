package com.sanganan.app.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanganan.app.R;

/**
 * Created by Randhir Patel on 7/7/17.
 */

public class ViewDealsFragment extends BaseFragment {


    View view;
    Typeface ubuntuR, ubuntuB;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.view_deals_layout, container, false);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);


        TextView titletextView = (TextView) view.findViewById(R.id.titletextView);
        ubuntuR = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-R.ttf");
        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
//        TextView textView = (TextView) view.findViewById(R.id.message);
//        textView.setTypeface(ubuntuR);
        titletextView.setTypeface(ubuntuB);


        return view;
    }
}
