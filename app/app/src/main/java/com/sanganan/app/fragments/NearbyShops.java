package com.sanganan.app.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.ResultReceiver;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.activities.StartSearchPage;
import com.sanganan.app.adapters.NearbyShopAdapter;
import com.sanganan.app.adapters.ShopCategoryHLAdapter;

import com.sanganan.app.adapters.YourNeighborsAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.GetFavouriteList;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.customview.HorizontalListView;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.Category;
import com.sanganan.app.model.NearByShopSearch;

import com.sanganan.app.utility.GPSTracker;
import com.sanganan.app.utility.Utility;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 3/8/16.
 */
public class NearbyShops extends BaseFragment {
    Typeface ubuntuB;
    View view;
    ListView nearbyshop;
    ArrayList<NearByShopSearch> nearbyshopList = new ArrayList<>();
    ProgressBar progressBar;
    Common common;
    HorizontalListView horizontalListView;
    ImageView filter, search_shop;
    public static ArrayList<Category> alist = new ArrayList<>();
    ShopCategoryHLAdapter adapterHL;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    NearbyShopAdapter adapter;


    boolean userScrolled = false;
    int cc = 1;

    private int mLastFirstVisibleItem;
    private boolean mIsScrollingUp;
    private boolean loadingCompleted = false;

    public Handler mHandler;
    public View ftView;
    public boolean isLoading = false;

    double latitude = 900;
    double longitude = 900;
    ImageView userhelp;

