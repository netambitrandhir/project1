package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanganan.app.R;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.model.Remark;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 3/5/17.
 */

public class AddComplainComment extends BaseFragment {

    View view;
    RequestQueue requestQueue;
    Common common;
    EditText etCommentBox;
    TextView saveComment, titletextView;
    String commentText, complainId;
    Bundle bundle;
    Bundle mainBundle;

    Typeface ubuntuB, karlaB, karlaR, wsregular;
    String remarkInGsonFormat;
    ArrayList<Remark> complainRemarks;

    Remark remark;
    boolean isFromSearchComplain;
    String residentRwaId = "";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.addcomment_complain, container, false);

        initializeVariables(view);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        bundle = getArguments();
        complainId = bundle.getString("compId");
        mainBundle = bundle.getBundle("mainBundle");
        remarkInGsonFormat = mainBundle.getString("remarkInGson");
        Gson gson = new Gson();
        Type type = new TypeToken<List<Remark>>() {
        }.getType();
        complainRemarks = gson.fromJson(remarkInGsonFormat, type);

        residentRwaId = common.getStringValue(Constants.ResidentRWAID);

        if (mainBundle.containsKey("fromComplainSearch")) {
            isFromSearchComplain = mainBundle.getBoolean("fromComplainSearch");
        }


        saveComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (common.isNetworkAvailable()) {

                    commentText = etCommentBox.getText().toString();
                    if (!commentText.isEmpty()) {
                        addComplainComment();

                    } else {
                        common.showShortToast("Please add comment before saving");
                    }
                } else {
                    common.showShortToast("No internet");
                }
            }
        });

        return view;
    }


    private void initializeVariables(View view) {

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        karlaB = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");
        karlaR = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");
        wsregular = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");

        titletextView = (TextView) view.findViewById(R.id.titletextView);
        titletextView.setTypeface(ubuntuB);

        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        common = Common.getNewInstance(getActivity());

        saveComment = (TextView) view.findViewById(R.id.saveComment);
        saveComment.setTypeface(karlaR);
        etCommentBox = (EditText) view.findViewById(R.id.addComment);
        etCommentBox.setTypeface(wsregular);


    }


    private void addComplainComment() {

        common.showSpinner(getActivity());
        try {

            String uri = Constants.BaseUrl + "complaincomment";

            String comment = etCommentBox.getText().toString();
            comment = common.StringToBase64StringConvertion(comment);

            JSONObject json = new JSONObject();
            json.put("Remark", comment);
            json.put("CompID", complainId);
            json.put("EnteredBy", residentRwaId);


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                if (response.optString("Status").equals("1")) {

                                    remark = new Remark();
                                    remark.setRemark(etCommentBox.getText().toString());
                                    String name = common.getStringValue(Constants.FirstName);
                                    String flatString = common.getStringValue(Constants.flatNumber);
                                    remark.setFlat(flatString);
                                    remark.setName(name);
                                }
                                parsedData(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                                common.hideSpinner();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            VolleyLog.d("error", error.getMessage());
                            common.hideSpinner();

                        }

                    }) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    return headers;
                }
            };
            try {
                req.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
                common.hideSpinner();
            }
        } catch (Exception e) {
            e.printStackTrace();
            common.hideSpinner();
        }
    }

    void parsedData(JSONObject json) {
        String status = "";
        String message = "";
        common.hideKeyboard(getActivity());
        common.hideSpinner();
        try {
            status = json.optString("Status");
            message = json.optString("Message");

            if (status.equals("1")) {
                getActivity().onBackPressed();
                Constants.isCommentAdded = true;
                Constants.isComplainStatusChangedOrEdited = true;
                common.showShortToast(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
            common.hideSpinner();
        }

    }




    /*private void updateComplainsFragment() {

        complainRemarks.add(remark);
        Gson gson = new Gson();
        String jsonRemark = gson.toJson(complainRemarks);
        mainBundle.putString("remarkInGson", jsonRemark);
        Fragment fragment = new ComplainDetailsFragment();
        fragment.setArguments(mainBundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction tx1 = fragmentManager.beginTransaction();

        if (isFromSearchComplain) {
            getActivity().setContentView(R.layout.any_notification_activity);
            tx1.replace(R.id.container_body_notification, fragment);
        } else {
            tx1.replace(R.id.container_body, fragment);
        }
        tx1.addToBackStack("detailComplain");
        tx1.commit();
    }*/

}
