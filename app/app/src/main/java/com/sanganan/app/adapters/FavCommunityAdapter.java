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

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanganan.app.R;

import com.sanganan.app.common.Alphabets;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.fragments.FavouriteHelpersFragment;
import com.sanganan.app.model.HelperModel;
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
public class FavCommunityAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    Context context;
    Typeface karlaB, karlaR;
    ArrayList<HelperModel> communityHelperSearchArraylist;
    int color[] = {Color.parseColor("#DBA55B"), Color.parseColor("#DA7589"), Color.parseColor("#053B57"), Color.parseColor("#6599FF"), Color.parseColor("#D26A5D"), Color.parseColor("#86959F")};
    Random random;


    public FavCommunityAdapter(Context context, ArrayList<HelperModel> searchList) {
        this.context = context;
        this.communityHelperSearchArraylist = searchList;
        random = new Random();

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
            convertView = inflater.inflate(R.layout.fav_community_helper_adapter_layout, null);
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
            viewHolder.textInitial.setTypeface(karlaR);
            viewHolder.call_icon = (ImageView)convertView.findViewById(R.id.call_icon);
            viewHolder.notifyStatus = (ImageView)convertView.findViewById(R.id.notifyStatus);


            viewHolder.helperName.setTypeface(karlaB);
            viewHolder.helperProfession.setTypeface(karlaR);
            viewHolder.helperWages.setTypeface(karlaR);


            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        int randInt = random.nextInt(6);
        int col = color[randInt];

        String imageLink = communityHelperSearchArraylist.get(i).getProfilePhoto();
        imageLink.toLowerCase();
        if (imageLink.endsWith(".png") || imageLink.endsWith(".jpg")) {
            viewHolder.relativeLayoutTop.setVisibility(View.VISIBLE);
            viewHolder.relativeLayoutBelow.setVisibility(View.INVISIBLE);

            Picasso.with(context).load(communityHelperSearchArraylist.get(i).getProfilePhoto()).resize(1000, 1000).placeholder(R.drawable.galleryplacholder).into(viewHolder.helperProfilePicture);
        } else {
            /*viewHolder.relativeLayoutBelow.setVisibility(View.VISIBLE);
            viewHolder.relativeLayoutTop.setVisibility(View.INVISIBLE);*/
            String str = communityHelperSearchArraylist.get(i).getName();
            str = String.valueOf(str.charAt(0));
            int position = 0;

            for (int j = 0; j < Alphabets.alphabets.size(); j++) {
                if (str.equalsIgnoreCase(Alphabets.alphabets.get(j))) {
                    position = j;
                }
            }

            viewHolder.helperProfilePicture.setImageResource(Alphabets.alphabetsDrawable.get(position));
            // str = str.toUpperCase();
            /*viewHolder.textInitial.setText(str);
            viewHolder.colorback.setColorFilter(col);*/


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

        if(getItem(i).getIs_in().equalsIgnoreCase("1")){
            viewHolder.notifyStatus.setImageResource(R.drawable.status_green);
        }else{
            viewHolder.notifyStatus.setImageResource(R.drawable.status_grey);
        }

        viewHolder.favourite_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RemoveHelperFromFavouriteList(i).execute();
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
        ImageView call_icon;
        ImageView notifyStatus;
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
                    "\"HelperID\":\"" + communityHelperSearchArraylist.get(position).getHelperId() + "\",\n" +
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
            dialog.setMessage("removing helper from your favourites...");
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
                    communityHelperSearchArraylist.remove(position);
                    notifyDataSetChanged();

                    if (communityHelperSearchArraylist.size() == 0) {
                        ((ActionBarActivity) context).onBackPressed();
                        Fragment fragment = new FavouriteHelpersFragment();
                        FragmentTransaction fragmentTransaction = ((ActionBarActivity) context).getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack("FavouriteHelpers");

                        try {
                            fragmentTransaction.commit();
                        } catch (IllegalStateException ignored) {
                        }


                    }
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