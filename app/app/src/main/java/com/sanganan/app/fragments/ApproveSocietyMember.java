package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import com.sanganan.app.activities.MemberSearchActivity;
import com.sanganan.app.adapters.AdminMemberAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.ApprovedMember;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 5/10/16.
 */

public class ApproveSocietyMember extends BaseFragment {

    View view;
    ListView listMember;
    TextView localTitle;
    String url = Constants.BaseUrl + "approvesocietymembernew";
    RequestQueue requestQueue;
    ArrayList<ApprovedMember> listMemberApproval = new ArrayList<>();
    Common common;
    Typeface ubuntuBold;
    boolean userScrolled = false;
    int cc = 1;
    ImageView search;
    private int mLastFirstVisibleItem;
    private boolean mIsScrollingUp;
    private boolean loadingCompleted = false;
    public View ftView;
    public boolean isLoading = false;
    private static String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    AdminMemberAdapter adminMemberAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.admin_members_layout, container, false);
//        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        common = new Common(getActivity());
        Bundle bundle = getArguments();

        initializeVariables(view);
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = layoutInflater.inflate(R.layout.lazy_loading_footer_view, null);


        if (!bundle.containsKey("recieved")) {
            if (listMemberApproval.size() != 0) {
                adminMemberAdapter = new AdminMemberAdapter(getActivity(), listMemberApproval);
                listMember.setAdapter(adminMemberAdapter);
            } else {
                parsedData(Constants.responseForward);
            }
        } else {
            if (listMemberApproval.size() != 0) {
                adminMemberAdapter = new AdminMemberAdapter(getActivity(), listMemberApproval);
                listMember.setAdapter(adminMemberAdapter);
            } else {
               getData("1");
            }
        }



        listMember.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                // If scroll state is touch scroll then set userScrolledActionCompleted
                // true
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }


                if (view.getId() == listMember.getId()) {
                    final int currentFirstVisibleItem = listMember.getFirstVisiblePosition();

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
                    View v = listMember.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {

                        return;
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem) {
                    View v = listMember.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:

                        // Setup refresh listener which triggers new data loading

                        return;
                    }
                }
            }

        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent memberSearch = new Intent(getActivity(), MemberSearchActivity.class);
                startActivity(memberSearch);
            }
        });

        listMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new DetailsNeighbourFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString("ID", listMemberApproval.get(position).getId());
                bundle1.putBoolean("adminVisitor", true);
                fragment.setArguments(bundle1);
                activityCallback.onButtonClick(fragment, false);
            }
        });

        return view;
    }

    private void initializeVariables(View view) {

        listMember = (ListView) view.findViewById(R.id.listMember);
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        ubuntuBold = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        localTitle = (TextView) view.findViewById(R.id.titletextView);
        search = (ImageView) view.findViewById(R.id.search);
        localTitle.setTypeface(ubuntuBold);
    }

    private void getData(String pageNo) {
        listMember.addFooterView(ftView, null, false);
        String uri = url;
        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));
            json.put("Uid", common.getStringValue(Constants.id));
            json.put("PageNo", pageNo);

            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedData(response);
                                common.hideSpinner();
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            VolleyLog.d("error", error.getMessage());
                            listMember.removeFooterView(ftView);

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
                listMember.removeFooterView(ftView);
            }
            req.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        } catch (Exception e) {
            e.printStackTrace();
            listMember.removeFooterView(ftView);
        }
    }

    void parsedData(JSONObject json) {
        try {
            String status = "";


            JSONArray jsonArray = json.getJSONArray("approvesocietymember");
            status = json.optString("Status");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                ApprovedMember approvedMember = new ApprovedMember();
                approvedMember.setId(object.optString("ID"));
                approvedMember.setFirstName(object.optString("FirstName"));
                approvedMember.setLastName(object.optString("LastName"));
                approvedMember.setMiddleName(object.optString("MiddleName"));
                String addedOn = object.optString("AddedOn");
                String imageLink = object.optString("ProfilePic");
                String urlEncoded = Uri.encode(imageLink, ALLOWED_URI_CHARS);
                String isOwner = object.optString("IsOwner");

                approvedMember.setAddedOn(addedOn);
                approvedMember.setPhoneNbr(object.optString("PhoneNbr"));
                approvedMember.setEmailID(object.optString("EmailID"));
                approvedMember.setFlatNbr(object.optString("FlatNbr"));
                approvedMember.setPassword(object.optString("Password"));
                approvedMember.setApprovedOn(object.optString("ApprovedOn"));
                approvedMember.setApprovedBy(object.optString("ApprovedBy"));
                approvedMember.setApprovalStatus(object.optString("ApprovalStatus"));
                approvedMember.setGender(object.optString("Gender"));
                approvedMember.setOccupation(object.optString("Occupation"));
                approvedMember.setProfilePic(urlEncoded);
                approvedMember.setIsActive(object.optString("IsActive"));
                approvedMember.setTypeLiving(isOwner);

                listMemberApproval.add(approvedMember);
            }

        } catch (Exception e) {
            Log.d("Exception url encode", e.getMessage());
        }
        if (cc == 1) {
            adminMemberAdapter = new AdminMemberAdapter(getActivity(), listMemberApproval);
            listMember.setAdapter(adminMemberAdapter);
        } else {
            listMember.removeFooterView(ftView);
        }
        adminMemberAdapter.notifyDataSetChanged();
    }

}
