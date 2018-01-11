package com.sanganan.app.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSession;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.sanganan.app.adapters.ImageAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.fragments.RwaSearchFragment;
import com.sanganan.app.fragments.SignupFirstPage;
import com.sanganan.app.model.SearchModel;
import com.sanganan.app.utility.GPSTracker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static com.sanganan.app.R.drawable.v;
import static com.sanganan.app.R.id.coordinatorLayout;

/**
 * Created by pranav on 3/8/16.
 */
public class StartSearchPage extends AppCompatActivity {

    ListView startSearch;
    android.support.v4.app.FragmentManager fm;
    ArrayList<SearchModel> searchList = new ArrayList<>();
    ImageView search_icon;
    Common common;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    double latitude, longitude;
    Context context;
    boolean isLoggedIn = false;
    private CoordinatorLayout coordinatorLayout;
    ProgressBar loadingPanel;
    ImageAdapter adapter;
    FragmentManager fragmentManager;

    boolean userScrolled = false;
    int cc = 1;

    private int mLastFirstVisibleItem;
    private boolean mIsScrollingUp;
    private boolean loadingCompleted = false;
    RelativeLayout linearLayout;

    @TargetApi(Build.VERSION_CODES.M)
    private void methodPermissionHandler() {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityComreturn;pat#requestPermissions for more details.
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 4);


        } else {
            getLatLong();

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startsearch_page);

        //  methodPermissionHandler();
        context = StartSearchPage.this;
        common = Common.getNewInstance(this);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        search_icon = (ImageView) findViewById(R.id.search_rwa);
        linearLayout = (RelativeLayout) findViewById(R.id.linearLayout);

        Bundle bundleNotifyCheck = getIntent().getExtras();


        SharedPreferences perf = common.pref;
        if (perf.contains("ID") && Constants.fromWhere.equalsIgnoreCase("Blank")) {
            if (bundleNotifyCheck != null && bundleNotifyCheck.containsKey("fragmnet")) {
                bundleNotifyCheck.putString("ID", bundleNotifyCheck.getString("ID"));
                bundleNotifyCheck.putString("fragmnet", bundleNotifyCheck.getString("fragmnet"));
            } else {
                bundleNotifyCheck = new Bundle();
            }

            Intent i = new Intent(StartSearchPage.this, MainHomePageActivity.class);
            bundleNotifyCheck.putString("title", common.getStringValue("SocietyName"));
            i.putExtras(bundleNotifyCheck);
            startActivity(i);
            finish();
        } else {
            if (common.isNetworkAvailable()) {
                if (new GPSTracker(this).canGetLocation()) {
                    getLatLong();
                    searchList.clear();
                    cc = 1;
                    userScrolled = false;
                    loadingCompleted = false;
                    getData("1");
                } else {
                    cc = 1;
                    userScrolled = false;
                    loadingCompleted = false;
                    getData("1");
                }
            } else {
                common.showShortToast("No internet");

            }

        }

        loadingPanel = new ProgressBar(StartSearchPage.this);
        startSearch = (ListView) findViewById(R.id.startSearch);


        Typeface ubuntuB = Typeface.createFromAsset(this.getAssets(), "Ubuntu-B.ttf");
        TextView title = (TextView) findViewById(R.id.textView);
        title.setTypeface(ubuntuB);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);


        isLoggedIn = common.getBooleanValue(Constants.isLoggedIn);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new RwaSearchFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction tx1 = fragmentManager.beginTransaction();
                tx1.replace(R.id.container, fragment).addToBackStack("rwa_search");
                Bundle bundle;
                if (Constants.fromWhere.equals("signup_first_page")) {
                    bundle = getIntent().getExtras();
                } else {
                    bundle = new Bundle();
                    bundle.putString("fromclass", StartSearchPage.class.getName());
                }

                fragment.setArguments(bundle);
                tx1.commit();

            }
        });


        startSearch.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                // If scroll state is touch scroll then set userScrolled
                // true
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }


                if (findViewById(R.id.startSearch).getId() == startSearch.getId()) {
                    final int currentFirstVisibleItem = startSearch.getFirstVisiblePosition();

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
                // Now check if userScrolled is true and also check if
                // the item is end then update list view and set
                // userScrolled to false
                if (userScrolled && !mIsScrollingUp && !loadingCompleted
                        && firstVisibleItem + visibleItemCount == totalItemCount) {

                    userScrolled = false;
                    cc = cc + 1;
                    getData(String.valueOf(cc));
                }
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = startSearch.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:

                     /*   // Setup refresh listener which triggers new data loading
                        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                // Your code to refresh the list here.
                                // Make sure you call swipeContainer.setRefreshing(false)
                                notification2("1");
                            }
                        });*/
                        return;
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem) {
                    View v = startSearch.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:

                        // Setup refresh listener which triggers new data loading

                        return;
                    }
                }
            }

        });


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
                common.setStringValue(Constants.LATITUDE, searchList.get(position).getLatitude());
                common.setStringValue(Constants.LONGITUDE, searchList.get(position).getLongitude());


                if (getIntent().getExtras().getString("fromclass").equals(SignupFirstPage.class.getName())) {
                    finish();
                } else {
                    Intent i = new Intent(StartSearchPage.this, MainHomePageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", searchList.get(position).getSocietyName());
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }


            }
        });

    }

    private void getData(String pageNo) {


        progressDialog = new ProgressDialog(StartSearchPage.this);
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl + "rwa";
        try {

            JSONObject json = new JSONObject();
            if (new GPSTracker(this).canGetLocation()) {
                json.put("lat", latitude);
                json.put("long", longitude);
                json.put("PageNo", pageNo);  ///for time bieng its hardcoded
                json.put("distance", "100");
            } else {
                json.put("PageNo", pageNo);
            }

            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedData(response);
                            } catch (Exception e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            VolleyLog.d("error", error.getMessage());
                            common.showShortToast(error.getMessage());
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
                progressDialog.dismiss();
                e.printStackTrace();
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();

        }
    }

    void parsedData(JSONObject json) {

        progressDialog.dismiss();

        try {

            JSONArray jsonArray = json.getJSONArray("SocietyDetails");
            String status = json.optString("Status");
            if (status.equalsIgnoreCase("1")) {
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

                    if (!distance.isEmpty()) {
                        String[] strings = distance.split("\\.");
                        distance = strings[0] + "." + strings[1].substring(0, 1) + "KM";
                    }
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
            } else {
                loadingCompleted = true;
            }
            if (cc == 1) {
                adapter = new ImageAdapter(StartSearchPage.this, searchList);
                startSearch.setAdapter(adapter);
            }
            adapter.updateReceiptsList(searchList);

        } catch (Exception e) {

        }


    }

    public void getLatLong() {
        GPSTracker gpsTracker = new GPSTracker(StartSearchPage.this);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
    }


    @Override
    public void onBackPressed() {
        fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() == 1) {
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager()
                    .getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);

            fragmentManager.popBackStack();

        } else if (fragmentManager.getBackStackEntryCount() == 0) {
            finish();
        }
    }

}