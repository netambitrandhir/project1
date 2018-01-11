package com.sanganan.app.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.GetFavouriteList;
import com.sanganan.app.fragments.CallListDialog;
import com.sanganan.app.model.NearByShopSearch;
import com.sanganan.app.utility.GPSTracker;
import com.sanganan.app.utility.Utility;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class NearbyShopAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    Context context;
    Typeface ubuntuB, karlaB, ubantuM;
    ArrayList<NearByShopSearch> nearByShopSearchArrayList;
    boolean isLoggedIn;
    Common common;
    ViewHolder viewHolder = null;
    private FirebaseAnalytics mFirebaseAnalytics;

    public NearbyShopAdapter(Context context, ArrayList<NearByShopSearch> nearbyshopList) {
        this.context = context;
        this.nearByShopSearchArrayList = nearbyshopList;
        common = Common.getNewInstance(context);
        isLoggedIn = common.getBooleanValue(Constants.isLoggedIn);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void updateReceiptsList(ArrayList<NearByShopSearch> newlist) {
        nearByShopSearchArrayList = newlist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return nearByShopSearchArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return nearByShopSearchArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {


        if (view == null) {

            viewHolder = new ViewHolder();
            ubuntuB = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");
            karlaB = Typeface.createFromAsset(context.getAssets(), "WorkSans-Medium.ttf");
            ubantuM = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.shop_screen_list_single, null);
            viewHolder.listing_img_3 = (ImageView) view.findViewById(R.id.shopImage);

            viewHolder.bop_store = (TextView) view.findViewById(R.id.shopname);
            viewHolder.bop_store.setTypeface(ubuntuB);
            viewHolder.store = (TextView) view.findViewById(R.id.shoptype);
            viewHolder.store.setTypeface(karlaB);
            viewHolder.shop_complex = (TextView) view.findViewById(R.id.shop_complex);
            viewHolder.shop_complex.setTypeface(ubantuM);
            viewHolder.address = (TextView) view.findViewById(R.id.address);
            viewHolder.address.setTypeface(ubantuM);
            viewHolder.distance = (TextView) view.findViewById(R.id.distance);
            viewHolder.distance.setTypeface(ubuntuB);
            viewHolder.locater = (LinearLayout) view.findViewById(R.id.navigation_btn);
            viewHolder.favorite_indicator = (ImageView) view.findViewById(R.id.favoriteImage);
            viewHolder.call_icon = (ImageView) view.findViewById(R.id.call_icon);

            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String pathLink = nearByShopSearchArrayList.get(i).getImage2();
        if (!pathLink.isEmpty() && (pathLink.endsWith(".jpg") || pathLink.endsWith(".png")) && !pathLink.equalsIgnoreCase("null")) {
            Picasso.with(context).load(pathLink).placeholder(R.drawable.shopplaceholder).into(viewHolder.listing_img_3);
        }

        viewHolder.bop_store.setText(nearByShopSearchArrayList.get(i).getShopName());
        viewHolder.store.setText(nearByShopSearchArrayList.get(i).getType());
        viewHolder.shop_complex.setText(nearByShopSearchArrayList.get(i).getAddress1() + " " + nearByShopSearchArrayList.get(i).getAddress4());
        viewHolder.address.setText(nearByShopSearchArrayList.get(i).getCity());
        viewHolder.distance.setText(nearByShopSearchArrayList.get(i).getDistance());

        if (isLoggedIn) {
            if (GetFavouriteList.hsNearBy.contains(nearByShopSearchArrayList.get(i).getNearById())) {
                viewHolder.favorite_indicator.setImageResource(R.drawable.favorite_seletedheart);
            } else {
                viewHolder.favorite_indicator.setImageResource(R.drawable.favorite_unseletedheart);
            }
        }


        viewHolder.locater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GPSTracker gpsTracker = new GPSTracker(context);
                Location location = gpsTracker.getLocation();
                try {
                    if (gpsTracker.canGetLocation()) {
                        double lat = location.getLatitude();
                        double lang = location.getLongitude();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" + lat + "," + lang + "&daddr=" + nearByShopSearchArrayList.get(i).getLatitude() + "," + nearByShopSearchArrayList.get(i).getLongitude()));
                        context.startActivity(intent);
                    } else {
                        common.showShortToast("Check your gps setting...");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    common.showShortToast("Check your network setting...");
                }
            }
        });

        viewHolder.favorite_indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggedIn) {

                    if (GetFavouriteList.hsNearBy.contains(nearByShopSearchArrayList.get(i).getNearById())) {
                        GetFavouriteList.hsNearBy.remove(nearByShopSearchArrayList.get(i).getNearById());
                        notifyDataSetChanged();
                        new RemoveShopFromFavouriteList(i).execute();////doing this way to show instant addition or removal of favourite on ui

                    } else {
                        GetFavouriteList.hsNearBy.add(nearByShopSearchArrayList.get(i).getNearById());
                        notifyDataSetChanged();
                        new AddShopToFavouriteList(i).execute();
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(viewGroup, "Member of this Society ? Login or Sign-up ", Snackbar.LENGTH_SHORT);//change this message according to UX

                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(context.getResources().getColor(R.color.color_yesboss));
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    snackbar.show();
                }
            }
        });

        viewHolder.call_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> listPhoneNumbers = new ArrayList<String>();
                NearByShopSearch search = nearByShopSearchArrayList.get(i);
                listPhoneNumbers.add(search.getPhoneNbr1());
                listPhoneNumbers.add(search.getPhoneNbr2());
                listPhoneNumbers.add(search.getPhoneNbr3());
                if (listPhoneNumbers.size() != 0) {
                    // AlertBoxForImagePic(i);
                    CallListDialog dialog = new CallListDialog();
                    Bundle bundle1 = new Bundle();
                    bundle1.putStringArrayList("listNumber", listPhoneNumbers);
                    dialog.setArguments(bundle1);
                    dialog.setRetainInstance(true);
                    ActionBarActivity activity = (ActionBarActivity) context;
                    dialog.show(activity.getSupportFragmentManager(), "phone calling");
                } else
                    Toast.makeText(context, "No number available for shopkeeper", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private class ViewHolder {
        TextView bop_store;
        TextView store;
        TextView shop_complex;
        TextView address;
        TextView distance;
        ImageView listing_img_3;
        LinearLayout locater;
        ImageView favorite_indicator;
        ImageView call_icon;
    }

    void slidetext(TextView tv1) {
        tv1.setLines(1);
        tv1.setHorizontallyScrolling(true);
        tv1.setMarqueeRepeatLimit(-1);
        tv1.setSelected(true);
    }

    private class RemoveShopFromFavouriteList extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        int position;

        RemoveShopFromFavouriteList(int position) {
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... voids) {


            String stringResponse = null;
            String status = null;
            String jsonraw = "{\n" +
                    "\"NearByID\":\"" + nearByShopSearchArrayList.get(position).getNearById() + "\",\n" +
                    "\"UserID\":\"" + common.getStringValue(Constants.id) + "\"\n" +
                    "}";

            try {
                HttpResponse categoryResponse = Utility.postDataOnUrl(
                        Constants.BaseUrl + "deletefavshop", jsonraw);


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
            dialog.setMessage("Loading...");
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

                }
            }
        }
    }

    private class AddShopToFavouriteList extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        int position;

        AddShopToFavouriteList(int position) {
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... voids) {


            String stringResponse = null;
            String status = null;
            String jsonraw = "{\n" +
                    "\"NearByID\":\"" + nearByShopSearchArrayList.get(position).getNearById() + "\",\n" +
                    "\"UserID\":\"" + common.getStringValue(Constants.id) + "\"\n" +
                    "}";

            try {
                HttpResponse categoryResponse = Utility.postDataOnUrl(
                        Constants.BaseUrl + "addfavshop", jsonraw);


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
            dialog.setMessage("Loading...");
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
                    GetFavouriteList.hsNearBy.add(nearByShopSearchArrayList.get(position).getNearById());
                    notifyDataSetChanged();
                }
            }
        }
    }


    public void AlertBoxForImagePic(final int i) {


        new AlertDialog.Builder(context)
                .setTitle("Please Wait....")
                .setMessage("Want to call Shopkeeper")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                String uri = "tel:" + nearByShopSearchArrayList.get(i).getPhoneNbr1().trim();
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse(uri));
                                context.startActivity(callIntent);

                                if (isLoggedIn) {
                                    Bundle logBundleChat = new Bundle();
                                    logBundleChat.putString("shop_id", nearByShopSearchArrayList.get(i).getNearById());
                                    logBundleChat.putString("user_id", common.getStringValue(Constants.id));
                                    mFirebaseAnalytics.logEvent("call_shop", logBundleChat);
                                }

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
