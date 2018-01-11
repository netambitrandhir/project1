package com.sanganan.app.adapters;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.common.Alphabets;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.GetFavouriteList;
import com.sanganan.app.common.RandomColorAllocator;
import com.sanganan.app.model.HelperModel;
import com.sanganan.app.model.YourNeighbourSearch;
import com.sanganan.app.utility.Utility;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by root on 28/9/16.
 */
public class CommunityHelperAdapter extends BaseAdapter {


    private LayoutInflater inflater;
    Context context;
    Typeface karlaB, karlaR;
    ArrayList<HelperModel> communityHelperSearchArraylist;
    RandomColorAllocator colorAllocator;

    int positionSelected = 0;
    private FirebaseAnalytics mFirebaseAnalytics;
    Common common;


    public CommunityHelperAdapter(Context context, ArrayList<HelperModel> searchList) {
        this.context = context;
        this.communityHelperSearchArraylist = searchList;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        common = Common.getNewInstance(context);


    }

    public void updateReceiptsList(ArrayList<HelperModel> newlist) {
        communityHelperSearchArraylist = newlist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return communityHelperSearchArraylist.size();
    }

    @Override
    public HelperModel getItem(int i) {
        return communityHelperSearchArraylist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gridadapter_layout, null);
            karlaB = Typeface.createFromAsset(context.getAssets(), "Karla-Bold.ttf");
            karlaR = Typeface.createFromAsset(context.getAssets(), "Karla-Regular.ttf");
            viewHolder.helperProfilePicture = (ImageView) convertView.findViewById(R.id.profile_pic);
            viewHolder.helperName = (TextView) convertView.findViewById(R.id.helper_name);
            viewHolder.helperProfession = (TextView) convertView.findViewById(R.id.helper_profession);
            viewHolder.helperWages = (TextView) convertView.findViewById(R.id.helper_wages);
            viewHolder.favourite_icon = (ImageView) convertView.findViewById(R.id.favourite_icon);
            viewHolder.relativeLayoutTop = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutTop);
            viewHolder.relativeLayoutBelow = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutBelow);
            viewHolder.textInitial = (TextView) convertView.findViewById(R.id.textNameInitial);
            viewHolder.colorback = (ImageView) convertView.findViewById(R.id.colorback);
            viewHolder.starRating = (TextView) convertView.findViewById(R.id.starImage2);
            viewHolder.ratedTV = (TextView) convertView.findViewById(R.id.starImage1);
            viewHolder.textInitial.setTypeface(karlaR);
            viewHolder.call_icon = (ImageView) convertView.findViewById(R.id.call_icon);
            viewHolder.notifyStatus = (ImageView) convertView.findViewById(R.id.notifyStatus);


            viewHolder.helperName.setTypeface(karlaB);
            viewHolder.helperProfession.setTypeface(karlaR);
            viewHolder.helperWages.setTypeface(karlaR);

            colorAllocator = new RandomColorAllocator(getCount());

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        if (Constants.rolesGivenToUser.contains("Helperqr_Code")) {
            viewHolder.notifyStatus.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.notifyStatus.setVisibility(View.GONE);
        }

        if(getItem(i).getIs_in().equalsIgnoreCase("1")){
            viewHolder.notifyStatus.setImageResource(R.drawable.status_green);
        }else{
            viewHolder.notifyStatus.setImageResource(R.drawable.status_grey);
        }

        String pickLink = communityHelperSearchArraylist.get(i).getProfilePhoto();
        pickLink.toLowerCase();
        if (pickLink.endsWith(".png") || pickLink.endsWith(".jpg")) {
            viewHolder.relativeLayoutTop.setVisibility(View.VISIBLE);
            viewHolder.relativeLayoutBelow.setVisibility(View.INVISIBLE);
            Picasso.with(context).load(pickLink).placeholder(R.drawable.galleryplacholder).into(viewHolder.helperProfilePicture);
        } else {
          /*  viewHolder.relativeLayoutBelow.setVisibility(View.VISIBLE);
            viewHolder.relativeLayoutTop.setVisibility(View.INVISIBLE);*/
            String str = communityHelperSearchArraylist.get(i).getName();
            str = String.valueOf(str.charAt(0));
            int positionH = 0;
            for (int j = 0; j < Alphabets.alphabets.size(); j++) {
                if (str.equalsIgnoreCase(Alphabets.alphabets.get(j))) {
                    positionH = j;

                    viewHolder.helperProfilePicture.setImageResource(Alphabets.alphabetsDrawable.get(positionH));
                }
            }



//            str = str.toUpperCase();
          /*  viewHolder.textInitial.setText(str);
            viewHolder.colorback.setColorFilter(colorAllocator.listColor.get(i));*/
        }
        viewHolder.helperName.setText(communityHelperSearchArraylist.get(i).getName());
        viewHolder.helperProfession.setText(communityHelperSearchArraylist.get(i).getServiceOffered());
        viewHolder.helperWages.setText(communityHelperSearchArraylist.get(i).getServiceCharge());
        String rating = communityHelperSearchArraylist.get(i).getRating();
        if (rating.length() >= 3) {
            rating = rating.substring(0, 3);
            viewHolder.starRating.setText(rating);
        }
        else if (!rating.isEmpty()) {
            viewHolder.starRating.setText(rating);
        } else {
            viewHolder.starRating.setText("NR");
        }
        viewHolder.starRating.setTypeface(karlaB);
        Double d = 0.0;
        if (!rating.isEmpty()) {
            d = Double.parseDouble(rating);
        }
        if (d >= 4) {
            viewHolder.starRating.setBackgroundResource(R.drawable.green_rate_rect);
        } else {
            viewHolder.starRating.setBackgroundResource(R.drawable.red_rate_rect);
        }

        if (GetFavouriteList.hsHelper.contains(communityHelperSearchArraylist.get(i).getHelperId())) {
            viewHolder.favourite_icon.setImageResource(R.drawable.favorite_seletedheart);
        } else {
            viewHolder.favourite_icon.setImageResource(R.drawable.favorite_unseleted);
        }


        viewHolder.favourite_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionSelected = i;

                if (GetFavouriteList.hsHelper.contains(communityHelperSearchArraylist.get(i).getHelperId())) {
                    new RemoveHelperFromFavouriteList(i).execute();

                } else {
                    new AddHelperToFavouriteList(i).execute();
                }
            }
        });

        viewHolder.call_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!communityHelperSearchArraylist.get(i).getPrimaryContactNbr().trim().isEmpty()) {
                    AlertBoxForImagePic(i);
                } else {
                    Toast.makeText(context, "No number available for helper", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        ImageView helperProfilePicture;
        ImageView call_image;
        ImageView favorite_image;
        TextView helperName;
        TextView id_number;
        TextView company_tag;
        TextView helperProfession;
        TextView helperWages;
        ImageView favourite_icon;
        RelativeLayout relativeLayoutTop;
        TextView textInitial;
        RelativeLayout relativeLayoutBelow;
        ImageView colorback;
        TextView starRating;
        TextView ratedTV;
        ImageView call_icon,notifyStatus;
    }

    private class RemoveHelperFromFavouriteList extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        int position;
        //community_helper_fav_0
        RemoveHelperFromFavouriteList(int position) {
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... voids) {


            String stringResponse = null;
            String status = null;
            String jsonraw = "{\n" +
                    "\"HelperID\":\"" + communityHelperSearchArraylist.get(positionSelected).getHelperId() + "\",\n" +
                    "\"UserID\":\"" + Common.getNewInstance(context).getStringValue(Constants.id) + "\"\n" +
                    "}";

            try {
                HttpResponse categoryResponse = Utility.postDataOnUrl(
                        Constants.BaseUrl+"deletefavhelper", jsonraw);


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

            dialog = new ProgressDialog(context);
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
                    GetFavouriteList.hsHelper.remove(communityHelperSearchArraylist.get(positionSelected).getHelperId());
                    Bundle logBundleRemoveFavourite = new Bundle();
                    logBundleRemoveFavourite.putString("society_id", common.getStringValue(Constants.userRwa));
                    logBundleRemoveFavourite.putString("user_id", common.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("community_helper_fav_0", logBundleRemoveFavourite);
                    notifyDataSetChanged();
                }
            }
        }
    }

    private class AddHelperToFavouriteList extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        int position;

        AddHelperToFavouriteList(int position) {
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... voids) {


            String stringResponse = null;
            String status = null;
            String jsonraw = "{\n" +
                    "\"UserID\":\"" + Common.getNewInstance(context).getStringValue(Constants.id) + "\",\n" +
                    "\"HelperID\":\"" + communityHelperSearchArraylist.get(positionSelected).getHelperId() + "\"\n" +
                    "}";

            try {
                HttpResponse categoryResponse = Utility.postDataOnUrl(
                        Constants.BaseUrl+"addfavhelper", jsonraw);


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

            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("adding helper to favourite list...");
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
                    GetFavouriteList.hsHelper.add(communityHelperSearchArraylist.get(positionSelected).getHelperId());

                    Bundle logBundleAddFavourite = new Bundle();
                    logBundleAddFavourite.putString("society_id", common.getStringValue(Constants.userRwa));
                    logBundleAddFavourite.putString("user_id", common.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("community_helper_fav_1", logBundleAddFavourite);
                    notifyDataSetChanged();

                }
            }
        }
    }


    public void AlertBoxForImagePic(final int i) {


        new AlertDialog.Builder(context)
                .setTitle("Please Wait....")
                .setMessage("Want to call your helper")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                String uri = "tel:" + communityHelperSearchArraylist.get(i).getPrimaryContactNbr().trim();
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse(uri));
                                context.startActivity(callIntent);
                                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

}