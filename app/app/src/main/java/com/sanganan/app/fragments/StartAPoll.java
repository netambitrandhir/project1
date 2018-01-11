package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sanganan.app.R;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.InsertToMangoDb;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 29/11/16.
 */

public class StartAPoll extends BaseFragment {

    View view;
    Common common;
    RequestQueue requestQueue;
    Typeface ubuntuB, ubuntuR;
    EditText poll_edit;
    ImageView start;
    ProgressDialog progressDialog;
    String pollQuestion;
    TextView message;
    public static boolean responseHandled = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.start_a_poll, container, false);
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        setupUI(view);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        TextView title = (TextView) view.findViewById(R.id.textViewtitle);
        title.setTypeface(ubuntuB);
        ubuntuR = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-R.ttf");


        poll_edit = (EditText) view.findViewById(R.id.poll_edit);
        poll_edit.setTypeface(ubuntuR);
        start = (ImageView) view.findViewById(R.id.start);

        message = (TextView) view.findViewById(R.id.message);
        message.setTypeface(ubuntuR);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!poll_edit.getText().toString().isEmpty()) {
                    if (common.isNetworkAvailable()) {
                        pollQuestion = common.StringToBase64StringConvertion(poll_edit.getText().toString());
                        getData();
                    } else {
                        common.showShortToast("No internet...!!");
                    }
                } else {
                    common.showShortToast("Enter a question");
                }
            }
        });


        return view;
    }


    public void getData() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl + "addpole";
        try {
            JSONObject json = new JSONObject();
            json.put("ResidentRWAID", common.getStringValue(Constants.ResidentRWAID));
            json.put("Question", pollQuestion);
            json.put("ExpirationDt", "1717171723476");
            json.put("IsActive", "1");
            json.put("UserID", common.getStringValue(Constants.id));


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedData(response);
                                responseHandled = true;
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
                req.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    void parsedData(JSONObject json) {
        String status = "";
        String message = "";
        String poleID = "";

        try {
            status = json.optString("Status");
            message = json.optString("Message");
            if (status.equalsIgnoreCase("1")) {
                poleID = json.optString("PoleID");
            }
            progressDialog.dismiss();

            if (status.equalsIgnoreCase("1")) {
                common.showShortToast(message);
                Constants.isAnyPollCreatedOrRemoved = true;
                InsertToMangoDb obj = new InsertToMangoDb(getActivity(), "poll", poleID, poll_edit.getText().toString(), "");
                obj.insertDataToServer();
                Constants.isAnyNewPostAdded = true;
                getActivity().onBackPressed();
            }

        } catch (Exception e) {

        }


    }


    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    common.hideSoftKeyboard(getActivity());
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
