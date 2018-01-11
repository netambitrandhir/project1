package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sanganan.app.R;
import com.sanganan.app.activities.MainHomePageActivity;
import com.sanganan.app.activities.StartSearchPage;
import com.sanganan.app.adapters.ImageAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.SearchModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 5/1/17.
 */

public class RwaListingFragment extends BaseFragment {


    View view;
    ProgressDialog progressDialog;
    ImageView search_icon;
    Common common;
    RequestQueue requestQueue;
    ArrayList<SearchModel> searchList = new ArrayList<>();
    ListView startSearch;

    int x = 1;
    ProgressBar loadingPanel;
    boolean userScrolled = false;
    ImageAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.startsearch_page, container, false);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        TextView title = (TextView) view.findViewById(R.id.textView);

        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        search_icon = (ImageView) view.findViewById(R.id.search_rwa);

        startSearch = (ListView) view.findViewById(R.id.startSearch);


        Typeface ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        title.setTypeface(ubuntuB);

        loadingPanel = new ProgressBar(getActivity());
        startSearch = (ListView) view.findViewById(R.id.startSearch);


        if (common.isNetworkAvailable()) {
            getData("1");
        } else {
            common.showShortToast("No internet");

        }

        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new RwaSearchFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction tx1 = fragmentManager.beginTransaction();
                tx1.replace(R.id.container, fragment).addToBackStack("rwa_search");
                tx1.commit();

            }
        });
        return view;
    }


    private void getData(String pageNo) {


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl+"rwa";
        try {

            JSONObject json = new JSONObject();
            json.put("lat", "28.60716001");
            json.put("long", "77.38161842");
            json.put("PageNo", "1");  ///for time bieng its hardcoded
            json.put("distance", "100");


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
                    headers.put("Country", "Singapore");
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

        progressDialog.dismiss();

        try {

            JSONArray jsonArray = json.getJSONArray("SocietyDetails");
            String status = json.optString("Status");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                SearchModel model = new SearchModel();

                String societyId = object.optString("ID");
                String SocietyName = object.optString("Name");
                String Image = object.optString("BannerImage");
                String Address1 = object.optString("Address1");
                String Address2 = object.optString("Address2");
                String locality = object.optString("Locality");
                String City = object.optString("City");
                String PinCode = object.optString("PostalCode");
                String IsActive = object.optString("IsActive");
                String distance = object.optString("distance");
                String SocietyPrivateinfo = object.optString("SocietyPrivateinfo");
                String SocietyPublicInfo = object.optString("SocietyPublicInfo");
                String SocietyBanner = object.optString("SocietyBanner");
                String SocietyPendingInfo = object.optString("SocietyPendingInfo");

                String latitudeLocal = object.optString("Latitude");
                String longitudeLocal = object.optString("Longitude");

                String channelUrl = object.optString("ChatUrl");

                String[] strings = distance.split("\\.");
                distance = strings[0] + "." + strings[1].substring(0, 1) + "KM";

                model.setSocietyId(societyId);
                model.setSocietyName(SocietyName);
                model.setImage(Image);
                model.setAddress1(Address1);
                model.setAddress2(Address2);
                model.setLocality(locality);
                model.setCity(City);
                model.setPinCode(PinCode);
                model.setIsActive(IsActive);
                model.setDistance(distance);
                model.setSocietyPrivateinfo(SocietyPrivateinfo);
                model.setSocietyPublicInfo(SocietyPublicInfo);
                model.setSocietyBanner(SocietyBanner);
                model.setSocietyPendingInfo(SocietyPendingInfo);
                model.setChatUrl(channelUrl);
                model.setLatitude(latitudeLocal);
                model.setLongitude(longitudeLocal);

                searchList.add(model);

            }
            if (x == 1) {
                adapter = new ImageAdapter(getActivity(), searchList);
                startSearch.setAdapter(adapter);
            }
            adapter.updateReceiptsList(searchList);

            startSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                    common.setStringValue("ID", searchList.get(position).getSocietyId());
                    common.setStringValue("SocietyName", searchList.get(position).getSocietyName());
                    common.setStringValue("Image", searchList.get(position).getImage());
                    common.setStringValue("Address1", searchList.get(position).getAddress1());
                    common.setStringValue("Address2", searchList.get(position).getAddress2());
                    common.setStringValue("Address3", searchList.get(position).getLocality());
                    common.setStringValue("City", searchList.get(position).getCity());
                    common.setStringValue("PinCode", searchList.get(position).getPinCode());
                    common.setStringValue(Constants.SocietyPrivateinfo, searchList.get(position).getSocietyPrivateinfo());
                    common.setStringValue(Constants.SocietyPublicInfo, searchList.get(position).getSocietyPublicInfo());
                    common.setStringValue(Constants.SocietyBanner, searchList.get(position).getSocietyBanner());
                    common.setStringValue(Constants.SocietyPendingInfo, searchList.get(position).getSocietyPendingInfo());
                    common.setStringValue("chat", searchList.get(position).getChatUrl());
                    getActivity().onBackPressed();

                }
            });
        } catch (Exception e) {

        }


    }
}
