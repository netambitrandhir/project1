package com.sanganan.app.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sanganan.app.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

/**
 * Created by root on 10/10/16.
 */
public class HorizontalImageListComplainDetail extends BaseAdapter {
    private LayoutInflater inflater;
    Context context;
    ArrayList<String> imageList;


    public HorizontalImageListComplainDetail(Context context, ArrayList<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageList.get(position);
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
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.horizontal_complain_detail_image, null);
            viewHolder.complainimage = (ImageView) convertView.findViewById(R.id.complainimage);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            Picasso.with(context).load(imageList.get(position)).placeholder(R.drawable.galleryplacholder).into(viewHolder.complainimage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {

        ImageView complainimage;


    }
}
