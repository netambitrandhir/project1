package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.adapters.GMemberNotificationsAdapter;
import com.sanganan.app.adapters.ShopCategoryHLAdapter;
import com.sanganan.app.adapters.YourNeighborsAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.customview.HorizontalListView;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.Category;
import com.sanganan.app.model.GeneralNotification;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 3/8/16.
 */
public class Notification extends BaseFragment {
    Common common;
    TextView rwaNotification, generalNotification, nolisttext, titletextView;
    View view;
    public static ArrayList<Category> alistNotification = new ArrayList<>();
    RequestQueue requestQueue;
    Typeface ubuntuB, worksansR;
    ProgressDialog progressDialog;
    ArrayList<GeneralNotification> notificationArrayList = new ArrayList<>();
    GMemberNotificationsAdapter adapter;
    ListView notificationList;
    boolean userScrolled = false;
    int cc = 1;

    private int mLastFirstVisibleItem;
    private boolean mIsScrollingUp;
    private boolean loadingCompleted = false;

    ImageView search, filter;
    public View ftView;
    public boolean isLoading = false;

    private FirebaseAnalytics mFirebaseAnalytics;
    HorizontalListView horizontalListView;
    ShopCategoryHLAdapter adapterHL;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.notification, container, false);
        common = Common.getNewInstance(getActivity());

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        worksansR = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        TextView titletextView = (TextView) view.findViewById(R.id.titletextView);
        titletextView.setTypeface(ubuntuB);
        nolisttext = (TextView) view.findViewById(R.id.nolisttext);
        nolisttext.setTypeface(worksansR);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        Bundle logBundleNotify = new Bundle();
        logBundleNotify.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleNotify.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("notification_clicked", logBundleNotify);


        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = layoutInflater.inflate(R.layout.lazy_loading_footer_view, null);


        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        notificationList = (ListView) view.findViewById(R.id.notificationList);
        search = (ImageView) view.findViewById(R.id.search);
        filter = (ImageView) view.findViewById(R.id.filter);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NotificationSearchActivity.class);
                startActivity(intent);
            }
        });


        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationFilterFragment fragment = new NotificationFilterFragment();
                activityCallback.onButtonClick(fragment, false);

                // common.showShortToast("need ui to start implementation");
            }
        });

        horizontalListView = (HorizontalListView) view.findViewById(R.id.horizontal_listview);
        prepareToCallAsyntaskWithRemainingIds();


        if (alistNotification.size() != 0) {
            horizontalListView.setVisibility(View.VISIBLE);
            adapterHL = new ShopCategoryHLAdapter(getActivity(), alistNotification);
            horizontalListView.setAdapter(adapterHL);

        } else {
            horizontalListView.setVisibility(View.GONE);
        }

        if (common.isNetworkAvailable()) {

            if (notificationArrayList.size() == 0) {
                notificationArrayList.clear();
                cc = 1;
                userScrolled = false;
                loadingCompleted = false;
                getNotification("1");
            } else {
                adapter = new GMemberNotificationsAdapter(getActivity(), notificationArrayList);
                notificationList.setAdapter(adapter);
                notificationList.setVisibility(View.VISIBLE);
            }
        } else {
            common.showShortToast("no internet");
        }

        notificationList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                // If scroll state is touch scroll then set userScrolledActionCompleted
                // true
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }


                if (view.getId() == notificationList.getId()) {
                    final int currentFirstVisibleItem = notificationList.getFirstVisiblePosition();

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
                    cc = cc + 1;
                    getNotification(String.valueOf(cc));
                }
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = notificationList.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {

                        return;
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem) {
                    View v = notificationList.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:

                        // Setup refresh listener which triggers new data loading

                        return;
                    }
                }
            }

        });


        horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alistNotification.remove(alistNotification.get(position));
                prepareToCallAsyntaskWithRemainingIds();

                if (common.isNetworkAvailable()) {
                    notificationArrayList.clear();
                    cc = 1;
                    userScrolled = false;
                    loadingCompleted = false;
                    getNotification("1");
                } else {
                    common.showShortToast("no internet");
                }

                adapterHL.notifyDataSetChanged();
            }
        });


        notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("title", notificationArrayList.get(position).getTitle());
                bundle.putString("date", notificationArrayList.get(position).getDatecreated());
                bundle.putString("detail", notificationArrayList.get(position).getText());
                bundle.putStringArrayList("imageList", notificationArrayList.get(position).getImageList());
                bundle.putString("Severity", notificationArrayList.get(position).getSeverity());
                Fragment fragment = new NotificationDetailsFragment();
                fragment.setArguments(bundle);
                activityCallback.onButtonClick(fragment, false);
            }
        });

        return view;
    }


    private void prepareToCallAsyntaskWithRemainingIds() {
        String localIds = "";
        for (int i = 0; i < alistNotification.size(); i++) {
            localIds = localIds + alistNotification.get(i).getIdCategory() + ",";
        }
        if (alistNotification.size() > 0) {
            localIds = localIds.substring(0, localIds.length() - 1);
        }
        Constants.filterToCategoryNotification = localIds;

        if (alistNotification.size() == 0) {
            horizontalListView.setVisibility(View.GONE);
        }
    }

    private void getNotification(String pageNumber) {
        if (cc == 1) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait Data is loading...");
            progressDialog.show();
        } else {
            notificationList.addFooterView(ftView, null, false);
        }
        String uri = Constants.BaseUrl + "notification";


        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));
            json.put("PageNo", pageNumber);
            if (!Constants.filterToCategoryNotification.isEmpty()) {
                json.put("Filter", Constants.filterToCategoryNotification);
            }

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
        try {

            String status = json.optString("Status");
            String count = json.optString("Count");

            if (status.equalsIgnoreCase("1")) {
                JSONArray jsonArray = json.getJSONArray("notification");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    GeneralNotification notification = new GeneralNotification();

                    String ID = jsonObject.optString("ID");
                    String RWAID = jsonObject.optString("RWAID");
                    String Severity = jsonObject.optString("Severity");
                    String Title = "";
                    String Text = "";

                    try {
                        Title = common.funConvertBase64ToString(jsonObject.optString("Title"));
                        Text = common.funConvertBase64ToString(jsonObject.optString("Text"));
                    } catch (Exception e) {
                        Title = jsonObject.optString("Title");
                        Text = jsonObject.optString("Text");
                    }

                    String Image = jsonObject.optString("Image");
                    String Image1 = jsonObject.optString("Image1");
                    String Image2 = jsonObject.optString("Image2");
                    String Image3 = jsonObject.optString("Image3");
                    String IsCalendarEvent = jsonObject.optString("IsCalendarEvent");
                    String Datecreated = jsonObject.optString("Datecreated");
                    String CreatedBy = jsonObject.optString("CreatedBy");
                    String EventStartDate = jsonObject.optString("EventStartDate");
                    String EventEndDateTime = jsonObject.optString("EventEndDateTime");
                    String Status = jsonObject.optString("Status");

                    notification.setID(ID);
                    notification.setRWAID(RWAID);
                    if (Severity.equalsIgnoreCase("null") || Severity.equalsIgnoreCase("0"))
                        Severity = "1";
                    notification.setSeverity(Severity);
                    notification.setTitle(Title);
                    notification.setText(Text);

                    ArrayList<String> listDocImage = new ArrayList<>();

                    if (!Image.trim().isEmpty()) {
                        String[] arr = Image.split(",");
                        for (String s : arr) {
                            if (s.endsWith(".png") || s.endsWith(".jpg")) {
                                listDocImage.add(s);
                            }
                        }
                    } else if (!Image1.trim().isEmpty() || !Image2.trim().isEmpty() || !Image3.trim().isEmpty()) {
                        if (!Image1.trim().isEmpty()) listDocImage.add(Image1);
                        if (!Image2.trim().isEmpty()) listDocImage.add(Image2);
                        if (!Image3.trim().isEmpty()) listDocImage.add(Image3);
                        ;
                    }

                    notification.setImageList(listDocImage);
                    notification.setIsCalendarEvent(IsCalendarEvent);
                    notification.setDatecreated(Datecreated);
                    notification.setCreatedBy(CreatedBy);
                    notification.setEventStartDate(EventStartDate);
                    notification.setEventEndDateTime(EventEndDateTime);
                    notification.setStatus(Status);
                    notificationArrayList.add(notification);

                }
            } else {
                loadingCompleted = true;
            }


            if (notificationArrayList.size() > 0) {
                nolisttext.setVisibility(View.GONE);
                notificationList.setVisibility(View.VISIBLE);
                if (cc == 1) {
                    adapter = new GMemberNotificationsAdapter(getActivity(), notificationArrayList);
                    notificationList.setAdapter(adapter);
                } else {
                    notificationList.removeFooterView(ftView);
                }
                adapter.updateReceiptsList(notificationArrayList);
                common.setStringValue(Constants.notificationLastSeenTime, String.valueOf(common.getUnixTime()));
            } else {
                notificationList.setVisibility(View.GONE);
                nolisttext.setVisibility(View.VISIBLE);

            }

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (notificationList.getFooterViewsCount() != 0) {
                notificationList.removeFooterView(ftView);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
