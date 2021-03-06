package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by pranav on 15/12/16.
 */

public class UpdateAboutMe extends BaseFragment {

    View view;
    RequestQueue requestQueue;
    Common common;
    Typeface ubantuBold, karlaBold, karlaRegular;
    TextView title;
    ImageView done;
    EditText aboutMeText;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aboutme_edit_layout, container, false);

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        initializeVariables(view);
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        title.setTypeface(ubantuBold);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!aboutMeText.getText().toString().isEmpty()) {
                    getData();
                } else {
                    common.showShortToast("Enter about yourself");
                }
            }
        });


        return view;
    }

    private void initializeVariables(View view) {

        common = Common.getNewInstance(getActivity());
        ubantuBold = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        karlaBold = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");
        karlaRegular = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");

        title = (TextView) view.findViewById(R.id.titletextView);
        done = (ImageView) view.findViewById(R.id.done);
        aboutMeText = (EditText) view.findViewById(R.id.aboutmeText);
        aboutMeText.setTypeface(karlaRegular);
    }


    private void getData() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl+"updateuserinfo";
        try {
            JSONObject json = new JSONObject();
            json.put("ID", common.getStringValue(Constants.id));
            json.put("About", aboutMeText.getText().toString());


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

            String status = json.optString("Status");
            String messsage = json.optString("Message");

            JSONObject obj = json.getJSONObject("User");
            String aboutMeString = obj.optString("About");

            if (status.equalsIgnoreCase("1")) {
                common.setStringValue(Constants.aboutMe, aboutMeString);
                getActivity().onBackPressed();
            }

            common.showShortToast(messsage);


        } catch (Exception e) {

        }

        progressDialog.dismiss();


    }
}
