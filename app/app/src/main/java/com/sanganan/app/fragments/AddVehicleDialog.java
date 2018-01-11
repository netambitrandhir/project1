package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pranav on 15/12/16.
 */

public class AddVehicleDialog extends DialogFragment {

    View view;
    Common common;
    Typeface ubuntuB, wsRegular, karlaR;
    RequestQueue requestQueue;
    ImageView done, cancleButton;
    EditText vehicleEdit;
    TextView wheelerTypeText, wheelerNumber;
    String wheelerType[];
    Spinner spinner;
    String selectedSpinnerItem;


    ArrayList<WheelerModel> vehicleList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_vehicle_dialog_layout, container);

        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        wsRegular = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");
        karlaR = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");

        cancleButton = (ImageView) view.findViewById(R.id.close);
        vehicleEdit = (EditText) view.findViewById(R.id.dialogMsg);

        ArrayList<InputFilter> curInputFilters = new ArrayList<InputFilter>(Arrays.asList(vehicleEdit.getFilters()));
        curInputFilters.add(0, new AlphaNumericInputFilter());
        curInputFilters.add(1, new InputFilter.AllCaps());
        InputFilter[] newInputFilters = curInputFilters.toArray(new InputFilter[curInputFilters.size()]);
        vehicleEdit.setFilters(newInputFilters);

        wheelerTypeText = (TextView) view.findViewById(R.id.wheelerType);
        spinner = (Spinner) view.findViewById(R.id.wheeler);
        done = (ImageView) view.findViewById(R.id.done);

        Gson gson = new Gson();
        Type type = new TypeToken<List<WheelerModel>>() {
        }.getType();
        String jsonVehicles = common.getStringValue(Constants.VehicleList);
        vehicleList = gson.fromJson(jsonVehicles, type);


        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        wheelerType = new String[]{"2", "3", "4", "6", "8"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_view, wheelerType);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSpinnerItem = spinner.getSelectedItem().toString();
                wheelerTypeText.setText(selectedSpinnerItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                wheelerTypeText.setText("2");
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chk1 = vehicleEdit.getText().toString();
                String chk2 = wheelerTypeText.getText().toString();

                String aftre = chk1.replaceAll("\\s+", "");
                if (!chk1.isEmpty() && !chk1.equalsIgnoreCase("0") && !chk2.isEmpty() && aftre.length() >= 5 ) {
                    getData();
                } else {
                    common.showShortToast("Enter correct vehicle details");
                }
            }
        });


        return view;
    }


    public static class AlphaNumericInputFilter implements InputFilter {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            // Only keep characters that are alphanumeric
            StringBuilder builder = new StringBuilder();
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (Character.isLetterOrDigit(c)|Character.isWhitespace(c)) {
                    builder.append(c);
                }
            }

            // If all characters are valid, return null, otherwise only return the filtered characters
            boolean allCharactersValid = (builder.length() == end - start);
            return allCharactersValid ? null : builder.toString();
        }
    }

    private void getData() {

        common.showSpinner(getActivity());

        String uri = Constants.BaseUrl + "addvehicle";
        try {
            JSONObject json = new JSONObject();
            json.put("ResidentRWAFlatID", common.getStringValue(Constants.flatId));
            json.put("VehicleNbr", vehicleEdit.getText().toString());
            json.put("VehicleType", wheelerTypeText.getText().toString());

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
                req.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
                common.hideSpinner();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    void parsedData(JSONObject json) {
        try {

            common.hideSpinner();

            String status = json.optString("Status");
            String messsage = json.optString("Message");

            if (status.equalsIgnoreCase("1")) {
                String vehicleId = json.optString("VehicleId");
                WheelerModel wheelerModel = new WheelerModel();
                wheelerModel.setID(vehicleId);
                wheelerModel.setVehicleNumber(vehicleEdit.getText().toString());
                wheelerModel.setVehicheType(wheelerTypeText.getText().toString());
                vehicleList.add(wheelerModel);
                Gson gson = new Gson();
                String jsonVehicles = gson.toJson(vehicleList);
                Log.d("TAG", "jsonCars = " + jsonVehicles);
                common.setStringValue(Constants.VehicleList, jsonVehicles);

            }
            common.showShortToast(messsage);
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
