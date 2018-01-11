package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sanganan.app.R;
import com.sanganan.app.activities.FullViewGalleryImages;
import com.sanganan.app.adapters.GalleryAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.GalleryData;
import com.sanganan.app.model.GalleryParent;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 10/10/16.
 */

public class SocietyGalleryFragment extends BaseFragment {

    View view;
    TextView title, nolisttext;
    Typeface ubantuBold, worksansR;
    ListView galleryListView;
    ImageView addImagePic;
    Common common;
    RequestQueue requestQueue;

    ProgressDialog progressDialog;
    String url = Constants.BaseUrl + "photogallery";
    GalleryAdapter galleryAdapter;

    private final static String TAG = "SocietyGalleryFragment";

    boolean loadingComplete = false;

    boolean userScrolled = false;
    int cc = 1;

    private int mLastFirstVisibleItem;
    private boolean mIsScrollingUp;

    public View ftView;
    public boolean isLoading = false;


    ArrayList<String> dateList = new ArrayList<>();
    public static ArrayList<GalleryParent> parentList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.gallery_layout, container, false);
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        initializeVariables(view);
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        if (Constants.rolesGivenToUser.contains("Can_Add_pic")) {
            addImagePic.setVisibility(View.VISIBLE);
        } else {
            addImagePic.setVisibility(View.GONE);
        }

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = layoutInflater.inflate(R.layout.lazy_loading_footer_view, null);

        addImagePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment1 = new AddPicToGallleryFragment();
                activityCallback.onButtonClick(fragment1, false);
            }
        });

        if (common.isNetworkAvailable()) {
            if (parentList.size() == 0 || Constants.isAnyPicAddedOrRemoved) {
                parentList.clear();
                cc = 1;
                userScrolled = false;
                loadingComplete = false;
                getData("1");
            } else {
                galleryAdapter = new GalleryAdapter(getActivity(), parentList);
                galleryListView.setAdapter(galleryAdapter);
                galleryListView.setVisibility(View.VISIBLE);

            }
        } else {
            common.showShortToast("no internet...!!");
        }


        galleryListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                // If scroll state is touch scroll then set userScrolledActionCompleted
                // true
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }


                if (view.getId() == galleryListView.getId()) {
                    final int currentFirstVisibleItem = galleryListView.getFirstVisiblePosition();

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
                if (userScrolled && !mIsScrollingUp && !loadingComplete
                        && firstVisibleItem + visibleItemCount == totalItemCount) {

                    userScrolled = false;
                    isLoading = true;
                    cc = cc + 1;
                    getData(String.valueOf(cc));
                }
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = galleryListView.getChildAt(0);
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
                    View v = galleryListView.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {


                        return;
                    }
                }
            }

        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        galleryAdapter = new GalleryAdapter(getActivity(), parentList);
        galleryListView.setAdapter(galleryAdapter);
        galleryListView.setVisibility(View.VISIBLE);
        galleryListView.setSelection(Constants.positionListEdited);

    }

    private void initializeVariables(View view) {
        common = Common.getNewInstance(getActivity());
        ubantuBold = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        worksansR = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");

        title = (TextView) view.findViewById(R.id.titletextView);
        title.setTypeface(ubantuBold);
        nolisttext = (TextView) view.findViewById(R.id.nolisttext);
        nolisttext.setTypeface(worksansR);

        galleryListView = (ListView) view.findViewById(R.id.galleryListView);
        addImagePic = (ImageView) view.findViewById(R.id.addpic_image);

    }


    private void getData(String pageNo) {
        if (cc == 1) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait Data is loading...");
            progressDialog.show();
        } else {
            galleryListView.addFooterView(ftView, null, false);
        }


        String uri = url;
        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));
            json.put("PageNo", pageNo);
            json.put("ResidentID", common.getStringValue(Constants.id));

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
            String status = "";

            status = json.optString("Status");
            if (status.equalsIgnoreCase("1")) {
                JSONArray jsonArray = json.getJSONArray("Date");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    GalleryParent galleryParent = new GalleryParent();
                    String date = object.optString("Date");
                    galleryParent.setDate(date);
                    dateList.add(date);

                    if (object.has("PhotoGallery")) {
                        JSONArray arrayRowByDate = object.getJSONArray("PhotoGallery");
                        ArrayList<GalleryData> listOfImages = new ArrayList<>();

                        for (int j = 0; j < arrayRowByDate.length(); j++) {
                            JSONObject gObj = arrayRowByDate.getJSONObject(j);
                            GalleryData galleryData = new GalleryData();

                            galleryData.setFirstName(gObj.optString("FirstName"));
                            galleryData.setFlatNbr(gObj.optString("FlatNbr"));
                            galleryData.setPhotoId(gObj.optString("PhotoID"));

                            galleryData.setLIKES(gObj.optString("LIKES"));
                            galleryData.setIsLIKES(gObj.optString("IsLIKES"));
                            try {
                                galleryData.setPhotoCaption(common.funConvertBase64ToString(gObj.optString("PhotoCaption")));
                            } catch (Exception e) {
                                e.printStackTrace();
                                galleryData.setPhotoCaption(gObj.optString("PhotoCaption"));
                            }
                            galleryData.setPhotoPath(gObj.optString("PhotoPath"));
                            galleryData.setAddedBy(gObj.optString("AddedBy"));
                            listOfImages.add(galleryData);
                        }
                        galleryParent.setDataArrayList(listOfImages);
                        parentList.add(galleryParent);
                    }

                }
            } else {
                loadingComplete = true;
            }


            if (parentList.size()>0) {
                nolisttext.setVisibility(View.GONE);
                galleryListView.setVisibility(View.VISIBLE);
                if (cc == 1) {
                    galleryAdapter = new GalleryAdapter(getActivity(), parentList);
                    galleryListView.setAdapter(galleryAdapter);
                    common.setStringValue(Constants.galleryLastSeenTime, String.valueOf(common.getUnixTime()));
                } else {
                    galleryListView.removeFooterView(ftView);
                }
            }else{
                galleryListView.setVisibility(View.GONE);
                nolisttext.setVisibility(View.VISIBLE);
            }

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            galleryAdapter.updateReceiptsList(parentList);

            if (galleryListView.getFooterViewsCount() != 0) {
                galleryListView.removeFooterView(ftView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
