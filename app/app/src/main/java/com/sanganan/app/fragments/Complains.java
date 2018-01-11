package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.gson.Gson;
import com.sanganan.app.R;
import com.sanganan.app.adapters.ComplainListAllAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.ComplainData;
import com.sanganan.app.model.Remark;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 22/9/16.
 */

public class Complains extends BaseFragment {

    private JsonObjectRequest req;
    View view;
    ListView listedcomplains;
    ImageView notificationIcon, resolved, unResolved, personal, society, complainNow, showOff;
    ArrayList<ComplainData> listComplain = new ArrayList<>();
    Common common;

    ProgressDialog progressDialog;
    String RWAID = "5";
    String pageNo;
    private LayoutInflater mInflater;
    private ViewGroup mContainer;
    LinearLayout viewContentHolder;
    RelativeLayout complainInfoId;
    ArrayList<String> imageLink = new ArrayList<String>();
    RequestQueue requestQueue;
    ImageView addNew;
    Typeface ubantuB;
    ComplainListAllAdapter adapter;

    boolean userScrolled = false;
    int cc = 1;

    private int mLastFirstVisibleItem;
    private boolean mIsScrollingUp;
    private boolean loadingCompleted = false;
    ImageView search;

    public View ftView;
    boolean isFromHome = false;
    boolean isResumedOnly = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();


        if (b.containsKey("home")) {
            isFromHome = b.getBoolean("home");
            if (isFromHome) {
                Constants.isCommonAreaComplain = "0";
                Constants.statusComplain = "0";
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.complains, container, false);
        isResumedOnly = false;
        mInflater = inflater;
        mContainer = container;
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        viewContentHolder = (LinearLayout) view.findViewById(R.id.viewContentHolder);
        complainInfoId = (RelativeLayout) view.findViewById(R.id.complainInfoId);
        search = (ImageView) view.findViewById(R.id.search);

        ubantuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);


        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = layoutInflater.inflate(R.layout.lazy_loading_footer_view, null);

        common = new Common(getActivity());

        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        TextView titletextView = (TextView) view.findViewById(R.id.titletextView);
        titletextView.setTypeface(ubantuB);

        listedcomplains = (ListView) view.findViewById(R.id.listedcomplains);
        addNew = (ImageView) view.findViewById(R.id.addnew);


        resolved = (ImageView) view.findViewById(R.id.resolved);
        unResolved = (ImageView) view.findViewById(R.id.unresolved);

        society = (ImageView) view.findViewById(R.id.society);
        personal = (ImageView) view.findViewById(R.id.personal);
        showOff = (ImageView) view.findViewById(R.id.showOff);
        complainNow = (ImageView) view.findViewById(R.id.complainNow);


        TextView title = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title.setText("Complains");
        title.setVisibility(View.VISIBLE);

