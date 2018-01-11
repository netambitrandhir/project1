package com.sanganan.app.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationMode;
import com.sanganan.app.R;
import com.sanganan.app.common.Alphabets;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.model.WheelerModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Created by raj on 10/27/2016.
 */

public class DetailsNeighbourFragment extends BaseFragment {
    View view;
    ImageView profile_pic, callbtn, emailbtn, reportthisuser;
    TextView tvHeaderTitle, adminMemberName, adminMemberAddress, adminMemberProfession, aboutme, approveText, rejectText;

    String ApprovalStatus;
    LinearLayout approveLayout;
    WebView tvDetails;
    ImageView approveButton, rejectButton;
    String userId;
    ProgressDialog progressDialog;
    Common common;
    Typeface karlaB, karlaR, worksansR;
    RequestQueue requestQueue;
    ProgressDialog progressDialog1;
    boolean adminVisitor = false;

    private FirebaseAnalytics mFirebaseAnalytics;

    boolean tresPassed = false;
    Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.details_layout_neighbour, container, false);
        initializeView();
        bundle = getArguments();
        common.hideKeyboard(getActivity());
        if (!tresPassed) {
            userId = bundle.getString("ID");
            tresPassed = true;
        }

        if(bundle.containsKey("fromPostADapter")){
            android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
        }

        if (userId.equalsIgnoreCase(Constants.id)) {
            reportthisuser.setVisibility(View.INVISIBLE);
        }

        if (bundle.containsKey("fromChat")) {
            RelativeLayout layoutTopOfChat = (RelativeLayout) getActivity().findViewById(R.id.top_bar_container);
            layoutTopOfChat.setVisibility(View.GONE);
        }

        if (bundle.containsKey("adminVisitor")) {
            if (bundle.getBoolean("adminVisitor")) {
                approveLayout.setVisibility(View.VISIBLE);
            } else {
                approveLayout.setVisibility(View.INVISIBLE);
            }
        }



        if (bundle.containsKey("adminVisitor")) {
            adminVisitor = bundle.getBoolean("adminVisitor");
        }

        Bundle logBundleChat = new Bundle();
        logBundleChat.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleChat.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("neighbor_details", logBundleChat);

        try {
            if (userId != null || !userId.isEmpty() || userId.equalsIgnoreCase("null")) {
                getData();
            } else {
                common.showShortToast("Self Approved");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ProfilePic != null) {
                    if (ProfilePic.endsWith(".png") || ProfilePic.endsWith(".jpg")) {
                        ArrayList<String> list = new ArrayList<String>();
                        list.clear();
                        list.add(ProfilePic);

                        DialogFragment fragment = new ImageSlideShow();
                        Bundle bundle1 = new Bundle();
                        bundle1.putStringArrayList("imagelist", list);
                        bundle1.putInt("position", 0);
                        if (bundle.containsKey("fromChat")) {
                            bundle1.putBoolean("fromChat", true);
                        }
                        fragment.setArguments(bundle1);
                        fragment.setRetainInstance(true);
                        fragment.show(getFragmentManager(), "profile_pic_show");
                    }
                }
            }
        });
      /*  addedbyblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new DetailsNeighbourFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString("ID", ApprovedBy);
                fragment.setArguments(bundle1);
                activityCallback.onButtonClick(fragment,false);

                //  onCreate(bundle1);
            }
        });*/


        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDataToApprove("Y");
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataToApprove("N");
            }
        });

        reportthisuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Instabug.setUserEmail(common.getStringValue(Constants.email));
                Instabug.setUserData("User Detail Page User Id : " + userId + "UserID who reported : "
                        + common.getStringValue(Constants.id) + " Society ID : " + common.getStringValue("ID"));
                Instabug.invoke(InstabugInvocationMode.PROMPT_OPTION);
            }
        });

        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsPhonePublic.equalsIgnoreCase("1")) {
                    AlertBoxForImagePic();
                } else {


                    Bundle logBundleChat = new Bundle();
                    logBundleChat.putString("society_id", common.getStringValue(Constants.userRwa));
                    logBundleChat.putString("user_id", common.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("neighbor_details_call", logBundleChat);

                    PrivatePhoneDialog privatePhoneDialog = new PrivatePhoneDialog();
                    privatePhoneDialog.setRetainInstance(true);
                    bundle.putString("email_id", EmailID);
                    privatePhoneDialog.setArguments(bundle);
                    privatePhoneDialog.show(getActivity().getSupportFragmentManager(), "private_phone");

                }
            }
        });


        emailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Bundle logBundle = new Bundle();
                logBundle.putString("society_id", common.getStringValue("ID"));
                logBundle.putString("user_id", common.getStringValue(Constants.id));
                mFirebaseAnalytics.logEvent("neighbor_details_email", logBundle);

                Fragment fragment = new SendEmailFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString("email_id", EmailID);
                bundle1.putString("Username", FirstName);
                fragment.setArguments(bundle1);
                if (!bundle.containsKey("fromChat") && !bundle.containsKey("fromMemberSearch")) {
                    activityCallback.onButtonClick(fragment, false);
                }else if(bundle.containsKey("fromMemberSearch")){
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_body_notification, fragment).addToBackStack("openSearchedprofile")
                            .commit();
                }
                else{
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment).addToBackStack("openprofile")
                            .commit();
                }
            }
        });


        return view;
    }

    public void AlertBoxForImagePic() {


        new AlertDialog.Builder(getActivity())
                .setTitle("Please Wait....")
                .setMessage("Want to call your Neighbour")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                String uri = "tel:" + PhoneNbr.trim();
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


    private void getData() {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String uri = Constants.BaseUrl + "userdetails";
        try {
            JSONObject json = new JSONObject();
            json.put("ID", userId);

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

            req.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(req);

            try {
                requestQueue.add(req);


            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    String PhoneNbr, IsPhonePublic, EmailID, FlatNbr, FirstName, ProfilePic, Occupation="", About="", ApprovedBy="", ApName ="", ApFlatNbr="", status = "";
    ArrayList<WheelerModel> listVehicle = new ArrayList<>();

    void parsedData(JSONObject json) {
        try {
            progressDialog.dismiss();
            JSONObject object = json.getJSONObject("User");
            status = json.optString("Status");
            PhoneNbr = object.optString("PhoneNbr");
            IsPhonePublic = object.optString("IsPhonePublic");
            EmailID = object.optString("EmailID");
            ApprovalStatus = object.optString("ApprovalStatus");

            ApprovedBy = object.optString("ApprovedBy");

            boolean justLikeThat = false;

            if(!ApprovedBy.equalsIgnoreCase("null")) {
                if(object.getJSONObject("approveby") != null) {
                    JSONObject objectApprovedBy = object.getJSONObject("approveby");
                    ApName = objectApprovedBy.optString("ApName");
                    ApName = ApName.split(" ")[0];
                    ApFlatNbr = objectApprovedBy.optString("ApFlatNbr");
                    if (ApFlatNbr.equalsIgnoreCase("null")) {
                        ApFlatNbr = "";
                    }
                    justLikeThat = true;
                }
            }

            String flatString = "";
            if (object.has("Flats")) {
                JSONArray flatArr = object.getJSONArray("Flats");

                if(bundle.containsKey("adminVisitor")){
                if (flatArr.length() > 0) {
                    for (int k = 0; k < flatArr.length(); k++) {
                        JSONObject flatObject = flatArr.getJSONObject(k);
                        flatString = flatString + flatObject.optString("FlatNbr");
                        if (flatArr.getJSONObject(k).optString("IsOwner").equals("1")) {
                            flatString = flatString + "(owner)" + ",";
                        } else {
                            flatString = flatString + "(tenant)" + ",";
                        }
                    }

                    flatString = flatString.substring(0, flatString.length() - 1);
                }}else{
                    if (flatArr.length() > 0) {
                        for (int k = 0; k < flatArr.length(); k++) {
                            JSONObject flatObject = flatArr.getJSONObject(k);
                            flatString = flatString + flatObject.optString("FlatNbr");
                            if (flatArr.getJSONObject(k).optString("IsOwner").equals("1")) {
                                flatString = flatString + ",";
                            } else {
                                flatString = flatString +  ",";
                            }
                        }

                        flatString = flatString.substring(0, flatString.length() - 1);
                        if(justLikeThat) {
                            flatString = flatString + "(Added by " + ApName + "," + ApFlatNbr + ")";
                        }
                    }
                }
            }

            if (object.has("Vehicles")) {
                JSONArray array = object.getJSONArray("Vehicles");

                listVehicle.clear();
                for (int k = 0; k < array.length(); k++) {
                    String wheelweType = array.getJSONObject(k).optString("VehicleType");
                    String wheelerNumber = array.getJSONObject(k).optString("VehicleNbr");
                    WheelerModel model = new WheelerModel();
                    model.setVehicheType(wheelweType);
                    model.setVehicleNumber(wheelerNumber);
                    listVehicle.add(model);
                }
            }

            FlatNbr = flatString;

            FirstName = object.optString("FirstName");
            FirstName = FirstName.trim();
            ProfilePic = object.optString("ProfilePic");
            Occupation = object.optString("Occupation");
            setNtoB(Occupation);
            About = object.optString("About");
            setNtoB(About);
            if (About.equalsIgnoreCase("null") || About.equals("(null)")) {
                About = "";
            }

        } catch (Exception e) {

        }
        setUiDataFromAPi();
    }

    private void getDataToApprove(final String approvalStatus) {

        progressDialog1 = new ProgressDialog(getActivity());
        progressDialog1.setMessage("Please wait Data is loading...");
        progressDialog1.show();

        String uri = Constants.BaseUrl + "approve";
        try {
            JSONObject json = new JSONObject();
            json.put("ApprovalStatus", approvalStatus);
            json.put("ApprovedBy", common.getStringValue(Constants.id));
            json.put("ResidentID", userId);
            json.put("RWAID", common.getStringValue("ID"));


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedDataToApprove(response, approvalStatus);
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
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    void parsedDataToApprove(JSONObject json, String approvalStatus) {
        String status = "";
        String message = "";
        progressDialog1.dismiss();
        try {
            status = json.optString("Status");
            message = json.optString("message");
        } catch (Exception e) {

        }

        common.showShortToast(message);
        if (status.equalsIgnoreCase("1")) {
            ApprovedBy = common.getStringValue(Constants.id);
            // ApprovedBy = userId;
            //   approveButton.setVisibility(View.INVISIBLE);
            if (approvalStatus.equals("Y")) {
                approveText.setVisibility(View.VISIBLE);
                approveButton.setVisibility(View.GONE);
                rejectButton.setVisibility(View.VISIBLE);
            } else if (approvalStatus.equals("N")) {
                rejectText.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.GONE);
                approveButton.setVisibility(View.VISIBLE);
            }

        } else {
            common.showShortToast(message);
        }

    }


    private void setUiDataFromAPi() {


        String vehicleList = "";

        if (listVehicle.size() > 0) {
            for (WheelerModel model : listVehicle) {
                vehicleList = vehicleList + "<tr><td class='textStyle' width='50%'>" + model.getVehicleNumber() + "</td><td class='textStyle' width='50%'>" + model.getVehicheType() + "</td></tr>";
            }
        }

        String htmlToLoadInDetails = "<html><center><style>.textStyle{font-family:HelveticaNeue; " +
                "font-size:12px;color:#FAA042; text-align: center; font-weight:bold;} " +
                ".normalStyle{font-family:HelveticaNeue; font-weight:600; font-size:16px; text-align: center; color:#000000;} " +
                ".boldStyle{font-family:HelveticaNeue; font-weight:bold; font-size:16px; text-align: center; color:#44B3B7;} " +
                ".headingStyle{font-family:HelveticaNeue; font-weight:normal; font-size:18px; color:#f8af22;}" +
                "</style><body style='margin:0;padding:0;'><center><div style='text-align:justify;'>" + About + "</center></div><br/>";

        String phonemailData = "<center>" +
                " <div style='text-align:left;padding-top:10px;font-size:14px;'><b>Email : </b><a href='mailto:" + EmailID + "'style=color:#000000 !important;text-decoration:none !important;'>" + EmailID + "</a></div>" +
                "                <div style='text-align:left;padding-top:10px;font-size:14px;'><b>Phone No. " +
                "                :</b> <a href='tel:+91-" + PhoneNbr + " 'style=color:#000000 !important; text-decoration: none !important;'><span style='text-decoration:none !important;'>+91-" + PhoneNbr + "</span></a></div><br><br>";


        if(bundle.containsKey("adminVisitor")){
            htmlToLoadInDetails = htmlToLoadInDetails+phonemailData;
        }

        String vehicleData = "<br><div class='boldStyle'>Vehicle List</center></div><br/><center><table style='border:1px; " +
                "border-collapse: collapse; border: 1px solid #44B3B7; width='240px'><tr><th class='boldStyle' width='50%'>Vehicle Number</th>" +
                "<th class='boldStyle' width='50%'>Wheeler</th></tr>" + vehicleList + "</table></center><br/>" +
                "</body></center></html>";


        if (listVehicle.size() > 0 && adminVisitor) {
            htmlToLoadInDetails = htmlToLoadInDetails + vehicleData;
        } else {
            htmlToLoadInDetails = htmlToLoadInDetails + "</body></center></html>";
        }

        tvDetails.loadData(htmlToLoadInDetails, "text/html", "UTF-8");

        tvDetails.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith("mailto:")) {

                    Bundle logBundle = new Bundle();
                    logBundle.putString("society_id", common.getStringValue("ID"));
                    logBundle.putString("user_id", common.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("neighbor_details_email", logBundle);

                    Fragment fragment = new SendEmailFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("email_id", EmailID);
                    bundle1.putString("Username", FirstName);
                    fragment.setArguments(bundle1);
                    if (!bundle.containsKey("fromChat") && !bundle.containsKey("fromMemberSearch")) {
                        activityCallback.onButtonClick(fragment, false);
                    }else if(bundle.containsKey("fromMemberSearch")){
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_body_notification, fragment).addToBackStack("openSearchedprofile")
                                .commit();
                    }
                    else{
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment).addToBackStack("openprofile")
                                .commit();
                    }
                } else if (url.startsWith("tel:")) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);

                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });

        tvHeaderTitle.setText(FirstName);
        adminMemberName.setText(FirstName);
        adminMemberAddress.setText(FlatNbr);
        if (Occupation.equalsIgnoreCase("null") || Occupation.equalsIgnoreCase("(null)")) {
            adminMemberAddress.setText("");
        } else {
            adminMemberProfession.setText(Occupation);
        }
        if (ProfilePic != null) {
            if (ProfilePic.endsWith(".png") || ProfilePic.endsWith(".jpg")) {
                Picasso.with(getActivity()).load(ProfilePic).placeholder(R.drawable.galleryplacholder).into(profile_pic);
            } else {
                String str = String.valueOf(FirstName.charAt(0));
                str.toLowerCase();
                int position = 0;
                for (int i = 0; i < Alphabets.alphabets.size(); i++) {
                    if (str.equalsIgnoreCase(Alphabets.alphabets.get(i))) {
                        position = i;
                    }
                }
                profile_pic.setImageResource(Alphabets.alphabetsDrawable.get(position));
            }
        }

        if (ApprovalStatus.equals("Y")) {
            approveText.setVisibility(View.VISIBLE);
            approveButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.VISIBLE);
        } else if (ApprovalStatus.equals("N")) {
            rejectText.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.GONE);
            approveButton.setVisibility(View.VISIBLE);
        } else if (ApprovalStatus.equals("P")) {
            approveText.setVisibility(View.GONE);
            rejectText.setVisibility(View.GONE);
            rejectButton.setVisibility(View.VISIBLE);
            approveButton.setVisibility(View.VISIBLE);

        }


    }


    private void initializeView() {
        karlaB = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");
        karlaR = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");
        worksansR = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");

        approveLayout = (LinearLayout) view.findViewById(R.id.approveLayout);
        approveButton = (ImageView) view.findViewById(R.id.approveButton);
        rejectButton = (ImageView) view.findViewById(R.id.rejectButton);
        approveText = (TextView) view.findViewById(R.id.approveText);
        approveText.setTypeface(karlaB);
        rejectText = (TextView) view.findViewById(R.id.rejectText);
        rejectText.setTypeface(karlaB);
        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        tvHeaderTitle = (TextView) view.findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setTypeface(karlaB);
        adminMemberName = (TextView) view.findViewById(R.id.adminMemberName);
        adminMemberName.setTypeface(karlaB);
        adminMemberAddress = (TextView) view.findViewById(R.id.adminMemberAddress);
        adminMemberAddress.setTypeface(karlaR);
        adminMemberProfession = (TextView) view.findViewById(R.id.adminMemberProfession);
        adminMemberProfession.setTypeface(karlaR);
        aboutme = (TextView) view.findViewById(R.id.aboutme);
        aboutme.setTypeface(karlaB);
        tvDetails = (WebView) view.findViewById(R.id.tvDetails);

        /*tvDetails.setTypeface(worksansR);*/

        profile_pic = (ImageView) view.findViewById(R.id.profile_pic);
        callbtn = (ImageView) view.findViewById(R.id.callmailbtn);
        emailbtn = (ImageView) view.findViewById(R.id.emailbtn);
        reportthisuser = (ImageView) view.findViewById(R.id.reportthisuser);

    }


    void setNtoB(String string){
        if(string == null){
            string = "";
        }
    }
}