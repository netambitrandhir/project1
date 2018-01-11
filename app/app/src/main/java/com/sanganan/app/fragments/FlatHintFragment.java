package com.sanganan.app.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sanganan.app.adapters.FlatHintAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.UpdateFlatNo;
import com.sanganan.app.model.Flat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by pranav on 10/1/17.
 */

public class FlatHintFragment extends DialogFragment {

    View view;
    ListView flatsearchedlist;
    RelativeLayout searchLayout, searchLayoutBelow;
    EditText searchEditText;
    String edittextString;
    Common common;
    RequestQueue requestQueue;
    Typeface ubuntuB;
    TextView tvsearch;
    ArrayList<Flat> arrayListFlat = new ArrayList<>();
    FlatHintAdapter adapter;
    UpdateFlatNo callBack;
    boolean isToast =true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.flatsearch_layout, container, false);
        common = Common.getNewInstance(getActivity());
        callBack = (UpdateFlatNo) getActivity();
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        initializeView();


        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.GONE);
                searchLayoutBelow.setVisibility(View.VISIBLE);
            }
        });

        if (common.isNetworkAvailable()) {
            getData();
        } else {
            common.showShortToast("No internet");
        }

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = searchEditText.getText().toString().toLowerCase(Locale.getDefault());
                if (arrayListFlat.size() > 0) {
                    adapter.filter(text);
                } else if(isToast){
                    isToast = false;
                    common.showShortToast("No flats added in society");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });


        flatsearchedlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //    new SignupFirstPage().setFlat(arrayListFlat.get(position));
                callBack.setFlat(arrayListFlat.get(position));
                dismiss();
            }
        });

        return view;
    }

    private void initializeView() {

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        flatsearchedlist = (ListView) view.findViewById(R.id.flatsearchedlist);
        tvsearch = (TextView) view.findViewById(R.id.tvsearch);
        searchLayout = (RelativeLayout) view.findViewById(R.id.searhLayout);
        searchLayoutBelow = (RelativeLayout) view.findViewById(R.id.searhLayoutBelow);
        searchEditText = (EditText) view.findViewById(R.id.etsearch);


    }

    private void getData() {


        common.showSpinner(getActivity());
        String uri = Constants.BaseUrl + "flatnumber";
        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));

            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();

                                if (response.optString("Status").equalsIgnoreCase("1")) {

                                    JSONArray array = response.getJSONArray("FlatNbr");
                                    arrayListFlat.clear();
                                    // arrayListFlat.add(edittextString);
                                    for (int i = 0; i < array.length(); i++) {
                                        Flat flat = new Flat();
                                        JSONObject object = array.getJSONObject(i);
                                        String flatName = object.optString("FlatNbr");
                                        String flatId = object.optString("ID");
                                        flat.setId(flatId);
                                        flat.setName(flatName);
                                        arrayListFlat.add(flat);
                                    }

                                    adapter = new FlatHintAdapter(getActivity(), arrayListFlat);
                                    flatsearchedlist.setAdapter(adapter);
                                }
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
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


}
