package com.sanganan.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.sanganan.app.R;
import com.sanganan.app.adapters.FullscreenImageAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.VolleySingleton;
import java.util.ArrayList;

/**
 * Created by pranav on 8/12/16.
 */

public class ImageSlideShow extends DialogFragment {


    View view;
    Bundle bundle;
    ArrayList<String> list;
    Common common;
    RequestQueue requestQueue;
    ViewPager image_swipe;
    int position;
    boolean isFromChat = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.image_slide_show, container, false);
        common = Common.getNewInstance(getActivity());

        bundle = getArguments();
        list = bundle.getStringArrayList("imagelist");
        if (bundle.containsKey("position")) {
            position = bundle.getInt("position");
        }

        if (bundle.containsKey("fromChat")) {
            isFromChat = bundle.getBoolean("fromChat");
        }
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        if (!isFromChat) {
            Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
            if(toolbar!=null)
            toolbar.setVisibility(View.GONE);
        }
        else{

        }
        getDialog().setCanceledOnTouchOutside(false);

        image_swipe = (ViewPager) view.findViewById(R.id.image_pager);


        FullscreenImageAdapter adapter = new FullscreenImageAdapter(getActivity(), list,"singleIMage");
        image_swipe.setAdapter(adapter);
        image_swipe.setCurrentItem(position);


        return view;
    }


}
