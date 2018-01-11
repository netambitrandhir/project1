package com.sanganan.app.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sanganan.app.R;
import com.sanganan.app.adapters.FavouriteAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.GetFavouriteList;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 27/9/16.
 */
public class ForgotPasswordActivity extends Activity {

    TextView title;
    Typeface ubantuBold;

    EditText edOTPassword, edNewPassword, edRetypeNewPassword;
    Button btnSave;

    ProgressDialog progressDialog;
    Common common;
    RequestQueue requestQueue;
    Bundle bundle;
    String retypePass;
    String Pass;
    String otp;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);


        initializeVariables();

        edOTPassword = (EditText) findViewById(R.id.edOTPassword);
        edNewPassword = (EditText) findViewById(R.id.edNewPassword);
        edRetypeNewPassword = (EditText) findViewById(R.id.edRetypeNewPassword);
        btnSave = (Button) findViewById(R.id.btnSave);


        ubantuBold = Typeface.createFromAsset(this.getAssets(), "Ubuntu-B.ttf");
        title.setTypeface(ubantuBold);
        title.setText("Forgot Password");

        bundle = getIntent().getExtras();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                retypePass = edRetypeNewPassword.getText().toString();
                Pass = edNewPassword.getText().toString();
                otp = edOTPassword.getText().toString();

                if (Pass.equals(retypePass)) {
                    if (otp.equals(bundle.getString("Result"))) {
                        getData();
                    } else {
                        common.showShortToast("OTP not matching");
                    }
                } else {
                    common.showShortToast("Enter same password in both fields");
                }
            }
        });

    }


    private void initializeVariables() {

        title = (TextView) findViewById(R.id.titletextView);
        common = Common.getNewInstance(this);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

    }

    private void getData() {

        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        progressDialog.setMessage("Please wait Data is loading.");
        progressDialog.show();

        JSONObject json = new JSONObject();

        String uri = Constants.BaseUrl + "updateuserinfo";


        try {

            json.put("Password", edNewPassword.getText().toString());
            json.put("Phone", bundle.getString("number"));

            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                progressDialog.dismiss();

                                String s = response.toString();

                                String status = response.optString("Status");
                                String message = response.optString("Message");

                                common.showShortToast(message);
                                if (status.equalsIgnoreCase("1")) {
                                    finish();

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            VolleyLog.d("error", error.getMessage());
                            progressDialog.dismiss();

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
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


}