        Typeface ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        title.setTypeface(ubuntuB);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ComplainSearchFragment.class);
                startActivity(intent);
            }
        });

        if (Constants.statusComplain.equals("0")) {
            resolved.setImageResource(R.drawable.resolved_unselected);
            unResolved.setImageResource(R.drawable.unresolved_selected);
        } else if (Constants.statusComplain.equals("0")) {
            resolved.setImageResource(R.drawable.resolved_selected);
            unResolved.setImageResource(R.drawable.unresolved_unselected);

        }

        if (Constants.isCommonAreaComplain.equals("0")) {
            showOff.setImageResource(R.drawable.bottomimagepersonalseelcted);

        } else if (Constants.isCommonAreaComplain.equals("1")) {
            showOff.setImageResource(R.drawable.bottomimagesocietyseelcted);

        }

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddComplaintActivity.class);
                startActivity(intent);
            }
        });

        resolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.statusComplain = "1";
                resolved.setImageResource(R.drawable.resolved_selected);
                unResolved.setImageResource(R.drawable.unresolved_unselected);
                if (common.isNetworkAvailable()) {
                    listComplain.clear();
                    if (adapter != null) {
                        adapter.updateReceiptsList(listComplain);
                    }
                    cc = 1;
                    userScrolled = false;
                    loadingCompleted = false;
                    mLastFirstVisibleItem = 0;
                    mIsScrollingUp = false;

                    if (req != null)
                        req.cancel();
                    getData("1");
                } else {
                    Toast.makeText(getActivity(), "No internet...!!", Toast.LENGTH_SHORT);
                }
            }
        });

        unResolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.statusComplain = "0";
                resolved.setImageResource(R.drawable.resolved_unselected);
                unResolved.setImageResource(R.drawable.unresolved_selected);
                if (common.isNetworkAvailable()) {
                    listComplain.clear();
                    if (adapter != null) {
                        adapter.updateReceiptsList(listComplain);
                    }
                    cc = 1;
                    userScrolled = false;
                    loadingCompleted = false;
                    mLastFirstVisibleItem = 0;
                    mIsScrollingUp = false;

                    if (req != null)
                        req.cancel();
                    getData("1");

                } else {
                    Toast.makeText(getActivity(), "No internet...!!", Toast.LENGTH_SHORT);
                }
            }
        });

        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.isCommonAreaComplain = "0";
                showOff.setImageResource(R.drawable.bottomimagepersonalseelcted);

                if (common.isNetworkAvailable()) {
                    listComplain.clear();
                    if (adapter != null) {
                        adapter.updateReceiptsList(listComplain);
                    }
                    cc = 1;
                    userScrolled = false;
                    loadingCompleted = false;
                    mLastFirstVisibleItem = 0;
                    mIsScrollingUp = false;

                    if (req != null)
                        req.cancel();
                    getData("1");

                } else {
                    Toast.makeText(getActivity(), "No internet...!!", Toast.LENGTH_SHORT);
                }
            }
        });

        society.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.isCommonAreaComplain = "1";
                showOff.setImageResource(R.drawable.bottomimagesocietyseelcted);
                if (common.isNetworkAvailable()) {
                    listComplain.clear();
                    if (adapter != null) {
                        adapter.updateReceiptsList(listComplain);
                    }
                    cc = 1;
                    userScrolled = false;
                    loadingCompleted = false;
                    mLastFirstVisibleItem = 0;
                    mIsScrollingUp = false;

                    if (req != null)
                        req.cancel();
                    getData("1");

                } else {
                    Toast.makeText(getActivity(), "No internet...!!", Toast.LENGTH_SHORT);
                }

            }
        });

        complainNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddComplaintActivity.class);
                startActivity(intent);
            }
        });


        listedcomplains.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                // If scroll state is touch scroll then set userScrolledActionCompleted
                // true
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }


                if (view.getId() == listedcomplains.getId()) {
                    final int currentFirstVisibleItem = listedcomplains.getFirstVisiblePosition();

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
                    View v = listedcomplains.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        return;
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem) {
                    View v = listedcomplains.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:

                        // Setup refresh listener which triggers new data loading

                        return;
                    }
                }
            }

        });

        listedcomplains.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                try {
                    Log.d("Nuk", "Nukkad fragment onClick start");
                    Bundle bundle = new Bundle();
                    bundle.putString("title", listComplain.get(position).getComplaintDetails());
                    bundle.putString("date", listComplain.get(position).getDateCreated());
                    bundle.putString("flatNumber", listComplain.get(position).getFlatNbr());
                    bundle.putString("assignedby", listComplain.get(position).getAssignedBy());
                    bundle.putString("assignedto", listComplain.get(position).getAssignedTo());
                    bundle.putString("complainBy", listComplain.get(position).getComplainBy());
                    bundle.putString("Status", listComplain.get(position).getStatus());
                    bundle.putString("complainId", listComplain.get(position).getID());
                    bundle.putString("cmpId", listComplain.get(position).getComplainByID());
                    bundle.putString("categoryId", listComplain.get(position).getCategoryID());
                    bundle.putString("Description", listComplain.get(position).getDescription());

                    ArrayList<String> imageList = new ArrayList<String>();
                    imageList.add(listComplain.get(position).getImage1());
                    imageList.add(listComplain.get(position).getImage2());
                    imageList.add(listComplain.get(position).getImage3());
                    bundle.putStringArrayList("imageList", imageList);
                    bundle.putString("rating", listComplain.get(position).getRating());
                    bundle.putString("feedbackText", listComplain.get(position).getAcknowledgementNote());
                    bundle.putString("phone", listComplain.get(position).getHelperNumber());
                    List<Remark> remarkList = listComplain.get(position).getRemarks();
                    Gson gson = new Gson();
                    String jsonRemark = gson.toJson(remarkList);
                    bundle.putString("remarkInGson", jsonRemark);

                    Fragment fragment = new ComplainDetailsFragment();
                    fragment.setArguments(bundle);
                    activityCallback.onButtonClick(fragment, false);
                    Log.d("Nuk", "Nukkad fragment onClick end");
                } catch (Exception e) {
                    Log.e("Nuk", "Exception at: " + e.getMessage());
                }
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
            listedcomplains.addFooterView(ftView, null, false);
        }

        String uri = Constants.BaseUrl + "showcomplain";

        try {
            JSONObject json = new JSONObject();
            json.put("IsCommonAreaComplaint", Constants.isCommonAreaComplain);
            json.put("Status", Constants.statusComplain);
            json.put("RWAID", common.getStringValue("ID"));
            json.put("PageNo", pageNo);
            json.put("RWAResidentFlatID", common.getStringValue(Constants.flatId));

            req = new JsonObjectRequest(uri, json,
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
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }

    void parsedData(JSONObject json) {
        String status = null;

        try {
            status = json.optString("Status");
            if (status.equalsIgnoreCase("1")) {
                JSONArray jsonArray = json.getJSONArray("Show Complain");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    ComplainData complainData = new ComplainData();

                    String ID = object.optString("ID");

                    String DateCreated = object.optString("Datecreated");
                    String Image1 = object.optString("Image1");
                    String Image2 = object.optString("Image2");
                    String Image3 = object.optString("Image3");
                    String Description = object.optString("Description");
                    String categoryId = object.optString("CategoryID");
                    String ComplaintDetails = "";
                    try {
                        ComplaintDetails = common.funConvertBase64ToString(object.optString("ComplaintDetails"));
                    } catch (Exception e) {
                        ComplaintDetails = object.optString("ComplaintDetails");
                    }
                    String FlatNbr = object.optString("FlatNbr");
                    String ComplainBy = object.optString("ComplainBy");
                    String AssignedBy = object.optString("AssignedBy");
                    String AcknowledgementNote = object.optString("AcknowledgementNote");
                    String Rating = object.optString("Rating");
                    String Status = object.optString("Status");
                    if (Status.equalsIgnoreCase("null")) {
                        Status = "0";
                    }
                    String AssignedToName = object.optString("AssignedToName");
                    String AssignToNumber = object.optString("AssignToNumber");
                    String ComplainByID = object.optString("ComplainByID");
                    String ResolvedOn = object.optString("ResolvedOn");

                    boolean haveKeyRemark = object.has("Remark");

                    ArrayList<Remark> remarkArrayList = new ArrayList<>();
                    remarkArrayList.clear();

                    if (haveKeyRemark) {
                        JSONArray arrayRemark = object.getJSONArray("Remark");
                        for (int m = 0; m < arrayRemark.length(); m++) {
                            JSONObject remark = arrayRemark.getJSONObject(m);
                            Remark remarkObject = new Remark();
                            remarkObject.setID(remark.optString("ID"));
                            try {
                                remarkObject.setRemark(common.funConvertBase64ToString(remark.optString("Remark")));
                            } catch (Exception e) {
                                remarkObject.setRemark(remark.optString("Remark"));
                                e.printStackTrace();
                            }
                            String remarkDate = remark.optString("RemarkDate");
                            remarkDate = common.convertFromUnix1(remarkDate);
                            remarkObject.setRemarkDate(remarkDate);
                            remarkObject.setCompID(remark.optString("CompID"));
                            remarkObject.setEnteredBy(remark.optString("EnteredBy"));
                            remarkObject.setName(remark.optString("FirstName"));
                            remarkObject.setFlat(remark.optString("FlatNbr"));
                            remarkArrayList.add(remarkObject);
                        }
                    }

                    complainData.setRemarks(remarkArrayList);

                    complainData.setID(ID);
                    complainData.setComplaintDetails(ComplaintDetails);
                    complainData.setDateCreated(DateCreated);
                    complainData.setImage1(Image1);
                    complainData.setImage2(Image2);
                    complainData.setImage3(Image3);
                    complainData.setDescription(Description);
                    complainData.setCategoryID(categoryId);
                    complainData.setFlatNbr(FlatNbr);
                    complainData.setComplainBy(ComplainBy);
                    complainData.setAssignedBy(AssignedBy);
                    complainData.setAcknowledgementNote(AcknowledgementNote);
                    complainData.setRating(Rating);
                    complainData.setStatus(Status);
                    complainData.setAssignedTo(AssignedToName);
                    complainData.setComplainByID(ComplainByID);
                    complainData.setHelperNumber(AssignToNumber);
                    complainData.setResolvedOn(ResolvedOn);

                    listComplain.add(complainData);

                }
            } else {
                loadingCompleted = true;
            }

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (status.equalsIgnoreCase("1")) {

                complainInfoId.setVisibility(View.GONE);
                listedcomplains.setVisibility(View.VISIBLE);
                try {
                    if (cc == 1) {

                        adapter = new ComplainListAllAdapter(getActivity(), listComplain);
                        listedcomplains.setAdapter(adapter);


                    } else {
                        listedcomplains.removeFooterView(ftView);

                    }

                    if (adapter != null) {
                        adapter.updateReceiptsList(listComplain);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("AIOOBE", "Exception Caught");
                }
                common.setStringValue(Constants.complaintLastSeenTime, String.valueOf(common.getUnixTime()));

            } else if (listComplain.size() == 0) {
                complainInfoId.setVisibility(View.VISIBLE);
                listedcomplains.setVisibility(View.GONE);
            }
            if (listedcomplains.getFooterViewsCount() != 0) {
                listedcomplains.removeFooterView(ftView);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onResume() {
        super.onResume();

        if (Constants.statusComplain.equals("0")) {
            resolved.setImageResource(R.drawable.resolved_unselected);
            unResolved.setImageResource(R.drawable.unresolved_selected);
        } else if (Constants.statusComplain.equals("1")) {
            resolved.setImageResource(R.drawable.resolved_selected);
            unResolved.setImageResource(R.drawable.unresolved_unselected);

        }

        if (Constants.isCommonAreaComplain.equals("0")) {
            showOff.setImageResource(R.drawable.bottomimagepersonalseelcted);

        } else if (Constants.isCommonAreaComplain.equals("1")) {
            showOff.setImageResource(R.drawable.bottomimagesocietyseelcted);

        }


        if (common.isNetworkAvailable()) {

            if (listComplain.size() == 0 || Constants.isComplainStatusChangedOrEdited) {
                listComplain.clear();
                if (adapter != null) {
                    adapter.updateReceiptsList(listComplain);
                }
                cc = 1;
                userScrolled = false;
                loadingCompleted = false;
                Constants.isComplainStatusChangedOrEdited = false;
                getData("1");
            } else {
                adapter = new ComplainListAllAdapter(getActivity(), listComplain);
                listedcomplains.setAdapter(adapter);
            }
        } else {
            common.showShortToast("no internet");
        }

    }

}
