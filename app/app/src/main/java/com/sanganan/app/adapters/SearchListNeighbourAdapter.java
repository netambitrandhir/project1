package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.model.SearchModel;
import com.sanganan.app.model.YourNeighbourSearch;

import java.util.ArrayList;

/**
 * Created by root on 24/10/16.
 */

public class SearchListNeighbourAdapter extends BaseAdapter {


    Context context;
    ArrayList<YourNeighbourSearch> searchModels;
    LayoutInflater inflater;
    Typeface karlaB, karlaR;

    public SearchListNeighbourAdapter(Context context, ArrayList<YourNeighbourSearch> listRwa) {
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
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.name.setTypeface(karlaB);
            viewHolder.flat_job = (TextView) convertView.findViewById(R.id.fieldAny);
            viewHolder.flat_job.setTypeface(karlaR);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(searchModels.get(position).getFirstName());
        if (!searchModels.get(position).getOccupation().isEmpty()) {
            viewHolder.flat_job.setText(searchModels.get(position).getFlatNbr() + " , " + searchModels.get(position).getOccupation());
        } else {
            viewHolder.flat_job.setText(searchModels.get(position).getFlatNbr());
        }

        if(getCount()==0){
            System.out.print("Hello blank list");
        }

        return convertView;
    }

    private class ViewHolder {
        TextView name, flat_job;
    }
}
