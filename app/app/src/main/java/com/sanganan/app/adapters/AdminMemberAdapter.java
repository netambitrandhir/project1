package com.sanganan.app.adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import com.sanganan.app.common.Alphabets;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.fragments.AlertDialogApprove;
import com.sanganan.app.fragments.AlertDialogLogin;
import com.sanganan.app.model.ApprovedMember;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 5/10/16.
 */

public class AdminMemberAdapter extends BaseAdapter {
    ArrayList<ApprovedMember> listMember;
    Context context;
    Typeface ubuntuB, workSansMedium;
    LayoutInflater inflater;
    Typeface karlaBold,karlaRegular;
    ProgressDialog progressDialog;

    RequestQueue requestQueue;
    int color[] = {Color.parseColor("#DBA55B"),Color.parseColor("#DA7589"),Color.parseColor("#053B57"),Color.parseColor("#6599FF"),Color.parseColor("#D26A5D"), Color.parseColor("#86959F")};



    public AdminMemberAdapter(Context context, ArrayList<ApprovedMember> listMember) {
        this.context = context;
        this.listMember = listMember;
        requestQueue = VolleySingleton.getInstance(context).getRequestQueue();
    }

    @Override
    public int getCount() {
        return listMember.size();
    }

    @Override
    public ApprovedMember getItem(int position) {
        return listMember.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            karlaBold = Typeface.createFromAsset(context.getAssets(), "Karla-Bold.ttf");
            karlaRegular = Typeface.createFromAsset(context.getAssets(),"Karla-Regular.ttf");

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.admin_member_adapter_layout, null);

            viewHolder.profile_pic = (ImageView) convertView.findViewById(R.id.shopImage);
            viewHolder.approveButton = (ImageView) convertView.findViewById(R.id.approveLayout);
            viewHolder.rejectButton = (ImageView) convertView.findViewById(R.id.rejectButton);
            viewHolder.adminMemberName = (TextView) convertView.findViewById(R.id.adminMemberName);
            viewHolder.adminMemberName.setTypeface(karlaBold);
            viewHolder.adminMemberAddress = (TextView) convertView.findViewById(R.id.adminMemberAddress);
            viewHolder.adminMemberAddress.setTypeface(karlaBold);
            viewHolder.adminMemberProfession = (TextView) convertView.findViewById(R.id.adminMemberProfession);
            viewHolder.adminMemberProfession.setTypeface(karlaBold);
            viewHolder.date_field = (TextView) convertView.findViewById(R.id.date_field);
            viewHolder.date_field.setTypeface(karlaBold);
            viewHolder.time_field = (TextView) convertView.findViewById(R.id.time_field);

            viewHolder.approve = (TextView) convertView.findViewById(R.id.approveText);
            viewHolder.approve.setTypeface(karlaBold);
            viewHolder.reject = (TextView) convertView.findViewById(R.id.rejectText);
            viewHolder.reject.setTypeface(karlaBold);



            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }




        String path = listMember.get(position).getProfilePic();

        if (path.endsWith(".png") || path.endsWith(".jpg")) {
            Picasso.with(context).load(path).into(viewHolder.profile_pic);
        } else {

            String str = getItem(position).getFirstName();
            if(str.length()==0){
                str = "a";
            }
            str = String.valueOf(str.charAt(0));
            int positionH = 0;
            for (int j = 0; j < Alphabets.alphabets.size(); j++) {
                if (str.equalsIgnoreCase(Alphabets.alphabets.get(j))) {
                    positionH = j;

                    Picasso.with(context).load(Alphabets.alphabetsDrawable.get(positionH)).into(viewHolder.profile_pic);
                    //viewHolder.profile_pic.setImageResource(Alphabets.alphabetsDrawable.get(positionH));
                }
            }
        }

        viewHolder.adminMemberName.setText(listMember.get(position).getFirstName());
        String residentType = listMember.get(position).getTypeLiving();

        if(residentType.equalsIgnoreCase("1")){
            residentType = "owner";
        }
        else{
            residentType = "tenant";
        }
        viewHolder.adminMemberAddress.setText(listMember.get(position).getFlatNbr() + "("+residentType+")");   ///add full address of society member when added
        viewHolder.adminMemberProfession.setText(listMember.get(position).getOccupation());

        String adddate = listMember.get(position).getAddedOn();

        viewHolder.date_field.setText(adddate);

        if (listMember.get(position).getApprovalStatus().equalsIgnoreCase("Y")) {
            viewHolder.approveButton.setVisibility(View.GONE);
            viewHolder.approve.setVisibility(View.VISIBLE);
            viewHolder.rejectButton.setVisibility(View.VISIBLE);
            viewHolder.reject.setVisibility(View.GONE);

        } else if (listMember.get(position).getApprovalStatus().equalsIgnoreCase("N")) {
            viewHolder.approveButton.setVisibility(View.VISIBLE);
            viewHolder.approve.setVisibility(View.GONE);
            viewHolder.rejectButton.setVisibility(View.GONE);
            viewHolder.reject.setVisibility(View.VISIBLE);

        } else if (listMember.get(position).getApprovalStatus().equalsIgnoreCase("P")) {
            viewHolder.approveButton.setVisibility(View.VISIBLE);
            viewHolder.approve.setVisibility(View.GONE);
            viewHolder.rejectButton.setVisibility(View.VISIBLE);
            viewHolder.reject.setVisibility(View.GONE);
        }


        viewHolder.approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData("Y", new Common(context).getStringValue(Constants.id), listMember.get(position).getId(), position);
            }
        });

        viewHolder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.alert_dialog_approve);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                // set the custom dialog components - text, image and button
               Typeface ubuntuB = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");
               Typeface wsRegular = Typeface.createFromAsset(context.getAssets(), "WorkSans-Regular.ttf");
               ImageView no_btn = (ImageView) dialog.findViewById(R.id.no);
               ImageView yes_btn = (ImageView) dialog.findViewById(R.id.yes);
               TextView headerTitle = (TextView) dialog.findViewById(R.id.headerTitle);
               TextView dialogMsg = (TextView) dialog.findViewById(R.id.dialogMsg);
                headerTitle.setTypeface(ubuntuB);
                dialogMsg.setTypeface(wsRegular);
                // if button is clicked, close the custom dialog
                no_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                yes_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getData("N", new Common(context).getStringValue(Constants.id), listMember.get(position).getId(), position);
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });


        return convertView;
    }

    private class ViewHolder {
        ImageView profile_pic, approveButton, rejectButton;
        TextView adminMemberName, adminMemberAddress, adminMemberProfession, date_field, time_field, approve, reject;

    }

    private void getData(final String approvalStatus, String approvedBy, String residentId, final int position) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl+"approve";
        try {
            JSONObject json = new JSONObject();
            json.put("ApprovalStatus", approvalStatus);
            json.put("ApprovedBy", approvedBy);
            json.put("ResidentID", residentId);
            json.put("RWAID",new Common(context).getStringValue("ID"));


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedData(response, approvalStatus, position);
                            } catch (Exception e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            VolleyLog.d("error", error.getMessage());
                            progressDialog.dismiss();

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
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();

        }
    }

    void parsedData(JSONObject json, String approvalStatus, int position) {
        String status = "";
        String message = "";

        try {


            status = json.optString("Status");
            message = json.optString("message");
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();


        } catch (Exception e) {

        }

        if (status.equalsIgnoreCase("1")) {
            listMember.get(position).setApprovalStatus(approvalStatus);
            notifyDataSetChanged();
        } else {
            notifyDataSetChanged();
        }

        progressDialog.dismiss();
    }
}
