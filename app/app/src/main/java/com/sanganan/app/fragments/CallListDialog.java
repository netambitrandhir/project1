package com.sanganan.app.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.adapters.CalliDialogAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;

import java.util.ArrayList;

/**
 * Created by raj on 1/5/2017.
 */

public class CallListDialog extends DialogFragment {
    View view;
    Common common;
    ImageView close, dialogMsg, dialogMsg2, dialogMsg3;

    ListView callingList;
    ArrayList<String> numberList = new ArrayList<>();
    String shopId = "";
    boolean isLoggedIn;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.calli_dialog, container);
        common = Common.getNewInstance(getActivity());

        Bundle bundle = getArguments();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        close = (ImageView) view.findViewById(R.id.close);

        numberList = bundle.getStringArrayList("listNumber");

        ArrayList<String> numberListFinal = new ArrayList<>();
        for(String str : numberList){
            if(!str.isEmpty()){
                numberListFinal.add(str);
            }
        }

        shopId = bundle.getString("shopId");
        isLoggedIn = common.getBooleanValue(Constants.isLoggedIn);

        callingList = (ListView) view.findViewById(R.id.callingList);



        CalliDialogAdapter callAdepter = new CalliDialogAdapter(getActivity(), numberListFinal);
        callingList.setAdapter(callAdepter);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        callingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isLoggedIn) {
                    Bundle logBundle = new Bundle();
                    logBundle.putString("shop_id", shopId);
                    logBundle.putString("user_id", common.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("call_shop", logBundle);
                }
                String uri = "tel:" + numberList.get(position).trim();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(uri));
                startActivity(callIntent);
            }
        });


        return view;
    }
}
