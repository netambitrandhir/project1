package com.sanganan.app.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sanganan.app.R;
import com.sanganan.app.adapters.CommunityHelperAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.GetFavouriteList;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.customview.AutoGridView;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.HelperModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommunityHelperFragment extends BaseFragment {
    View view;
    Common common;
    RequestQueue requestQueue;
    AutoGridView gridlist;
    CommunityHelperAdapter helperAdapter;
    ImageView searchButton, addhelper;
    ArrayList<HelperModel> helperSearchList = new ArrayList<>();
    Typeface ubuntuB;
    boolean userScrolled = false;
    int cc = 1;
    private int mLastFirstVisibleItem;
    private boolean mIsScrollingUp;
    private boolean loadingCompleted = false;
    public View ftView;
    public boolean isLoading = false;


    public CommunityHelperFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (helperSearchList.size() == 0) {
            view = inflater.inflate(R.layout.community_helpers_layout, container, false);
        }
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        TextView title = (TextView) view.findViewById(R.id.textViewtitle);
        title.setTypeface(ubuntuB);

        gridlist = (AutoGridView) view.findViewById(R.id.gridView);
        searchButton = (ImageView) view.findViewById(R.id.searchButton);
        addhelper = (ImageView) view.findViewById(R.id.addhelper);

        // LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = view.findViewById(R.id.loadItemsLayout_listView);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CommunitySearchFragment.class);
                startActivity(intent);
            }
        });

        addhelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddHelperActivity.class);
                startActivity(intent);
            }
        });


        gridlist.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                // If scroll state is touch scroll then set userScrolledActionCompleted
                // true
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;

                }


                if (view.getId() == gridlist.getId()) {
                    final int currentFirstVisibleItem = gridlist.getFirstVisiblePosition();

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
                    getData(String.valueOf(cc));
                }
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = gridlist.getChildAt(0);
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
                    View v = gridlist.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:

                        // Setup refresh listener which triggers new data loading

                        return;
                    }
                }
            }

        });


        gridlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new CommunityHelperDetails();
                Bundle bundle = new Bundle();
                bundle.putString("helperId", helperSearchList.get(position).getID());
                bundle.putString("profile_pic", helperSearchList.get(position).getProfilePhoto());
                fragment.setArguments(bundle);
                activityCallback.onButtonClick(fragment, false);

            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (common.isNetworkAvailable()) {

            if (helperSearchList.size() == 0 || Constants.isAnyHelperEditedorAdded) {
                helperSearchList.clear();
                cc = 1;
                userScrolled = false;
                loadingCompleted = false;

                getData("1");
                Constants.isAnyHelperEditedorAdded = false;
            }
        } else {
            common.showShortToast("no internet");
        }

    }

    private void getData(String pageNo) {


        if (cc == 1) {
            common.showSpinner(getActivity());
        } else {
            addhelper.setVisibility(View.INVISIBLE);
            ftView.setVisibility(View.VISIBLE);
        }

        String uri = Constants.BaseUrl + "allhelper";
        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));
            json.put("PageNo", pageNo);
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
                            common.hideSpinner();
                            //  getActivity().onBackPressed();
                            common.showShortToast(error.getMessage());

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
            if (status.equalsIgnoreCase("1")) {
                JSONArray jsonArray = json.getJSONArray("All  Helper");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    HelperModel helperModel = new HelperModel();

                    String ID = object.optString("ID");
                    String RWAID = object.optString("RWAID");
                    String Name = object.optString("Name");
                    String ServiceOffered = object.optString("ServiceOffered");
                    String ResidentialAddress = object.optString("ResidentialAddress");
                    String PrimaryContactNbr = object.optString("PrimaryContactNbr");
                    String PhoneNbr2 = object.optString("PhoneNbr2");
                    String EmailId = object.optString("EmailId");
                    String AddedBy = object.optString("AddedBy");
                    String AddedOn = object.optString("AddedOn");
                    String ServiceCharge = object.optString("ServiceCharges");
                    String ProfilePhoto = object.optString("ProfilePhoto");
                    String PoilceVerificationScanImage1 = object.optString("OtherDocScanImage1");
                    String PoliceVerificationScanImage2 = object.optString("OtherDocScanImage2");
                    String PoliceVerificationScanImage3 = object.optString("OtherDocScanImage3");
                    String EntryCardExpiry = object.optString("EntryCardExpiry");
                    String IsActive = object.optString("IsActive");
                    String rating = object.optString("Rated");
                    String is_in = object.optString("is_in");
                    String isFavourite = object.optString("IsFavAdded");
                    if (isFavourite.equalsIgnoreCase("1")) {
                        GetFavouriteList.hsHelper.add(ID);
                    }

                    helperModel.setID(ID);
                    helperModel.setRWAID(RWAID);
                    helperModel.setRating(rating);
                    helperModel.setName(Name);
                    helperModel.setHelperId(ID);
                    helperModel.setServiceOffered(ServiceOffered);
                    helperModel.setResidentialAddress(ResidentialAddress);
                    helperModel.setPrimaryContactNbr(PrimaryContactNbr);
                    helperModel.setPhoneNbr2(PhoneNbr2);
                    helperModel.setEmailId(EmailId);
                    helperModel.setAddedBy(AddedBy);
                    helperModel.setAddedOn(AddedOn);
                    helperModel.setServiceCharge(ServiceCharge);
                    helperModel.setProfilePhoto(ProfilePhoto);
                    helperModel.setPoilceVerificationScanImage1(PoilceVerificationScanImage1);
                    helperModel.setPoliceVerificationScanImage2(PoliceVerificationScanImage2);
                    helperModel.setPoliceVerificationScanImage3(PoliceVerificationScanImage3);
                    helperModel.setEntryCardExpiry(EntryCardExpiry);
                    helperModel.setIsActive(IsActive);
                    helperModel.setIs_in(is_in);
                    helperSearchList.add(helperModel);

                }

            } else {
                loadingCompleted = true;
                ftView.setVisibility(View.GONE);
            }

            common.hideSpinner();


            if (cc == 1) {
                helperAdapter = new CommunityHelperAdapter(getActivity(), helperSearchList);
                gridlist.setAdapter(helperAdapter);
                common.setStringValue(Constants.helperLastSeenTime, String.valueOf(common.getUnixTime()));
            } else {
                addhelper.setVisibility(View.VISIBLE);
                ftView.setVisibility(View.GONE);
            }

            helperAdapter.updateReceiptsList(helperSearchList);


        } catch (Exception e) {

        }


    }

}
