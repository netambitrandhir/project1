package com.sanganan.app.fragments;

import android.app.ProgressDialog;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 13/12/16.
 */

public class SendEmailFragment extends BaseFragment {
    View view;
    ImageView sendButton;
    Common common;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    EditText bodyField, subjectField;
    String emailID, message1, subject,firstName;
    Typeface ubuntuB, karlaB, karlaR;
    TextView subjectText, bodyText, tvHeaderTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_email, container, false);

        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        sendButton = (ImageView) view.findViewById(R.id.sendButton);
        bodyField = (EditText) view.findViewById(R.id.bodyField);
        subjectField = (EditText) view.findViewById(R.id.subjectField);
        subjectText = (TextView) view.findViewById(R.id.subjectText);
        bodyText = (TextView) view.findViewById(R.id.bodyText);
        tvHeaderTitle = (TextView) view.findViewById(R.id.tvHeaderTitle);


        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        karlaB = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");
        karlaR = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");

        subjectText.setTypeface(karlaR);
        bodyText.setTypeface(karlaR);
        tvHeaderTitle.setTypeface(ubuntuB);
        bodyField.setTypeface(karlaR);
        subjectField.setTypeface(karlaR);

        Bundle bundle = getArguments();
        emailID = bundle.getString("email_id");
        firstName  = bundle.getString("Username");


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message1 = bodyField.getText().toString();
                subject = subjectField.getText().toString();
                if(common.isNetworkAvailable()) {
                    if (!message1.isEmpty()) {
                        getData();
                    } else {
                        common.showShortToast("Enter text in body field to send");
                    }
                }else{
                    common.showShortToast("no internet");
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

        String uri = Constants.BaseUrl+"sendmail";

      /*  {
            "EmailID":"anil.thakur@sanganan.in",
                "UName":"Anil Thakur",
                "Message":"hello abhi


            mail  aaap me ...?",
            "Name":"Anuj Sinha",
                "Flatno":"G-203",
                "SenderMailId":"anuj.sinha@sanganan.in"
        }*/

        try {
            JSONObject json = new JSONObject();
            json.put("EmailID", emailID);
            json.put("UName",firstName);
            json.put("Name",common.getStringValue(Constants.FirstName));
            json.put("SenderMailId",common.getStringValue(Constants.email));
            json.put("Message", common.StringToBase64StringConvertion(message1));
            json.put("Flatno",common.getStringValue(Constants.flatNumber));


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedData(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
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
            progressDialog.dismiss();

        }
    }


    void parsedData(JSONObject json) {
        try {
            String status = "";
            String message = "";
            progressDialog.dismiss();
            status = json.optString("status");
            message = json.optString("message");

            if (status.equalsIgnoreCase("1")) {
                common.showShortToast(message);
                getActivity().onBackPressed();
            }
            else{
                common.showShortToast(message);
                getActivity().onBackPressed();
            }
        } catch (Exception e) {

        }

    }

}
