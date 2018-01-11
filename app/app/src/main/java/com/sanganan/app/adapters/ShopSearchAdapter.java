package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.model.HelperModel;
import com.sanganan.app.model.NearByShopSearch;

import java.util.ArrayList;

/**
 * Created by root on 24/10/16.
 */

public class ShopSearchAdapter extends BaseAdapter {


    Context context;
    ArrayList<NearByShopSearch> searchModels;
    LayoutInflater inflater;
    Typeface karlaB,karlaR;

    public ShopSearchAdapter(Context context, ArrayList<NearByShopSearch> listRwa) {
        this.context = context;
        this.searchModels = listRwa;
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
            convertView = inflater.inflate(R.layout.search_single_view, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.name);
            viewHolder.name.setTypeface(karlaB);
            viewHolder.job = (TextView)convertView.findViewById(R.id.fieldAny);
            viewHolder.job.setTypeface(karlaR);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(searchModels.get(position).getShopName());
        viewHolder.job.setText(searchModels.get(position).getType());


        return convertView;
    }

    private class ViewHolder {
        TextView name, job;
    }
}
