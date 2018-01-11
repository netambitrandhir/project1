package com.sanganan.app.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sanganan.app.R;
import com.sanganan.app.activities.AnyNotificationActivity;
import com.sanganan.app.adapters.HorizontalImageListComplainDetail;
import com.sanganan.app.adapters.RemarkAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.customview.HorizontalListView;
import com.sanganan.app.model.ComplainData;
import com.sanganan.app.model.ComplainRemark;
import com.sanganan.app.model.Remark;
import com.sanganan.app.model.WheelerModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComplainDetailsFragment extends BaseFragment {
    View view;
    Common common;
    TextView complainDetailsField, complainerName, complainDate, complainAssigneeName, headerText, feedbackText, feedbackField, complainerFlatNumber, feedbackgiven, assignedtoText,categoryNameTV;
    TextView remarkText;
    ListView remarkList;
    HorizontalListView listhorizontal;
    RelativeLayout afterFeedback, beforeFeedback;
    ImageView call_helper;
    Typeface ubuntuB, karlaB, karlaR;
    RatingBar ratingBar;
    String date, flatNumber, rating, feedbackString, userIdWhoComplained, Status, StatusName, ID, phoneNumber, remarkInGsonFormat,categoryName;
    String[] monthArray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    LinearLayout statusBox, assigneBox, remarkBox;

    String colorString[] = {"#6276e4", "#378500", "#d2001e", "#a98a73", "#92007d"};

    ArrayList<Remark> complainRemarks;
    ArrayList<String> listImages;
    ArrayList<String> listImagesFinal;
    String categoryId;
    ImageView addComment;
    boolean isFromSearchComplain = false;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;

    public ComplainDetailsFragment() {

    }

    TextView text1, text2, picsText, statusText1, receivedText, statusmessage;

    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.complain_details_layout, container, false);
        common = Common.getNewInstance(getActivity());
        common.hideKeyboard(getActivity());
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        final Bundle bundle = getArguments();
        categoryId = bundle.getString("categoryId");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        Bundle logBundleInitial = new Bundle();
        logBundleInitial.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleInitial.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("complaint_details", logBundleInitial);

        initializeVariable(view);


        if (bundle.containsKey("fromComplainSearch")) {
            isFromSearchComplain = bundle.getBoolean("fromComplainSearch");
        }

        complainDetailsField.setText(bundle.getString("title"));
        complainerName.setText(bundle.getString("complainBy").trim().split(" ")[0] + ",");
        complainDate.setText(bundle.getString("date"));
        String assidnedHelperName = bundle.getString("assignedto");
        categoryName = bundle.getString("Description");

        categoryNameTV.setText(categoryName);

        if(assidnedHelperName.isEmpty()){
            complainAssigneeName.setText("(no name found)");
        }else {
            complainAssigneeName.setText(assidnedHelperName);
        }
        date = bundle.getString("date");
        flatNumber = bundle.getString("flatNumber");
        rating = bundle.getString("rating");
        Status = bundle.getString("Status");
        ID = bundle.getString("complainId");
        phoneNumber = bundle.getString("phone");
        remarkInGsonFormat = bundle.getString("remarkInGson");

        Gson gson = new Gson();
        Type type = new TypeToken<List<Remark>>() {
        }.getType();
        complainRemarks = gson.fromJson(remarkInGsonFormat, type);

        headerText.setText("ID : " + ID);


        switch (Status) {
            case "0":
                StatusName = "Registered";
                statusBox.setBackgroundColor(Color.parseColor(colorString[0]));
                break;
            case "1":
                StatusName = "Assigned";
                statusBox.setBackgroundColor(Color.parseColor(colorString[1]));
                break;
            case "2":
                StatusName = "Resolved";
                statusBox.setBackgroundColor(Color.parseColor(colorString[2]));
                break;
            case "3":
                StatusName = "Acknowledged";
                statusBox.setBackgroundColor(Color.parseColor(colorString[3]));
                break;
            case "4":
                StatusName = "Invalid";
                statusBox.setBackgroundColor(Color.parseColor(colorString[4]));
                break;
        }


        feedbackString = bundle.getString("feedbackText");
        userIdWhoComplained = bundle.getString("cmpId");   // Disallow ScrollView to intercept touch events.

        String[] exactTime = date.split(" ");
        String[] arrDate = exactTime[0].split("/");
        Integer index = Integer.parseInt(arrDate[1]) - 1;
        date = "";
        date = arrDate[0] + " " + monthArray[index] + " " + arrDate[2];


        complainDate.setText(date);
        complainerFlatNumber.setText(flatNumber);
        feedbackgiven = (TextView) view.findViewById(R.id.feedbackgiven);
        feedbackgiven.setTypeface(karlaR);

        if (!(StatusName.equals("Acknowledged")) && ((Constants.rolesGivenToUser.contains("Can_Change_Status")) || common.getStringValue(Constants.id).equalsIgnoreCase(userIdWhoComplained))) {
            statusmessage.setText(StatusName + " (tap here to change)");
        } else {
            statusmessage.setText(StatusName);
        }


        statusBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(StatusName.equals("Acknowledged")) && ((Constants.rolesGivenToUser.contains("Can_Change_Status")) || common.getStringValue(Constants.id).equalsIgnoreCase(userIdWhoComplained))) {
                    Fragment fragment = new StatusChangeByAdmin();
                    Bundle bundle1 = new Bundle();
                    bundle1.putBundle("mainBundle", bundle);
                    bundle1.putString("ID", ID);
                    bundle1.putString("cmpId", userIdWhoComplained);
                    bundle1.putString("categoryId", categoryId);
                    if (!Constants.rolesGivenToUser.contains("Can_Change_Status")) {
                        bundle1.putString("not_admin", "yes");
                    }
                    bundle1.putString("status", StatusName);
                    if (StatusName.equals("Assigned")) {
                        bundle1.putString("assignedToName", bundle.getString("assignedto"));
                    }
                    fragment.setArguments(bundle1);
                    activityCallback.onButtonClick(fragment, false);
                }
            }
        });


        if (Status.equalsIgnoreCase("0")) {
            assigneBox.setVisibility(View.GONE);
        } else {
            assigneBox.setVisibility(View.VISIBLE);
        }

        int tempStatus = Integer.parseInt(Status);

        if (!common.getStringValue(Constants.id).equalsIgnoreCase(userIdWhoComplained) || !Status.equalsIgnoreCase("2")) {
            beforeFeedback.setVisibility(View.GONE);
        } else {
            beforeFeedback.setVisibility(View.VISIBLE);
        }


        if (!rating.isEmpty() || !feedbackString.isEmpty()) {
            String tempRating = rating;
            if (rating.isEmpty()) {
                tempRating = "0";
            }
            feedbackField.setText(feedbackString);
            ratingBar.setRating(Float.parseFloat(tempRating));
            afterFeedback.setVisibility(View.VISIBLE);

        } else {
            afterFeedback.setVisibility(View.GONE);
        }


        if (tempStatus < 2) {
            afterFeedback.setVisibility(View.GONE);
            beforeFeedback.setVisibility(View.GONE);
        }

        listImages = bundle.getStringArrayList("imageList");
        listImagesFinal = new ArrayList<>();
        for (int i = 0; i < listImages.size(); i++) {
            if (listImages.get(i).endsWith(".png") || listImages.get(i).endsWith(".jpg") || listImages.get(i).startsWith(Constants.imageUrlStartingTag)) {
                listImagesFinal.add(listImages.get(i));
            }
        }

        HorizontalImageListComplainDetail adapter = new HorizontalImageListComplainDetail(getActivity(), listImagesFinal);
        listhorizontal.setAdapter(adapter);


        listhorizontal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DialogFragment fragment = new ImageSlideShow();
                Bundle bundle1 = new Bundle();
                bundle1.putStringArrayList("imagelist", listImagesFinal);
                bundle1.putInt("position", position);
                fragment.setArguments(bundle1);
                fragment.setRetainInstance(true);
                fragment.show(getFragmentManager(), "tag_delivery");
            }
        });

        beforeFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (common.getStringValue(Constants.id).equalsIgnoreCase(userIdWhoComplained)) {
                    FeedbackFragment dialog = new FeedbackFragment();
                    dialog.setArguments(bundle);
                    dialog.setRetainInstance(true);
                    dialog.show(getFragmentManager(), "give_feedback_him_her");
                } else {
                    common.showShortToast("You are not allowed to give feedback.");
                }
            }
        });


        if (complainRemarks.size() != 0) {
            remarkBox.setVisibility(View.VISIBLE);
            RemarkAdapter remarkAdapter = new RemarkAdapter(getActivity(), complainRemarks);
            remarkList.setAdapter(remarkAdapter);
            setListViewHeightBasedOnItems(remarkAdapter, remarkList);
        } else {
            remarkBox.setVisibility(View.GONE);
        }

        if (Constants.rolesGivenToUser.contains("Can_Change_Status") || Constants.isCommonAreaComplain.equals("1") || common.getStringValue(Constants.id).equalsIgnoreCase(userIdWhoComplained)) {
            addComment.setVisibility(View.VISIBLE);
            remarkBox.setVisibility(View.VISIBLE);
            RemarkAdapter remarkAdapter = new RemarkAdapter(getActivity(), complainRemarks);
            remarkList.setAdapter(remarkAdapter);
            setListViewHeightBasedOnItems(remarkAdapter, remarkList);
        } else {
            addComment.setVisibility(View.GONE);
            remarkBox.setVisibility(View.GONE);
        }

        call_helper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phoneNumber.isEmpty() && !phoneNumber.equals("null") && !phoneNumber.equals("(null)")) {
                    AlertBoxToCall();
                } else {
                    common.showShortToast("Number not exist for assigned helper");
                }
            }
        });


        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AddComplainComment();
                Bundle commentBundle = new Bundle();
                commentBundle.putBundle("mainBundle", bundle);
                commentBundle.putString("compId", ID);
                fragment.setArguments(commentBundle);
                activityCallback.onButtonClick(fragment, false);
            }
        });

        complainDetailsField.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });


        return view;
    }

    public void AlertBoxToCall() {


        new AlertDialog.Builder(getActivity())
                .setTitle("Call")
                .setMessage(phoneNumber.trim())
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                String uri = "tel:" + phoneNumber.trim();
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse(uri));
                                getActivity().startActivity(callIntent);
                                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }


                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).setIcon(android.R.drawable.ic_dialog_alert).show();

    }


    void initializeVariable(View view) {
        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        karlaB = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");
        karlaR = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        headerText = (TextView) view.findViewById(R.id.headerText);
        headerText.setTypeface(ubuntuB);

        text1 = (TextView) view.findViewById(R.id.text1);
        text2 = (TextView) view.findViewById(R.id.text2);
        complainerName = (TextView) view.findViewById(R.id.complainerName);
        complainDate = (TextView) view.findViewById(R.id.complainDate);
        complainerFlatNumber = (TextView) view.findViewById(R.id.complainerFlatNumber);
        categoryNameTV = (TextView) view.findViewById(R.id.categoryName);
        categoryNameTV.setTypeface(ubuntuB);
        text1.setTypeface(ubuntuB);
        text2.setTypeface(ubuntuB);
        complainerName.setTypeface(ubuntuB);
        complainDate.setTypeface(ubuntuB);
        complainerFlatNumber.setTypeface(ubuntuB);

        picsText = (TextView) view.findViewById(R.id.picsText);
        picsText.setTypeface(karlaB);
        statusText1 = (TextView) view.findViewById(R.id.statusText1);
        statusText1.setTypeface(karlaB);
        statusmessage = (TextView) view.findViewById(R.id.statusmessage);
        statusmessage.setTypeface(karlaB);
        listhorizontal = (HorizontalListView) view.findViewById(R.id.listhorizontal);
        complainDetailsField = (TextView) view.findViewById(R.id.complainDetailsField);
        complainDetailsField.setTypeface(karlaR);
        complainDetailsField.setMovementMethod(new ScrollingMovementMethod());

        assignedtoText = (TextView) view.findViewById(R.id.assignedtoText);
        assignedtoText.setTypeface(karlaR);
        complainAssigneeName = (TextView) view.findViewById(R.id.complainAssigneeName);
        complainAssigneeName.setTypeface(karlaB);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

        feedbackText = (TextView) view.findViewById(R.id.feedbackText);
        feedbackText.setTypeface(karlaB);
        feedbackField = (TextView) view.findViewById(R.id.feedbacktext);
        feedbackField.setTypeface(karlaB);

        afterFeedback = (RelativeLayout) view.findViewById(R.id.afterFeedback);
        beforeFeedback = (RelativeLayout) view.findViewById(R.id.beforeFeedback);
        statusBox = (LinearLayout) view.findViewById(R.id.statusBox);
        assigneBox = (LinearLayout) view.findViewById(R.id.assigneBox);
        remarkBox = (LinearLayout) view.findViewById(R.id.remarkBox);

        remarkText = (TextView) view.findViewById(R.id.remarkText);
        remarkText.setTypeface(karlaR);
        remarkList = (ListView) view.findViewById(R.id.remarkList);

        call_helper = (ImageView) view.findViewById(R.id.call_helper);
        addComment = (ImageView) view.findViewById(R.id.addComment);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constants.isCommentAdded) {
            Constants.isCommentAdded = false;
            getDataComplaint();
        }

    }


    private void getDataComplaint() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl + "complaindetail";
        try {

            JSONObject json = new JSONObject();
            json.put("ID", ID);


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedDataComplaint(response);
                                progressDialog.dismiss();
                            } catch (Exception e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            VolleyLog.d("error", error.getMessage());
                            common.showShortToast("Network error");
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
                progressDialog.dismiss();
                e.printStackTrace();
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();

        }
    }

    private void parsedDataComplaint(JSONObject response) {

        String status = null;
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        try {
            status = response.optString("Status");

            common.setStringValue(Constants.complaintCount, "0");
            common.setStringValue(Constants.complaintLastSeenTime, String.valueOf(common.getUnixTime()));

            if (status.equalsIgnoreCase("1")) {
                JSONArray jsonArray = response.getJSONArray("Show Complain");

                JSONObject object = jsonArray.getJSONObject(0);
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
                            e.printStackTrace();
                            remarkObject.setRemark(remark.optString("Remark"));
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

                setRefreshedUi(complainData);
            }

        } catch (Exception e) {

        }


    }

    private void setRefreshedUi(ComplainData data) {

        ArrayList<Remark> remarkArrayList = data.getRemarks();
        if (remarkArrayList.size() != 0) {
            remarkBox.setVisibility(View.VISIBLE);
            RemarkAdapter remarkAdapter = new RemarkAdapter(getActivity(), remarkArrayList);
            remarkList.setAdapter(remarkAdapter);
            setListViewHeightBasedOnItems(remarkAdapter, remarkList);
        }
    }

    public static boolean setListViewHeightBasedOnItems(RemarkAdapter adapter, ListView listView) {

        RemarkAdapter listAdapter = adapter;
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

// Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 300 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

// Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);


// Set list height.

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();


            return true;

        } else {
            return false;
        }

    }


}
