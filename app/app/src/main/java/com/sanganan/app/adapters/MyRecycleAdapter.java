package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sanganan.app.R;
import com.sanganan.app.fragments.BaseFragment;
import com.sanganan.app.fragments.HomePageLogOutFragment;
import com.sanganan.app.fragments.HomePageOfSociety;
import com.sanganan.app.model.TabData;

import java.util.ArrayList;


/**
 * Created by Randhir Patel on 24/5/17.
 */


public class MyRecycleAdapter extends RecyclerView.Adapter<MyRecycleAdapter.MyViewHolder> {

    private ArrayList<TabData> dataSet;
    private Context context;
    private BaseFragment fragment;
    Typeface ubantuM;
    int topMargin, sideMargin;
    private String fromWhere;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        ImageView imageViewIcon;
        View viewSeperator;
        private View itemView;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.textViewName = (TextView) itemView.findViewById(R.id.txt_count_tab);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.image_container);
            this.viewSeperator = itemView.findViewById(R.id.viewSeperator);


        }

        public View getView() {
            return itemView;
        }
    }

    public MyRecycleAdapter(Context context, ArrayList<TabData> data, int topMargin, int sideMargin, BaseFragment fragment, String fromWhere) {
        this.dataSet = data;
        this.context = context;
        this.topMargin = topMargin;
        this.sideMargin = sideMargin;
        this.fragment = fragment;
        this.fromWhere = fromWhere;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tab_single_view, parent, false);


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

        View itemView = holder.getView();
        if (itemView != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fromWhere.equalsIgnoreCase("login")) {
                        ((HomePageOfSociety) fragment).onItemListClicked(dataSet.get(listPosition).getTabName());
                    } else if (fromWhere.equalsIgnoreCase("logout")) {
                        ((HomePageLogOutFragment) fragment).onItemListClicked(dataSet.get(listPosition).getTabName());
                    }
                }
            });
        }

        if (dataSet.get(listPosition).getTabData().equalsIgnoreCase("0")) {
            textViewName.setVisibility(View.GONE);
        } else {
            textViewName.setVisibility(View.VISIBLE);
            textViewName.setText(dataSet.get(listPosition).getTabData());
        }
        imageView.setImageResource(dataSet.get(listPosition).getTabImage());

       /* if (listPosition==dataSet.size()-1){
            viewSeperator.setVisibility(View.GONE);
        }*/
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private void setLayoutParameters(MyViewHolder viewHolder) {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((ActionBarActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;

        android.view.ViewGroup.LayoutParams layoutParams = viewHolder.imageViewIcon.getLayoutParams();
        layoutParams.width = ((screenWidth - (4 * topMargin + 2 * sideMargin)) / 5);
        layoutParams.height = layoutParams.width;
        viewHolder.imageViewIcon.setLayoutParams(layoutParams);

        android.view.ViewGroup.LayoutParams layoutParamsView = viewHolder.viewSeperator.getLayoutParams();
        layoutParamsView.width = screenWidth * 10 / 736;
        layoutParamsView.height = screenHeight * 81 / 736;
        viewHolder.viewSeperator.setLayoutParams(layoutParamsView);

    }
}