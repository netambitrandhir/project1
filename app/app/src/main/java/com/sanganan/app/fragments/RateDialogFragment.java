package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.activities.MainHomePageActivity;
import com.sanganan.app.adapters.CommunityHelperAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.GetFavouriteList;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.model.HelperModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 21/10/16.
 */
public class RateDialogFragment extends DialogFragment {

    View view;
    ImageView close, submit;
    EditText feedback;
    TextView headerTitle, dialogMsg, dialogMsg2, dialogMsg3;
    Typeface ubuntuB, wsRegular, karlaR;
    Common common;
    float ratingValue;
    String feedbackString, helperId;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    RatingBar ratingBar;
    Bundle bundle;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_to_rate, container);

        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());


        getDialog().setCanceledOnTouchOutside(false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MyDialog);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        wsRegular = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");
        karlaR = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");

        headerTitle = (TextView) view.findViewById(R.id.headerTitleRate);
        headerTitle.setTypeface(ubuntuB);
        dialogMsg = (TextView) view.findViewById(R.id.dialogMsg);
        dialogMsg.setTypeface(wsRegular);
        dialogMsg2 = (TextView) view.findViewById(R.id.dialogMsg2);
        dialogMsg2.setTypeface(wsRegular);
        dialogMsg3 = (TextView) view.findViewById(R.id.dialogMsg3);
        dialogMsg.setTypeface(wsRegular);
        feedback = (EditText) view.findViewById(R.id.commentbox);
        feedback.setTypeface(karlaR);

        close = (ImageView) view.findViewById(R.id.ivclose);
        submit = (ImageView) view.findViewById(R.id.submit);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        ratingBar.setIsIndicator(false);

        bundle = getArguments();
        helperId = bundle.getString("helperId");

        //   ArrayList<View> list = container.getTouchables();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedbackString = feedback.getText().toString();
                ratingValue = ratingBar.getRating();
                if (ratingValue > 0.0) {
                    getData();
                } else {
                    common.showShortToast("Please give rating");
                }
            }
        });
        return view;
    }

    private void getData() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl + "helperfeedback";
        try {
            JSONObject json = new JSONObject();
            json.put("HelperID", helperId);
            json.put("Rating", String.valueOf(ratingValue));
            json.put("Comments", feedbackString);
            json.put("AddedBy", common.getStringValue(Constants.ResidentRWAID));


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedData(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            VolleyLog.d("error", error.getMessage());

                        }

                    }) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    return headers;
                }
            };
            try {
                req.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    void parsedData(JSONObject json) {
        String status = null;
        String message = null;
        try {
            status = json.optString("Status");
            message = json.optString("Message");

        } catch (Exception e) {

        }

        if (status.equalsIgnoreCase("1")) {
            common.showShortToast(message);
            Constants.isAnyHelperEditedorAdded = true;
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
            Bundle logBundle = new Bundle();
            logBundle.putString("society_id", common.getStringValue("ID"));
            logBundle.putString("user_id", common.getStringValue(Constants.id));
            logBundle.putString("helper_id", helperId);
            mFirebaseAnalytics.logEvent("community_helper_rated", logBundle);


        } else {
            common.showShortToast("no internet...!");
        }
        progressDialog.dismiss();

        dismiss();
        Fragment fragment = new CommunityHelperDetails();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction tx1 = fragmentManager.beginTransaction();
        fragment.setArguments(bundle);
        if (bundle.containsKey("fromSearch")) {
            tx1.replace(R.id.container_body_notification, fragment);
        } else {
            tx1.replace(R.id.container_body, fragment);
            getActivity().onBackPressed();
        }
        tx1.addToBackStack(fragment.getClass().getName().toString());
        tx1.commit();

    }


   /*[FIRAnalytics logEventWithName:@"sign_in"
    parameters:nil]
            [8:19:08 PM] Neeraj Tiwari: [FIRAnalytics logEventWithName:@"forgot_pass"
    parameters:nil];
    [8:19:17 PM] Neeraj Tiwari: [FIRAnalytics logEventWithName:@"sign_up_2nd"
    parameters:nil];
    [8:19:30 PM] Neeraj Tiwari: [FIRAnalytics logEventWithName:@"sign_up_success"
    parameters:nil];
    [8:19:43 PM] Neeraj Tiwari: [FIRAnalytics logEventWithName:@"sign_up_success"
    parameters:nil];
    [8:19:52 PM] Neeraj Tiwari: [FIRAnalytics logEventWithName:@"sign_up"
    parameters:nil];
    [8:20:01 PM] Neeraj Tiwari: [FIRAnalytics logEventWithName:@"notification_photo_clicked"
    parameters:@{
        @"society_id": [APPDELEGATE selectedsocietyid],
        @"user_id" : [[ModalController getContforKey:ksuserlogin] objectForKey:@"ID"]
    }];*/


}
