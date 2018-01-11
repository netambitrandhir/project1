package com.sanganan.app.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.common.Common;

/**
 * Created by root on 19/10/16.
 */

public class AlertDialogLogin extends DialogFragment {
    View view;
    ImageView closeButton;
    TextView headerTitle, dialogMsg;
    Typeface ubuntuB, wsRegular;
    Common common;

    public AlertDialogLogin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.alert_dialog_login_check, container, false);
        common = Common.getNewInstance(getActivity());
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        wsRegular = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");
        closeButton = (ImageView) view.findViewById(R.id.submit);
        headerTitle = (TextView) view.findViewById(R.id.headerTitle);
        dialogMsg = (TextView) view.findViewById(R.id.dialogMsg);
        headerTitle.setTypeface(ubuntuB);
        dialogMsg.setTypeface(wsRegular);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }

}
