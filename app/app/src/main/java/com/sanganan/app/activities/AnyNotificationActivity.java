package com.sanganan.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IntegerRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.sanganan.app.R;
import com.sanganan.app.adapters.ClassifiedListAdapter;
import com.sanganan.app.adapters.ListPollAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.fragments.ApproveSocietyMember;
import com.sanganan.app.fragments.AttandenceSheetFragment;
import com.sanganan.app.fragments.BaseFragment;
import com.sanganan.app.fragments.CallOutDetails;
import com.sanganan.app.fragments.ClassifiedDetails;
import com.sanganan.app.fragments.ComplainDetailsFragment;
import com.sanganan.app.fragments.NotificationDetailsFragment;
import com.sanganan.app.fragments.PollingDetailsFragment;
import com.sanganan.app.interfaces.ToolbarListner;
import com.sanganan.app.model.CalloutData;
import com.sanganan.app.model.ClassifiedModel;
import com.sanganan.app.model.ComplainData;
import com.sanganan.app.model.GeneralNotification;
import com.sanganan.app.model.Poll;
import com.sanganan.app.model.Remark;

import com.sanganan.app.sample.SendBirdOpenChatActivity;

import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pranav on 3/2/17.
 */

public class AnyNotificationActivity extends ActionBarActivity implements ToolbarListner {