    private FirebaseAnalytics mFirebaseAnalytics;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.nearby_shops, container, false);
        common = Common.getNewInstance(getActivity());
        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        filter = (ImageView) view.findViewById(R.id.filter);
        search_shop = (ImageView) view.findViewById(R.id.search_shop);
        userhelp = (ImageView) view.findViewById(R.id.userhelp);

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        TextView title_done = (TextView) getActivity().findViewById(R.id.title_done);
        title_done.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        TextView title = (TextView) view.findViewById(R.id.titletextView);
        title.setTypeface(ubuntuB);

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = layoutInflater.inflate(R.layout.lazy_loading_footer_view, null);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        Bundle logBundleInitial = new Bundle();
        logBundleInitial.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleInitial.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("nearby_clicked", logBundleInitial);


        horizontalListView = (HorizontalListView) view.findViewById(R.id.horizontal_listview);
        prepareToCallAsyntaskWithRemainingIds();


        if (alist.size() != 0) {
            horizontalListView.setVisibility(View.VISIBLE);

            adapterHL = new ShopCategoryHLAdapter(getActivity(), alist);
            horizontalListView.setAdapter(adapterHL);

        } else {
            horizontalListView.setVisibility(View.GONE);
        }

        nearbyshop = (ListView) view.findViewById(R.id.nearbyshop);


        if (common.isNetworkAvailable()) {

            if (nearbyshopList.size() == 0) {
                if (new GPSTracker(getActivity()).canGetLocation()) {
                    getLatLong();
                    nearbyshopList.clear();
                    cc = 1;
                    userScrolled = false;
                    loadingCompleted = false;
                    getData("1");
                } else {
                    showSettingsAlert();

                }
            } else {
                adapter = new NearbyShopAdapter(getActivity(), nearbyshopList);
                nearbyshop.setAdapter(adapter);
            }

        } else {
            common.showShortToast("no internet");
        }


        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment1 = new FiltersFragment();
                android.support.v4.app.FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction tx1 = fragmentManager1.beginTransaction();
                tx1.replace(R.id.container_body, fragment1).addToBackStack("FiltersFragment");
                tx1.commit();
                filter.setVisibility(View.GONE);
            }
        });

        horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alist.remove(alist.get(position));
                prepareToCallAsyntaskWithRemainingIds();

                if (common.isNetworkAvailable()) {
                    nearbyshopList.clear();
                    cc = 1;
                    userScrolled = false;
                    loadingCompleted = false;
                    getData("1");
                } else {
                    common.showShortToast("no internet");
                }

                adapterHL.notifyDataSetChanged();
            }
        });


        nearbyshop.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                // If scroll state is touch scroll then set userScrolledActionCompleted
                // true
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }


                if (view.getId() == nearbyshop.getId()) {
                    final int currentFirstVisibleItem = nearbyshop.getFirstVisiblePosition();

                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        mIsScrollingUp = false;
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        mIsScrollingUp = true;
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // Now check if userScrolledActionCompleted is true and also check if
                // the item is end then update list view and set
                // userScrolledActionCompleted to false
                if (userScrolled && !mIsScrollingUp && !loadingCompleted
                        && firstVisibleItem + visibleItemCount == totalItemCount) {

                    userScrolled = false;
                    isLoading = true;
                    cc = cc + 1;
                    getData(String.valueOf(cc));

                }
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = nearbyshop.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        return;
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem) {
                    View v = nearbyshop.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:

                        // Setup refresh listener which triggers new data loading

                        return;
                    }
                }
            }

        });


        nearbyshop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("Name", nearbyshopList.get(position).getName());
                bundle.putString("bannerimage", nearbyshopList.get(position).getBannerImage());
                bundle.putString("image2", nearbyshopList.get(position).getImage2());
                bundle.putString("image3", nearbyshopList.get(position).getImage3());
                bundle.putString("type", nearbyshopList.get(position).getType());
                bundle.putString("address", nearbyshopList.get(position).getAddress1() + "" + nearbyshopList.get(position).getAddress4() + "" + nearbyshopList.get(position).getCity());
                bundle.putString("ishomedelivery", nearbyshopList.get(position).getIsHomeDelivery());
                bundle.putString("is247", nearbyshopList.get(position).getIs247());
                bundle.putString("description", nearbyshopList.get(position).getDescription());
                bundle.putString("phoneNbr1", nearbyshopList.get(position).getPhoneNbr1());
                bundle.putString("phoneNbr2", nearbyshopList.get(position).getPhoneNbr2());
                bundle.putString("phoneNbr3", nearbyshopList.get(position).getPhoneNbr3());
                bundle.putString("nearbyid", nearbyshopList.get(position).getNearById());
                bundle.putString("distance",nearbyshopList.get(position).getDistance());
                bundle.putString("latitude",nearbyshopList.get(position).getLatitude());
                bundle.putString("longitude",nearbyshopList.get(position).getLongitude());

                Fragment fragment = new NearByShop_Details();
                fragment.setArguments(bundle);
                activityCallback.onButtonClick(fragment, false);

            }
        });


        if (common.getBooleanValue(Constants.isLoggedIn)) {
            userhelp.setVisibility(View.VISIBLE);
        } else {
            userhelp.setVisibility(View.GONE);
        }


        search_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new NearByShopsSearchFragmnet();
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", latitude);
                bundle.putDouble("long", longitude);
                fragment.setArguments(bundle);
                activityCallback.onButtonClick(fragment, false);
            }
        });

        userhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "[mynukad] : New registration for Nearby");
                intent.putExtra(Intent.EXTRA_TEXT, "Thanks for adding to mynukad, please fill below form:\n" +
                        "\n" +
                        "Shop/Service Name :\n" +
                        "Address :\n" +
                        "Type(like tiffin or Medical shop):\n" +
                        "Phone number :\n" +
                        "\n" +
                        "If possible, please add some pics of service/shop in attachment.\n");
                intent.setData(Uri.parse("mailto:info@mynukad.com")); // or just "mailto:" for blank
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView relativeLayoutHeading = (TextView) getActivity().findViewById(R.id.toolbarTitleHeading);
        relativeLayoutHeading.setVisibility(View.GONE);
        ImageView downarrow = (ImageView) getActivity().findViewById(R.id.downarrow);
        downarrow.setVisibility(View.GONE);

    }

    private void prepareToCallAsyntaskWithRemainingIds() {
        String localIds = "";
        for (int i = 0; i < alist.size(); i++) {
            localIds = localIds + alist.get(i).getIdCategory() + ",";
        }
        if (alist.size() > 0) {
            localIds = localIds.substring(0, localIds.length() - 1);
        }
        Constants.filterToCategoryById = localIds;

        if (alist.size() == 0) {
            horizontalListView.setVisibility(View.GONE);
        }
    }


    private void getData(String pageNo) {

        if (cc == 1) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait Data is loading...");
            progressDialog.show();
        } else {
            nearbyshop.addFooterView(ftView, null, false);
        }

        String uri = Constants.BaseUrl + "shopdetail";

        try {
            JSONObject json = new JSONObject();
            if (latitude == 900 && longitude == 900) {
                latitude = Double.parseDouble(common.getStringValue(Constants.LATITUDE));
                longitude = Double.parseDouble(common.getStringValue(Constants.LONGITUDE));
                json.put("lat", common.getStringValue(Constants.LATITUDE));
                json.put("long", common.getStringValue(Constants.LONGITUDE));
            } else {
                json.put("lat", latitude);
                json.put("long", longitude);
            }
            json.put("distance", "50");
            json.put("PageNo", pageNo);
            json.put("CategoryID", Constants.filterToCategoryById);
            json.put("UserID", common.getStringValue(Constants.id));


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
                   /* headers.put("Country", "Singapore");*/
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
        String status = null;
        try {
            status = json.optString("Status");

            if (status.equalsIgnoreCase("1")) {
                JSONArray jsonArray = json.getJSONArray("shop");

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
                        distance = String.valueOf(dist) + " Km";
                        nearByShopSearch.setDistance(distance);
                    } else {

                    }
                    nearbyshopList.add(nearByShopSearch);

                }

            } else {
                loadingCompleted = true;
            }

        } catch (Exception e) {

        }

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (status.equalsIgnoreCase("1")) {

            if (cc == 1) {
                adapter = new NearbyShopAdapter(getActivity(), nearbyshopList);
                nearbyshop.setAdapter(adapter);
            } else {
                nearbyshop.removeFooterView(ftView);
            }

            adapter.updateReceiptsList(nearbyshopList);

        } else if (cc != 1 && status.equalsIgnoreCase("0")) {
            nearbyshop.removeFooterView(ftView);
        }

    }


    private void initializeVariables() {

        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("Nearby");
        title.setTypeface(ubuntuB);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    public void getLatLong() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("GPS Settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to enable GPS?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivityForResult(intent, 9001);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                nearbyshopList.clear();
                latitude = Double.parseDouble(common.getStringValue(Constants.LATITUDE));
                longitude = Double.parseDouble(common.getStringValue(Constants.LONGITUDE));
                cc = 1;
                userScrolled = false;
                loadingCompleted = false;
                getData("1");
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}
