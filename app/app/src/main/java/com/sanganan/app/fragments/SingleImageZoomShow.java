package com.sanganan.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.sanganan.app.R;
import com.sanganan.app.adapters.FullscreenImageAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import java.util.ArrayList;

/**
 * Created by pranav on 8/12/16.
 */

public class SingleImageZoomShow extends DialogFragment {


    View view;
    Bundle bundle;
    ArrayList<String> list;
    Common common;
    RequestQueue requestQueue;
    ViewPager image_swipe;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.image_slide_show, container, false);
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        getDialog().setCanceledOnTouchOutside(false);

        image_swipe = (ViewPager) view.findViewById(R.id.image_pager);


        bundle = getArguments();
        list = bundle.getStringArrayList("imagelist");



        FullscreenImageAdapter adapter = new FullscreenImageAdapter(getActivity(), list,"single");
        image_swipe.setAdapter(adapter);




        return view;
    }


}
