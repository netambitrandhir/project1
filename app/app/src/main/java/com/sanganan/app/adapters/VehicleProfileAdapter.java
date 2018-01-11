package com.sanganan.app.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.sanganan.app.fragments.MyProfileFragment;
import com.sanganan.app.model.WheelerModel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 5/10/16.
 */

public class VehicleProfileAdapter extends BaseAdapter {
    ArrayList<WheelerModel> vehicleList;
    Context context;
    Typeface ubantuMedium;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;

    public VehicleProfileAdapter(Context context, ArrayList<WheelerModel> vehicleList) {
        this.vehicleList = vehicleList;
        this.context = context;
        ubantuMedium = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");
        requestQueue = VolleySingleton.getInstance(context).getRequestQueue();

    }

    @Override
    public int getCount() {
        return vehicleList.size();
    }

    @Override
    public Object getItem(int position) {
        return vehicleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.vehicle_list_single_view, null);
            viewHolder = new ViewHolder();

            viewHolder.remove = (ImageView) convertView.findViewById(R.id.remove);
            viewHolder.vehicleNumber = (TextView) convertView.findViewById(R.id.vehicleNumber);
            viewHolder.vehicleType = (TextView) convertView.findViewById(R.id.vehicleType);
            viewHolder.vehicleNumber.setTypeface(ubantuMedium);
            viewHolder.vehicleType.setTypeface(ubantuMedium);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.vehicleNumber.setText(vehicleList.get(position).getVehicleNumber());
        viewHolder.vehicleType.setText("( " + vehicleList.get(position).getVehicheType() + " wheeler" + " )");

        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(vehicleList.get(position).getID(), position);
            }
        });


        return convertView;
    }

    private class ViewHolder {
        TextView vehicleNumber, vehicleType;
        ImageView remove;

    }

    private void getData(String vehicleId, final int position) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl+"deletevehicle";
        try {
            JSONObject json = new JSONObject();
            json.put("ID", vehicleId);


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedData(response, position);
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
            progressDialog.dismiss();

        }
    }

    void parsedData(JSONObject json, int po) {
        try {

            progressDialog.dismiss();

            String status = json.optString("Status");
            String messsage = json.optString("Message");
            Common common = Common.getNewInstance(context);
            if (status.equalsIgnoreCase("1")) {
                vehicleList.remove(po);
                Gson gson = new Gson();
                String jsonVehicles = gson.toJson(vehicleList);
                Log.d("TAG", "jsonCars = " + jsonVehicles);
                common.setStringValue(Constants.VehicleList, jsonVehicles);
                notifyDataSetChanged();
                ((ActionBarActivity)context).onBackPressed();
                Fragment fragment = new MyProfileFragment();
                FragmentManager fragmentManager =  ((ActionBarActivity)context).getSupportFragmentManager();
                FragmentTransaction tx1 = fragmentManager.beginTransaction();
                tx1.replace(R.id.container_body, fragment);
                tx1.addToBackStack("profile");
                tx1.commit();
            }

            common.showShortToast(messsage);

        } catch (Exception e) {

        }
    }
}
