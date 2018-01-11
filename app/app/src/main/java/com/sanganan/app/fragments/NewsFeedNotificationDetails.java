package com.sanganan.app.fragments;


import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.adapters.HorizontalImageList;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.customview.HorizontalListView;


import java.util.ArrayList;



/**
 * Created by pranav on 7/3/17.
 */


public class NewsFeedNotificationDetails extends BaseFragment {


    View view;
    TextView title, time_added_on, text, titletextView;
    ImageView image, share;
    RelativeLayout imageHolder;

    RelativeLayout contentHolder;

    String titleText, textDescription,time;
    ArrayList<String> listImage = new ArrayList<>();
    ArrayList<String> finalListImage = new ArrayList<>();
    Typeface karlaB, wsRegular, wsLight, ubuntuB;



    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();

    HorizontalListView listhorizontal;
    TextView imageTitle;
    private FirebaseAnalytics mFirebaseAnalytics;
    Common common;
    ImageView imgSeverity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.notification_detail_layout1, container, false);

        common = Common.getNewInstance(getActivity());
        imgSeverity = (ImageView) view.findViewById(R.id.imgSeverity);
        imgSeverity.setVisibility(View.GONE);
        initializeVariables(view);
        titletextView.setText("App Feed");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        Bundle logBundle = new Bundle();
        logBundle.putString("society_id", common.getStringValue("ID"));
        logBundle.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("notification_details_clicked", logBundle);

        Bundle bundle = getArguments();
        titleText = bundle.getString("title");
        textDescription = bundle.getString("detail");
        listImage.clear();
        if(bundle.containsKey("imageList")) {
            listImage = bundle.getStringArrayList("imageList");
        }
        time = bundle.getString("date");

        time_added_on.setText(time);
        title.setText(titleText);
        text.setText(textDescription);

        finalListImage.clear();

        for (int i = 0; i < listImage.size(); i++) {
            if (listImage.get(i).endsWith(".png") || listImage.get(i).endsWith(".jpg")) {
                finalListImage.add(listImage.get(i));
            }
        }


        HorizontalImageList imageAdapter = new HorizontalImageList(getActivity(), finalListImage);
        listhorizontal.setAdapter(imageAdapter);


        if (finalListImage.size() == 0) {
            imageHolder.setVisibility(View.INVISIBLE);
            contentHolder.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }

        listhorizontal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DialogFragment fragment = new ImageSlideShow();
                Bundle bundle1 = new Bundle();
                bundle1.putStringArrayList("imagelist", finalListImage);
                bundle1.putInt("position", position);
                fragment.setArguments(bundle1);
                fragment.setRetainInstance(true);
                fragment.show(getFragmentManager(), "tag_delivery");
            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Notification for" + common.getStringValue(Constants.userRwaName) + "date :" + time + " Title :" + titleText + "Details: " + textDescription + "Download mynukad app from apple or google store http://www.mynukad.com/");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        return view;
    }

    private void initializeVariables(View view) {

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        karlaB = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");
        wsRegular = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");
        wsLight = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Light.ttf");

        title = (TextView) view.findViewById(R.id.title);
        title.setTypeface(karlaB);
        time_added_on = (TextView) view.findViewById(R.id.time_added_on);
        time_added_on.setTypeface(wsLight);
        text = (TextView) view.findViewById(R.id.text);
        text.setTypeface(wsRegular);
        titletextView = (TextView) view.findViewById(R.id.titletextView);
        titletextView.setTypeface(ubuntuB);
        listhorizontal = (HorizontalListView) view.findViewById(R.id.listhorizontal);
        imageHolder = (RelativeLayout) view.findViewById(R.id.imageHolder);

        imageTitle = (TextView) view.findViewById(R.id.imageTitle);
        share = (ImageView) view.findViewById(R.id.share);
        imageTitle.setTypeface(karlaB);
        contentHolder = (RelativeLayout) view.findViewById(R.id.contentHolder);

    }


}
