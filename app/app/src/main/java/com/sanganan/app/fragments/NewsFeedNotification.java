package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.sanganan.app.adapters.YourNeighborsAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.GeneralNotification;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 7/3/17.
 */
public class NewsFeedNotification extends BaseFragment {
    Common common;
    TextView rwaNotification, generalNotification, titletextView;
    View view;
    ListView notificationList;
    RequestQueue requestQueue;
    Typeface ubuntuB, worksansR;
    ProgressDialog progressDialog;
    ArrayList<GeneralNotification> notificationArrayList = new ArrayList<>();
    GMemberNotificationsAdapter adapter;


    boolean userScrolled = false;
    int cc = 1;

    private int mLastFirstVisibleItem;
    private boolean mIsScrollingUp;
    private boolean loadingCompleted = false;


    public View ftView;
    public boolean isLoading = false;
    TextView nolisttext;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.feed_notification, container, false);
        common = Common.getNewInstance(getActivity());

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        worksansR = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        TextView titletextView = (TextView) view.findViewById(R.id.titletextView);
        titletextView.setTypeface(ubuntuB);
        titletextView.setText("App Feeds");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        Bundle logBundleNotify = new Bundle();
        logBundleNotify.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleNotify.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("notification_clicked", logBundleNotify);


        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = layoutInflater.inflate(R.layout.lazy_loading_footer_view, null);


        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        notificationList = (ListView) view.findViewById(R.id.notificationList);
        nolisttext = (TextView) view.findViewById(R.id.nolisttext);
        nolisttext.setTypeface(worksansR);


        if (common.isNetworkAvailable()) {

            if (notificationArrayList.size() == 0) {
                notificationArrayList.clear();
                cc = 1;
                userScrolled = false;
                loadingCompleted = false;
                getNotification("1");
            } else {
                nolisttext.setVisibility(View.GONE);
                notificationList.setVisibility(View.VISIBLE);
                adapter = new GMemberNotificationsAdapter(getActivity(), notificationArrayList);
                notificationList.setAdapter(adapter);

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


        notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("title", notificationArrayList.get(position).getTitle());
                bundle.putString("date", notificationArrayList.get(position).getDatecreated());
                bundle.putString("detail", notificationArrayList.get(position).getText());
                bundle.putStringArrayList("imageList", notificationArrayList.get(position).getImageList());
                Fragment fragment = new NewsFeedNotificationDetails();
                fragment.setArguments(bundle);
                activityCallback.onButtonClick(fragment, false);
            }
        });

        return view;
    }

    private void getNotification(String pageNumber) {
        if (cc == 1) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait Data is loading...");
            progressDialog.show();
        } else {
            notificationList.addFooterView(ftView, null, false);
        }
        String uri = Constants.BaseUrl + "appnotification";


        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));
            json.put("PageNo", pageNumber);


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
                JSONArray jsonArray = json.getJSONArray("Appnotification");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    GeneralNotification notification = new GeneralNotification();

                    String ID = jsonObject.optString("ID");
                    String RWAID = jsonObject.optString("RWAID");
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
                    String IsCalendarEvent = jsonObject.optString("IsCalendarEvent");
                    String Datecreated = jsonObject.optString("DateCreated");
                    String CreatedBy = jsonObject.optString("CreatedBy");
                    String EventStartDate = jsonObject.optString("EventStartDate");
                    String EventEndDateTime = jsonObject.optString("EventEndDateTime");
                    String Status = jsonObject.optString("Status");

                    notification.setID(ID);
                    notification.setRWAID(RWAID);
                    notification.setSeverity("");
                    notification.setTitle(Title);
                    notification.setText(Text);
                    ArrayList<String> listDocImage = new ArrayList<>();
                    String[] arr = Image.split(",");

                    for (String s : arr) {
                        if (s.endsWith(".png") || s.endsWith(".jpg")) {
                            listDocImage.add(s);
                        }
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



            if(notificationArrayList.size()>0){
                notificationList.setVisibility(View.VISIBLE);
                nolisttext.setVisibility(View.GONE);
            }else{
                nolisttext.setVisibility(View.VISIBLE);
                notificationList.setVisibility(View.GONE);
            }

            if (cc == 1) {
                adapter = new GMemberNotificationsAdapter(getActivity(), notificationArrayList);
                notificationList.setAdapter(adapter);
            } else {
                adapter.updateReceiptsList(notificationArrayList);
                notificationList.removeFooterView(ftView);
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
