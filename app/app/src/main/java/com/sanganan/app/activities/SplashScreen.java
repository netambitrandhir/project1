package com.sanganan.app.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

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

import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import java.util.*;

import android.widget.Toast;

import org.json.JSONObject;


public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_SHOW_TIME = 500;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 10;
    ProgressDialog progressDialog;
    String version = "";
    Common common;
    RequestQueue requestQueue;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitysplash_screen);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        common = Common.getNewInstance(SplashScreen.this);
        context = SplashScreen.this;

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            //vers= "1.0";
        }

        if (common.isNetworkAvailable()) {
            checkVersion();
        } else {
            common.showLongToast("no internet");
        }


    }


    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readinternal = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int callphone = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (readinternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (write != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (callphone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        } else {
            new BackgroundSplashTask().execute();
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        Intent intent = new Intent(SplashScreen.this, StartSearchPage.class);
        Bundle bundle = new Bundle();
        bundle.putString("fromclass", SplashScreen.class.getName());
        intent.putExtras(bundle);
        startActivity(intent);
        finish();

        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {

            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {

                    if (permissions[i].equals(Manifest.permission.GET_ACCOUNTS)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "accounts granted");

                        }
                    } else if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "storage granted");

                        }
                    } else if (permissions[i].equals(Manifest.permission.CALL_PHONE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "call granted");

                        }
                    } else if (permissions[i].equals(Manifest.permission.RECEIVE_SMS)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "sms granted");

                        }
                    } else if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("msg", "location granted");

                        }
                    } else if (permissions[i].equals(Manifest.permission.CALL_PHONE)) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            Log.e("calling", "call granted");

                        }
                    }
                }
            }
        }


    }

    private class BackgroundSplashTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            SharedPreferences spf = getSharedPreferences(Constants.preference, MODE_PRIVATE);
            boolean isLoggedIn = spf.getBoolean(Constants.isLoggedIn, false);

            try {
                Thread.sleep(SPLASH_SHOW_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        boolean isLoggedIn;

        @Override
        protected void onPostExecute(Object o) {
            try {
                super.onPostExecute(o);

                SharedPreferences preferences = getSharedPreferences(Constants.preference, Context.MODE_PRIVATE);

                isLoggedIn = preferences.getBoolean(Constants.isLoggedIn, false);

                if (isLoggedIn) {
                    Intent i = new Intent(SplashScreen.this,
                            MainHomePageActivity.class);

                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                        if (bundle.containsKey("body") || bundle.containsKey("UrHd2")) {
                            String ID = bundle.getString("body");
                            String page = bundle.getString("UrHd2");

                            Bundle bundleToNextActivity = new Bundle();
                            bundleToNextActivity.putString("ID", ID);
                            if (page.equalsIgnoreCase("Society Notification")) {
                                bundleToNextActivity.putString("fragmnet", "notification");
                            } else if (page.equalsIgnoreCase("Approval message")) {
                                bundleToNextActivity.putString("fragmnet", "approval");
                                Common common = Common.getNewInstance(SplashScreen.this);
                                if (common.getStringValue(Constants.approvalStatus).equalsIgnoreCase("N") || common.getStringValue(Constants.approvalStatus).equalsIgnoreCase("P")) {
                                    common.setStringValue(Constants.approvalStatus, "Y");
                                }
                            } else if (page.equalsIgnoreCase("Society CallOuts")) {
                                bundleToNextActivity.putString("fragmnet", "callout");
                            } else if (page.equalsIgnoreCase("Complaint Status")) {
                                bundleToNextActivity.putString("fragmnet", "complaint");
                            } else if (page.equalsIgnoreCase("mynukad helper attendance")) {
                                bundleToNextActivity.putString("fragmnet", "attendence");
                            } else if (page.equalsIgnoreCase("mynukad " + common.getStringValue(Constants.userRwaName) + " chat")) {
                                bundleToNextActivity.putString("fragmnet", "openchat");
                            } else if (page.equalsIgnoreCase("mynukad Classified")) {
                                bundleToNextActivity.putString("fragmnet", "classified");
                            } else if (page.equalsIgnoreCase("Society Photo Gallery")) {
                                bundleToNextActivity.putString("fragmnet", "gallery");
                            } else if (page.equalsIgnoreCase("mynukad Poll")) {
                                bundleToNextActivity.putString("fragmnet", "polling");
                            } else if (page.equalsIgnoreCase("New Registration")) {
                                bundleToNextActivity.putString("fragmnet", "registration");
                            }else {
                                bundleToNextActivity.putString("fragmnet", "newsfeed");
                            }
                            i.putExtras(bundleToNextActivity);
                        }
                    }
                    ///manipulated code to set society name same as already saved
                    common.setStringValue("ID", common.getStringValue(Constants.userRwa));
                    common.setStringValue("SocietyName", common.getStringValue(Constants.userRwaName));
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashScreen.this,
                            StartSearchPage.class);

                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                        if (bundle.containsKey("body") || bundle.containsKey("UrHd2")) {

                            String ID = bundle.getString("body");
                            String page = bundle.getString("UrHd2");
                            Bundle bundleToNextActivity = new Bundle();
                            bundleToNextActivity.putString("fromclass", SplashScreen.class.getName());
                            bundleToNextActivity.putString("ID", ID);
                            if (page.equalsIgnoreCase("Society Notification")) {
                                bundleToNextActivity.putString("fragmnet", "notification");
                            } else if (page.equalsIgnoreCase("Approval message")) {
                                bundleToNextActivity.putString("fragmnet", "approval");
                                Common common = Common.getNewInstance(SplashScreen.this);
                                if (common.getStringValue(Constants.approvalStatus).equalsIgnoreCase("N") || common.getStringValue(Constants.approvalStatus).equalsIgnoreCase("P")) {
                                    common.setStringValue(Constants.approvalStatus, "Y");
                                }
                            } else if (page.equalsIgnoreCase("Society CallOuts")) {
                                bundleToNextActivity.putString("fragmnet", "callout");
                            } else if (page.equalsIgnoreCase("Complaint Status")) {
                                bundleToNextActivity.putString("fragmnet", "complaint");
                            } else if (page.equalsIgnoreCase("mynukad helper attendance")) {
                                bundleToNextActivity.putString("fragmnet", "attendence");
                            } else if (page.equalsIgnoreCase("mynukad " + common.getStringValue(Constants.userRwaName) + " chat")) {
                                bundleToNextActivity.putString("fragmnet", "openchat");
                            }else if (page.equalsIgnoreCase("mynukad Classified")) {
                                bundleToNextActivity.putString("fragmnet", "classified");
                            } else if (page.equalsIgnoreCase("Society Photo Gallery")) {
                                bundleToNextActivity.putString("fragmnet", "gallery");
                            } else if (page.equalsIgnoreCase("mynukad Poll")) {
                                bundleToNextActivity.putString("fragmnet", "polling");
                            }
                            else {
                                bundleToNextActivity.putString("fragmnet", "newsfeed");
                            }
                           /* if (bundle != null) {
                                if (bundle.containsKey("ID") && bundle.containsKey("fragmnet")) {
                                    if (!bundle.getString("fragmnet").equalsIgnoreCase("approval")) {
                                        Intent intent = new Intent(this, AnyNotificationActivity.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                    else{

                                    }
                                }
                            }*/
                            i.putExtras(bundleToNextActivity);
                        }
                    } else {
                        Bundle bundleX = new Bundle();
                        bundleX.putString("fromclass", SplashScreen.class.getName());
                        i.putExtras(bundleX);
                    }
                    startActivity(i);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }


    private void checkVersion() {


        progressDialog = new ProgressDialog(SplashScreen.this);
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl + "versioncheck";
        try {

            JSONObject json = new JSONObject();

            json.put("OSVersion", "Android");
            json.put("APPVersion", version);


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                responseAfter(response);

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

    private void responseAfter(JSONObject response) {

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        String message = response.optString("message");
        int status = response.optInt("Status");

        switch (status) {
            case 0: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkAndRequestPermissions();

                } else {
                    new BackgroundSplashTask().execute();

                }
                break;
            }
            case 1: {
                AlertBoxForImagePic(message);
                break;
            }
            case 2: {
                AlertBoxForImagePic1(message);
                break;
            }
            default:
                break;
        }

    }


    public void AlertBoxForImagePic(String message) {


        new AlertDialog.Builder(context)
                .setTitle("Please Wait")
                .setMessage(message)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }


                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    checkAndRequestPermissions();

                                } else {
                                    new BackgroundSplashTask().execute();

                                }

                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();

    }

    public void AlertBoxForImagePic1(String message) {


        new AlertDialog.Builder(context)
                .setTitle("Please Wait")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();

    }
}


