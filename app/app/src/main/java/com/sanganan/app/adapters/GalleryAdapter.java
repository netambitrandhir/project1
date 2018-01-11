package com.sanganan.app.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.activities.FullViewGalleryImages;
import com.sanganan.app.common.RandomColorAllocator;
import com.sanganan.app.customview.AutoGridView;
import com.sanganan.app.customview.MyGridView;
import com.sanganan.app.fragments.AddComplaintActivity;
import com.sanganan.app.fragments.ImageSlideShow;
import com.sanganan.app.fragments.ShowGalleryImagesFragmnet;
import com.sanganan.app.model.GalleryData;
import com.sanganan.app.model.GalleryParent;
import com.sanganan.app.model.GeneralNotification;
import com.sanganan.app.utility.Helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Created by root on 10/10/16.
 */

public class GalleryAdapter extends BaseAdapter {

    Context context;
    ArrayList<GalleryParent> parentArrayList;
    LayoutInflater inflater;
    Typeface worksansM;
    RandomColorAllocator colorAllocator;


    public GalleryAdapter(Context context, ArrayList<GalleryParent> parentArrayList) {
        this.context = context;
        this.parentArrayList = parentArrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        colorAllocator = new RandomColorAllocator(getCount());


    }

    public void updateReceiptsList(ArrayList<GalleryParent> newlist) {
        this.parentArrayList = newlist;
        notifyDataSetChanged();
        colorAllocator = new RandomColorAllocator(getCount());

    }

    @Override
    public int getCount() {
        return parentArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return parentArrayList.get(position);
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

            convertView = inflater.inflate(R.layout.gallery_list_singleview, null);

            viewHolder.imagegridView = (MyGridView) convertView.findViewById(R.id.imagegridView);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);

            worksansM = Typeface.createFromAsset(context.getAssets(), "WorkSans-Medium.ttf");
            viewHolder.date.setTypeface(worksansM);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        long timestamp = Long.parseLong(parentArrayList.get(position).getDate()); //Example -> in ms
        Date d = new Date();
        d.setTime(timestamp * 1000L);
        String date = d.toString();
        String[] dateArr = date.split(" ");
        date = dateArr[2] + " " + dateArr[1] + " " + dateArr[5].substring(2, 4);


        viewHolder.date.setText(date);
        viewHolder.date.setTextColor(colorAllocator.listColor.get(position));

        ArrayList<GalleryData> galleryDatas = parentArrayList.get(position).getDataArrayList();
        int listSize = galleryDatas.size();

        GalleryGridListAdapter galleryGridListAdapter = new GalleryGridListAdapter(context, galleryDatas);
        viewHolder.imagegridView.setAdapter(galleryGridListAdapter);
        setDynamicHeight(viewHolder.imagegridView, 3);

        viewHolder.imagegridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {

                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putInt("picNumber", i);
                Intent intent = new Intent(context, FullViewGalleryImages.class);
                intent.putExtras(bundle);
                ((Activity) context).startActivityForResult(intent, 1000);

            }
        });


        return convertView;
    }


    private static class ViewHolder {
        MyGridView imagegridView;
        TextView date;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setDynamicHeight(GridView gridView, int numColumns) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = gridViewAdapter.getCount();
        int rows = 0;

        View listItem = gridViewAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        double x = 1;
        int y = 1;
        if (items > numColumns) {
            x = (double) items / numColumns;
            y = items / numColumns;
            if (x > y) {
                rows = y + 1;
            } else {
                rows = y;
            }
            totalHeight *= rows;
            totalHeight = totalHeight + gridView.getVerticalSpacing() * (rows - 1);
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }
}
