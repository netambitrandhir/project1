package com.sanganan.app.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.sanganan.app.R;
import com.sanganan.app.adapters.GalleryShowImageAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.model.GalleryData;
import com.sanganan.app.model.GalleryParent;
import java.util.ArrayList;

/**
 * Created by root on 12/10/16.
 */

public class ShowGalleryImagesFragmnet extends DialogFragment {

    View view;
    RequestQueue requestQueue;
    Gallery gallery;
    ImageView dSwipingImage;
    Typeface ubuntuB, karlaB, karlaR;
    TextView name, detail, title;
    ImageView share, reportasspam;
    int picChoosenToShow = 0;

    ArrayList<GalleryData> listDataGallery;

    Common common;
    LinearLayout pager_indicator;
    RelativeLayout boundry_image;

    Bundle bundle;
    int listPosition;
    int imageChoosenPosition;
    ViewPager image_swipe;
    GalleryShowImageAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.image_slide_show, container, false);
        common = Common.getNewInstance(getActivity());

        bundle = getArguments();
        listPosition = bundle.getInt("position");
        imageChoosenPosition = bundle.getInt("picNumber");

        GalleryParent galleryParent = SocietyGalleryFragment.parentList.get(listPosition);
        listDataGallery = galleryParent.getDataArrayList();

        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        getDialog().setCanceledOnTouchOutside(false);

        image_swipe = (ViewPager) view.findViewById(R.id.image_pager);


        if (listDataGallery.size() == 1) {
            adapter = new GalleryShowImageAdapter(getActivity(), listDataGallery, "singleImage");
            image_swipe.setAdapter(adapter);
            image_swipe.setCurrentItem(imageChoosenPosition);
        } else {
            adapter = new GalleryShowImageAdapter(getActivity(), listDataGallery);
            image_swipe.setAdapter(adapter);
            image_swipe.setCurrentItem(imageChoosenPosition);
        }




        return view;



/*

        long timestamp = Long.parseLong(galleryParent.getDate()); //Example -> in ms
        Date d = new Date();
        d.setTime(timestamp * 1000L);
        String date = d.toString();
        String[] dateArr = date.split(" ");
        date = dateArr[2] + " " + dateArr[1] + " " + dateArr[5].substring(2, 4);
        title.setText(date);

        maxSwipes = listDataGallery.size() - 1;
        leftSwipesLeft = picChoosenToShow;
        rightSwipesLeft = maxSwipes - leftSwipesLeft;
        mIndicator = leftSwipesLeft;
*/

/*
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        try {
            Picasso.with(getActivity()).load(listDataGallery.get(picChoosenToShow).getPhotoPath()).into(dSwipingImage);
            name.setText(listDataGallery.get(picChoosenToShow).getFirstName() + "," + listDataGallery.get(picChoosenToShow).getFlatNbr());
            detail.setText(listDataGallery.get(picChoosenToShow).getPhotoCaption());
            setUiPageViewController(mIndicator);

            dSwipingImage.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {


                public void onSwipeRight() {

                    if (leftSwipesLeft > 0) {
                        leftSwipesLeft--;
                        rightSwipesLeft++;
                        callImageChanger(rightSwipesLeft);
                        mIndicator--;
                        setUiPageViewController(mIndicator);
                    }
                }

                public void onSwipeLeft() {
                    if (rightSwipesLeft > 0) {
                        leftSwipesLeft++;
                        rightSwipesLeft--;
                        callImageChanger(rightSwipesLeft);
                        mIndicator++;
                        setUiPageViewController(mIndicator);
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        reportasspam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int recentPosition = listDataGallery.size() - leftSwipesLeft - 1;
                Instabug.setUserEmail(common.getStringValue(Constants.email));
                Instabug.setUserData("Gallery Image   Picture Path is : " + listDataGallery.get(recentPosition).getPhotoPath() + "User ID who reported : "
                        + common.getStringValue(Constants.id) + " Society ID : " + common.getStringValue("ID"));
                Instabug.invoke(InstabugInvocationMode.PROMPT_OPTION);
            }
        });
        return view;*/
    }

    private void callImageChanger(int leftSwipesLeft) {
     /*   int recentPosition = listDataGallery.size() - leftSwipesLeft - 1;
        Picasso.with(getActivity()).load(listDataGallery.get(recentPosition).getPhotoPath()).into(dSwipingImage);
        name.setText(listDataGallery.get(recentPosition).getFirstName() + "," + listDataGallery.get(recentPosition).getFlatNbr());
        detail.setText(listDataGallery.get(recentPosition).getPhotoCaption());*/
    }

    private void initializeVariables(View view) {
       /* dSwipingImage = (ImageView) view.findViewById(R.id.imageViewSwipe);
        boundry_image = (RelativeLayout) view.findViewById(R.id.boundry_image);
        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        karlaB = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");
        karlaR = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Regular.ttf");
        name = (TextView) view.findViewById(R.id.nameTxtView);
        detail = (TextView) view.findViewById(R.id.detailTxtViewId);
        title = (TextView) view.findViewById(R.id.titletextView);
        share = (ImageView) view.findViewById(R.id.share);
        reportasspam = (ImageView) view.findViewById(R.id.reportasspam);

        name.setTypeface(karlaB);
        detail.setTypeface(karlaR);
        title.setTypeface(ubuntuB);
        pager_indicator = (LinearLayout) view.findViewById(R.id.indi);*/

    }

    private void setUiPageViewController(int positionSelected) {

/*
        int dotsCount = listDataGallery.size();
        ImageView[] dots = new ImageView[dotsCount];
        pager_indicator.removeAllViews();

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(10, 2, 10, 2);

            pager_indicator.addView(dots[i], params);
        }

        dots[positionSelected].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));*/
    }


}
