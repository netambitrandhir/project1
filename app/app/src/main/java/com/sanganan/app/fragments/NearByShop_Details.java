package com.sanganan.app.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.adapters.HomepageCustomPagerAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.GetFavouriteList;
import com.sanganan.app.model.NearByShopSearch;
import com.sanganan.app.utility.GPSTracker;
import com.sanganan.app.utility.Utility;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by raj on 1/3/2017.
 */

public class NearByShop_Details extends BaseFragment {
    View view;
    ImageView full_image, calli, favorite_helper;
    TextView titletextView, bopstore, generalstore, shopno, homedelivery, yes, opentime, yes2, description, details,getdirection;
    Typeface faceWSM, faceWSR, ubuntuM, ubuntuR, ubuntuB;

    String full_imageStr, yesStr, yes2Str, titletextViewStr, bopstoreStr, shopnoStr, generalstoreStr, detailsStr, imageStr2, imageStr3,distance,latitude,longitude;
    Common common;
    String dialogMsgStr, dialogMsgStr2, dialogMsgStr3;
    boolean isLoggedIn;
    String nearbyid;
    ArrayList<String> listNumber = new ArrayList<>();
    ArrayList<String> imagelist = new ArrayList<>();
    private ArrayList<String> finalListImage = new ArrayList<>();

    WebView webView;

    private FirebaseAnalytics mFirebaseAnalytics;


    void slidetext(TextView tv1) {
        tv1.setLines(1);
        tv1.setHorizontallyScrolling(true);
        tv1.setMarqueeRepeatLimit(-1);
        tv1.setSelected(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.nearbyshop_detailsbop, container, false);
        initializeView();
        imagelist.clear();
        listNumber.clear();

        final Bundle bundle = getArguments();
        common = Common.getNewInstance(getActivity());
        NearByShopsSearchFragmnet.isFromBack = true;
        titletextViewStr = bundle.getString("Name");
        bopstoreStr = bundle.getString("Name");
        generalstoreStr = bundle.getString("type");
        shopnoStr = bundle.getString("address");
        yesStr = bundle.getString("ishomedelivery");
        yes2Str = bundle.getString("is247");
        detailsStr = bundle.getString("description");
        distance = bundle.getString("distance");
        latitude = bundle.getString("latitude");
        longitude = bundle.getString("longitude");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());


        String sArr[] = detailsStr.split(" ");

        detailsStr = "";
        for (int i = 0; i < sArr.length; i++) {

            if (sArr[i].length() >= 20) {
                sArr[i] = sArr[i].substring(0, 20) + " " + sArr[i].substring(20, sArr[i].length());
            }
            detailsStr = detailsStr + sArr[i] + " ";
        }

        nearbyid = bundle.getString("nearbyid");

        dialogMsgStr = bundle.getString("phoneNbr1");
        dialogMsgStr2 = bundle.getString("phoneNbr2");
        dialogMsgStr3 = bundle.getString("phoneNbr3");

        full_imageStr = bundle.getString("bannerimage");
        imageStr2 = bundle.getString("image2");
        imageStr3 = bundle.getString("image3");

        listNumber.add(dialogMsgStr);
        listNumber.add(dialogMsgStr2);
        listNumber.add(dialogMsgStr3);

        imagelist.add(full_imageStr);
        imagelist.add(imageStr2);
        imagelist.add(imageStr3);

        getdirection.setText("Get Direction ("+distance+") >");

        for (int i = 0; i < listNumber.size(); i++) {

            if (listNumber.get(i).isEmpty()) {
                listNumber.remove(i);
            }
        }

        for (int i = 0; i < imagelist.size(); i++) {

            if (imagelist.get(i).isEmpty()) {
                imagelist.remove(i);
            }
        }
        String rightImageLink = "http://mynukad.com/images/right_btn.png";
        String noImageLink = "http://mynukad.com/images/close_btn.png";

        String t4x7 = "";
        String delivery = "";

        if (yesStr.equalsIgnoreCase("1")) {
            delivery = rightImageLink;
        } else {
            delivery = noImageLink;
        }

        if (yes2Str.equalsIgnoreCase("1")) {
            t4x7 = rightImageLink;
        } else {
            t4x7 = noImageLink;
        }


        titletextView.setText(titletextViewStr);
        bopstore.setText(bopstoreStr);
        generalstore.setText(generalstoreStr);
        shopno.setText(shopnoStr);

        String str = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                " <title>MyNukad</title>\n  <style type=\"text/css\">\n" +
                "        body {\n" +
                "            font-family: 'Ubuntu', sans-serif;\n" +
                "        }\n" +
                "        </style>" +
                "        <link href=\"https://fonts.googleapis.com/css?family=Ubuntu\" rel=\"stylesheet\">" +
                "</head>\n" +
                "<body style=\"line-height:22px;color:#4a4a4a;margin:0;padding:0;\">\n" +
                " <div style=\"background:#eeeeee;border:1px #979797 solid;padding:20px;border-radius:8px;margin:10px;\">\n" +
                "  <h3 style=\"width:100%;text-align:center;margin:0;padding-bottom:10px;border-bottom:1px #979797 solid;margin-bottom:10px;\">Other Info</h3>\n" +
                "  <div><b>Home delivery :</b> <img src = " + delivery + " style=\"width:23px;height:23px;margin-bottom:-6px;\"/></div>\n" +
                "  <div style=\"padding-top:0px;padding-bottom:0px;\"><b>24*7 open :</b> <img src = " + t4x7 + " style=\"width:23px;height:23px;margin-bottom:-6px;\"/></div>\n" +
                "  <div><b>Description :</b></div>\n <div>" + detailsStr + "</div>\n" +
                " </div>\n" +
                "</body>\n" +
                "</html>";

