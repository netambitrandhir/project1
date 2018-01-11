package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.adapters.CalloutListAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.CalloutData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 30/11/16.
 */

public class CallOut extends BaseFragment {


    View view;
    Common common;
    RequestQueue requestQueue;
    Typeface ubuntuB;
    ListView calloutlist;
    ImageView broadcast,help;
    CalloutListAdapter calloutListAdapter;
    ArrayList<CalloutData> listCallouts = new ArrayList<>();


    private FirebaseAnalytics mFirebaseAnalytics;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.callout_layout, container, false);
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        common = Common.getNewInstance(getActivity());
        common.hideKeyboard(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        TextView title = (TextView) view.findViewById(R.id.textViewtitle);
        title.setTypeface(ubuntuB);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        Bundle logBundleInitial = new Bundle();
        logBundleInitial.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleInitial.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("callout_clicked", logBundleInitial);


        calloutlist = (ListView) view.findViewById(R.id.calloutlist);
        broadcast = (ImageView) view.findViewById(R.id.broadcast);
        help = (ImageView) view.findViewById(R.id.question);


        if (common.isNetworkAvailable()) {
            if(listCallouts.size()==0 || Constants.isAnyCallOutCreatedOrRemoved) {
                getData();
            }
            else{
                calloutListAdapter = new CalloutListAdapter(getActivity(), listCallouts);
                calloutlist.setAdapter(calloutListAdapter);
            }
        } else {
            common.showShortToast("No internet...!!");
        }


        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),HelpCalloutActivity.class);
                startActivity(intent);
            }
        });


        broadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new BroadcastCallout();
                activityCallback.onButtonClick(fragment, false);
            }
        });


        calloutlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new CallOutDetails();
                Bundle bundle = new Bundle();
                bundle.putString("Id",listCallouts.get(position).getID());
                bundle.putString("senderId", listCallouts.get(position).getSenderID());
                bundle.putString("userId", listCallouts.get(position).getUserID());
                bundle.putString("name", listCallouts.get(position).getFirstName());
                bundle.putString("flat", listCallouts.get(position).getFlatNbr());
                bundle.putString("description", listCallouts.get(position).getDescription());
                fragment.setArguments(bundle);
                activityCallback.onButtonClick(fragment, false);
            }
        });

        return view;
    }

    private void getData() {

       common.showSpinner(getActivity());

        String uri = Constants.BaseUrl+"showcallout";
        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));

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
            common.hideSpinner();
            JSONArray jsonArray = json.getJSONArray("CallOuts");
            status = json.optString("Status");
            listCallouts.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                CalloutData calloutData = new CalloutData();
                String dateSent = object.optString("DateSent");
                dateSent = common.convertFromUnix2(dateSent);
                calloutData.setDateSent(dateSent);

                calloutData.setDescription(common.funConvertBase64ToString(object.optString("Description")));
                calloutData.setFirstName(object.optString("FirstName"));
                calloutData.setFlatNbr(object.optString("FlatNbr"));
                calloutData.setID(object.optString("ID"));
                calloutData.setUserID(object.optString("UserID"));
                calloutData.setSenderID(object.optString("SenderID"));

                listCallouts.add(calloutData);

            }

        } catch (Exception e) {

        }
        calloutListAdapter = new CalloutListAdapter(getActivity(), listCallouts);
        calloutlist.setAdapter(calloutListAdapter);
        common.setStringValue(Constants.calloutLastSeenTime, String.valueOf(common.getUnixTime()));
        common.hideSpinner();
    }

}
