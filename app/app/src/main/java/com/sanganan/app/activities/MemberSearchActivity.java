package com.sanganan.app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.sanganan.app.adapters.AdminMemberAdapter;
import com.sanganan.app.adapters.ComplainListAllAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.fragments.BaseFragment;
import com.sanganan.app.fragments.ComplainDetailsFragment;
import com.sanganan.app.fragments.DetailsNeighbourFragment;
import com.sanganan.app.interfaces.ToolbarListner;
import com.sanganan.app.model.ApprovedMember;
import com.sanganan.app.model.ComplainData;
import com.sanganan.app.model.Remark;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberSearchActivity extends ActionBarActivity implements ToolbarListner {

    ListView membersListView;
    FrameLayout frmlayout;
    RelativeLayout box1, box2, searchBoxTop;
    EditText editTextsearch;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    String editTextString = "";
    Common common;
    Typeface karlaR, karlaB;
    ArrayList<ApprovedMember> listMemberApproval = new ArrayList<>();
    ComplainListAllAdapter adapter;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private static String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    AdminMemberAdapter adminMemberAdapter;
    InputMethodManager imm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_search);

        initializeVariables();

        box1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                box1.setVisibility(View.GONE);
                box2.setVisibility(View.VISIBLE);
                editTextsearch.setFocusableInTouchMode(true);
                editTextsearch.requestFocus();
                editTextsearch.performClick();

                imm.showSoftInput(editTextsearch, InputMethodManager.SHOW_IMPLICIT);

            }
        });


        editTextsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    common.hideSoftKeyboard(MemberSearchActivity.this);
                    return true;
                }
                return false;
            }
        });


        membersListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                common.hideSoftKeyboard(MemberSearchActivity.this);
                return false;
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
                    listMemberApproval.clear();
                    membersListView.setVisibility(View.INVISIBLE);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    membersListView.setVisibility(View.VISIBLE);
                    listMemberApproval.clear();
                    requestQueue.cancelAll("tag_search");
                    getData();
                }
            }

        });

    }


    private void initializeVariables() {

        box1 = (RelativeLayout) findViewById(R.id.boxOne);
        box2 = (RelativeLayout) findViewById(R.id.boxTwo);
        editTextsearch = (EditText) findViewById(R.id.editTextsearch);
        membersListView = (ListView) findViewById(R.id.searchedList);
        searchBoxTop = (RelativeLayout) findViewById(R.id.searchBox);
        frmlayout = (FrameLayout) findViewById(R.id.container_body);

        karlaR = Typeface.createFromAsset(this.getAssets(), "Karla-Regular.ttf");
        karlaB = Typeface.createFromAsset(this.getAssets(), "Karla-Bold.ttf");
        common = Common.getNewInstance(this);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    private void getData() {
        String uri = Constants.BaseUrl + "approvesocietymembernew";
        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));
            json.put("Name", editTextString);
            json.put("Uid", common.getStringValue("ID"));

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
        }else {
            finish();
        }

        ///if requirement demands search page to show add a condition here


    }

    void parsedData(JSONObject json) {
        try {
            String status = "";
            listMemberApproval.clear();

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

        adminMemberAdapter = new AdminMemberAdapter(this, listMemberApproval);
        membersListView.setAdapter(adminMemberAdapter);

        adminMemberAdapter.notifyDataSetChanged();
        membersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                common.hideSoftKeyboard(MemberSearchActivity.this);
                setContentView(R.layout.any_notification_activity);
                membersListView.setVisibility(View.GONE);
                frmlayout.setVisibility(View.VISIBLE);
                searchBoxTop.setVisibility(View.GONE);
                Fragment fragment = new DetailsNeighbourFragment();
                fragmentManager = getSupportFragmentManager();
                Bundle bundle1 = new Bundle();
                bundle1.putString("ID", listMemberApproval.get(position).getId());
                bundle1.putBoolean("adminVisitor", true);
                bundle1.putString("fromMemberSearch", "yes");
                fragment.setArguments(bundle1);

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit,
                        R.anim.slide_in_left, R.anim.slide_in_right);
                fragmentTransaction.replace(R.id.container_body_notification, fragment);
                fragmentTransaction.addToBackStack(fragment.getClass().getName());

                try {
                    fragmentTransaction.commit();
                } catch (IllegalStateException ignored) {
                }
            }
        });
    }

}
