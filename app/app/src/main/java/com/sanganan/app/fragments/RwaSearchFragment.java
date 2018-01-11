package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.sanganan.app.activities.MainHomePageActivity;
import com.sanganan.app.adapters.SearchListAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.model.SearchModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 25/10/16.
 */

public class RwaSearchFragment extends Fragment {

    View view;
    ListView searchedList;
    RelativeLayout box1, box2;
    EditText editTextsearch;
    boolean aBoolean = false;
    ListView startSearchList;

    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    String editTextString = "";
    Common common;
    ImageView contactusmail;
    Bundle bundle;

    ArrayList<SearchModel> listRWA = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.search_rwa_layout, container, false);


        initializeVariables();

        box1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!aBoolean) {
                    aBoolean = true;
                    box1.setVisibility(View.GONE);
                    box2.setVisibility(View.VISIBLE);
                    editTextsearch.setFocusableInTouchMode(true);
                    editTextsearch.requestFocus();
                    editTextsearch.performClick();
                    common.showSoftKeyboard(getActivity());
                }
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
                requestQueue.cancelAll("tag_search");
                getData();
            }
        });

        contactusmail = (ImageView)view.findViewById(R.id.contactusmail);


        contactusmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "[mynukad] : New Society registration");
                intent.putExtra(Intent.EXTRA_TEXT, "Thanks for adding to mynukad, please fill below form:\n" +
                        "\n" +
                        "Society name :\n" +
                        "Society address :\n" +
                        "Phone number :\n" +
                        "Contact person name :\n" +
                        "\n" +
                        "Thanks,\n" +
                        "Team mynukad\n");
                intent.setData(Uri.parse("mailto:info@mynukad.com")); // or just "mailto:" for blank
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                startActivity(intent);
            }
        });


        editTextsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    common.hideSoftKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });


        startSearchList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                common.hideSoftKeyboard(getActivity());
                return false;
            }
        });

        return view;
    }

    private void initializeVariables() {

        bundle = getArguments();
        box1 = (RelativeLayout) view.findViewById(R.id.boxOne);
        box2 = (RelativeLayout) view.findViewById(R.id.boxTwo);
        editTextsearch = (EditText) view.findViewById(R.id.editTextsearch);
        startSearchList = (ListView) view.findViewById(R.id.searchedList);
        TextView info = (TextView) view.findViewById(R.id.info);
        Typeface karlaR = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");
        Typeface karlaB = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");
        info.setTypeface(karlaB);

        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();


    }

    private void getData() {

        String uri = Constants.BaseUrl+"rwabyname";
        try {
            JSONObject json = new JSONObject();
            json.put("Name", editTextString);


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

            listRWA.clear();
            JSONArray jsonArray = json.getJSONArray("Societyname Details");
            String status = json.optString("Status");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                SearchModel model = new SearchModel();

                String societyId = object.optString("ID");
                String SocietyName = object.optString("Name");
                String Image = object.optString("BannerImage");
                String Address1 = object.optString("Address1");
                String Address2 = object.optString("Address2");
                String locality = object.optString("Locality");
                String City = object.optString("City");
                String PinCode = object.optString("PostalCode");
                String IsActive = object.optString("IsActive");
                String distance = object.optString("distance");

                String SocietyPrivateinfo = object.optString("SocietyPrivateinfo");
                String SocietyPublicInfo = object.optString("SocietyPublicInfo");
                String SocietyBanner = object.optString("SocietyBanner");
                String SocietyPendingInfo = object.optString("SocietyPendingInfo");

                String channelUrl = object.optString("ChatUrl");

                String latitudeLocal = object.optString("Latitude");
                String longitudeLocal = object.optString("Longitude");

                model.setSocietyId(societyId);
                model.setSocietyName(SocietyName);
                model.setImage(Image);
                model.setAddress1(Address1);
                model.setAddress2(Address2);
                model.setLocality(locality);
                model.setCity(City);
                model.setPinCode(PinCode);
                model.setIsActive(IsActive);
                model.setDistance(distance);
                model.setLatitude(latitudeLocal);
                model.setLongitude(longitudeLocal);
                model.setSocietyPrivateinfo(SocietyPrivateinfo);
                model.setSocietyPublicInfo(SocietyPublicInfo);
                model.setSocietyBanner(SocietyBanner);
                model.setSocietyPendingInfo(SocietyPendingInfo);
                model.setChatUrl(channelUrl);

                listRWA.add(model);

            }

        } catch (Exception e) {

        }


        SearchListAdapter adapter = new SearchListAdapter(getActivity(), listRWA);
        startSearchList.setAdapter(adapter);

        startSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                common.setStringValue("ID", listRWA.get(position).getSocietyId());
                common.setStringValue("SocietyName", listRWA.get(position).getSocietyName());
                common.setStringValue("Image", listRWA.get(position).getImage());
                common.setStringValue("Address1", listRWA.get(position).getAddress1());
                common.setStringValue("Address2", listRWA.get(position).getAddress2());
                common.setStringValue("Address3", listRWA.get(position).getLocality());
                common.setStringValue("City", listRWA.get(position).getCity());
                common.setStringValue("PinCode", listRWA.get(position).getPinCode());
                common.setStringValue(Constants.LATITUDE, listRWA.get(position).getPinCode());
                common.setStringValue(Constants.LONGITUDE, listRWA.get(position).getPinCode());

                common.setStringValue(Constants.SocietyPrivateinfo, listRWA.get(position).getSocietyPrivateinfo());
                common.setStringValue(Constants.SocietyPublicInfo, listRWA.get(position).getSocietyPublicInfo());
                common.setStringValue(Constants.SocietyBanner, listRWA.get(position).getSocietyBanner());
                common.setStringValue(Constants.SocietyPendingInfo, listRWA.get(position).getSocietyPendingInfo());
                common.setStringValue("chat", listRWA.get(position).getChatUrl());
                common.setStringValue(Constants.LATITUDE, listRWA.get(position).getLatitude());
                common.setStringValue(Constants.LONGITUDE, listRWA.get(position).getLongitude());


                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                if(bundle.getString("fromclass").equals(SignupFirstPage.class.getName())){
                    getActivity().finish();
                }
                else{
                    Intent i = new Intent(getActivity(), MainHomePageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", listRWA.get(position).getSocietyName());
                    i.putExtras(bundle);
                    startActivity(i);
                    getActivity().finish();
                }


            }
        });
    }
}
