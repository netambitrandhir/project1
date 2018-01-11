package com.sanganan.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.common.Common;
import com.sanganan.app.model.GalleryData;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by root on 21/4/17.
 */

public class GalleryShowImageAdapter extends PagerAdapter {


    private Context _activity;
    private ArrayList<GalleryData> _imagePaths;
    private LayoutInflater inflater;
    String typeTag;
    Typeface karlaB, karlaR;
    Common common;

    // constructor
    public GalleryShowImageAdapter(Context activity, ArrayList<GalleryData> imagePaths) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        this.typeTag = "";
        karlaB = Typeface.createFromAsset(_activity.getAssets(), "Karla-Bold.ttf");
        karlaR = Typeface.createFromAsset(_activity.getAssets(), "Karla-Regular.ttf");
        common = Common.getNewInstance(activity);
    }

    public GalleryShowImageAdapter(Context activity, ArrayList<GalleryData> imagePaths, String typeTag) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        this.typeTag = typeTag;
        karlaB = Typeface.createFromAsset(_activity.getAssets(), "Karla-Bold.ttf");
        karlaR = Typeface.createFromAsset(_activity.getAssets(), "Karla-Regular.ttf");
        common = Common.getNewInstance(activity);
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imgDisplay, share;
        TextView page_number;
        TextView nameTxtView, detailTxtViewId;


        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.gallerylastpage, container,
                false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        page_number = (TextView) viewLayout.findViewById(R.id.page_number);
        nameTxtView = (TextView) viewLayout.findViewById(R.id.nameTxtView);
        nameTxtView.setTypeface(karlaB);
        detailTxtViewId = (TextView) viewLayout.findViewById(R.id.detailTxtViewId);
        detailTxtViewId.setTypeface(karlaR);
        share = (ImageView) viewLayout.findViewById(R.id.share);

        try {
            Picasso.with(_activity).load(_imagePaths.get(position).getPhotoPath()).into(imgDisplay);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int total = _imagePaths.size();
        int number = position + 1;

        if (typeTag.isEmpty()) {
            page_number.setText(number + "  of   " + total);
        } else {
            page_number.setVisibility(View.GONE);
        }

        nameTxtView.setText(_imagePaths.get(position).getFirstName() + " , " + _imagePaths.get(position).getFlatNbr());
        detailTxtViewId.setText(_imagePaths.get(position).getPhotoCaption());

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                //USE DIFFERENT LINK FOR PRODUCTION and DEVELOPMENT
                //http://52.91.49.255/nukkad_dev/media_view.php?
                //http://mynukad.com/Nukkad-admin/media_view.php?
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "http://mynukad.com/Nukkad-admin/media_view.php?sid=" + common.getStringValue("ID") + "&mid=" + _imagePaths.get(position).getPhotoId());
                sendIntent.setType("text/plain");
                _activity.startActivity(sendIntent);
            }
        });

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

}
