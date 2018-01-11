package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sanganan.app.R;
import com.sanganan.app.adapters.NearbyShopAdapter;
import com.sanganan.app.adapters.SearchListAdapter;
import com.sanganan.app.adapters.ShopSearchAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.GetFavouriteList;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.NearByShopSearch;
import com.sanganan.app.model.SearchModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 13/2/17.
 */

public class NearByShopsSearchFragmnet extends BaseFragment {


    View view;
    ListView searchedList;
    RelativeLayout box1, box2;
    EditText editTextsearch;
    RequestQueue requestQueue;
    String editTextString = "";
    Common common;
    Typeface karlaR, karlaB;
    double latitude;
    double longitude;
    ShopSearchAdapter adapter;


    ArrayList<NearByShopSearch> shopList = new ArrayList<>();
    public static boolean isFromBack = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_nearby_layout, container, false);

        initializeVariables();


        Bundle bundle = getArguments();

        latitude = bundle.getDouble("lat");
        longitude = bundle.getDouble("long");


        box1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                box1.setVisibility(View.GONE);
                box2.setVisibility(View.VISIBLE);
                editTextsearch.setFocusableInTouchMode(true);
                editTextsearch.requestFocus();
                editTextsearch.performClick();
                common.showSoftKeyboard(getActivity());
            }
        });


        editTextsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    common.hideSoftKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });

        editTextsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editTextString = editTextsearch.getText().toString();
                if (common.isNetworkAvailable()) {
                    if (editTextString.isEmpty()) {
                        shopList.clear();
                        searchedList.setVisibility(View.GONE);
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    } else {

                        shopList.clear();
                        searchedList.setVisibility(View.VISIBLE);
                        requestQueue.cancelAll("tag_search");
                        getData();
                    }
                } else {
                    common.showShortToast("no internet");
                }
            }
        });


        searchedList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                common.hideSoftKeyboard(getActivity());
                return false;
            }
        });

        searchedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("Name", shopList.get(position).getName());
                bundle.putString("bannerimage", shopList.get(position).getBannerImage());
                bundle.putString("image2", shopList.get(position).getImage2());
                bundle.putString("image3", shopList.get(position).getImage3());
                bundle.putString("type", shopList.get(position).getType());
                bundle.putString("address", shopList.get(position).getAddress1() + "" + shopList.get(position).getAddress4() + "" + shopList.get(position).getCity());
                bundle.putString("ishomedelivery", shopList.get(position).getIsHomeDelivery());
                bundle.putString("is247", shopList.get(position).getIs247());
                bundle.putString("description", shopList.get(position).getDescription());
                bundle.putString("phoneNbr1", shopList.get(position).getPhoneNbr1());
                bundle.putString("phoneNbr2", shopList.get(position).getPhoneNbr2());
                bundle.putString("phoneNbr3", shopList.get(position).getPhoneNbr3());
                bundle.putString("nearbyid", shopList.get(position).getNearById());
                bundle.putString("distance", shopList.get(position).getDistance());
                bundle.putString("latitude", shopList.get(position).getLatitude());
                bundle.putString("longitude", shopList.get(position).getLongitude());

                Fragment fragment = new NearByShop_Details();
                fragment.setArguments(bundle);
                activityCallback.onButtonClick(fragment, false);
            }
        });


        return view;
    }

    private void initializeVariables() {

        box1 = (RelativeLayout) view.findViewById(R.id.boxOne);
        box2 = (RelativeLayout) view.findViewById(R.id.boxTwo);
        editTextsearch = (EditText) view.findViewById(R.id.editTextsearch);
        searchedList = (ListView) view.findViewById(R.id.searchedList);
        karlaR = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");
        karlaB = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");

        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();


    }


    private void getData() {

        String uri = Constants.BaseUrl + "shopdetail";
        try {
            JSONObject json = new JSONObject();
            json.put("lat", String.valueOf(latitude));
            json.put("long", String.valueOf(longitude));
            json.put("distance", "200");
            json.put("UserID", common.getStringValue(Constants.id));
            json.put("Name", editTextString);


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
                req.setTag("tag_search");
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    void parsedData(JSONObject json) {
        String status = null;
        try {
            status = json.optString("Status");

            if (status.equalsIgnoreCase("1")) {
                JSONArray jsonArray = json.getJSONArray("shop");
                shopList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    NearByShopSearch nearByShopSearch = new NearByShopSearch();

                    String nearById = object.optString("ID");
                    String Name = object.optString("Name");
                    String Address1 = object.optString("Address1");
                    String Address4 = object.optString("Address2");
                    String City = object.optString("City");
                    String Is247 = object.optString("Is247");
                    String Description = object.optString("Description");
                    String IsHomeDelivery = object.optString("IsHomeDelivery");
                    String Latitude = object.optString("Latitude");
                    String Longitude = object.optString("Longitude");
                    String PhoneNbr1 = object.optString("PhoneNbr1");
                    String PhoneNbr2 = object.optString("PhoneNbr2");
                    String PhoneNbr3 = object.optString("PhoneNbr3");
                    String distance = object.optString("distance");
                    String Type = object.optString("Type");
                    String BannerImage = object.optString("BannerImage");
                    String Image2 = object.optString("Image2");
                    String Image3 = object.optString("Image3");
                    String isFavourite = object.optString("IsFavAdded");
                    if (isFavourite.equalsIgnoreCase("1")) {
                        GetFavouriteList.hsNearBy.add(nearById);
                    }


                    nearByShopSearch.setName(Name);
                    nearByShopSearch.setShopName(Name);
                    nearByShopSearch.setAddress1(Address1);
                    nearByShopSearch.setAddress4(Address4);
                    nearByShopSearch.setCity(City);
                    nearByShopSearch.setIs247(Is247);
                    nearByShopSearch.setDescription(Description);
                    nearByShopSearch.setIsHomeDelivery(IsHomeDelivery);
                    nearByShopSearch.setLatitude(Latitude);
                    nearByShopSearch.setLongitude(Longitude);
                    nearByShopSearch.setPhoneNbr1(PhoneNbr1);
                    nearByShopSearch.setPhoneNbr2(PhoneNbr2);
                    nearByShopSearch.setPhoneNbr3(PhoneNbr3);
                    nearByShopSearch.setDistance(distance);
                    nearByShopSearch.setNearById(nearById);
                    nearByShopSearch.setType(Type);
                    nearByShopSearch.setBannerImage(BannerImage);
                    nearByShopSearch.setImage2(Image2);
                    nearByShopSearch.setImage3(Image3);


                    if (!distance.isEmpty()) {
                        double dist = Double.parseDouble(distance);
                        dist = round(dist, 2);
                        distance = String.valueOf(dist) + "Km";
                        nearByShopSearch.setDistance(distance);
                    } else {

                    }
                    shopList.add(nearByShopSearch);

                }

            }
        } catch (Exception e) {

        }


        if (status.equalsIgnoreCase("1")) {
            adapter = new ShopSearchAdapter(getActivity(), shopList);
            searchedList.setAdapter(adapter);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isFromBack) {
            box1.setVisibility(View.GONE);
            box2.setVisibility(View.VISIBLE);
            isFromBack = false;
        }
    }


}
