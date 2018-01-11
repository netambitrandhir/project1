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
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import com.sanganan.app.R;

import com.sanganan.app.adapters.GMemberNotificationsAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.ToolbarListner;

import com.sanganan.app.model.GeneralNotification;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;

/**
 * Created by root on 26/9/16.
 */

public class NotificationSearchActivity extends ActionBarActivity implements ToolbarListner {

    ListView listView;
    RelativeLayout box1, box2;
    EditText editTextsearch;
    RequestQueue requestQueue;
    String editTextString = "";
    Common common;
    Typeface karlaR, karlaB;

    ArrayList<GeneralNotification> list = new ArrayList<>();
    GMemberNotificationsAdapter adapter;
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
                common.showSoftKeyboard(NotificationSearchActivity.this);
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
                    list.clear();
                    listView.setVisibility(View.GONE);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    list.clear();
                    listView.setVisibility(View.VISIBLE);
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
                    common.hideSoftKeyboard(NotificationSearchActivity.this);
                    return true;
                }
                return false;
            }
        });


        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                common.hideSoftKeyboard(NotificationSearchActivity.this);
                return false;
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                common.hideSoftKeyboard(NotificationSearchActivity.this);
                setContentView(R.layout.any_notification_activity);
                Bundle bundle = new Bundle();
                bundle.putString("title", list.get(position).getTitle());
                bundle.putString("date", list.get(position).getDatecreated());
                bundle.putString("detail", list.get(position).getText());
                bundle.putStringArrayList("imageList", list.get(position).getImageList());
                bundle.putString("Severity", list.get(position).getSeverity());
                Fragment fragment = new NotificationDetailsFragment();
                fragment.setArguments(bundle);
                onButtonClick(fragment, false);


            }
        });
    }


    private void initializeVariables() {

        box1 = (RelativeLayout) findViewById(R.id.boxOne);
        box2 = (RelativeLayout) findViewById(R.id.boxTwo);
        editTextsearch = (EditText) findViewById(R.id.editTextsearch);
        listView = (ListView) findViewById(R.id.searchedList);
        karlaR = Typeface.createFromAsset(this.getAssets(), "Karla-Regular.ttf");
        karlaB = Typeface.createFromAsset(this.getAssets(), "Karla-Bold.ttf");
        common = Common.getNewInstance(this);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

    }


    private void getData() {
        String uri = Constants.BaseUrl + "notification";
        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));
            json.put("Search", editTextString);

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onButtonClick(Fragment newfragment, Boolean isCommingBack) {

        Common.hideSoftKeyboard(this);
        fragment = (BaseFragment) newfragment;
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
        try {

            String status = json.optString("Status");
            String count = json.optString("Count");

            if (status.equalsIgnoreCase("1")) {
                JSONArray jsonArray = json.getJSONArray("notification");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    GeneralNotification notification = new GeneralNotification();

                    String ID = jsonObject.optString("ID");
                    String RWAID = jsonObject.optString("RWAID");
                    String Severity = jsonObject.optString("Severity");
                    String Title = "";
                    String Text = "";

                    try {
                        Title = common.funConvertBase64ToString(jsonObject.optString("Title"));
                        Text = common.funConvertBase64ToString(jsonObject.optString("Text"));
                    } catch (Exception e) {
                        Title = jsonObject.optString("Title");
                        Text = jsonObject.optString("Text");
                    }
                    String Image = jsonObject.optString("image");
                    String IsCalendarEvent = jsonObject.optString("IsCalendarEvent");
                    String Datecreated = jsonObject.optString("Datecreated");
                    String CreatedBy = jsonObject.optString("CreatedBy");
                    String EventStartDate = jsonObject.optString("EventStartDate");
                    String EventEndDateTime = jsonObject.optString("EventEndDateTime");
                    String Status = jsonObject.optString("Status");

                    notification.setID(ID);
                    notification.setRWAID(RWAID);
                    if (Severity.equalsIgnoreCase("null") || Severity.equalsIgnoreCase("0"))
                        Severity = "1";
                    notification.setSeverity(Severity);
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
                    list.add(notification);

                }
            }
            adapter = new GMemberNotificationsAdapter(this, list);
            listView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
