package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.sanganan.app.R;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.model.Category;
import com.sanganan.app.model.Remark;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 3/5/17.
 */

public class StatusChangeByAdmin extends BaseFragment {

    View view;
    RequestQueue requestQueue;
    Common common;
    TextView idNumber, idDesc, tvStatus, tvAssign, etStatus, etAssign, titletextView;
    ImageView btnSaveStatus;


    ArrayAdapter<String> statusAdapter;
    ArrayAdapter<String> helperAdapter;
    int positionAccordId = -1;
    int helperPositionInDD = -1;
    private String[] status_array = {"Assign", "Resolve", "Invalid"};
    private String[] status_array_UWC = {"Resolve", "Invalid"};//UWC=user_who_complained
    private String[] helper_array;
    ProgressDialog progressDialog;
    Bundle bundle;
    Bundle mainBundle;
    String complainId, UserIdWhoComplained, categoryId, statusName, assignedTo;
    ArrayList<Category> listOfHelpers = new ArrayList<>();

    Typeface ubuntuB, karlaB, karlaR, wsregular;
    boolean notAnAdmin = false;
    String array[];
    String StatusOfComplain = "";
    boolean isFromSearchComplain;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.edit_complain_status, container, false);

        initializeVariables(view);

        mainBundle = bundle.getBundle("mainBundle");
        complainId = bundle.getString("ID");
        UserIdWhoComplained = bundle.getString("cmpId");
        categoryId = bundle.getString("categoryId");
        statusName = bundle.getString("status");
        if (bundle.containsKey("assignedToName")) {
            assignedTo = bundle.getString("assignedToName");
        }
        if (bundle.containsKey("not_admin")) {
            notAnAdmin = true;
        }

        if (mainBundle.containsKey("fromComplainSearch")) {
            isFromSearchComplain = mainBundle.getBoolean("fromComplainSearch");
        }

        idNumber.setText(complainId);
        idNumber.setTypeface(ubuntuB);


        if (notAnAdmin) {
            statusAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, status_array_UWC);
        } else {
            statusAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, status_array);
        }


        etStatus.setText(statusName);
        if (statusName.equals("Assigned")) {
            etAssign.setText(assignedTo);
        }

        if (statusName.equals("Registered") || statusName.equals("Assigned")) {
            etAssign.setVisibility(View.VISIBLE);
            tvAssign.setVisibility(View.VISIBLE);
        } else {
            etAssign.setVisibility(View.GONE);
            tvAssign.setVisibility(View.GONE);
        }
