package com.sanganan.app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

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
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.fragments.BaseFragment;
import com.sanganan.app.fragments.NewsFeedNotification;
import com.sanganan.app.fragments.NewsFeedNotificationDetails;
import com.sanganan.app.interfaces.ToolbarListner;
import com.sanganan.app.model.GeneralNotification;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 7/3/17.
 */

public class NewsFeedNotificationActivity extends ActionBarActivity implements ToolbarListner {


    Context context;
    Common common;
    RequestQueue requestQueue;
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ProgressDialog progressDialog;
    String Id;

    private static long backPressed = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.any_notification_activity);


        context = NewsFeedNotificationActivity.this;
        common = Common.getNewInstance(this);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();


        Bundle bundle = getIntent().getExtras();

        String fragmentToReplace = bundle.getString("fragmnet");

        if (bundle.containsKey("ID")) {
            Id = bundle.getString("ID");
        }

        if (fragmentToReplace.equalsIgnoreCase("listNotificationNFeed")) {
            fragment = new NewsFeedNotification();
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction tx1 = fragmentManager.beginTransaction();
            tx1.replace(R.id.container_body_notification, fragment);
            tx1.addToBackStack("nfNotifyList");
            tx1.commit();
        } else if (fragmentToReplace.equalsIgnoreCase("newsfeed")) {
            getDataNotification();
        }

    }


    @Override
    public void onButtonClick(Fragment newfragment, Boolean isCommingBack) {

        Common.hideSoftKeyboard(this);
        fragment = (BaseFragment) newfragment;
        fragmentManager = getSupportFragmentManager();
        if (isCommingBack) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body_notification, fragment);

            try {
                fragmentTransaction.commit();
            } catch (IllegalStateException ignored) {
            }
        } else {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body_notification, fragment);
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit,
                    R.anim.slide_in_left, R.anim.slide_in_right);
            fragmentTransaction.replace(R.id.container_body_notification, fragment);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());

            try {
                fragmentTransaction.commit();
            } catch (IllegalStateException ignored) {
            }

        }
    }

    @Override
    public void onButtonClickNoBack(Fragment newfragment) {
        fragment = (BaseFragment) newfragment;
        fragmentManager = getSupportFragmentManager();
        fragment = (BaseFragment) newfragment;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit,
                R.anim.slide_in_left, R.anim.slide_in_right);
        fragmentTransaction.replace(R.id.container_body_notification, fragment);
        fragmentTransaction.commit();
    }


    private void getDataNotification() {


        progressDialog = new ProgressDialog(NewsFeedNotificationActivity.this);
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl + "appnotificationdetail";
        try {

            JSONObject json = new JSONObject();
            json.put("ID", Id);


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedDataNotification(response);
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

    void parsedDataNotification(JSONObject json) {

        if (progressDialog.isShowing())
            progressDialog.dismiss();

        try {
            String status = json.optString("Status");

            // common.setStringValue(Constants.notificationLastSeenTime, String.valueOf(common.getUnixTime()));

            if (status.equalsIgnoreCase("1")) {
                JSONArray jsonArray = json.getJSONArray("Appnotification");

                JSONObject jsonObject = jsonArray.getJSONObject(0);

                GeneralNotification notification = new GeneralNotification();


                String Title = "";
                String Text = "";
                try {
                    Title = common.funConvertBase64ToString(jsonObject.optString("Title"));
                    Text = common.funConvertBase64ToString(jsonObject.optString("Text"));
                } catch (Exception e) {
                    Title = jsonObject.optString("Title");
                    Text = jsonObject.optString("Text");
                }
                String Image1 = jsonObject.optString("Image1");
                String Image2 = jsonObject.optString("Image2");
                String Image3 = jsonObject.optString("Image3");
                String Datecreated = jsonObject.optString("DateCreated");

                Bundle notify = new Bundle();
                notify.putString("title", Title);
                notify.putString("date", Datecreated);
                notify.putString("detail", Text);
                notify.putString("image1", Image1);
                notify.putString("image2", Image2);
                notify.putString("image3", Image3);

                Fragment fragment = new NewsFeedNotificationDetails();
                fragment.setArguments(notify);
                onButtonClick(fragment, false);


            } else {
                finish();
            }


        } catch (Exception e) {
            finish();
        }

    }

    @Override
    public void onBackPressed() {

        fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 1) {
            android.support.v4.app.FragmentManager.BackStackEntry backEntry = getSupportFragmentManager()
                    .getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);

            fragmentManager.popBackStack();

        } else if (fragmentManager.getBackStackEntryCount() == 1) {
            finish();
        }
    }

}
