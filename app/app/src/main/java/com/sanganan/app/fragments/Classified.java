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
import com.sanganan.app.adapters.ClassifiedListAdapter;
import com.sanganan.app.adapters.ListPollAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.ClassifiedModel;
import com.sanganan.app.model.Poll;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 30/11/16.
 */

public class Classified extends BaseFragment {

    View view;
    Common common;
    RequestQueue requestQueue;
    Typeface ubuntuB;
    ListView list_classified;
    ImageView addyourclassified;
    ArrayList<ClassifiedModel> listClassified = new ArrayList<>();
    private FirebaseAnalytics mFirebaseAnalytics;
    ClassifiedListAdapter adapter;
    TextView textNoClassifiedTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.classified_layout, container, false);
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        TextView title = (TextView) view.findViewById(R.id.textViewtitle);
        title.setTypeface(ubuntuB);

        textNoClassifiedTV = (TextView)view.findViewById(R.id.textMessage);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        Bundle logBundleInitial = new Bundle();
        logBundleInitial.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleInitial.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("Classified_clicked", logBundleInitial);

        list_classified = (ListView) view.findViewById(R.id.list_classified);
        addyourclassified = (ImageView) view.findViewById(R.id.addyourclassified);

        if (Constants.rolesGivenToUser.contains("Can_Add_Classified")) {
            addyourclassified.setVisibility(View.VISIBLE);
        } else {
            addyourclassified.setVisibility(View.GONE);
        }

        if (common.isNetworkAvailable()) {
            if (listClassified.size() == 0 || Constants.isAnyClassifiedCreatedOrRemoved) {
                getData();
            } else {
                textNoClassifiedTV.setVisibility(View.GONE);
                adapter = new ClassifiedListAdapter(getActivity(), listClassified);
                list_classified.setAdapter(adapter);
            }
        } else {
            common.showShortToast("No internet...!!");
        }

        addyourclassified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent   = new Intent(getActivity(),AddClassified.class);
                startActivity(intent);
            }
        });

        list_classified.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new ClassifiedDetails();
                Bundle bundle = new Bundle();
                bundle.putString("classifiedId", listClassified.get(position).getId());
                bundle.putString("userId", listClassified.get(position).getUserID());
                bundle.putString("image1", listClassified.get(position).getImage1());
                bundle.putString("image2", listClassified.get(position).getImage2());
                bundle.putString("image3", listClassified.get(position).getImage3());
                bundle.putString("name", listClassified.get(position).getFirstName());
                bundle.putString("flat", listClassified.get(position).getFlatNbr());
                bundle.putString("addedOn", listClassified.get(position).getAddedOn());
                bundle.putString("addedBy", listClassified.get(position).getAddedBy());
                bundle.putString("title", listClassified.get(position).getTitle());
                bundle.putString("desc", listClassified.get(position).getDescription());
                fragment.setArguments(bundle);
                activityCallback.onButtonClick(fragment, false);
            }
        });


        return view;
    }

    private void getData() {

        common.showSpinner(getActivity());

        String uri = Constants.BaseUrl + "classified";
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

            JSONArray jsonArray = json.getJSONArray("Show classified");
            listClassified.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                ClassifiedModel classifiedModel = new ClassifiedModel();
                classifiedModel.setId(object.optString("Id"));
                try {
                    classifiedModel.setTitle(common.funConvertBase64ToString(object.optString("Title")));
                    classifiedModel.setDescription(common.funConvertBase64ToString(object.optString("Description")));
                }catch(Exception e){
                    classifiedModel.setTitle(object.optString("Title"));
                    classifiedModel.setDescription(object.optString("Description"));
                    e.printStackTrace();
                }
                classifiedModel.setImage1(object.optString("Image1"));
                classifiedModel.setImage2(object.optString("Image2"));
                classifiedModel.setImage3(object.optString("Image3"));
                classifiedModel.setAddedBy(object.optString("AddedBy"));
                classifiedModel.setFlatNbr(object.optString("FlatNbr"));
                classifiedModel.setFirstName(object.optString("FirstName"));
                classifiedModel.setUserID(object.optString("UserID"));
                String addedOn = object.optString("AddedOn");
                addedOn = common.convertFromUnix2(addedOn);
                classifiedModel.setAddedOn(addedOn);
                classifiedModel.setExpiryDt(object.optString("ExpiryDt"));
                listClassified.add(classifiedModel);
            }
            status = json.optString("Status");

        } catch (Exception e) {

        }

        adapter = new ClassifiedListAdapter(getActivity(), listClassified);
        list_classified.setAdapter(adapter);
        common.setStringValue(Constants.classifiedLastSeenTime, String.valueOf(common.getUnixTime()));


        if (listClassified.size() > 0) {
            textNoClassifiedTV.setVisibility(View.GONE);
            list_classified.setVisibility(View.VISIBLE);

        } else {
            list_classified.setVisibility(View.GONE);
            textNoClassifiedTV.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        if (common.isNetworkAvailable()) {
            if (Constants.isAnyClassifiedCreatedOrRemoved) {
                getData();
            }
        } else {
            common.showShortToast("No internet...!!");
        }
    }
}