//bug solved by below two lines check this if problrm persist
        if (notAnAdmin) {
            etAssign.setVisibility(View.GONE);
            tvAssign.setVisibility(View.GONE);
        }

        etStatus.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Select Status")
                        .setAdapter(statusAdapter, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                String statusChanged = "";
                                if (notAnAdmin) {
                                    statusChanged = status_array_UWC[which].toString();
                                } else {
                                    statusChanged = status_array[which].toString();
                                }
                                etStatus.setText(statusChanged);
                                if (statusChanged.equals("Assign")) {
                                    etAssign.setVisibility(View.VISIBLE);
                                    tvAssign.setVisibility(View.VISIBLE);
                                    methodGetHelpers();
                                } else {
                                    etAssign.setVisibility(View.GONE);
                                    tvAssign.setVisibility(View.GONE);
                                }
                                positionAccordId = which;
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });

        etAssign.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (listOfHelpers.size() > 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Select Helper")
                            .setAdapter(helperAdapter, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    etAssign.setText(helper_array[which].toString());
                                    helperPositionInDD = which;
                                    dialog.dismiss();
                                }
                            }).create().show();

                } else {
                    common.showShortToast("No helpers added");
                }
            }
        });


        if (common.isNetworkAvailable()) {
            if (listOfHelpers.size() == 0) {
                getHelpers();
            }
        }

        btnSaveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (common.isNetworkAvailable()) {
                    if(positionAccordId!=-1) {
                        changeStatus();
                    }else{
                        common.showShortToast("Choose a status ");
                    }
                } else {
                    common.showShortToast("No internet...!");
                }
            }
        });


        return view;
    }


    private void methodGetHelpers() {
        if (common.isNetworkAvailable()) {
            if (listOfHelpers.size() == 0) {
                getHelpers();
            }
        }
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
        bundle = getArguments();
        idNumber = (TextView) view.findViewById(R.id.idNumber);
        idDesc = (TextView) view.findViewById(R.id.idDesc);
        idDesc.setTypeface(karlaB);
        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        tvStatus.setTypeface(karlaB);
        tvAssign = (TextView) view.findViewById(R.id.tvAssign);
        tvAssign.setTypeface(karlaB);
        etStatus = (TextView) view.findViewById(R.id.etStatus);
        etStatus.setTypeface(wsregular);
        etAssign = (TextView) view.findViewById(R.id.etAssign);
        etAssign.setTypeface(wsregular);
        btnSaveStatus = (ImageView) view.findViewById(R.id.btnSaveStatus);

    }


    private void changeStatus() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();


        String uri = Constants.BaseUrl + "compstatuschange";
        try {


            if (notAnAdmin) {
                array = status_array_UWC;
            } else {
                array = status_array;
            }

            switch (array[positionAccordId]) {
                case "Assign":
                    StatusOfComplain = "1";
                    break;
                case "Resolve":
                    StatusOfComplain = "2";
                    break;
                case "Invalid":
                    StatusOfComplain = "4";
                    break;
            }

            JSONObject json = new JSONObject();
            json.put("ComplainByID", UserIdWhoComplained);
            if (StatusOfComplain.equals("1") && helperPositionInDD != -1) {
                json.put("AssignedName", listOfHelpers.get(helperPositionInDD).getName());
                json.put("AssignedTo", listOfHelpers.get(helperPositionInDD).getIdCategory());
                json.put("AssignedBy", common.getStringValue(Constants.ResidentRWAID));
            }
            json.put("Status", StatusOfComplain);
            json.put("ComplaintID", complainId);


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedDatachangeStatus(response, StatusOfComplain);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            VolleyLog.d("error", error.getMessage());
                            common.showShortToast("server error");

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

    void parsedDatachangeStatus(JSONObject json, String statusComplain) {

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        String status = "";
        try {
            status = json.optString("Status");

            if (status.equals("1")) {
                getActivity().onBackPressed();
                getActivity().onBackPressed();
                updateComplainsFragment(statusComplain);
                Constants.isComplainStatusChangedOrEdited = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateComplainsFragment(String statusComplain) {


        mainBundle.putString("assignedby", common.getStringValue(Constants.ResidentRWAID));
        if (helperPositionInDD != -1) {
            mainBundle.putString("assignedto", listOfHelpers.get(helperPositionInDD).getName());
        } else {
            mainBundle.putString("assignedto", "");
        }
        mainBundle.putString("Status", statusComplain);

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
    }


    private void getHelpers() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        try {

            String uri = Constants.BaseUrl + "assignstaff?Id=" + common.getStringValue("ID") + "&type=" + categoryId;

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, uri, new JSONObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedDataCategory(response);
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
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void parsedDataCategory(JSONObject response) {
        String status = "";
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        try {
            status = response.optString("Status");
            if (status.equals("1")) {
                JSONArray array = response.getJSONArray("list");
                listOfHelpers.clear();
                for (int i = 0; i < array.length(); i++) {
                    Category category = new Category();///used temp as both fields are there in category class later make a Helper class and replace
                    JSONObject object = array.getJSONObject(i);
                    String id = object.optString("ID");
                    String name = object.optString("Name");
                    category.setIdCategory(id);
                    category.setName(name);

                    listOfHelpers.add(category);
                }
            }

            if (listOfHelpers.size() > 0) {
                helper_array = new String[listOfHelpers.size()];
                for (int j = 0; j < listOfHelpers.size(); j++) {
                    helper_array[j] = listOfHelpers.get(j).getName();
                }
                helperAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, helper_array);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
