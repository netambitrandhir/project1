package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationMode;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sanganan.app.R;
import com.sanganan.app.activities.AnyNotificationActivity;
import com.sanganan.app.adapters.ListPollAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.InsertToMangoDb;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.Poll;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PollingDetailsFragment extends BaseFragment {

    View view;
    Common common;
    RequestQueue requestQueue;
    Typeface ubuntuB, uMedium, wsRegular, wsBold;
    TextView DetailsBy, description, go;
    String detailByText1, descText, questionId, userId;
    ImageView remove, reportasspam;
    ProgressDialog progressDialog;
    String responseID, yesNbr = "0", noNbr = "0";
    TextView noText, textCheckbox, textCheckbox1, addedby, yesText;
    CheckBox checkbox, checkbox1;
    RelativeLayout checkboxContainer, percentageContainer;
    ProgressBar progressBar1, progressBar2;
    int choiceState = 0;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pollingdetailsfragment, container, false);

        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        uMedium = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-M.ttf");
        wsRegular = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");
        wsBold = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Bold.ttf");

        TextView title = (TextView) view.findViewById(R.id.textViewtitle);
        title.setTypeface(ubuntuB);

        RelativeLayout detailsByLayout1 = (RelativeLayout) view.findViewById(R.id.detailsByLayout);

        DetailsBy = (TextView) view.findViewById(R.id.DetailsBy);
        DetailsBy.setTypeface(uMedium);
        description = (TextView) view.findViewById(R.id.description);
        description.setTypeface(wsRegular);
        remove = (ImageView) view.findViewById(R.id.remove);
        reportasspam = (ImageView) view.findViewById(R.id.reportasspam);
        go = (TextView) view.findViewById(R.id.go);
        go.setTypeface(uMedium);


        textCheckbox = (TextView) view.findViewById(R.id.textCheckbox);
        textCheckbox.setTypeface(wsBold);
        textCheckbox1 = (TextView) view.findViewById(R.id.textCheckbox1);
        textCheckbox1.setTypeface(wsBold);
        addedby = (TextView) view.findViewById(R.id.addedby);
        addedby.setVisibility(View.GONE);
        yesText = (TextView) view.findViewById(R.id.yestext);
        yesText.setTypeface(wsBold);
        noText = (TextView) view.findViewById(R.id.notext);
        noText.setTypeface(wsBold);

        checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        checkbox1 = (CheckBox) view.findViewById(R.id.checkbox1);

        checkboxContainer = (RelativeLayout) view.findViewById(R.id.checkboxContainer);
        percentageContainer = (RelativeLayout) view.findViewById(R.id.percentageContainer);
        progressBar1 = (ProgressBar) view.findViewById(R.id.progressBarYes);
        progressBar2 = (ProgressBar) view.findViewById(R.id.progressBarNo);

        Bundle bundle = getArguments();

        detailByText1 = bundle.getString("name1") + ", " + bundle.getString("flat1");
        descText = bundle.getString("question");
        questionId = bundle.getString("questionId");
        responseID = bundle.getString("response");
        if (responseID.equalsIgnoreCase("1")) {
            yesNbr = bundle.getString("yesNbr");
            noNbr = bundle.getString("noNbr");
        }
        questionId = bundle.getString("questionId");
        userId = bundle.getString("UserID");

        if (common.getStringValue(Constants.id).equals(userId)) {
            reportasspam.setVisibility(View.GONE);
        }else reportasspam.setVisibility(View.VISIBLE);


        if (responseID.equalsIgnoreCase("0")) {
            checkboxContainer.setVisibility(View.VISIBLE);
            percentageContainer.setVisibility(View.GONE);
        } else {
            checkboxContainer.setVisibility(View.GONE);
            percentageContainer.setVisibility(View.VISIBLE);
            int yesPoll = Integer.parseInt(yesNbr);
            int noPoll = Integer.parseInt(noNbr);
            int total = yesPoll + noPoll;
            int yesPercent = (yesPoll * 100) / total;
            yesText.setText("    " + yesPercent + "% " + "YES (" + yesPoll + " votes)");
            noText.setText("    " + (100 - yesPercent) + "% " + "NO (" + noPoll + " votes)");
            progressBar1.setProgress(yesPercent);
            progressBar2.setProgress(100 - yesPercent);

            // 80% YES (96 votes)

        }


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        Bundle logBundle = new Bundle();
        logBundle.putString("society_id", common.getStringValue("ID"));
        logBundle.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("poll_details", logBundle);


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

        reportasspam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Instabug.setUserEmail(common.getStringValue(Constants.email));
                Instabug.setUserData("Poll Detail Page  " + "Poll ID : " + questionId + "UserID who reported : "
                        + common.getStringValue(Constants.id) + " Society ID : " + common.getStringValue("ID"));
                Instabug.invoke(InstabugInvocationMode.PROMPT_OPTION);

            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (common.isNetworkAvailable()) {
                    getData();
                } else {
                    common.showShortToast("No internet...!!");
                }
            }
        });


        if (userId.equalsIgnoreCase(common.getStringValue(Constants.id))) {
            remove.setVisibility(View.VISIBLE);
        }

        DetailsBy.setText(detailByText1);
        description.setText(descText);


        checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    choiceState = 1;
                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
                    Bundle logBundle = new Bundle();
                    logBundle.putString("society_id", common.getStringValue("ID"));
                    logBundle.putString("user_id", common.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("poll_yes", logBundle);

                    getDataPoles();

                }
            }
        });

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    choiceState = 2;
                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
                    Bundle logBundle = new Bundle();
                    logBundle.putString("society_id", common.getStringValue("ID"));
                    logBundle.putString("user_id", common.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("poll_no", logBundle);

                    getDataPoles();
                }
            }
        });


        return view;
    }


    public void getData() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String uri = Constants.BaseUrl + "removepoles";
        try {
            JSONObject json = new JSONObject();
            json.put("QuestionId", questionId);
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
            progressDialog.dismiss();

            status = json.optString("Status");
            message = json.optString("Poles");
            common.showShortToast(message);
            if (status.equalsIgnoreCase("1")) {
                Constants.isAnyPollCreatedOrRemoved = true;
                InsertToMangoDb insertToMangoDb = new InsertToMangoDb(getActivity(), "poll", questionId, "", "");
                insertToMangoDb.deleteDataFromServer();
                Constants.isAnyNewPostAdded = true;
                getActivity().onBackPressed();
            }

        } catch (Exception e) {

        }


    }


    private void getDataPoles() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String uri = Constants.BaseUrl + "polestatus";
        try {
            JSONObject json = new JSONObject();
            json.put("RespondentId", common.getStringValue(Constants.ResidentRWAID));
            json.put("QuestionId", questionId);
            if (choiceState == 1) {
                json.put("ResponseOptionId", "1");
            } else {
                json.put("ResponseOptionId", "2");
            }
            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedDataPoles(response);
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

    void parsedDataPoles(JSONObject json) {
        try {

            String status = "";
            common.showShortToast(json.optString("Message"));
            Constants.isAnyPollCreatedOrRemoved = true;
            getDataPolling();

        } catch (Exception e) {

        }


    }

    private void getDataPolling() {


        String uri = Constants.BaseUrl + "postpoledetail";
        try {

            JSONObject json = new JSONObject();
            json.put("ID", questionId);
            json.put("ResidentRWAID", common.getStringValue(Constants.ResidentRWAID));

            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                JSONArray array = response.getJSONArray("ShowPoles");
                                JSONObject object = array.getJSONObject(0);
                                yesNbr = object.optString("YesNbr");
                                noNbr = object.optString("NoNbr");
                                parseFinalPollCalculation();

                            } catch (Exception e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            VolleyLog.d("error", error.getMessage());
                            common.showShortToast(error.getMessage());
                        }

                    }) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Country", "Singapore");
                    return headers;
                }
            };
            try {
                req.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(req);
            } catch (Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();

        }
    }

    private void parseFinalPollCalculation() {
        progressDialog.dismiss();
        checkboxContainer.setVisibility(View.GONE);
        percentageContainer.setVisibility(View.VISIBLE);
        int yesPoll = Integer.parseInt(yesNbr);
        int noPoll = Integer.parseInt(noNbr);
        int total = yesPoll + noPoll;
        int yesPercent = (yesPoll * 100) / total;
        yesText.setText("    " + yesPercent + "% " + "YES (" + yesPoll + " votes)");
        noText.setText("    " + (100 - yesPercent) + "% " + "NO (" + noPoll + " votes)");
        progressBar1.setProgress(yesPercent);
        progressBar2.setProgress(100 - yesPercent);
    }


}
