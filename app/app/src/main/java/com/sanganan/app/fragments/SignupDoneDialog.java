package com.sanganan.app.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.activities.MainHomePageActivity;
import com.sanganan.app.common.Common;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupDoneDialog extends DialogFragment {
    View view;
    ImageView closeButton;
    TextView headerTitle, dialogMsg;
    Typeface ubuntuB, wsRegular;
    Common common;

    public SignupDoneDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.signup_complete,container,false);
        common=Common.getNewInstance(getActivity());
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
        ubuntuB=Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        wsRegular=Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");
        closeButton= (ImageView) view.findViewById(R.id.submit);
        headerTitle= (TextView) view.findViewById(R.id.headerTitle);
        dialogMsg= (TextView) view.findViewById(R.id.dialogMsg);
        headerTitle.setTypeface(ubuntuB);
        dialogMsg.setTypeface(wsRegular);
        dialogMsg.setText("You are registered with"+" "+common.getStringValue("SocietyName") +" "+"Now RWA will verify your data and on approval, you can browse other features of app");

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainHomePageActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {

            }
        };
    }

}
