package com.sanganan.app.common;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.sanganan.app.activities.MainHomePageActivity;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Randhir Patel on 8/6/17.
 */

public class InsertToMangoDb {
    RequestQueue requestQueue;
    String feedtype;
    String postRefId;
    Common common;
    String text;
    String imagePath;
    Context context;


    public InsertToMangoDb(Context context, String feedtype, String postRefId, String text, String imagePath) {
        this.context = context;
        this.feedtype = feedtype;
        this.postRefId = postRefId;
        this.text = text;
        this.imagePath = imagePath;
    }


    public void insertDataToServer() {

        common = Common.getNewInstance(context);
        requestQueue = VolleySingleton.getInstance(context).getRequestQueue();

        String uri = Constants.MongoBaseUrl+"onlineinsert.php";
        try {


            StringRequest req = new StringRequest(Request.Method.POST, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                postDataManipulation(response);
                            } catch (Exception ex) {
                                ex.printStackTrace();
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
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("RWAID", common.getStringValue("ID"));
                    params.put("ResidentRwaID", common.getStringValue(Constants.ResidentRWAID));
                    params.put("ResidentName", common.getStringValue(Constants.FirstName));
                    params.put("ResidentFlatNo", common.getStringValue(Constants.flatNumber));
                    params.put("userId", common.getStringValue(Constants.id));
                    params.put("FeedType", feedtype);
                    params.put("Description", common.StringToBase64StringConvertion(text));
                    if (!imagePath.isEmpty()) {
                        params.put("PhotoPath", imagePath);
                    }
                    params.put("ProfilePic", common.getStringValue(Constants.ProfilePic));
                    params.put("PostRefID", postRefId);
                    return params;

                }
            };
            try {
                req.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


    }


    private void postDataManipulation(String responseObj) {
        if (responseObj.contains("ID")) {
            // common.showShortToast("added to mango db");
            Constants.isAnyNewPostAdded = true;
        } else {
            common.showShortToast("Server error");
        }
    }


    public void deleteDataFromServer() {

        common = Common.getNewInstance(context);
        requestQueue = VolleySingleton.getInstance(context).getRequestQueue();

        String uri = Constants.MongoBaseUrl + "postdelete.php";
        try {


            StringRequest req = new StringRequest(Request.Method.POST, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Constants.isAnyNewPostAdded = true;
                            } catch (Exception ex) {
                                ex.printStackTrace();
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
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("FeedType", feedtype);
                    params.put("PostRefID", postRefId);
                    return params;

                }
            };
            try {
                req.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


    }





}
