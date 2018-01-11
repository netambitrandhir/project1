package com.sanganan.app.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.activities.StartSearchPage;
import com.sanganan.app.model.GeneralNotification;
import com.sanganan.app.model.SearchModel;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.*;

public class ImageAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    Context context;
    ArrayList<SearchModel> searchModelArrayList;
    Typeface ubuntuB, karlaB;

/*
    int color[] = {Color.parseColor("#DBA55B"), Color.parseColor("#DA7589"), Color.parseColor("#053B57"), Color.parseColor("#6599FF"), Color.parseColor("#D26A5D"), Color.parseColor("#86959F")};
*/

    public ImageAdapter(Context context, ArrayList<SearchModel> searchModelArrayList) {
        this.context = context;
        this.searchModelArrayList = searchModelArrayList;

    }

    public void updateReceiptsList(ArrayList<SearchModel> newlist) {
        searchModelArrayList = newlist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return searchModelArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return searchModelArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        ubuntuB = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");
        karlaB = Typeface.createFromAsset(context.getAssets(), "Karla-Bold.ttf");

        if (view == null) {
            holder = new ViewHolder();

            inflater = (LayoutInflater) context.getSystemService(StartSearchPage.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_image_adapter, null);

            holder.imageView = (ImageView) view.findViewById(R.id.imageView);
            holder.name = (TextView) view.findViewById(R.id.nameTxtViewId);
            holder.name.setTypeface(ubuntuB);
            holder.address = (TextView) view.findViewById(R.id.addressTxtViewId);
            holder.address.setTypeface(karlaB);
            holder.distance = (TextView) view.findViewById(R.id.distance);
            holder.distance.setTypeface(karlaB);
            view.setTag(holder);


        } else {
            holder = (ViewHolder) view.getTag();
        }

        try {
            Picasso.with(context).load(searchModelArrayList.get(i).getImage()).placeholder(R.drawable.society_placeholder).into(holder.imageView);
            holder.name.setText(searchModelArrayList.get(i).getSocietyName());
            holder.address.setText(searchModelArrayList.get(i).getAddress1() + "," + searchModelArrayList.get(i).getCity());
            holder.distance.setText(searchModelArrayList.get(i).getDistance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;


    }

    private class ViewHolder {

        private ImageView imageView;
        private TextView name;
        private TextView address;
        private TextView distance;

    }
}