        webView.loadData(str, "text/html", null);


        full_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalListImage.size() > 0) {
                    DialogFragment fragment = new ImageSlideShow();
                    Bundle bundle1 = new Bundle();
                    bundle1.putStringArrayList("imagelist", finalListImage);
                    fragment.setArguments(bundle1);
                    fragment.setRetainInstance(true);
                    fragment.show(getFragmentManager(), "tag_slide_show");
                }
            }
        });


        getdirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSTracker gpsTracker = new GPSTracker(getActivity());
               try {
                   if (gpsTracker.canGetLocation()) {
                       Location location = gpsTracker.getLocation();
                       double lat = location.getLatitude();
                       double lang = location.getLongitude();

                       Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                               Uri.parse("http://maps.google.com/maps?saddr=" + lat + "," + lang + "&daddr=" + latitude + "," + longitude));
                       startActivity(intent);
                   } else {
                       common.showShortToast("Check gps setting of your device");
                   }
               }catch (Exception e){
                   e.printStackTrace();
                   common.showShortToast("Check network setting of your device");
               }
            }
        });

        for (int i = 0; i < imagelist.size(); i++) {
            String pathImage = imagelist.get(i);
            if (pathImage.endsWith(".jpg") || pathImage.endsWith(".png")) {
                finalListImage.add(pathImage);
            }
        }

        if (finalListImage.size() > 0) {
            Picasso.with(getActivity()).load(finalListImage.get(0)).placeholder(R.drawable.shopplaceholder).into(full_image);
        }
        isLoggedIn = common.getBooleanValue(Constants.isLoggedIn);

        if (isLoggedIn && common.getStringValue(Constants.approvalStatus).equalsIgnoreCase("Y")) {
            if (GetFavouriteList.hsNearBy.contains(nearbyid)) {
                favorite_helper.setImageResource(R.drawable.favorite_selectednew);
            } else {
                favorite_helper.setImageResource(R.drawable.favorite_helper);
            }
        }

        favorite_helper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggedIn && common.getStringValue(Constants.approvalStatus).equalsIgnoreCase("Y")) {

                    if (GetFavouriteList.hsNearBy.contains(nearbyid)) {
                        new RemoveShopFromFavouriteList().execute();
                        favorite_helper.setImageResource(R.drawable.favorite_helper);
                    } else {
                        new AddShopToFavouriteList().execute();
                        favorite_helper.setImageResource(R.drawable.favorite_selectednew);
                    }
                } else {
                   common.showShortToast("Please sign-up/login as society member");
                }
            }

        });

        calli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLoggedIn) {
                    Bundle logBundleChat = new Bundle();
                    logBundleChat.putString("shop_id", nearbyid);
                    logBundleChat.putString("user_id", common.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("call_shop", logBundleChat);
                }

                CallListDialog dialog = new CallListDialog();
                Bundle bundle1 = new Bundle();
                bundle1.putStringArrayList("listNumber", listNumber);
                bundle1.putString("shopID", nearbyid);
                dialog.setArguments(bundle1);
                dialog.setRetainInstance(true);
                dialog.show(getFragmentManager(), "phone calling");
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    private void initializeView() {

        ubuntuR = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-R.ttf");
        ubuntuM = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-M.ttf");
        faceWSR = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");
        faceWSM = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Medium.ttf");
        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        Typeface karlaBold = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");


        webView = (WebView) view.findViewById(R.id.webviewDescription);
        full_image = (ImageView) view.findViewById(R.id.full_imagebanner);
        calli = (ImageView) view.findViewById(R.id.calli);
        favorite_helper = (ImageView) view.findViewById(R.id.favorite_help);

        titletextView = (TextView) view.findViewById(R.id.titletextView);
        bopstore = (TextView) view.findViewById(R.id.bopstore);
        generalstore = (TextView) view.findViewById(R.id.generalstore);
        shopno = (TextView) view.findViewById(R.id.shopno);
        getdirection = (TextView)view.findViewById(R.id.getdirection);
        getdirection.setTypeface(karlaBold);


       /* homedelivery = (TextView) view.findViewById(R.id.homedelivery);
        yes = (TextView) view.findViewById(R.id.yes);
        opentime = (TextView) view.findViewById(R.id.opentime);
        yes2 = (TextView) view.findViewById(R.id.yes2);
        description = (TextView) view.findViewById(R.id.description);*/

        // details = (TextView) view.findViewById(R.id.details);


        titletextView.setTypeface(ubuntuM);
        bopstore.setTypeface(faceWSM);
        generalstore.setTypeface(faceWSR);
        shopno.setTypeface(faceWSR);

       /* homedelivery.setTypeface(ubuntuM);
        yes.setTypeface(ubuntuR);*/
/*
        opentime.setTypeface(ubuntuM);
*/
       /* yes2.setTypeface(ubuntuR);
        description.setTypeface(ubuntuM);*/
/*
        details.setTypeface(ubuntuR);
*/
    }


    private class RemoveShopFromFavouriteList extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;


        @Override
        protected String doInBackground(Void... voids) {
            String stringResponse = null;
            String status = null;
            String jsonraw = "{\n" +
                    "\"NearByID\":\"" + nearbyid + "\",\n" +
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

            dialog = new ProgressDialog(getActivity());
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
                    GetFavouriteList.hsNearBy.remove(nearbyid);

                }
            }
        }
    }

    private class AddShopToFavouriteList extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;


        @Override
        protected String doInBackground(Void... voids) {


            String stringResponse = null;
            String status = null;
            String jsonraw = "{\n" +
                    "\"NearByID\":\"" + nearbyid + "\",\n" +
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

            dialog = new ProgressDialog(getActivity());
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
                    GetFavouriteList.hsNearBy.add(nearbyid);
                }
            }
        }
    }
}
