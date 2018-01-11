package com.sanganan.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import com.example.elitebook8740p.pynt_sanganan.R;
import com.sanganan.app.R;
import com.sanganan.app.common.Constants;
import com.sanganan.app.model.NavDrawerItem;

import java.util.ArrayList;


public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;
    Typeface worksansM;
    TextView txtTitle;
    ImageView imgIcon;


    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            worksansM = Typeface.createFromAsset(context.getAssets(), "WorkSans-Medium.ttf");

            convertView = mInflater.inflate(R.layout.drawer_list_item, null);

            txtTitle = (TextView) convertView.findViewById(R.id.title);
            txtTitle.setTypeface(worksansM);
            imgIcon = (ImageView) convertView.findViewById(R.id.navImage);
        }


        imgIcon.setImageResource(navDrawerItems.get(position).getImage());
        txtTitle.setText(navDrawerItems.get(position).getTitle());

        return convertView;
    }




}
