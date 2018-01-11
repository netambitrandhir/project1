package com.sanganan.app.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationMode;
import com.sanganan.app.R;
import com.sanganan.app.adapters.HorizontalImageList;
import com.sanganan.app.adapters.UserRatingAdapter;
import com.sanganan.app.common.Alphabets;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.GetFavouriteList;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.customview.HorizontalListView;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.HelperModel;
import com.sanganan.app.model.UserComment;
import com.sanganan.app.utility.Utility;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 19/10/16.
 */


public class CommunityHelperDetails extends BaseFragment {

    View view;
    Common common;
    RequestQueue requestQueue;
    String helperId;
    Bundle bundle;
    HorizontalListView horizontalListView;
    ListView listView;
    boolean isRatingAvailable;
    HelperModel helperModelToBundle;
    TextView name, addedByName, profession, address, salary, textDocs, textRating, rating, textRC, title, textNameInitial, textnotify, attendence;
    ImageView ratingBack, profile_pic, colorBack, rateHimHer, reportHimHer, favorite_helper, call_helper, edit_helper_details, notifyStatus, isInSociety;
    ArrayList<UserComment> userList = new ArrayList<>();
    Typeface karlaB, ubantuB, karlaR, ubantuR;
    RelativeLayout relativeLayoutTop, relativeLayoutBelow, relativeLayoutComment;
    String profile_pic_url, addedById1, phoneNumberOfHelper, notifyStatusValue;
    RelativeLayout notification_tab;
    private FirebaseAnalytics mFirebaseAnalytics;
    public static final int REQUEST_CODE_EDIT_HELPER = 9010;