    Context context;
    Common common;
    RequestQueue requestQueue;
    Fragment fragment;
    FragmentManager fragmentManager;
    ProgressDialog progressDialog;
    String Id;
    FragmentTransaction fragmentTransaction;
    private static final String appId = "EBCD2CB7-F5CC-4F6C-8EF4-86548DCCDE38";
    String sUserId, mNickname;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.any_notification_activity);


        context = AnyNotificationActivity.this;
        common = Common.getNewInstance(this);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();


        Bundle bundle = getIntent().getExtras();

        String fragmentToReplace = bundle.getString("fragmnet");
        Id = bundle.getString("ID");


        if (fragmentToReplace.equalsIgnoreCase("notification")) {
            getDataNotification();
        } else if (fragmentToReplace.equalsIgnoreCase("callout")) {
            getCallout();
        } else if (fragmentToReplace.equalsIgnoreCase("complaint")) {
            getDataComplaint();
        } else if (fragmentToReplace.equalsIgnoreCase("classified")) {
            getDataClassified();
        } else if (fragmentToReplace.equalsIgnoreCase("polling")) {
            getDataPolling();
        } else if (fragmentToReplace.equalsIgnoreCase("openchat")) {
            if (SendBird.getConnectionState().equals(SendBird.ConnectionState.CLOSED)) {
                SendBird.init(appId, AnyNotificationActivity.this);
                sUserId = common.getStringValue(Constants.id);
                mNickname = common.getStringValue(Constants.FirstName);
                connect();
            } else {
                Intent intent = new Intent(AnyNotificationActivity.this, SendBirdOpenChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                Bundle bundle1 = new Bundle();
                bundle1.putString("channel_url", common.getStringValue("chat"));
                intent.putExtras(bundle1);
                finish();
                startActivity(intent);
            }
        } else if (fragmentToReplace.equalsIgnoreCase("attendence")) {
            Bundle bundleDataForward = new Bundle();
            bundleDataForward.putString("helper_id", Id);
            Fragment fragment = new AttandenceSheetFragment();
            fragment.setArguments(bundleDataForward);
            onButtonClick(fragment, false);
        }else if(fragmentToReplace.equalsIgnoreCase("registration")){
            fragment = new ApproveSocietyMember();
            Bundle bundle1 = new Bundle();
            bundle1.putString("recieved","esu");
            fragment.setArguments(bundle1);
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction tx1 = fragmentManager.beginTransaction();
            tx1.replace(R.id.container_body_notification, fragment);
            tx1.addToBackStack("approve member");
            tx1.commit();

        }

    }


    private void connect() {
        progressDialog = new ProgressDialog(AnyNotificationActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("chat starting...");
        progressDialog.show();
        SendBird.connect(sUserId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(AnyNotificationActivity.this, "Unable to start chat....", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                } else {
                    progressDialog.dismiss();
                    Intent intent = new Intent(AnyNotificationActivity.this, SendBirdOpenChatActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("channel_url", common.getStringValue("chat"));
                    intent.putExtras(bundle1);
                    finish();
                    startActivity(intent);

                }

                String nickname = mNickname;

                SendBird.updateCurrentUserInfo(nickname, common.getStringValue(Constants.ProfilePic), new SendBird.UserInfoUpdateHandler() {
                    @Override
                    public void onUpdated(SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(AnyNotificationActivity.this, "User info not updated...", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        common.setStringValue("user_id", sUserId);
                        common.setStringValue("nickname", mNickname);


                    }
                });

                if (FirebaseInstanceId.getInstance().getToken() == null) return;

                SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(), true, new SendBird.RegisterPushTokenWithStatusHandler() {
                    @Override
                    public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(AnyNotificationActivity.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        });
    }


    private void getDataNotification() {


        progressDialog = new ProgressDialog(AnyNotificationActivity.this);
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl + "notificationdetail";
        try {

            JSONObject json = new JSONObject();
            json.put("ID", Id);


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedDataNotification(response);
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
                            common.showShortToast(error.getMessage());
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
                progressDialog.dismiss();
                e.printStackTrace();
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();

        }
    }

    void parsedDataNotification(JSONObject json) {

        progressDialog.dismiss();

        try {
            String status = json.optString("Status");

            common.setStringValue(Constants.notificationLastSeenTime, String.valueOf(common.getUnixTime()));

            if (status.equalsIgnoreCase("1")) {
                JSONArray jsonArray = json.getJSONArray("notification");

                JSONObject jsonObject = jsonArray.getJSONObject(0);

                GeneralNotification notification = new GeneralNotification();

                String ID = jsonObject.optString("ID");
                String RWAID = jsonObject.optString("RWAID");
                String Severity = jsonObject.optString("Severity");
                String Title = common.funConvertBase64ToString(jsonObject.optString("Title"));
                String Text = common.funConvertBase64ToString(jsonObject.optString("Text"));
                String Image = jsonObject.optString("Image");
                String Image1 = jsonObject.optString("Image1");
                String Image2 = jsonObject.optString("Image2");
                String Image3 = jsonObject.optString("Image3");
                String IsCalendarEvent = jsonObject.optString("IsCalendarEvent");
                String Datecreated = jsonObject.optString("Datecreated");
                String CreatedBy = jsonObject.optString("CreatedBy");
                String EventStartDate = jsonObject.optString("EventStartDate");
                String EventEndDateTime = jsonObject.optString("EventEndDateTime");
                String Status = jsonObject.optString("Status");


                ArrayList<String> listDocImage = new ArrayList<>();


                if(!Image.trim().isEmpty()) {
                    String[] arr = Image.split(",");
                    for (String s : arr) {
                        if (s.endsWith(".png") || s.endsWith(".jpg")) {
                            listDocImage.add(s);
                        }
                    }
                }else if(!Image1.trim().isEmpty()||!Image2.trim().isEmpty()||!Image3.trim().isEmpty()){
                    if(!Image1.trim().isEmpty()) listDocImage.add(Image1);
                    if(!Image2.trim().isEmpty())  listDocImage.add(Image2);
                    if(!Image3.trim().isEmpty()) listDocImage.add(Image3);;
                }

                Bundle notify = new Bundle();
                notify.putString("title", Title);
                notify.putString("date", Datecreated);
                notify.putString("detail", Text);
                notify.putStringArrayList("imageList", listDocImage);
                notify.putString("Severity", Severity);


                fragment = new NotificationDetailsFragment();
                fragment.setArguments(notify);
                fragmentManager = getSupportFragmentManager();
                FragmentTransaction tx1 = fragmentManager.beginTransaction();
                tx1.replace(R.id.container_body_notification, fragment);
                tx1.addToBackStack("detailNotify");
                tx1.commit();


            } else {
                finish();
            }

        } catch (Exception e) {

        }

    }


    void getCallout() {

        progressDialog = new ProgressDialog(AnyNotificationActivity.this);
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl + "calloutdetail";
        try {

            JSONObject json = new JSONObject();
            json.put("ID", Id);


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedDataCallout(response);
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
                            common.showShortToast(error.getMessage());
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
                req.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

    private void parsedDataCallout(JSONObject response) {
        try {
            String status = "";
            progressDialog.dismiss();

            status = response.optString("Status");

            if (status.equalsIgnoreCase("1")) {
                JSONArray jsonArray = response.getJSONArray("CallOuts");

                JSONObject object = jsonArray.getJSONObject(0);
                CalloutData calloutData = new CalloutData();
                String dateSent = object.optString("DateSent");
                dateSent = common.convertFromUnix2(dateSent);
                calloutData.setDateSent(dateSent);
                calloutData.setDescription(common.funConvertBase64ToString(object.optString("Description")));
                calloutData.setFirstName(object.optString("FirstName"));
                calloutData.setFlatNbr(object.optString("FlatNbr"));
                calloutData.setID(object.optString("ID"));
                calloutData.setUserID(object.optString("UserID"));
                calloutData.setSenderID(object.optString("SenderID"));


                Bundle bundleCallout = new Bundle();
                bundleCallout.putString("Id", calloutData.getID());
                bundleCallout.putString("senderId", calloutData.getSenderID());
                bundleCallout.putString("userId", calloutData.getUserID());
                bundleCallout.putString("name", calloutData.getFirstName());
                bundleCallout.putString("flat", calloutData.getFlatNbr());
                bundleCallout.putString("description", calloutData.getDescription());
                fragment = new CallOutDetails();
                fragment.setArguments(bundleCallout);
                fragmentManager = getSupportFragmentManager();
                FragmentTransaction tx1 = fragmentManager.beginTransaction();
                tx1.replace(R.id.container_body_notification, fragment);
                tx1.addToBackStack("detailNotify");
                tx1.commit();
            }

        } catch (Exception e) {

        }


    }


    private void getDataClassified() {


        progressDialog = new ProgressDialog(AnyNotificationActivity.this);
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl + "classifieddetail";
        try {

            JSONObject json = new JSONObject();
            json.put("id", Id);

            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedDataClassified(response);
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
                            common.showShortToast(error.getMessage());
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
                progressDialog.dismiss();
                e.printStackTrace();
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();

        }
    }

    private void parsedDataClassified(JSONObject response) {
        try {
            String status = "";
            progressDialog.dismiss();
            status = response.optString("Status");
            if (status.equals("1")) {
                JSONObject object = response.getJSONObject("classified detail");

                ClassifiedModel classifiedModel = new ClassifiedModel();
                classifiedModel.setId(object.optString("Id"));
                classifiedModel.setTitle(common.funConvertBase64ToString(object.optString("Title")));
                classifiedModel.setDescription(common.funConvertBase64ToString(object.optString("Description")));
                classifiedModel.setImage1(object.optString("Image1"));
                classifiedModel.setImage2(object.optString("Image2"));
                classifiedModel.setImage3(object.optString("Image3"));
                classifiedModel.setAddedBy(object.optString("AddedBy"));
                classifiedModel.setFlatNbr(object.optString("FlatNbr"));
                classifiedModel.setFirstName(object.optString("FirstName"));
                classifiedModel.setUserID(object.optString("UserID"));
                String addedOn = object.optString("AddedOn");
                addedOn = common.convertFromUnix2(addedOn);
                classifiedModel.setAddedOn(addedOn);
                classifiedModel.setExpiryDt(response.optString("ExpiryDt"));


                Bundle bundle = new Bundle();
                bundle.putString("classifiedId", classifiedModel.getId());
                bundle.putString("userId", classifiedModel.getUserID());
                bundle.putString("image1", classifiedModel.getImage1());
                bundle.putString("image2", classifiedModel.getImage2());
                bundle.putString("image3", classifiedModel.getImage3());
                bundle.putString("name", classifiedModel.getFirstName());
                bundle.putString("flat", classifiedModel.getFlatNbr());
                bundle.putString("addedOn", classifiedModel.getAddedOn());
                bundle.putString("addedBy", classifiedModel.getAddedBy());
                bundle.putString("title", classifiedModel.getTitle());
                bundle.putString("desc", classifiedModel.getDescription());
                fragment = new ClassifiedDetails();
                fragment.setArguments(bundle);
                fragmentManager = getSupportFragmentManager();
                FragmentTransaction tx1 = fragmentManager.beginTransaction();
                tx1.replace(R.id.container_body_notification, fragment);
                tx1.addToBackStack("detailClassified");
                tx1.commit();


            }


        } catch (Exception e) {

        }

    }

    private void getDataComplaint() {

        progressDialog = new ProgressDialog(AnyNotificationActivity.this);
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl + "complaindetail";
        try {

            JSONObject json = new JSONObject();
            json.put("ID", Id);


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
                String categoryID = object.optString("CategoryID");
                try {
                   int ero = Integer.parseInt(Status);
                }catch (Exception e){
                    e.printStackTrace();Status = "1";
                }
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
                complainData.setCategoryID(categoryID);

                Bundle bundle = new Bundle();
                bundle.putString("title", complainData.getComplaintDetails());
                bundle.putString("date", complainData.getDateCreated());
                bundle.putString("flatNumber", complainData.getFlatNbr());
                bundle.putString("assignedby", complainData.getAssignedBy());
                bundle.putString("assignedto", complainData.getAssignedTo());
                bundle.putString("complainBy", complainData.getComplainBy());
                bundle.putString("Status", complainData.getStatus());
                bundle.putString("complainId", complainData.getID());
                bundle.putString("cmpId", complainData.getComplainByID());
                bundle.putString("Description", complainData.getDescription());
                bundle.putBoolean("fromComplainSearch", true);
                bundle.putString("categoryId", complainData.getCategoryID());

                ArrayList<String> imageList = new ArrayList<String>();
                imageList.add(complainData.getImage1());
                imageList.add(complainData.getImage2());
                imageList.add(complainData.getImage3());
                bundle.putStringArrayList("imageList", imageList);
                bundle.putString("rating", complainData.getRating());
                bundle.putString("feedbackText", complainData.getAcknowledgementNote());
                bundle.putString("phone", complainData.getHelperNumber());

                List<Remark> remarkList = complainData.getRemarks();
                Gson gson = new Gson();
                String jsonRemark = gson.toJson(remarkList);
                bundle.putString("remarkInGson", jsonRemark);
                Constants.isComplainStatusChangedOrEdited = true;
                fragment = new ComplainDetailsFragment();
                fragment.setArguments(bundle);
                fragmentManager = getSupportFragmentManager();
                FragmentTransaction tx1 = fragmentManager.beginTransaction();
                tx1.replace(R.id.container_body_notification, fragment);
                tx1.addToBackStack("detailComplain");
                tx1.commit();
            }

        } catch (Exception e) {

        }


    }

    private void getDataPolling() {

        progressDialog = new ProgressDialog(AnyNotificationActivity.this);
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl + "postpoledetail";
        try {

            JSONObject json = new JSONObject();
            json.put("ID", Id);
            json.put("ResidentRWAID", common.getStringValue(Constants.ResidentRWAID));

            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedDataPolling(response);
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
                            common.showShortToast(error.getMessage());
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
                progressDialog.dismiss();
                e.printStackTrace();
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();

        }
    }

    private void parsedDataPolling(JSONObject response) {
        try {
            String status = "";
            progressDialog.dismiss();
            status = response.optString("Status");

            if (status.equalsIgnoreCase("1")) {

                JSONArray array = response.getJSONArray("ShowPoles");
                JSONObject object = array.getJSONObject(0);
                Poll polls = new Poll();
                polls.setId(object.optString("Id"));
                polls.setResidentRWAID(object.optString("ResidentRWAID"));
                polls.setQuestion(common.funConvertBase64ToString(object.optString("Question")));
                String dateAdded = object.optString("DateAdded");
                if (!dateAdded.isEmpty()) {
                    dateAdded = common.convertFromUnix3(dateAdded);
                }
                polls.setDateAdded(dateAdded);
                polls.setExpirationDt(object.optString("ExpirationDt"));
                polls.setIsActive(object.optString("IsActive"));
                polls.setFirstName(object.optString("FirstName"));
                polls.setFlatNbr(object.optString("FlatNbr"));
                JSONArray flatArr = object.getJSONArray("FlatNbr");
                String flatString = "";
                if (flatArr.length() > 0) {
                    for (int k = 0; k < flatArr.length(); k++) {
                        JSONObject flatObject = flatArr.getJSONObject(0);
                        flatString = flatString + flatObject.optString("FlatNbr") + ",";
                    }

                    flatString = flatString.substring(0, flatString.length() - 1);
                }

                polls.setFlatNbr(flatString);


                polls.setResponseID(object.optString("Ispoll"));
                polls.setUserID(object.optString("UserID"));
                if (object.optString("Ispoll").equalsIgnoreCase("1")) {
                    polls.setYesNbr(object.optString("YesNbr"));
                    polls.setNoNbr(object.optString("NoNbr"));
                }


                Bundle bundle = new Bundle();
                bundle.putString("questionId", polls.getId());
                bundle.putString("name1", polls.getFirstName());
                bundle.putString("flat1", polls.getFlatNbr());
                bundle.putString("resident", polls.getResidentRWAID());
                bundle.putString("question", polls.getQuestion());
                bundle.putString("dateAdded", polls.getDateAdded());
                bundle.putString("expratoin", polls.getExpirationDt());
                bundle.putString("isActive", polls.getIsActive());
                bundle.putString("response", polls.getResponseID());
                bundle.putString("UserID", polls.getUserID());
                bundle.putString("post", "yes");


                if (polls.getResponseID().equalsIgnoreCase("1")) {
                    bundle.putString("yesNbr", polls.getYesNbr());
                    bundle.putString("noNbr", polls.getNoNbr());
                }


                fragment = new PollingDetailsFragment();
                fragment.setArguments(bundle);
                fragmentManager = getSupportFragmentManager();
                FragmentTransaction tx1 = fragmentManager.beginTransaction();
                tx1.replace(R.id.container_body_notification, fragment);
                tx1.addToBackStack("detailPolling");
                tx1.commit();

            }
        } catch (Exception e) {

        }

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
    }

    @Override
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
    public void onButtonClickNoBack(Fragment newfragment) {
        fragment = (BaseFragment) newfragment;
        fragmentManager = getSupportFragmentManager();
        fragment = (BaseFragment) newfragment;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit,
                R.anim.slide_in_left, R.anim.slide_in_right);
        fragmentTransaction.replace(R.id.container_body_notification, fragment);
        fragmentTransaction.commit();
    }
}