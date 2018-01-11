package com.sanganan.app.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanganan.app.R;
import com.squareup.picasso.Picasso;

public class FullscreenImageAdapter extends PagerAdapter {

    private Context _activity;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;
    String typeTag;

    public FullscreenImageAdapter(Context activity, ArrayList<String> imagePaths) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        this.typeTag = "";
    }

    public FullscreenImageAdapter(Context activity, ArrayList<String> imagePaths, String typeTag) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        this.typeTag = typeTag;
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
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;
        TextView page_number;
        Button btnClose;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        page_number = (TextView) viewLayout.findViewById(R.id.page_number);

        try {
            Picasso.with(_activity).load(_imagePaths.get(position)).placeholder(R.drawable.galleryplacholder).into(imgDisplay);
          //for loader till image load//  Picasso.with(_activity).load(_imagePaths.get(position)).placeholder(R.drawable.picasso_loader_drawable).into(imgDisplay);
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
        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}