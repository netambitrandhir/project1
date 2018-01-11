package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.model.Flat;
import com.sanganan.app.model.SearchModel;
import com.sanganan.app.model.YourNeighbourSearch;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by root on 24/10/16.
 */

public class FlatHintAdapter extends BaseAdapter {


    Context context;
    ArrayList<Flat> searchModels;
    LayoutInflater inflater;
    Typeface karlaB, karlaR;
    private ArrayList<Flat> arraylist;


    public FlatHintAdapter(Context context, ArrayList<Flat> listflat) {
        this.context = context;
        this.searchModels = listflat;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(listflat);
    }

    @Override
    public int getCount() {
        return searchModels.size();
    }

    @Override
    public Object getItem(int position) {
        return searchModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            karlaR = Typeface.createFromAsset(context.getAssets(), "Karla-Regular.ttf");
            karlaB = Typeface.createFromAsset(context.getAssets(), "Karla-Bold.ttf");

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_flathint_single_view, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.name.setTypeface(karlaB);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.name.setTextColor(Color.parseColor("#4a4a4a"));
        viewHolder.name.setText(searchModels.get(position).getName());

        return convertView;
    }

    private class ViewHolder {
        TextView name;
    }


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        searchModels.clear();
        if (charText.length() == 0) {
            searchModels.addAll(arraylist);
        } else {
            for (Flat wp : arraylist) {
                if (wp.toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                    searchModels.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
