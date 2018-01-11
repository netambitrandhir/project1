package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.sanganan.app.adapters.YourNeighborsAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.YourNeighbourSearch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 3/8/16.
 */
public class YourNeighbours extends BaseFragment {

    View view;
    ListView neighboursList;
    Typeface ubuntuB;
    android.support.v4.app.FragmentManager fm;
    EditText searchEditText;
    ArrayList<YourNeighbourSearch> searchList = new ArrayList<>();
    ProgressDialog dialog;
    Common common;

    ImageView searchButton;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    YourNeighborsAdapter adapter;


    boolean userScrolled = false;
    int cc = 1;

    private int mLastFirstVisibleItem;
    private boolean mIsScrollingUp;
    private boolean loadingCompleted = false;

    public Handler mHandler;
    public View ftView;
    public boolean isLoading = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.your_neighbours, container, false);
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);
        Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);

        // mHandler = new MyHandler();
        common = Common.getNewInstance(getActivity());
        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        textViewTitle.setTypeface(ubuntuB);

        neighboursList = (ListView) view.findViewById(R.id.neighbours);
        searchButton = (ImageView) view.findViewById(R.id.searchButton);

        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = layoutInflater.inflate(R.layout.lazy_loading_footer_view, null);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), YourNeighbourSearchFragment.class);
                startActivity(intent);
            }
        });


        neighboursList.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                // If scroll state is touch scroll then set userScrolledActionCompleted
                // true
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }


                if (view.getId() == neighboursList.getId()) {
                    final int currentFirstVisibleItem = neighboursList.getFirstVisiblePosition();

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
                    View v = neighboursList.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        return;
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem) {
                    View v = neighboursList.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:

                        // Setup refresh listener which triggers new data loading

                        return;
                    }
                }
            }

        });
        if (common.isNetworkAvailable()) {

            if (searchList.size() == 0) {
                searchList.clear();
                cc = 1;
                userScrolled = false;
                loadingCompleted = false;
                getData("1");
            } else {
                adapter = new YourNeighborsAdapter(getActivity(), searchList);
                neighboursList.setAdapter(adapter);
            }
        } else {
            common.showShortToast("no internet");
        }


        neighboursList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("ID", searchList.get(position).getID());
                Fragment fragment = new DetailsNeighbourFragment();
                fragment.setArguments(bundle);
                activityCallback.onButtonClick(fragment, false);
            }
        });

        return view;
    }


    private void getData(String pageNo) {

        if (cc == 1) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait Data is loading...");
            progressDialog.show();
        } else {
            neighboursList.addFooterView(ftView, null, false);
        }
        String uri = Constants.BaseUrl + "neighbors";
        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));
            json.put("PageNo", pageNo);
            json.put("UID", common.getStringValue(Constants.id));


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
            if (status.equalsIgnoreCase("1")) {
                JSONArray jsonArray = json.getJSONArray("Neighbours");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    YourNeighbourSearch modelNeighbour = new YourNeighbourSearch();

                    String ID = object.optString("ID");
                    String PhoneNbr = object.optString("PhoneNbr");
                    String EmailID = object.optString("EmailID");
                    String Password = object.optString("Password");
                    String FlatNbr = object.optString("FlatNbr");
                    String FirstName = object.optString("FirstName").trim();
                    String MiddleName = object.optString("MiddleName");
                    String LastName = object.optString("LastName");
                    String ProfilePic = object.optString("ProfilePic");
                    String Gender = object.optString("Gender");
                    String Occupation = object.optString("Occupation");
                    String AddedOn = object.optString("AddedOn");
                    String VehicleNbr = object.optString("VehicleNbr");
                    String IsActive = object.optString("IsActive");
                    String IsPhonePublic = object.optString("IsPhonePublic");

                    modelNeighbour.setID(ID);
                    modelNeighbour.setPhoneNbr(PhoneNbr);
                    modelNeighbour.setEmailID(EmailID);
                    modelNeighbour.setPassword(Password);
                    modelNeighbour.setFlatNbr(FlatNbr);
                    modelNeighbour.setFirstName(FirstName);
                    modelNeighbour.setMiddleName(MiddleName);
                    modelNeighbour.setLastName(LastName);
                    modelNeighbour.setProfilePic(ProfilePic);
                    modelNeighbour.setGender(Gender);
                    modelNeighbour.setOccupation(Occupation);
                    modelNeighbour.setAddedOn(AddedOn);
                    modelNeighbour.setIsActive(IsActive);
                    modelNeighbour.setVehicleNbr(VehicleNbr);
                    modelNeighbour.setIsPhonePublic(IsPhonePublic);
                    searchList.add(modelNeighbour);
                }

            } else {
                loadingCompleted = true;
            }


            if (cc == 1) {
                adapter = new YourNeighborsAdapter(getActivity(), searchList);
                neighboursList.setAdapter(adapter);
                common.setStringValue(Constants.neighborsLastSeenTime, String.valueOf(common.getUnixTime()));
            } else {
                neighboursList.removeFooterView(ftView);
            }

            adapter.updateReceiptsList((searchList));
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }


            if (neighboursList.getFooterViewsCount() != 0) {
                neighboursList.removeFooterView(ftView);
            }

           /* Message message = mHandler.obtainMessage(1, searchList);
            mHandler.sendMessage(message);*/


        } catch (Exception e) {

        }

    }

    private void initializeVariables() {

        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("YOURS NEIGHBOURS");
    }


    public class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //Add loading view during search processing

                    isLoading = true;
                    break;


                case 1:
                    //Update data apater and UI
                    try {
                        updateList((ArrayList<YourNeighbourSearch>) msg.obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Remove loading view after update listview
                    neighboursList.removeFooterView(ftView);
                    isLoading = false;
                    break;

                default:
                    isLoading = false;
                    break;

            }

        }
    }

    public class ThreadGetMoreData extends Thread {
        @Override
        public void run() {
          /*  mHandler.sendEmptyMessage(0);
            cc = cc + 1;
            getData(String.valueOf(cc));*/


        }
    }


    public void updateList(ArrayList<YourNeighbourSearch> listUpdated) throws Exception {

    }


}