package com.sanganan.app.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sanganan.app.R;
import com.sanganan.app.adapters.AdminAdapter;
import com.sanganan.app.adapters.AdminMemberAdapter;
import com.sanganan.app.adapters.FavouriteAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.ApprovedMember;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 5/10/16.
 */

public class AdminFragment extends BaseFragment {

    View view;
    TextView title, top, middle, end;
    ListView listViewAdmin;
    Typeface ubantuBold, ubantuMedium;
    ArrayList drawable = new ArrayList();

  //  String url = Constants.BaseUrl + "approvesocietymember";
    String url = Constants.BaseUrl + "approvesocietymembernew";
    Common common;
    RequestQueue requestQueue;
    ArrayList<ApprovedMember> listMemberApproval = new ArrayList<>();
    int usersInPendingState = 0;
    ArrayList<String> arrayList = new ArrayList<>();
    boolean canAddHelper, canAddNotification, canApprove;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.admin_main_page, container, false);
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        initializeVariables(view);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        title.setTypeface(ubantuBold);
        top.setTypeface(ubantuBold);
        middle.setTypeface(ubantuBold);
        end.setTypeface(ubantuBold);

        arrayList.clear();


        if (Constants.rolesGivenToUser.contains("Can_Approve_Member")) {
            canApprove = true;
            arrayList.add("Approve Society Members");
            drawable.add(R.drawable.admin_member);
        } else {
            canApprove = false;
        }

        if (Constants.rolesGivenToUser.contains("Can_Send_Notification")) {
            canAddNotification = true;
            arrayList.add("Send Notifications");
            drawable.add(R.drawable.admin_notification);
        } else {
            canAddNotification = false;
        }


        listViewAdmin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (arrayList.get(position).equalsIgnoreCase("Approve Society Members")) {
                    Fragment fragment1 = new ApproveSocietyMember();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("viamain","esu");
                    fragment1.setArguments(bundle1);
                    activityCallback.onButtonClick(fragment1, false);
                } else {
                    Intent intent = new Intent(getActivity(), SendNotificationByAdmin.class);
                    startActivity(intent);
                }
            }
        });


        if (common.isNetworkAvailable()) {
            if (canApprove) {
                getData();
            } else {
                AdminAdapter adminAdapter = new AdminAdapter(getActivity(), arrayList, drawable, usersInPendingState);
                listViewAdmin.setAdapter(adminAdapter);
            }

        } else {
            common.showShortToast("No internet...!!");
        }


        return view;
    }

    private void initializeVariables(View view) {
        ubantuBold = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");

        listViewAdmin = (ListView) view.findViewById(R.id.listAdmin);
        title = (TextView) view.findViewById(R.id.titletextView);
        top = (TextView) view.findViewById(R.id.starttext);
        middle = (TextView) view.findViewById(R.id.middletext);
        end = (TextView) view.findViewById(R.id.endtext);
    }

    private void getData() {

       common.showSpinner(getActivity());

        String uri = url;
        try {
            JSONObject json = new JSONObject();
            json.put("RWAID", common.getStringValue("ID"));
            json.put("Uid", common.getStringValue(Constants.id));
            json.put("PageNo", "1");

            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                Constants.responseForward = response;
                                parsedData(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                                common.hideSpinner();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            VolleyLog.d("error", error.getMessage());
                            common.hideSpinner();

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
                common.hideSpinner();
            }
            req.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(req);

        } catch (Exception e) {
            e.printStackTrace();
            common.hideSpinner();
        }
    }

    void parsedData(JSONObject json) {
        try {
            String status = "";


            JSONArray jsonArray = json.getJSONArray("approvesocietymember");
            status = json.optString("Status");
            listMemberApproval.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                ApprovedMember approvedMember = new ApprovedMember();
                approvedMember.setId(object.optString("ID"));
                approvedMember.setFirstName(object.optString("FirstName"));
                approvedMember.setLastName(object.optString("LastName"));
                approvedMember.setMiddleName(object.optString("MiddleName"));
                approvedMember.setAddedOn(object.optString("AddedOn"));
                approvedMember.setPhoneNbr(object.optString("PhoneNbr"));
                approvedMember.setEmailID(object.optString("EmailID"));
                approvedMember.setFlatNbr(object.optString("FlatNbr"));
                approvedMember.setPassword(object.optString("Password"));
                approvedMember.setApprovedOn(object.optString("ApprovedOn"));
                approvedMember.setApprovedBy(object.optString("ApprovedBy"));
                approvedMember.setApprovalStatus(object.optString("ApprovalStatus"));
                approvedMember.setGender(object.optString("Gender"));
                approvedMember.setOccupation(object.optString("Occupation"));
                approvedMember.setProfilePic(object.optString("ProfilePic"));
                approvedMember.setIsActive(object.optString("IsActive"));

                listMemberApproval.add(approvedMember);
            }

        } catch (Exception e) {

        }

        usersInPendingState = 0;
        for (int i = 0; i < listMemberApproval.size(); i++) {
            if (listMemberApproval.get(i).getApprovalStatus().equalsIgnoreCase("P")) {
                usersInPendingState = usersInPendingState + 1;
            }
        }

        AdminAdapter adminAdapter = new AdminAdapter(getActivity(), arrayList, drawable, usersInPendingState);
        listViewAdmin.setAdapter(adminAdapter);

        common.hideSpinner();
    }
}
