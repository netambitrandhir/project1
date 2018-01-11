package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.instabug.library.invocation.InstabugInvocationMode;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sanganan.app.R;
import com.sanganan.app.adapters.ClassifiedListAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.InsertToMangoDb;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.ClassifiedModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pranav on 30/11/16.
 */

public class ClassifiedDetails extends BaseFragment {

    View view;
    Common common;
    RequestQueue requestQueue;
    TextView postedby, postedon, classifiedtitle, descriptionField;
    ImageView classifiedpic, reportspam, remove;
    String addedById, image1link, image2link, image3link, nametext, titletext, flattext, addedontext, desctext, userId, id, classifiedId;

    private Typeface karlaB, wsMedium, ubuntuB, wsRegular;
    private ArrayList<String> listImages = new ArrayList<>();
    private ArrayList<String> finalListImage = new ArrayList<>();
    RelativeLayout postedbyBlock;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.classified_details_layout, container, false);
        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        TextView title = (TextView) view.findViewById(R.id.textViewtitle);
        title.setTypeface(ubuntuB);

        initializeVariables(view);


        postedbyBlock = (RelativeLayout) view.findViewById(R.id.postedbyBlock);

        Bundle bundle = getArguments();
        classifiedId = bundle.getString("classifiedId");
        id = bundle.getString("Id");
        userId = bundle.getString("userId");
        image1link = bundle.getString("image1");
        image2link = bundle.getString("image2");
        image3link = bundle.getString("image3");
        nametext = bundle.getString("name");
        addedById = bundle.getString("addedBy");
        flattext = bundle.getString("flat");
        addedontext = bundle.getString("addedOn");
        titletext = bundle.getString("title");
        desctext = bundle.getString("desc");


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        Bundle logBundleInitial = new Bundle();
        logBundleInitial.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleInitial.putString("user_id", common.getStringValue(Constants.id));
        logBundleInitial.putString("Classified_Id", id);
        mFirebaseAnalytics.logEvent("Classified_Details", logBundleInitial);

        listImages.add(image1link);
        listImages.add(image2link);
        listImages.add(image3link);


        for (int i = 0; i < listImages.size(); i++) {
            if (!listImages.get(i).equalsIgnoreCase("null") && !listImages.get(i).equalsIgnoreCase("")) {
                finalListImage.add(listImages.get(i));
            }
        }

        if (userId.equalsIgnoreCase(common.getStringValue(Constants.id))) {
            remove.setVisibility(View.VISIBLE);
            reportspam.setVisibility(View.GONE);
        }

        Picasso.with(getActivity()).load(image1link).placeholder(R.drawable.galleryplacholder).into(classifiedpic);
        postedby.setText("By " + nametext + ", " + flattext);
        postedon.setText("Posted at " + addedontext);
        descriptionField.setText(desctext);
        classifiedtitle.setText(titletext);


        postedby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                Fragment fragment = new DetailsNeighbourFragment();
                bundle1.putString("ID", userId);
                fragment.setArguments(bundle1);
                activityCallback.onButtonClick(fragment, false);

            }
        });


        classifiedpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalListImage.size() != 0) {
                    DialogFragment fragment = new ImageSlideShow();
                    Bundle bundle1 = new Bundle();
                    bundle1.putStringArrayList("imagelist", finalListImage);
                    fragment.setArguments(bundle1);
                    fragment.setRetainInstance(true);
                    fragment.show(getFragmentManager(), "tag_slide_show");
                }
            }
        });


        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (common.isNetworkAvailable()) {
                    getData();
                } else {
                    common.showShortToast("No internet");
                }
            }
        });

        reportspam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Instabug.setUserEmail(common.getStringValue(Constants.email));
                Instabug.setUserData("Classified Detail Page  Classified Id : " + classifiedId + "User ID who reported : "
                        + common.getStringValue(Constants.id) + " Society ID : " + common.getStringValue("ID"));
                Instabug.invoke(InstabugInvocationMode.PROMPT_OPTION);
            }
        });

        classifiedtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                common.showShortToast("not implemented");
            }
        });


        return view;
    }

    private void initializeVariables(View view) {
        karlaB = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");
        wsMedium = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Medium.ttf");
        wsRegular = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");

        postedby = (TextView) view.findViewById(R.id.postedby);
        postedby.setTypeface(karlaB);
        postedon = (TextView) view.findViewById(R.id.postedon);
        postedon.setTypeface(karlaB);
        classifiedtitle = (TextView) view.findViewById(R.id.classifiedtitle);
        classifiedtitle.setTypeface(wsMedium);
        descriptionField = (TextView) view.findViewById(R.id.descriptionField);
        descriptionField.setMovementMethod(new ScrollingMovementMethod());
        descriptionField.setTypeface(wsRegular);

        classifiedpic = (ImageView) view.findViewById(R.id.classifiedpic);
        reportspam = (ImageView) view.findViewById(R.id.reportspam);
        remove = (ImageView) view.findViewById(R.id.remove);
    }


    private void getData() {

        common.showSpinner(getActivity());

        String uri = Constants.BaseUrl + "removeclassified";
        try {
            JSONObject json = new JSONObject();
            json.put("AddedBy", addedById);
            json.put("ID", classifiedId);

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
            String status = "";
            String message = "";
            common.hideSpinner();

            status = json.optString("Status");
            message = json.optString("Classifieds");
            common.showShortToast(message);
            if (status.equalsIgnoreCase("1")) {
                Constants.isAnyClassifiedCreatedOrRemoved = true;
                InsertToMangoDb insertToMangoDb = new InsertToMangoDb(getActivity(), "classified", classifiedId, "", "");
                insertToMangoDb.deleteDataFromServer();
                Constants.isAnyNewPostAdded = true;
                getActivity().onBackPressed();
            }


        } catch (Exception e) {

        }

    }
}
