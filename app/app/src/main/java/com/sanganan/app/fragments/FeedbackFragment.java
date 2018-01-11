package com.sanganan.app.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends DialogFragment {
    View view;
    Common common;
    RequestQueue requestQueue;
    EditText feedbackEdittext;
    TextView title;
    ImageView post, close;
    RatingBar feedbackrating;
    String feedback, complainID, RatingsCount;
    Bundle bundle;

    private FirebaseAnalytics mFirebaseAnalytics;


    public FeedbackFragment() {
        // Required empty public constructor
    }

    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.feedback_add_layout, container, false);
        common = Common.getNewInstance(getActivity());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        Bundle logBundleInitial = new Bundle();
        logBundleInitial.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleInitial.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("complaint_feedback", logBundleInitial);

        getDialog().setCanceledOnTouchOutside(false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MyDialog);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        bundle = getArguments();

        complainID = bundle.getString("complainId");

        title = (TextView) view.findViewById(R.id.headerTitleRate);
        feedbackEdittext = (EditText) view.findViewById(R.id.commentbox);
        post = (ImageView) view.findViewById(R.id.submit);
        close = (ImageView) view.findViewById(R.id.ivclose);
        feedbackrating = (RatingBar) view.findViewById(R.id.ratingBar);
        feedbackrating.setIsIndicator(false);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RatingsCount = String.valueOf(feedbackrating.getRating());
                feedback = feedbackEdittext.getText().toString().trim();

                if (RatingsCount.equalsIgnoreCase("0.0")) {
                    common.showShortToast("Please rate the Product first");
                } else if (feedback.equalsIgnoreCase("")) {
                    common.showShortToast("Please write a review first");
                } else if (common.isNetworkAvailable()) {
                    callApi();

                } else {
                    common.showShortToast("No internet");
                }

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    private void callApi() {

        String url = Constants.BaseUrl + "compfeedback";
        try {
            JSONObject json = new JSONObject();
            json.put("AcknowledgementNote", feedback);
            json.put("Rating", RatingsCount);
            json.put("ID", complainID);
            json.put("RWAID", common.getStringValue("ID"));
            json.put("Status", "3");


            JsonObjectRequest req = new JsonObjectRequest(url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String message = response.optString("Message");
                                String Status = response.optString("Status");

                                if (Status.equalsIgnoreCase("1")) {
                                    common.showShortToast(message);
                                    common.setStringValue("feedbackText", feedback);

                                    dismiss();
                                    getActivity().onBackPressed();

                                    Fragment fragment = new ComplainDetailsFragment();
                                    bundle.putString("rating", RatingsCount);
                                    bundle.putString("feedbackText", feedback);
                                    bundle.putString("Status", "3");
                                    fragment.setArguments(bundle);
                                    FragmentManager manager = getActivity().getSupportFragmentManager();
                                    FragmentTransaction tx = manager.beginTransaction();
                                    tx.replace(R.id.container_body, fragment);
                                    tx.addToBackStack(fragment.getClass().toString());
                                    tx.commit();

                                } else {

                                }
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
}


