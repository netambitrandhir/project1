package com.sanganan.app.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.fragments.PollingFragment;
import com.sanganan.app.model.Poll;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 29/11/16.
 */

public class ListPollAdapter extends BaseAdapter {

    Context context;
    ArrayList<Poll> pollArrayList;
    LayoutInflater inflater;
    Typeface karlaBold, karlaRegular,wsBold,wsRegular;
    int choiceState = 0;
    Common common;
    String questionId;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    private FirebaseAnalytics mFirebaseAnalytics;

    public ListPollAdapter(Context context, ArrayList<Poll> pollArrayList) {
        this.context = context;
        this.pollArrayList = pollArrayList;
        common = Common.getNewInstance(context);
        requestQueue = VolleySingleton.getInstance(context).getRequestQueue();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public int getCount() {
        return pollArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return pollArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            karlaBold = Typeface.createFromAsset(context.getAssets(), "Karla-Bold.ttf");
            karlaRegular = Typeface.createFromAsset(context.getAssets(), "Karla-Regular.ttf");
            wsBold = Typeface.createFromAsset(context.getAssets(), "WorkSans-Bold.ttf");
            wsRegular = Typeface.createFromAsset(context.getAssets(), "WorkSans-Regular.ttf");

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_poll_view, null);

            viewHolder.polltext = (TextView) convertView.findViewById(R.id.polltext);
            viewHolder.polltext.setTypeface(wsRegular);
            viewHolder.textCheckbox = (TextView) convertView.findViewById(R.id.textCheckbox);
            viewHolder.textCheckbox.setTypeface(wsBold);
            viewHolder.textCheckbox1 = (TextView) convertView.findViewById(R.id.textCheckbox1);
            viewHolder.textCheckbox1.setTypeface(wsBold);
            viewHolder.addedby = (TextView) convertView.findViewById(R.id.addedby);
            viewHolder.addedby.setTypeface(wsRegular);
            viewHolder.yesText = (TextView) convertView.findViewById(R.id.yestext);
            viewHolder.yesText.setTypeface(wsBold);
            viewHolder.noText = (TextView) convertView.findViewById(R.id.notext);
            viewHolder.noText.setTypeface(wsBold);

            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            viewHolder.checkbox1 = (CheckBox) convertView.findViewById(R.id.checkbox1);

            viewHolder.checkboxContainer = (RelativeLayout) convertView.findViewById(R.id.checkboxContainer);
            viewHolder.percentageContainer = (RelativeLayout) convertView.findViewById(R.id.percentageContainer);
            viewHolder.progressBar1 = (ProgressBar)convertView.findViewById(R.id.progressBarYes);
            viewHolder.progressBar2 = (ProgressBar)convertView.findViewById(R.id.progressBarNo);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.polltext.setText(pollArrayList.get(position).getQuestion());


        String str = "by " + pollArrayList.get(position).getFirstName() + ", " + pollArrayList.get(position).getFlatNbr() + " on " + pollArrayList.get(position).getDateAdded();
        viewHolder.addedby.setText(str);

        if (pollArrayList.get(position).getResponseID().equalsIgnoreCase("0")) {
            viewHolder.checkboxContainer.setVisibility(View.VISIBLE);
            viewHolder.percentageContainer.setVisibility(View.GONE);
        } else {
            viewHolder.checkboxContainer.setVisibility(View.GONE);
            viewHolder.percentageContainer.setVisibility(View.VISIBLE);
            int yesPoll = Integer.parseInt(pollArrayList.get(position).getYesNbr());
            int noPoll = Integer.parseInt(pollArrayList.get(position).getNoNbr());
            int total = yesPoll + noPoll;
            int yesPercent = (yesPoll * 100) / total;
            viewHolder.yesText.setText("    " + yesPercent + "% " + "YES (" + yesPoll + " votes)");
            viewHolder.noText.setText("    " + (100 - yesPercent) + "% " + "NO (" + noPoll + " votes)");
            viewHolder.progressBar1.setProgress(yesPercent);
            viewHolder.progressBar2.setProgress(100-yesPercent);

            // 80% YES (96 votes)

        }


        viewHolder.checkbox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    choiceState = 1;
                    questionId = pollArrayList.get(position).getId();

                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                    Bundle logBundle = new Bundle();
                    logBundle.putString("society_id", common.getStringValue("ID"));
                    logBundle.putString("user_id", common.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("poll_yes", logBundle);

                    getData(position);

                }
            }
        });

        viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    choiceState = 2;
                    questionId = pollArrayList.get(position).getId();

                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                    Bundle logBundle = new Bundle();
                    logBundle.putString("society_id", common.getStringValue("ID"));
                    logBundle.putString("user_id", common.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("poll_no", logBundle);

                    getData(position);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {

        TextView noText, polltext, textCheckbox, textCheckbox1, addedby, yesText;
        CheckBox checkbox, checkbox1;
        RelativeLayout checkboxContainer, percentageContainer;
        ProgressBar progressBar1,progressBar2;
    }

    private void getData(final int position) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String uri = Constants.BaseUrl+"polestatus";
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
                                parsedData(response,position);
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

    void parsedData(JSONObject json, int position) {
        try {
            progressDialog.dismiss();
            String status = "";
            common.showShortToast(json.optString("Message"));

            ActionBarActivity activity = (ActionBarActivity) (context);
            Fragment fragment = new PollingFragment();
            activity.onBackPressed();
            Bundle bundle = new Bundle();
            bundle.putInt("positionPolled",position);
            FragmentManager fm = activity.getSupportFragmentManager();
            FragmentTransaction tx1 = fm.beginTransaction();
            tx1.replace(R.id.container_body, fragment);
            tx1.addToBackStack(fragment.getClass().getName().toString());
            tx1.commit();

        } catch (Exception e) {

        }


    }
}