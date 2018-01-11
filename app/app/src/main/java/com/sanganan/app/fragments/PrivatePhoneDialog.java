package com.sanganan.app.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.sanganan.app.R;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.VolleySingleton;

/**
 * Created by pranav on 12/12/16.
 */

public class PrivatePhoneDialog extends DialogFragment {


    View view;
    Typeface ubuntuB, wsRegular, karlaR;
    Common common;
    RequestQueue requestQueue;
    ImageView emailButton, cancleButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.private_profile_popup, container);

        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        getDialog().setCanceledOnTouchOutside(false);

        final Bundle bundle = getArguments();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        wsRegular = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");
        karlaR = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");

        emailButton = (ImageView) view.findViewById(R.id.emailButton);
        cancleButton = (ImageView) view.findViewById(R.id.cancelButton);

        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Fragment fragment = new SendEmailFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction tx1 = fragmentManager.beginTransaction();
                if (bundle.containsKey("fromMemberSearch")) {
                    tx1.replace(R.id.container_body_notification, fragment);
                } else {
                    tx1.replace(R.id.container_body, fragment);
                }
                tx1.addToBackStack(fragment.getClass().getName());
                tx1.commit();
            }
        });


        return view;
    }
}
