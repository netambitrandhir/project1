package com.sanganan.app.fragments;

import android.app.ProgressDialog;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sanganan.app.R;
import com.sanganan.app.adapters.SearchListNeighbourAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.ToolbarListner;
import com.sanganan.app.model.YourNeighbourSearch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by randhir  on 10/24/2016.
 */

public class YourNeighbourSearchFragment extends ActionBarActivity implements ToolbarListner {


    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ImageView byname, byprofession, byflatno, bycarno, ivsearchicon, selectedicon1,
            selectedicon2, selectedicon3, selectedicon4;
    ProgressDialog progressDialog;
    TextView tvsearch;
    RelativeLayout searhLayout;
    EditText searchEditText;
    RelativeLayout searchLayout, searchLayoutBelow;
    Typeface ubuntuB;
    String searchCatogry[] = {"FirstName", "Occupation", "FlatNbr", "VehicleNbr"};
    String category = "FirstName";
    String edittextString;
    Common common;
    RequestQueue requestQueue;
    ArrayList<YourNeighbourSearch> searchList = new ArrayList<>();
    ListView searchedList;
    SearchListNeighbourAdapter adapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yourneighbourssearchfragment);

        initializeView();

        common = Common.getNewInstance(this);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        common.hideKeyboard(YourNeighbourSearchFragment.this);


        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.GONE);
                searchLayoutBelow.setVisibility(View.VISIBLE);
                searchEditText.setFocusableInTouchMode(true);
                searchEditText.requestFocus();
                searchEditText.performClick();
                common.showSoftKeyboard(YourNeighbourSearchFragment.this);
            }
        });


        byname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedicon1.setVisibility(View.VISIBLE);
                selectedicon2.setVisibility(View.INVISIBLE);
                selectedicon3.setVisibility(View.INVISIBLE);
                selectedicon4.setVisibility(View.INVISIBLE);
                category = searchCatogry[0];
                searchEditText.setHint("Search");
                edittextString = searchEditText.getText().toString();
                if (!edittextString.isEmpty()) {
                    requestQueue.cancelAll("tag_search");
                    getData();
                } else {
                    searchList.clear();
                    adapter = new SearchListNeighbourAdapter(YourNeighbourSearchFragment.this, searchList);
                    searchedList.setAdapter(adapter);
                }

            }
        });
        byprofession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedicon1.setVisibility(View.INVISIBLE);
                selectedicon2.setVisibility(View.VISIBLE);
                selectedicon3.setVisibility(View.INVISIBLE);
                selectedicon4.setVisibility(View.INVISIBLE);
                category = searchCatogry[1];
                searchEditText.setHint("Search");
                edittextString = searchEditText.getText().toString();
                if (!edittextString.isEmpty()) {
                    requestQueue.cancelAll("tag_search");
                    getData();
                } else {
                    searchList.clear();
                    adapter = new SearchListNeighbourAdapter(YourNeighbourSearchFragment.this, searchList);
                    searchedList.setAdapter(adapter);
                }
            }
        });

        byflatno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedicon1.setVisibility(View.INVISIBLE);
                selectedicon2.setVisibility(View.INVISIBLE);
                selectedicon3.setVisibility(View.VISIBLE);
                selectedicon4.setVisibility(View.INVISIBLE);
                category = searchCatogry[2];
                searchEditText.setHint("Search");
                edittextString = searchEditText.getText().toString();
                if (!edittextString.isEmpty()) {
                    requestQueue.cancelAll("tag_search");
                    getData();
                } else {
                    searchList.clear();
                    adapter = new SearchListNeighbourAdapter(YourNeighbourSearchFragment.this, searchList);
                    searchedList.setAdapter(adapter);
                }
            }
        });

        bycarno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedicon1.setVisibility(View.INVISIBLE);
                selectedicon2.setVisibility(View.INVISIBLE);
                selectedicon3.setVisibility(View.INVISIBLE);
                selectedicon4.setVisibility(View.VISIBLE);
                category = searchCatogry[3];
                searchEditText.setHint("Can search by last 4-digits of no.");
                edittextString = searchEditText.getText().toString();
                if (!edittextString.isEmpty()) {
                    requestQueue.cancelAll("tag_search");
                    getData();
                } else {
                    searchList.clear();
                    adapter = new SearchListNeighbourAdapter(YourNeighbourSearchFragment.this, searchList);
                    searchedList.setAdapter(adapter);
                }
            }
        });


        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    common.hideSoftKeyboard(YourNeighbourSearchFragment.this);
                    return true;
                }
                return false;
            }
        });


        searchedList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                common.hideSoftKeyboard(YourNeighbourSearchFragment.this);
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

                edittextString = searchEditText.getText().toString();
                if (edittextString.isEmpty()) {
                    searchList.clear();
                    searchedList.setVisibility(View.GONE);
                    adapter = new SearchListNeighbourAdapter(YourNeighbourSearchFragment.this, searchList);
                    searchedList.setAdapter(adapter);
                } else {
                    searchedList.setVisibility(View.VISIBLE);
                    searchList.clear();
                    requestQueue.cancelAll("tag_search");
                    getData();

                }

            }
        });

    }
    

    private void initializeView() {

        ubuntuB = Typeface.createFromAsset(this.getAssets(), "Ubuntu-B.ttf");


        searchedList = (ListView) findViewById(R.id.searchedList);
        byname = (ImageView) findViewById(R.id.byname);
        byprofession = (ImageView) findViewById(R.id.byprofession);
        byflatno = (ImageView) findViewById(R.id.byflatno);
        bycarno = (ImageView) findViewById(R.id.bytype);
        ivsearchicon = (ImageView) findViewById(R.id.ivsearchicon);
        selectedicon1 = (ImageView) findViewById(R.id.selectedicon1);
        selectedicon2 = (ImageView) findViewById(R.id.selectedicon2);
        selectedicon3 = (ImageView) findViewById(R.id.selectedicon3);
        selectedicon4 = (ImageView) findViewById(R.id.selectedicon4);
        tvsearch = (TextView) findViewById(R.id.tvsearch);
        searchLayout = (RelativeLayout) findViewById(R.id.searhLayout);
        searchLayoutBelow = (RelativeLayout) findViewById(R.id.searhLayoutBelow);
        searchEditText = (EditText) findViewById(R.id.etsearch);


    }

    private void getData() {


        String uri = Constants.BaseUrl + "neighbors";
        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));
            json.put(category, edittextString);
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
                            searchList.clear();
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }

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
                searchList.clear();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            searchList.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

        }
    }

    void parsedData(JSONObject json) {
        try {

            searchList.clear();
            JSONArray jsonArray = json.getJSONArray("Neighbours");
            String status = json.optString("Status");
            if (status.equals("1")) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    YourNeighbourSearch modelNeighbour = new YourNeighbourSearch();

                    String ID = object.optString("ID");
                    String PhoneNbr = object.optString("PhoneNbr");
                    String EmailID = object.optString("EmailID");
                    String Password = object.optString("Password");
                    String FlatNbr = object.optString("FlatNbr");
                    String FirstName = object.optString("FirstName");
                    String MiddleName = object.optString("MiddleName");
                    String LastName = object.optString("LastName");
                    String ProfilePic = object.optString("ProfilePic");
                    String Gender = object.optString("Gender");
                    String Occupation = object.optString("Occupation");
                    String AddedOn = object.optString("AddedOn");
                    String VehicleNbr = object.optString("VehicleNbr");
                    String IsActive = object.optString("IsActive");


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
                    searchList.add(modelNeighbour);

                }


            }
        } catch (Exception e) {

        }

        adapter = new SearchListNeighbourAdapter(this, searchList);
        searchedList.setAdapter(adapter);

        searchedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                common.hideSoftKeyboard(YourNeighbourSearchFragment.this);
                setContentView(R.layout.any_notification_activity);
                Bundle bundle = new Bundle();
                bundle.putString("ID", searchList.get(position).getID());
                bundle.putString("fromMemberSearch","yes");
                Fragment fragment = new DetailsNeighbourFragment();
                fragment.setArguments(bundle);
                onButtonClick(fragment, false);

            }
        });

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
        }else {
            finish();
        }


        ///if requirement demands search page to show add a condition here


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
    public void onButtonClickNoBack(Fragment fragment) {
    }

}

  