package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanganan.app.R;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.model.WheelerModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by pranav on 15/12/16.
 */

public class OTPDialog extends DialogFragment {

    View view;
    Common common;
    Typeface ubuntuB, wsRegular, karlaR;
    RequestQueue requestQueue;
    ImageView done, cancleButton;
    EditText vehicleEdit;

    ArrayList<WheelerModel> vehicleList;

    Random r;
    int Result;
    String mobileNumber = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.otp_dialog, container);

        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        wsRegular = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");
        karlaR = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");

        cancleButton = (ImageView) view.findViewById(R.id.close);
        vehicleEdit = (EditText) view.findViewById(R.id.dialogMsg);

        done = (ImageView) view.findViewById(R.id.done);

        r = new Random();
        r = new Random();
        int Low = 1000;
        int High = 9999;
        Result = r.nextInt(High - Low) + Low;

        Gson gson = new Gson();
        Type type = new TypeToken<List<WheelerModel>>() {
        }.getType();
        String jsonVehicles = common.getStringValue(Constants.VehicleList);
        vehicleList = gson.fromJson(jsonVehicles, type);


        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chk1 = vehicleEdit.getText().toString();
                mobileNumber = chk1;

                if (!chk1.isEmpty() && chk1.length() == 10) {
                    getData();

                    getDialog().dismiss();
                    Intent intent = new Intent(getActivity(),ForgotPasswordActivity.class);
                    Bundle b = new Bundle();
                    b.putString("number", mobileNumber);
                    b.putString("Result", String.valueOf(Result));
                    intent.putExtras(b);
                    startActivityForResult(intent,9012);

                } else {
                    common.showShortToast("Enter correct Mobile Number");
                }
            }
        });

        return view;
    }


    private void getData() {


        String uri = "http://textsms.co.in/app/smsapi/index.php?key=3588619015E605&routeid=6&type=text&contacts=" + vehicleEdit.getText().toString() + "&senderid=MNUKAD&msg=OTP+for+mynukad+is+" + Result;

        try {

            JsonObjectRequest req = new JsonObjectRequest(uri,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {


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

}
