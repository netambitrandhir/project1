package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 9/12/16.
 */

public class EditDialogFragment extends DialogFragment {

    View view;
    Common common;
    TextView headerTitle;
    ImageView close, done;
    EditText dialogMsg;
    String tag_of_dialog = "";
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    Bundle bundle;
    String name,emailId,profession;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.edit_dialog_layout, container, false);
        common = Common.getNewInstance(getActivity());
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bundle = getArguments();
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

       /* name = bundle.getString("name");
        emailId = bundle.getString("emailId");
        profession = bundle.getString("profession");*/


        headerTitle = (TextView) view.findViewById(R.id.headerTitle);
        close = (ImageView) view.findViewById(R.id.close);
        done = (ImageView) view.findViewById(R.id.done);

        dialogMsg = (EditText) view.findViewById(R.id.dialogMsg);

        tag_of_dialog = getTag().toString();


        headerTitle.setText("Edit your " + tag_of_dialog);
        dialogMsg.setHint("Enter your " + tag_of_dialog);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("ID", common.getStringValue(Constants.id));

                    switch (tag_of_dialog) {
                        case "Name":
                            json.put("FirstName", dialogMsg.getText().toString());
                            break;
                        case "Phone Number":
                            json.put("PhoneNbr", dialogMsg.getText().toString());
                            break;
                        case "Email ID":
                            json.put("EmailID", dialogMsg.getText().toString());
                            break;
                        case "Profession":
                            json.put("Occupation", dialogMsg.getText().toString());
                            break;

                    }
                   if(!dialogMsg.getText().toString().isEmpty()) {
                       getData(json);
                   }
                    else{
                       common.showShortToast("Nothing to update");
                   }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        });


        return view;
    }

    private void getData(JSONObject json) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri =Constants.BaseUrl+"updateuserinfo";
        try {


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

        progressDialog.dismiss();
        try {

            String status = json.optString("Status");
            String message = json.optString("Message");
            common.showShortToast(message);
            JSONObject userData = json.getJSONObject("User");
            String phone = userData.optString("PhoneNbr");
            String email = userData.optString("EmailID");
            String name = userData.optString("FirstName");
            String profession = userData.optString("Occupation");


            common.setStringValue(Constants.email, email);
            common.setStringValue(Constants.FirstName, name);
            common.setStringValue(Constants.Occupation, profession);
            common.setStringValue(Constants.Phone, phone);

            dismiss();
            getActivity().onBackPressed();
            Fragment fragment = new MyProfileFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction tx1 = fragmentManager.beginTransaction();
            tx1.replace(R.id.container_body, fragment);
            tx1.addToBackStack("profile");
            tx1.commit();

        } catch (Exception e) {

        }


    }


}
