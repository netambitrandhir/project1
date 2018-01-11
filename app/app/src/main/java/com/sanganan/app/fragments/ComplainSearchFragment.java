package com.sanganan.app.fragments;


import android.app.ProgressDialog;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.google.gson.Gson;
import com.sanganan.app.R;

import com.sanganan.app.adapters.ComplainListAllAdapter;

import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.ToolbarListner;
import com.sanganan.app.model.ComplainData;
import com.sanganan.app.model.Remark;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 26/9/16.
 */

public class ComplainSearchFragment extends ActionBarActivity implements ToolbarListner {

    ListView complainListView;
    RelativeLayout box1, box2;
    EditText editTextsearch;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    String editTextString = "";
    Common common;
    Typeface karlaR, karlaB;

    ArrayList<ComplainData> complainList = new ArrayList<>();
    ComplainListAllAdapter adapter;
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_complain_layout);

        initializeVariables();

        box1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                box1.setVisibility(View.GONE);
                box2.setVisibility(View.VISIBLE);
                editTextsearch.setFocusableInTouchMode(true);
                editTextsearch.requestFocus();
                editTextsearch.performClick();
                common.showSoftKeyboard(ComplainSearchFragment.this);
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

                if (editTextString.isEmpty()) {
                    complainList.clear();
                    complainListView.setVisibility(View.GONE);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    complainList.clear();
                    complainListView.setVisibility(View.VISIBLE);
                    requestQueue.cancelAll("tag_search");
                    getData();
                    Log.d("TAG_SEARCHED_AT", editTextString + "*********************************************************");
                }
            }

        });

        editTextsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    common.hideSoftKeyboard(ComplainSearchFragment.this);
                    return true;
                }
                return false;
            }
        });


        complainListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                common.hideSoftKeyboard(ComplainSearchFragment.this);
                return false;
            }
        });


        complainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                common.hideSoftKeyboard(ComplainSearchFragment.this);
                Bundle bundle = new Bundle();
                bundle.putString("title", complainList.get(position).getComplaintDetails());
                bundle.putString("date", complainList.get(position).getDateCreated());
                bundle.putString("flatNumber", complainList.get(position).getFlatNbr());
                bundle.putString("assignedby", complainList.get(position).getAssignedBy());
                bundle.putString("assignedto", complainList.get(position).getAssignedTo());
                bundle.putString("complainBy", complainList.get(position).getComplainBy());
                bundle.putString("Status", complainList.get(position).getStatus());
                bundle.putString("complainId", complainList.get(position).getID());
                bundle.putString("cmpId", complainList.get(position).getComplainByID());
                bundle.putString("Description", complainList.get(position).getDescription());


                ArrayList<String> imageList = new ArrayList<String>();
                imageList.add(complainList.get(position).getImage1());
                imageList.add(complainList.get(position).getImage2());
                imageList.add(complainList.get(position).getImage3());
                bundle.putStringArrayList("imageList", imageList);
                bundle.putString("rating", complainList.get(position).getRating());
                bundle.putString("feedbackText", complainList.get(position).getAcknowledgementNote());
                bundle.putString("phone", complainList.get(position).getHelperNumber());
                List<Remark> remarkList = complainList.get(position).getRemarks();
                Gson gson = new Gson();
                String jsonRemark = gson.toJson(remarkList);
                bundle.putString("remarkInGson", jsonRemark);
                bundle.putBoolean("fromComplainSearch", true);
                setContentView(R.layout.any_notification_activity);
                Fragment fragment = new ComplainDetailsFragment();
                fragment.setArguments(bundle);
                onButtonClick(fragment, false);

            }
        });
    }


    private void initializeVariables() {

        box1 = (RelativeLayout) findViewById(R.id.boxOne);
        box2 = (RelativeLayout) findViewById(R.id.boxTwo);
        editTextsearch = (EditText) findViewById(R.id.editTextsearch);
        complainListView = (ListView) findViewById(R.id.searchedList);
        karlaR = Typeface.createFromAsset(this.getAssets(), "Karla-Regular.ttf");
        karlaB = Typeface.createFromAsset(this.getAssets(), "Karla-Bold.ttf");
        common = Common.getNewInstance(this);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

    }


    private void getData() {
        String uri = Constants.BaseUrl + "showcomplain";
        try {
            JSONObject json = new JSONObject();
            json.put("IsCommonAreaComplaint", Constants.isCommonAreaComplain);
            json.put("Status", Constants.statusComplain);
            json.put("RWAID", common.getStringValue("ID"));
            json.put("PageNo", "1");
            json.put("RWAResidentFlatID", common.getStringValue(Constants.flatId));
            json.put("ComplaintDetails", editTextString);

            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedData(response);
                                Log.d(editTextString, s);

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
                req.setTag("tag_search");
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


    public void onButtonClick(Fragment newfragment, Boolean isCommingBack) {

        Common.hideSoftKeyboard(this);
        fragment = (BaseFragment) newfragment;
        fragmentManager = getSupportFragmentManager();
        if (isCommingBack) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);

            try {
                fragmentTransaction.commit();
            } catch (IllegalStateException ignored) {
            }
        } else {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body_notification, fragment);
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit,
                    R.anim.slide_in_left, R.anim.slide_in_right);
            fragmentTransaction.replace(R.id.container_body_notification, fragment);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());

            try {
                fragmentTransaction.commit();
            } catch (IllegalStateException ignored) {
            }

        }
    }

    @Override
    public void onButtonClickNoBack(Fragment fragment) {

    }


    @Override
    public void onBackPressed() {

        fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 1) {
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager()
                    .getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
            fragmentManager.popBackStack();
        } else if (fragmentManager.getBackStackEntryCount() == 1) {
            finish();
        } else {
            finish();
        }

        ///if requirement demands search page to show add a condition here


    }

    void parsedData(JSONObject json) {
        String status = "";
        try {
            status = json.optString("Status");

            if (status.equalsIgnoreCase("1")) {
                JSONArray jsonArray = json.getJSONArray("Show Complain");
                complainList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    ComplainData complainData = new ComplainData();
                    String ID = object.optString("ID");
                    String ComplaintDetails = "";
                    try {
                        ComplaintDetails = common.funConvertBase64ToString(object.optString("ComplaintDetails"));
                    } catch (Exception e) {
                        ComplaintDetails = object.optString("ComplaintDetails");
                    }
                    String DateCreated = object.optString("Datecreated");
                    String Image1 = object.optString("Image1");
                    String Image2 = object.optString("Image2");
                    String Image3 = object.optString("Image3");
                    String Description = object.optString("Description");
                    String FlatNbr = object.optString("FlatNbr");
                    String ComplainBy = object.optString("ComplainBy");
                    String AssignedBy = object.optString("AssignedBy");
                    String AcknowledgementNote = object.optString("AcknowledgementNote");
                    String Rating = object.optString("Rating");
                    String Status = object.optString("Status");
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
                    complainList.add(complainData);

                }
            }
        } catch (Exception e) {
        }

        adapter = new ComplainListAllAdapter(this, complainList);
        complainListView.setAdapter(adapter);


    }


}