    public CommunityHelperDetails() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.community_helpers_detail_layout, container, false);
        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        CommunitySearchFragment.isFromBack = true;
        bundle = getArguments();
        addedById1 = bundle.getString("addedBy");
        intializeVariable(view);
        helperId = bundle.getString("helperId");
        profile_pic_url = bundle.getString("profile_pic");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        Bundle logBundleInitial = new Bundle();
        logBundleInitial.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleInitial.putString("user_id", common.getStringValue(Constants.id));
        logBundleInitial.putString("helper_id", helperId);
        mFirebaseAnalytics.logEvent("community_helper_details", logBundleInitial);


        if (Constants.rolesGivenToUser.contains("Can_Edit_Helper")) {
            edit_helper_details.setVisibility(View.VISIBLE);
        } else {
            edit_helper_details.setVisibility(View.GONE);
        }

        if (Constants.rolesGivenToUser.contains("Helperqr_Code")) {
            isInSociety.setVisibility(View.VISIBLE);
            attendence.setVisibility(View.VISIBLE);
            notification_tab.setVisibility(View.VISIBLE);
        } else {
            isInSociety.setVisibility(View.GONE);
            attendence.setVisibility(View.GONE);
            notification_tab.setVisibility(View.GONE);
        }


        if (common.isNetworkAvailable()) {
            getData();
        } else {
            common.showShortToast("No internet...!!");
        }


        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> list = new ArrayList<String>();
                list.clear();

                if (profile_pic_url.endsWith(".png") || profile_pic_url.endsWith(".jpg")) {
                    list.add(profile_pic_url);
                }

                if (list.size() > 0) {
                    DialogFragment fragment = new ImageSlideShow();
                    Bundle bundle1 = new Bundle();
                    bundle1.putStringArrayList("imagelist", list);
                    bundle1.putInt("position", 0);
                    fragment.setArguments(bundle1);
                    fragment.setRetainInstance(true);
                    fragment.show(getFragmentManager(), "profile_pic_show");
                }
            }
        });

        rateHimHer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RateDialogFragment dialog = new RateDialogFragment();
                dialog.setArguments(bundle);
                dialog.setRetainInstance(true);
                dialog.show(getFragmentManager(), "tag_rate_him_her");
            }
        });

        addedByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1 = new Bundle();
                Fragment fragment = new DetailsNeighbourFragment();
                bundle1.putString("ID", addedById1);
                fragment.setArguments(bundle1);
                activityCallback.onButtonClick(fragment, false);
            }
        });

        reportHimHer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Instabug.setUserEmail(common.getStringValue(Constants.email));
                Instabug.setUserData("Community Helper Detail  Helper Id : " + helperId + "User ID who reported : "
                        + common.getStringValue(Constants.id) + " Society ID : " + common.getStringValue("ID"));
                Instabug.invoke(InstabugInvocationMode.PROMPT_OPTION);
            }
        });

        if (GetFavouriteList.hsHelper.contains(helperId)) {
            favorite_helper.setImageResource(R.drawable.favorite_selectednew);
        } else {
            favorite_helper.setImageResource(R.drawable.favorite_helper);
        }

        favorite_helper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GetFavouriteList.hsHelper.contains(helperId)) {
                    favorite_helper.setImageResource(R.drawable.favorite_helper);
                    new RemoveHelperFromFavouriteList(0).execute();
                } else {
                    favorite_helper.setImageResource(R.drawable.favorite_selectednew);
                    new AddHelperToFavouriteList(0).execute();
                }

            }
        });

        call_helper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phoneNumberOfHelper.isEmpty() && phoneNumberOfHelper != null) {

                    Bundle logBundleCall = new Bundle();
                    logBundleCall.putString("society_id", common.getStringValue(Constants.userRwa));
                    logBundleCall.putString("user_id", common.getStringValue(Constants.id));
                    logBundleCall.putString("helper_id", helperId);
                    mFirebaseAnalytics.logEvent("community_helper_details_call", logBundleCall);
                    AlertBoxForImagePic(phoneNumberOfHelper);
                }
            }
        });

        edit_helper_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditHelperActivity.class);
                Bundle b = new Bundle();
                Gson gson = new Gson();
                String helperObjectString = gson.toJson(helperModelToBundle);
                b.putString("helperObjcet", helperObjectString);
                intent.putExtras(b);
                getActivity().startActivityForResult(intent, REQUEST_CODE_EDIT_HELPER);
            }
        });

        notifyStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notifyStatusValue.equals("1")) {
                    notifyStatus.setImageResource(R.drawable.slider_off);
                    notifyStatusValue = "0";
                    callApiTOSaveHelperNotifyStatus();
                } else {
                    notifyStatus.setImageResource(R.drawable.slider_on);
                    notifyStatusValue = "1";
                    callApiTOSaveHelperNotifyStatus();
                }
            }
        });

        attendence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundleDataForward = new Bundle();
                bundleDataForward.putString("helper_id", helperModelToBundle.getHelperId());
                Fragment fragment = new AttandenceSheetFragment();
                fragment.setArguments(bundleDataForward);
                activityCallback.onButtonClick(fragment, false);
            }
        });

        return view;
    }

    private void callApiTOSaveHelperNotifyStatus() {

        String uri = Constants.BaseUrl + "notify";

        try {
            JSONObject json = new JSONObject();
            json.put("HelperID", helperId);
            json.put("ResidentRWAID", common.getStringValue(Constants.ResidentRWAID));
            json.put("Subscribe", notifyStatusValue);


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                String status = response.optString("Status");
                                //  String message = response.optString("message");

                                // common.showShortToast(message);

                                if (status.equalsIgnoreCase("1")) {

                                } else {
                                    if (notifyStatusValue.equalsIgnoreCase("1")) {
                                        notifyStatusValue = "0";
                                    } else {
                                        notifyStatusValue = "1";
                                    }
                                }

                                if (notifyStatusValue.equals("0")) {
                                    notifyStatus.setImageResource(R.drawable.slider_off);
                                } else {
                                    notifyStatus.setImageResource(R.drawable.slider_on);
                                }

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

    private void intializeVariable(View view) {

        notification_tab = (RelativeLayout) view.findViewById(R.id.notification_tab);
        listView = (ListView) view.findViewById(R.id.listviewUserRating);
        horizontalListView = (HorizontalListView) view.findViewById(R.id.horizontal_listview);
        favorite_helper = (ImageView) view.findViewById(R.id.favorite_helper);
        call_helper = (ImageView) view.findViewById(R.id.call_helper);
        edit_helper_details = (ImageView) view.findViewById(R.id.edit_helper_details);
        notifyStatus = (ImageView) view.findViewById(R.id.notifyStatus);
        isInSociety = (ImageView) view.findViewById(R.id.isInSociety);

        relativeLayoutTop = (RelativeLayout) view.findViewById(R.id.relativeLayoutTop);
        relativeLayoutBelow = (RelativeLayout) view.findViewById(R.id.relativeLayoutBelow);
        relativeLayoutComment = (RelativeLayout) view.findViewById(R.id.relativeLayoutComment);
        addedByName = (TextView) view.findViewById(R.id.addedBy);
        title = (TextView) view.findViewById(R.id.titletextView);
        name = (TextView) view.findViewById(R.id.name);
        profession = (TextView) view.findViewById(R.id.profession);
        salary = (TextView) view.findViewById(R.id.salary);
        address = (TextView) view.findViewById(R.id.address);
        textDocs = (TextView) view.findViewById(R.id.textDocs);
        textRating = (TextView) view.findViewById(R.id.textRating);
        rating = (TextView) view.findViewById(R.id.rating);
        textRC = (TextView) view.findViewById(R.id.textRC);
        textNameInitial = (TextView) view.findViewById(R.id.textNameInitial);
        textnotify = (TextView) view.findViewById(R.id.textnotify);
        attendence = (TextView) view.findViewById(R.id.attendence);


        ratingBack = (ImageView) view.findViewById(R.id.ratingBack);
        profile_pic = (ImageView) view.findViewById(R.id.profile_pic);
        rateHimHer = (ImageView) view.findViewById(R.id.rateBtn);
        colorBack = (ImageView) view.findViewById(R.id.colorback);
        reportHimHer = (ImageView) view.findViewById(R.id.reportspam);

        karlaB = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");
        karlaR = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");
        ubantuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        ubantuR = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-R.ttf");

        title.setTypeface(ubantuB);
        name.setTypeface(karlaB);
        profession.setTypeface(karlaR);
        salary.setTypeface(karlaR);
        address.setTypeface(karlaR);
        textRating.setTypeface(ubantuB);
        textDocs.setTypeface(karlaB);
        rating.setTypeface(ubantuB);
        textRC.setTypeface(karlaB);
        textNameInitial.setTypeface(karlaR);
        textnotify.setTypeface(karlaB);
        attendence.setTypeface(ubantuR);

    }

    public void AlertBoxForImagePic(final String telephoneNumber) {


        new AlertDialog.Builder(getActivity())
                .setTitle("Please Wait....")
                .setMessage("Want to call Helper")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                String uri = "tel:" + telephoneNumber.trim();
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse(uri));
                                startActivity(callIntent);

                                Bundle logBundleCall = new Bundle();
                                logBundleCall.putString("society_id", common.getStringValue(Constants.userRwa));
                                logBundleCall.putString("user_id", common.getStringValue(Constants.id));
                                mFirebaseAnalytics.logEvent("community_helper_details_called", logBundleCall);


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

    private void getData() {

        common.showSpinner(getActivity());

        String uri = Constants.BaseUrl + "helperdetail";
        try {
            JSONObject json = new JSONObject();
            json.put("ID", helperId);
            json.put("ResidentRWAID", common.getStringValue(Constants.ResidentRWAID));

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


            JSONArray jsonArray = json.getJSONArray("HelperDetail");
            String status = json.optString("Status");

            HelperModel helperModel = new HelperModel();
            helperModelToBundle = new HelperModel();

            JSONObject object = jsonArray.getJSONObject(0);

            String ID = object.optString("ID");
            String RWAID = object.optString("RWAID");
            String Name = object.optString("Name");
            String ServiceOffered = object.optString("ServiceOffered");
            String ServiceCharge = object.optString("ServiceCharges");
            String ResidentialAddress = object.optString("ResidentialAddress");
            String PrimaryContactNbr = object.optString("PrimaryContactNbr");
            phoneNumberOfHelper = PrimaryContactNbr;
            String PhoneNbr2 = object.optString("PhoneNbr2");
            String EmailId = object.optString("EmailId");
            String AddedBy = object.optString("AddedBy");
            addedById1 = AddedBy;
            String AddedOn = object.optString("AddedOn");
            String ProfilePhoto = object.optString("ProfilePhoto");
            String PoilceVerificationScanImage1 = object.optString("OtherDocScanImage1");
            String PoliceVerificationScanImage2 = object.optString("OtherDocScanImage2");
            String PoliceVerificationScanImage3 = object.optString("OtherDocScanImage3");
            String EntryCardExpiry = object.optString("EntryCardExpiry");
            String IsActive = object.optString("IsActive");
            String Rating = object.optString("Rating");
            String AddedbyName = object.optString("AddedbyName");
            String addedbyFlatNbr = object.optString("addedbyFlatNbr");
            String is_in = object.optString("is_in");
            String notifyStatus = object.optString("Subscribe");

            String helperQRCode = "";
            helperQRCode = object.optString("HelperQRCode");
            if (helperQRCode.equals("null")) {
                helperQRCode = "";
            }

            helperModel.setID(ID);
            helperModel.setRWAID(RWAID);
            helperModel.setName(Name);
            helperModel.setHelperId(ID);
            helperModel.setServiceOffered(ServiceOffered);
            helperModel.setServiceCharge(ServiceCharge);
            helperModel.setRating(Rating);
            helperModel.setResidentialAddress(ResidentialAddress);
            helperModel.setPrimaryContactNbr(PrimaryContactNbr);
            helperModel.setPhoneNbr2(PhoneNbr2);
            helperModel.setEmailId(EmailId);
            helperModel.setAddedBy(AddedBy);
            helperModel.setAddedOn(AddedOn);
            helperModel.setAddedOn(AddedOn);
            helperModel.setProfilePhoto(profile_pic_url);
            helperModel.setPoilceVerificationScanImage1(PoilceVerificationScanImage1);
            helperModel.setPoliceVerificationScanImage2(PoliceVerificationScanImage2);
            helperModel.setPoliceVerificationScanImage3(PoliceVerificationScanImage3);
            helperModel.setEntryCardExpiry(EntryCardExpiry);
            helperModel.setIsActive(IsActive);
            helperModel.setAddedbyFlatNbr(addedbyFlatNbr);
            helperModel.setAddedbyName(AddedbyName);
            helperModel.setIs_in(is_in);
            helperModel.setSubscribe(notifyStatus);
            helperModel.setHelperQRCode(helperQRCode);

            isRatingAvailable = false;
            if (object.has("Rating & Comment")) {
                JSONArray userArray = object.getJSONArray("Rating & Comment");
                isRatingAvailable = true;
                userList.clear();
                for (int j = 0; j < userArray.length(); j++) {
                    JSONObject user = userArray.getJSONObject(j);
                    UserComment userComment = new UserComment();
                    userComment.setUserId(user.optString("AddedBy"));
                    userComment.setUserName(user.optString("FirstName"));
                    userComment.setUserCommentId(user.optString("ID"));
                    userComment.setUserProfilePic(user.optString("ProfilePic"));
                    userComment.setRatingByUser(user.optString("Rating"));
                    userComment.setUsrFlatNumber(user.optString("FlatNbr"));
                    userComment.setAgo(user.optString("AddedOn"));
                    userComment.setCommentText(user.optString("Comments"));
                    userList.add(userComment);

                }
            }

            helperModel.setUserCommentList(userList);
            helperModelToBundle = helperModel;
            setDataToUi(helperModel);

        } catch (Exception e) {

        }

        common.hideSpinner();
    }

    private void setDataToUi(HelperModel helperModel) {
        listView = (ListView) view.findViewById(R.id.listviewUserRating);
        addedByName.setText("Added by " + helperModel.getAddedbyName().trim().split(" ")[0] + ", " + helperModel.getAddedbyFlatNbr());


        notifyStatusValue = helperModel.getSubscribe();

        if (notifyStatusValue.equals("0")) {
            notifyStatus.setImageResource(R.drawable.slider_off);
        } else {
            notifyStatus.setImageResource(R.drawable.slider_on);
        }


        if (helperModel.getAddedBy().equalsIgnoreCase("null") || helperModel.getAddedBy().equalsIgnoreCase("(null)")) {
            addedByName.setVisibility(View.GONE);
        }
        name.setText(helperModel.getName());
        if (!helperModel.getServiceOffered().isEmpty()) {
            profession.setText(" (" + helperModel.getServiceOffered() + ")");
        }
        salary.setText(helperModel.getServiceCharge());

        address.setText(helperModel.getResidentialAddress());

        if (helperModel.getRating().length() >= 3 && !helperModel.getRating().equalsIgnoreCase("null")) {
            rating.setText(helperModel.getRating().substring(0, 3));
        } else if (helperModel.getRating().equalsIgnoreCase("null")) {
            rating.setText("NR");
        } else {
            rating.setText(helperModel.getRating());
        }

        double ratingStar = 0.0;
        if (!helperModel.getRating().equalsIgnoreCase("null")) {
            ratingStar = Double.parseDouble(helperModel.getRating());
        }
        ArrayList<String> listImage = new ArrayList<>();

        listImage.add(helperModel.getPoilceVerificationScanImage1());
        listImage.add(helperModel.getPoliceVerificationScanImage2());
        listImage.add(helperModel.getPoliceVerificationScanImage3());

        final ArrayList<String> finalList = new ArrayList<>();

        for (int m = 0; m < listImage.size(); m++) {
            String docLink = listImage.get(m);
            docLink.toLowerCase();
            if (docLink.endsWith(".png") || docLink.endsWith(".jpg")) {
                finalList.add(docLink);
            }
        }

        HorizontalImageList adapter = new HorizontalImageList(getActivity(), finalList);
        horizontalListView.setAdapter(adapter);

        horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DialogFragment fragment = new ImageSlideShow();
                Bundle bundle1 = new Bundle();
                bundle1.putStringArrayList("imagelist", finalList);
                bundle1.putInt("position", position);
                fragment.setArguments(bundle1);
                fragment.setRetainInstance(true);
                fragment.show(getFragmentManager(), "tag_delivery");
            }
        });

        if (ratingStar < 3) {
            ratingBack.setImageResource(R.drawable.red_rate_rect);
        } else {
            ratingBack.setImageResource(R.drawable.green_rate_rect);
        }
        String profilePicLink = helperModel.getProfilePhoto();
        if (profilePicLink.endsWith(".png") || profilePicLink.endsWith(".jpg")) {
            relativeLayoutBelow.setVisibility(View.INVISIBLE);
            relativeLayoutTop.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(profilePicLink).into(profile_pic);
        } else {

            String str = helperModel.getName();
            str = String.valueOf(str.charAt(0));
            int positionH = 0;
            for (int j = 0; j < Alphabets.alphabets.size(); j++) {
                if (str.equalsIgnoreCase(Alphabets.alphabets.get(j))) {
                    positionH = j;
                    profile_pic.setImageResource(Alphabets.alphabetsDrawable.get(positionH));
                }
            }


        }

        if (helperModel.getIs_in().equals("1")) {
            isInSociety.setImageResource(R.drawable.status_green);
        } else {
            isInSociety.setImageResource(R.drawable.status_grey);
        }

        if (isRatingAvailable) {
            UserRatingAdapter userRatingAdapter = new UserRatingAdapter(getActivity(), helperModel.getUserCommentList());
            listView.setAdapter(userRatingAdapter);
        }


        if (helperModel.getAddedBy().equalsIgnoreCase(common.getStringValue(Constants.id))) {
            reportHimHer.setVisibility(View.INVISIBLE);
        }

    }

    private class RemoveHelperFromFavouriteList extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        int position;

        RemoveHelperFromFavouriteList(int position) {
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... voids) {


            String stringResponse = null;
            String status = null;
            String jsonraw = "{\n" +
                    "\"HelperID\":\"" + helperId + "\",\n" +
                    "\"UserID\":\"" + common.getStringValue(Constants.id) + "\"\n" +
                    "}";

            try {
                HttpResponse categoryResponse = Utility.postDataOnUrl(
                        Constants.BaseUrl + "deletefavhelper", jsonraw);


                stringResponse = EntityUtils.toString(categoryResponse.getEntity());
                JSONObject jobj = new JSONObject(stringResponse);

                status = jobj.getString("Status");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(getActivity());
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Removing helper from favourite list...");
            dialog.setCancelable(false);
            dialog.show();

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (s != null) {
                if (s.equalsIgnoreCase("1")) {
                    GetFavouriteList.hsHelper.remove(helperId);
                }
            }
        }
    }

    private class AddHelperToFavouriteList extends AsyncTask<Void, Void, String> {
        int position;

        AddHelperToFavouriteList(int position) {
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... voids) {


            String stringResponse = null;
            String status = null;
            String jsonraw = "{\n" +
                    "\"UserID\":\"" + common.getStringValue(Constants.id) + "\",\n" +
                    "\"HelperID\":\"" + helperId + "\"\n" +
                    "}";

            try {
                HttpResponse categoryResponse = Utility.postDataOnUrl(
                        Constants.BaseUrl + "addfavhelper", jsonraw);


                stringResponse = EntityUtils.toString(categoryResponse.getEntity());
                JSONObject jobj = new JSONObject(stringResponse);

                status = jobj.getString("Status");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            common.showSpinner(getActivity());

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            common.hideSpinner();
            if (s != null) {
                if (s.equalsIgnoreCase("1")) {
                    GetFavouriteList.hsHelper.add(helperId);
                }
            }
        }
    }
}
