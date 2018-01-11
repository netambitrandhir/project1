package com.sanganan.app.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sanganan.app.adapters.CommunityHelperAdapter;
import com.sanganan.app.model.HelperModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by root on 29/9/16.
 */

public class GetFavouriteList {
    Context context;
    RequestQueue requestQueue;
    Common common;
    String url;
    String purpose;
    public static HashSet<String> hsHelper = new HashSet<>();
   public static HashSet<String> hsNearBy = new HashSet<>();


    public GetFavouriteList(Context context, String purpose, String url) {
        this.context = context;
        requestQueue = VolleySingleton.getInstance(context).getRequestQueue();
        common = Common.getNewInstance(context);
        this.purpose = purpose;
        if(purpose.equalsIgnoreCase("helper")){
            hsHelper.clear();
            this.url = Constants.HELPER_LINK;
        }
        else if(purpose.equalsIgnoreCase("nearby")){
            this.url = Constants.NEARBY_LINK;
            hsNearBy.clear();
        }
    }

    public void getData() {


        try {
            JSONObject json = new JSONObject();

            json.put("UserID", common.getStringValue(Constants.id));


            JsonObjectRequest req = new JsonObjectRequest(url, json,
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
            String status = "";

            if (purpose.equalsIgnoreCase("helper")) {
                JSONArray jsonArray = json.getJSONArray("Fav Helpers");
                status = json.optString("Status");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String id = object.optString("HelperID");
                    hsHelper.add(id);

                }
            } else if (purpose.equalsIgnoreCase("nearby")) {
                JSONArray jsonArray = json.getJSONArray("Fav Shop ");
                status = json.optString("Status");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String id = object.optString("NearByID");
                    hsNearBy.add(id);
                }
            }

        } catch (Exception e) {

        }
    }
}
