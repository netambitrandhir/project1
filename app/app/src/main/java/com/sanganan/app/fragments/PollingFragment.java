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
import com.sanganan.app.R;
import com.sanganan.app.adapters.AdminAdapter;
import com.sanganan.app.adapters.ListPollAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.ApprovedMember;
import com.sanganan.app.model.Poll;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Randhir  on 25/11/16.
 */

public class PollingFragment extends BaseFragment {

    View view;
    Common common;
    RequestQueue requestQueue;
    Typeface ubuntuB;
    ListView list_poll;
    ProgressDialog progressDialog;
    ArrayList<Poll> poleList = new ArrayList<>();
    ImageView startapoll, nopoll;
    ListPollAdapter pollAdapter;
    Bundle bundleAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.polling_layout, container, false);


        bundleAdapter = getArguments();

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        common = Common.getNewInstance(getActivity());
        common.hideKeyboard(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        nopoll = (ImageView) view.findViewById(R.id.nopoll);
        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        TextView title = (TextView) view.findViewById(R.id.textViewtitle);
        title.setTypeface(ubuntuB);

        startapoll = (ImageView) view.findViewById(R.id.startapoll);

        if (Constants.rolesGivenToUser.contains("Can_Start_Poll")) {
            startapoll.setVisibility(View.VISIBLE);
        } else {
            startapoll.setVisibility(View.GONE);
        }


        list_poll = (ListView) view.findViewById(R.id.list_poll);


        if (common.isNetworkAvailable()) {
            if (poleList.size() == 0 || Constants.isAnyPollCreatedOrRemoved) {
                getData();
            } else {
                pollAdapter = new ListPollAdapter(getActivity(), poleList);
                list_poll.setAdapter(pollAdapter);

            }
        } else {
            common.showShortToast("No internet...!!");
        }

        startapoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new StartAPoll();
                activityCallback.onButtonClick(fragment, false);
            }
        });


        list_poll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Fragment fragment = new PollingDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("questionId", poleList.get(position).getId());
                bundle.putString("name1", poleList.get(position).getFirstName());
                bundle.putString("flat1", poleList.get(position).getFlatNbr());
                bundle.putString("resident", poleList.get(position).getResidentRWAID());
                bundle.putString("question", poleList.get(position).getQuestion());
                bundle.putString("dateAdded", poleList.get(position).getDateAdded());
                bundle.putString("expratoin", poleList.get(position).getExpirationDt());
                bundle.putString("isActive", poleList.get(position).getIsActive());
                bundle.putString("response", poleList.get(position).getResponseID());
                bundle.putString("UserID", poleList.get(position).getUserID());

                if (poleList.get(position).getResponseID().equalsIgnoreCase("1")) {
                    bundle.putString("yesNbr", poleList.get(position).getYesNbr());
                    bundle.putString("noNbr", poleList.get(position).getNoNbr());
                }
                fragment.setArguments(bundle);
                activityCallback.onButtonClick(fragment, false);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.isAnyPollCreatedOrRemoved = false;
    }

    public void getData() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String uri = Constants.BaseUrl + "showpoles";
        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));
            json.put("ResidentRWAID", common.getStringValue(Constants.ResidentRWAID));

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
        try {
            String status = "";
            progressDialog.dismiss();

            JSONArray jsonArray = json.getJSONArray("ShowPoles");
            status = json.optString("Status");
            poleList.clear();
            if (status.equalsIgnoreCase("1")) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Poll polls = new Poll();
                    polls.setId(object.optString("Id"));
                    polls.setResidentRWAID(object.optString("ResidentRWAID"));
                    polls.setQuestion(common.funConvertBase64ToString(object.optString("Question")));

                    String dateAdded = object.optString("DateAdded");
                    if (!dateAdded.isEmpty()) {
                        dateAdded = common.convertFromUnix3(dateAdded);
                    }
                    polls.setDateAdded(dateAdded);

                    polls.setExpirationDt(object.optString("ExpirationDt"));
                    polls.setIsActive(object.optString("IsActive"));
                    polls.setFirstName(object.optString("FirstName"));


                    polls.setFlatNbr(object.optString("FlatNbr"));
                    JSONArray flatArr = object.getJSONArray("FlatNbr");
                    String flatString = "";
                    if (flatArr.length() > 0) {
                        for (int k = 0; k < flatArr.length(); k++) {
                            JSONObject flatObject = flatArr.getJSONObject(0);
                            flatString = flatString + flatObject.optString("FlatNbr") + ",";
                        }

                        flatString = flatString.substring(0, flatString.length() - 1);
                    }

                    polls.setFlatNbr(flatString);


                    polls.setResponseID(object.optString("Ispoll"));
                    polls.setUserID(object.optString("UserID"));
                    if (object.optString("Ispoll").equalsIgnoreCase("1")) {
                        polls.setYesNbr(object.optString("YesNbr"));
                        polls.setNoNbr(object.optString("NoNbr"));
                    }

                    poleList.add(polls);
                }
            }
        } catch (Exception e) {

        }


        pollAdapter = new ListPollAdapter(getActivity(), poleList);
        list_poll.setAdapter(pollAdapter);
        common.setStringValue(Constants.pollLastSeenTime, String.valueOf(common.getUnixTime()));


        if (poleList.size() == 0) {
            nopoll.setVisibility(View.VISIBLE);
            list_poll.setVisibility(View.GONE);
        } else {
            nopoll.setVisibility(View.GONE);
            list_poll.setVisibility(View.VISIBLE);
            if(bundleAdapter.containsKey("positionPolled")){
                list_poll.setSelection(bundleAdapter.getInt("positionPolled"));
            }
        }
    }


}