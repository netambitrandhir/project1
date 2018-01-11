package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationMode;
import com.sanganan.app.R;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.InsertToMangoDb;
import com.sanganan.app.common.VolleySingleton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 30/11/16.
 */

public class CallOutDetails extends BaseFragment {

    View view;
    Common common;
    RequestQueue requestQueue;
    Typeface ubuntuB, uMedium, wsRegular;
    TextView DetailsBy, description, go;
    String detailByText, descText, userId;
    ImageView remove, reportspam;
    String Id, senderID;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.callout_details, container, false);
        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        uMedium = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-M.ttf");
        wsRegular = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");

        TextView title = (TextView) view.findViewById(R.id.textViewtitle);
        title.setTypeface(ubuntuB);

        RelativeLayout detailsByLayout1 = (RelativeLayout) view.findViewById(R.id.detailsByLayout);

        DetailsBy = (TextView) view.findViewById(R.id.DetailsBy);
        DetailsBy.setTypeface(uMedium);
        description = (TextView) view.findViewById(R.id.description);
        description.setTypeface(wsRegular);
        remove = (ImageView) view.findViewById(R.id.remove);
        reportspam = (ImageView) view.findViewById(R.id.reportspam);
        go = (TextView) view.findViewById(R.id.go);
        go.setTypeface(uMedium);


        Bundle bundle = getArguments();

        Id = bundle.getString("Id");
        senderID = bundle.getString("senderId");


        detailsByLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                Fragment fragment = new DetailsNeighbourFragment();
                bundle1.putString("ID", userId);
                fragment.setArguments(bundle1);
                activityCallback.onButtonClick(fragment, false);
            }
        });

        detailByText = bundle.getString("name") + ", " + bundle.getString("flat");
        descText = bundle.getString("description");
        userId = bundle.getString("userId");


        if (userId.equalsIgnoreCase(common.getStringValue(Constants.id))) {
            remove.setVisibility(View.VISIBLE);
            reportspam.setVisibility(View.GONE);
        }


        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();

            }
        });

        reportspam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Instabug.setUserEmail(common.getStringValue(Constants.email));
                Instabug.setUserData("Callout Detail Page  " + "Callout ID : " + Id + "UserID who reported : "
                        + common.getStringValue(Constants.id) + " Society ID : " + common.getStringValue("ID"));
                Instabug.invoke(InstabugInvocationMode.PROMPT_OPTION);
            }
        });

        DetailsBy.setText(detailByText);
        description.setText(descText);


        return view;
    }


    private void getData() {

        common.showSpinner(getActivity());

        String uri = Constants.BaseUrl + "removecallout";
        try {
            JSONObject json = new JSONObject();
            json.put("SenderID", senderID);
            json.put("ID", Id);

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
        try {
            String status = "";
            String message = "";
            common.hideSpinner();

            status = json.optString("Status");
            message = json.optString("CallOuts");

            if (status.equalsIgnoreCase("1")) {
                InsertToMangoDb insertToMangoDb = new InsertToMangoDb(getActivity(), "callout", Id, "", "");
                insertToMangoDb.deleteDataFromServer();
                Constants.isAnyNewPostAdded = true;
            }

            common.showShortToast(message);
            if (status.equalsIgnoreCase("1")) {
                Constants.isAnyCallOutCreatedOrRemoved = true;
                getActivity().onBackPressed();
            }

        } catch (Exception e) {

        }

    }

}
