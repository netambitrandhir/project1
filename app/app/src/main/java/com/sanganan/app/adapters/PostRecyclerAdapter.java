package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.model.TabData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Randhir Patel on 15/6/17.
 */

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.MyViewHolder> {

    private String[] dataSet;
    private Context context;
    Typeface ubantuM;
    int height;
    int totalLeftMargin;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        ImageView imageViewIcon;
        View viewSeperator;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.txt_count_tab);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.image_container);
            this.viewSeperator = itemView.findViewById(R.id.viewSeperator);

        }
    }

    public PostRecyclerAdapter(Context context, String[] data, int height, int totalLeftMargin) {
        this.dataSet = data;
        this.context = context;
        this.height = height;
        this.totalLeftMargin = totalLeftMargin;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_single_view, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        setLayoutParameters(myViewHolder);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textViewName = holder.textViewName;
        ImageView imageView = holder.imageViewIcon;
        View viewSeperator = holder.viewSeperator;
        ubantuM = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");
        textViewName.setTypeface(ubantuM);
        textViewName.setVisibility(View.GONE);
        Picasso.with(context).load(dataSet[listPosition]).placeholder(R.drawable.galleryplacholder).into(imageView);

    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }

    private void setLayoutParameters(MyViewHolder viewHolder) {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((ActionBarActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;


        android.view.ViewGroup.LayoutParams layoutParams = viewHolder.imageViewIcon.getLayoutParams();
        android.view.ViewGroup.LayoutParams layoutParamsView = viewHolder.viewSeperator.getLayoutParams();
        int width = (screenWidth - 2 * totalLeftMargin);

        layoutParamsView.height = height;
        layoutParamsView.width = (width - 3 * height) / 2;

        layoutParams.width = (width - 2*layoutParamsView.width) / 3;
        layoutParams.height = layoutParams.width;


        viewHolder.viewSeperator.setLayoutParams(layoutParamsView);

        viewHolder.imageViewIcon.setLayoutParams(layoutParams);

    }
}
