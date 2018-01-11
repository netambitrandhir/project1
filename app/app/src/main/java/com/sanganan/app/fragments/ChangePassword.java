package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.sanganan.app.interfaces.DrawerLocker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 11/1/17.
 */

public class ChangePassword extends BaseFragment {

    View view;
    Common common;
    RequestQueue requestQueue;
    Typeface worksans_regular, ubuntuB;
    EditText oldpassword, newpassword, retypepassword;
    ImageView cancle, save;
    TextView textViewtitle;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.changepassword, container, false);
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        worksans_regular = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");
        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");

        oldpassword = (EditText) view.findViewById(R.id.oldpassword);
        newpassword = (EditText) view.findViewById(R.id.newpassword);
        retypepassword = (EditText) view.findViewById(R.id.retypepassword);

        oldpassword.setTypeface(worksans_regular);
        newpassword.setTypeface(worksans_regular);
        retypepassword.setTypeface(worksans_regular);

        cancle = (ImageView) view.findViewById(R.id.cancle);
        save = (ImageView) view.findViewById(R.id.save);

        textViewtitle = (TextView) view.findViewById(R.id.textViewtitle);
        textViewtitle.setTypeface(ubuntuB);

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (common.isNetworkAvailable()) {
                    String newP = newpassword.getText().toString();
                    String oldP = oldpassword.getText().toString();
                    String renewP = retypepassword.getText().toString();
                    if(!newP.isEmpty()&&!oldP.isEmpty()&&!renewP.isEmpty()) {
                        if (newP.equalsIgnoreCase(renewP)) {
                            getData();
                        } else {
                            common.showShortToast("New and confirm password does not match");
                        }
                    }
                    else if(newP.isEmpty()){
                        common.showShortToast("Please enter new password");
                    }
                    else if(renewP.isEmpty()){
                        common.showShortToast("Please retype new password");
                    }
                    else{
                        common.showShortToast("Nothing to save");
                    }
                } else common.showShortToast("no internet...!!");
            }
        });

        return view;
    }

    private void getData() {

       common.showSpinner(getActivity());

        String uri = Constants.BaseUrl+"updateuserinfo";
        try {
            JSONObject json = new JSONObject();
            json.put("OldPass", oldpassword.getText().toString());
            json.put("NewPass", newpassword.getText().toString());
            json.put("ID", common.getStringValue(Constants.id));


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                               common.hideSpinner();
                                String s = response.toString();
                                String status = response.optString("Status");
                                String message = response.optString("Message");

                                if (status.equalsIgnoreCase("1")) {
                                    common.showShortToast(message);
                                    getActivity().onBackPressed();
                                } else {
                                    common.showShortToast("Wrong old password entered  try again");
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                common.hideSpinner();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            VolleyLog.d("error", error.getMessage());
                            common.hideSpinner();

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
                common.hideSpinner();
            }
        } catch (Exception e) {
            e.printStackTrace();
            common.hideSpinner();

        }
    }

}
