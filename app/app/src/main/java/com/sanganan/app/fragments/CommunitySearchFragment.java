package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.sanganan.app.R;
import com.sanganan.app.adapters.HelperSearchAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.ToolbarListner;
import com.sanganan.app.model.HelperModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by raj on 10/24/2016.
 */

public class CommunitySearchFragment extends ActionBarActivity implements ToolbarListner {


    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    View view;
    ImageView byname, bytype, ivsearchicon, selectedicon, selectedicon4;
    TextView tvsearch;
    EditText searchEditText;
    RelativeLayout searchLayout, searchLayoutBelow;
    Typeface ubuntuB;
    String searchCatogry[] = {"Name", "ServiceOffered"};
    String category = "Name";
    String edittextString;
    Common common;
    RequestQueue requestQueue;
    ArrayList<HelperModel> searchList = new ArrayList<>();
    ListView searchedList;
    HelperSearchAdapter helperAdapter;
    public static boolean isFromBack = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.helper_search_layout);

        initializeView();
        searchList.clear();

        common = Common.getNewInstance(this);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        common.hideKeyboard(CommunitySearchFragment.this);

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.GONE);
                searchLayoutBelow.setVisibility(View.VISIBLE);
                searchEditText.setFocusableInTouchMode(true);
                searchEditText.requestFocus();
                searchEditText.performClick();
                common.showSoftKeyboard(CommunitySearchFragment.this);
            }
        });


        byname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchList.clear();
                selectedicon.setVisibility(View.VISIBLE);
                selectedicon4.setVisibility(View.INVISIBLE);
                category = searchCatogry[0];
                edittextString = searchEditText.getText().toString();
                requestQueue.cancelAll("tag_search");
                getData();

            }
        });
        bytype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchList.clear();
                selectedicon.setVisibility(View.INVISIBLE);
                selectedicon4.setVisibility(View.VISIBLE);
                category = searchCatogry[1];
                edittextString = searchEditText.getText().toString();
                requestQueue.cancelAll("tag_search");
                getData();
            }
        });


        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    common.hideSoftKeyboard(CommunitySearchFragment.this);
                    return true;
                }
                return false;
            }
        });


        searchedList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                common.hideSoftKeyboard(CommunitySearchFragment.this);
                return false;
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                edittextString = s.toString();

                if (!edittextString.isEmpty()) {
                    searchedList.setVisibility(View.VISIBLE);
                    requestQueue.cancelAll("tag_search");
                    getData();
                } else {
                    searchedList.setVisibility(View.GONE);
                    if (helperAdapter != null) {
                        helperAdapter.notifyDataSetChanged();
                    }
                }

            }
        });

        searchedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                common.hideSoftKeyboard(CommunitySearchFragment.this);
                setContentView(R.layout.any_notification_activity);
                Fragment fragment = new CommunityHelperDetails();
                Bundle bundle = new Bundle();
                bundle.putString("helperId", searchList.get(position).getHelperId());
                bundle.putString("profile_pic", searchList.get(position).getProfilePhoto());
                bundle.putString("fromSearch", CommunitySearchFragment.class.getName());
                fragment.setArguments(bundle);
                onButtonClick(fragment, false);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragmentManager = getSupportFragmentManager();
        if (requestCode == CommunityHelperDetails.REQUEST_CODE_EDIT_HELPER) {
            if (resultCode == 123) {
                Fragment fragment = new CommunityHelperDetails();
                Bundle bundle = data.getExtras();
                fragment.setArguments(bundle);
                onButtonClickNoBack(fragment);

            } else if (resultCode == 1234) {
                finish();
            }
        }
    }

    private void initializeView() {

        ubuntuB = Typeface.createFromAsset(this.getAssets(), "Ubuntu-B.ttf");


        searchedList = (ListView) findViewById(R.id.searchedList);
        byname = (ImageView) findViewById(R.id.byname);
        bytype = (ImageView) findViewById(R.id.bytype);
        ivsearchicon = (ImageView) findViewById(R.id.ivsearchicon);
        selectedicon = (ImageView) findViewById(R.id.selectedicon3);
        selectedicon4 = (ImageView) findViewById(R.id.selectedicon4);
        tvsearch = (TextView) findViewById(R.id.tvsearch);
        searchLayout = (RelativeLayout) findViewById(R.id.searhLayout);
        searchLayoutBelow = (RelativeLayout) findViewById(R.id.searhLayoutBelow);
        searchEditText = (EditText) findViewById(R.id.etsearch);


    }

    private void getData() {


        String uri = Constants.BaseUrl + "allhelper";
        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));
            json.put("UserID", common.getStringValue(Constants.id));
            json.put(category, edittextString);


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
                    headers.put("Country", "Singapore");
                    return headers;
                }
            };
            try {
                req.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                req.setTag("tag_search");
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

            searchList.clear();
            String status = json.optString("Status");
            if (status.equals("1")) {
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
                    String ProfilePhoto = object.optString("ProfilePhoto");
                    String PoilceVerificationScanImage1 = object.optString("OtherDocScanImage1");
                    String PoliceVerificationScanImage2 = object.optString("OtherDocScanImage2");
                    String PoliceVerificationScanImage3 = object.optString("OtherDocScanImage3");
                    String EntryCardExpiry = object.optString("EntryCardExpiry");
                    String IsActive = object.optString("IsActive");
                    String rating = object.optString("Rated");


                    helperModel.setID(ID);
                    helperModel.setRWAID(RWAID);
                    helperModel.setName(Name);
                    helperModel.setHelperId(ID);
                    helperModel.setServiceOffered(ServiceOffered);
                    helperModel.setResidentialAddress(ResidentialAddress);
                    helperModel.setPrimaryContactNbr(PrimaryContactNbr);
                    helperModel.setPhoneNbr2(PhoneNbr2);
                    helperModel.setEmailId(EmailId);
                    helperModel.setAddedBy(AddedBy);
                    helperModel.setAddedOn(AddedOn);
                    helperModel.setAddedOn(AddedOn);
                    helperModel.setProfilePhoto(ProfilePhoto);
                    helperModel.setPoilceVerificationScanImage1(PoilceVerificationScanImage1);
                    helperModel.setPoliceVerificationScanImage2(PoliceVerificationScanImage2);
                    helperModel.setPoliceVerificationScanImage3(PoliceVerificationScanImage3);
                    helperModel.setEntryCardExpiry(EntryCardExpiry);
                    helperModel.setIsActive(IsActive);
                    helperModel.setRating(rating);
                    searchList.add(helperModel);

                    if (helperAdapter != null) {
                        helperAdapter.notifyDataSetChanged();
                    }

                }

                helperAdapter = new HelperSearchAdapter(this, searchList);
                searchedList.setAdapter(helperAdapter);
            } else {
                searchList.clear();
                if (helperAdapter != null) {
                    helperAdapter.notifyDataSetChanged();
                }
            }
            // Toast.makeText(this, "FavLength:" + GetFavouriteList.hsHelper.size(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

        }


    }

    @Override
    public void onResume() {
        super.onResume();
       /* if (isFromBack) {
            searchLayout.setVisibility(View.GONE);
            searchLayoutBelow.setVisibility(View.VISIBLE);
            isFromBack = false;
        }*/
    }


    @Override
    public void onButtonClick(Fragment thisfragment, Boolean isCommingBack) {
        Common.hideSoftKeyboard(this);
        fragment = (BaseFragment) thisfragment;
        fragmentManager = getSupportFragmentManager();
        if (isCommingBack) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body_notification, fragment);

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
    public void onButtonClickNoBack(Fragment fragment11) {
        fragment = (BaseFragment) fragment11;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit,
                R.anim.slide_in_left, R.anim.slide_in_right);
        fragmentTransaction.replace(R.id.container_body_notification, fragment);
        fragmentTransaction.commit();
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
}