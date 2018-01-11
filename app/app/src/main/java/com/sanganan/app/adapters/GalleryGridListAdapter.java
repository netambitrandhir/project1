package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.model.GalleryData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by root on 10/10/16.
 */

public class GalleryGridListAdapter extends BaseAdapter {
    ArrayList<GalleryData> galleryDataArrayList;
    Context context;
    LayoutInflater inflater;
    Typeface worksansM;

    public GalleryGridListAdapter(Context context, ArrayList<GalleryData> galleryDataArrayList) {
        this.context = context;
        this.galleryDataArrayList = galleryDataArrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return galleryDataArrayList.size();
    }

    @Override
    public GalleryData getItem(int position) {
        return galleryDataArrayList.get(position);
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

            convertView = inflater.inflate(R.layout.gallery_list_grid_singleview, null);

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.textNameFlt = (TextView) convertView.findViewById(R.id.textNameFlt);
            viewHolder.textLikeCount = (TextView) convertView.findViewById(R.id.textLikeCount);
            viewHolder.like_not_liked = (ImageView) convertView.findViewById(R.id.like_not_liked);
            worksansM = Typeface.createFromAsset(context.getAssets(), "WorkSans-Medium.ttf");
            viewHolder.textNameFlt.setTypeface(worksansM);
            viewHolder.textLikeCount.setTypeface(worksansM);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String link = galleryDataArrayList.get(position).getPhotoPath();
        try {
            Picasso.with(context).load(link).placeholder(R.drawable.galleryplacholder).resize(100, 100).into(viewHolder.imageView);
        } catch (Exception ex) {
        }
        String nameFlat = galleryDataArrayList.get(position).getFirstName() + "-" + galleryDataArrayList.get(position).getFlatNbr();
        viewHolder.textNameFlt.setText(nameFlat);
        viewHolder.textLikeCount.setText(getItem(position).getLIKES());



        return convertView;
    }

    private class ViewHolder {
        ImageView imageView,like_not_liked;
        TextView textNameFlt, textLikeCount;
    }
}
