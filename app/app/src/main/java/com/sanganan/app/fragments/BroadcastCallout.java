package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
 * Created by pranav on 30/11/16.
 */

public class BroadcastCallout extends BaseFragment {
    View view;
    Common common;
    RequestQueue requestQueue;
    Typeface ubuntuB, ubuntuR;
    EditText calloutText;
    ImageView sendCallout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.broadcast_callout_layout, container, false);
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        ubuntuR = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-R.ttf");
        TextView title = (TextView) view.findViewById(R.id.textViewtitle);
        title.setTypeface(ubuntuB);
        calloutText = (EditText) view.findViewById(R.id.calloutText);
        calloutText.setTypeface(ubuntuR);
        sendCallout = (ImageView) view.findViewById(R.id.sendCallout);


        sendCallout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!calloutText.getText().toString().isEmpty()) {
                    if (common.isNetworkAvailable()) {
                        getData();
                    } else {
                        common.showShortToast("No internet...!!");
                    }
                } else {
                    common.showShortToast("Enter message to broadcast");
                }
            }
        });
        return view;
    }


    private void getData() {

        common.showSpinner(getActivity());

        String uri = Constants.BaseUrl + "callout";
        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));
            json.put("SenderID", common.getStringValue(Constants.ResidentRWAID));

            json.put("Description", common.StringToBase64StringConvertion(calloutText.getText().toString()));
            json.put("UserID", common.getStringValue(Constants.id));


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                String message = response.optString("Message");
                                String id = "";
                                common.hideSpinner();
                                String status = response.optString("Status");
///finalized key yesterday
                                if (status.equalsIgnoreCase("1")) {
                                    id = response.optString("calloutId");
                                    common.showShortToast(message);
                                    Constants.isAnyCallOutCreatedOrRemoved = true;
                                    InsertToMangoDb insertToMangoDb = new InsertToMangoDb(getActivity(), "callout", id, calloutText.getText().toString(), "");
                                    insertToMangoDb.insertDataToServer();
                                    Constants.isAnyNewPostAdded = true;
                                } else {
                                    common.showShortToast(message);
                                }

                                getActivity().onBackPressed();


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
