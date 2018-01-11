package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.model.ComplainData;
import com.sanganan.app.model.GeneralNotification;

import java.util.ArrayList;

/**
 * Created by root on 23/9/16.
 */


public class ComplainListAllAdapter extends BaseAdapter {

    Context context;
    ArrayList<ComplainData> complainArrayList;
    LayoutInflater inflater;
    Typeface ubantuB, ubantuI, karlaB;
    int drawablesStatus[] = {R.drawable.registered, R.drawable.assigned, R.drawable.resolved, R.drawable.acknowledged, R.drawable.invalid};


    public ComplainListAllAdapter(Context context, ArrayList<ComplainData> complainArrayList) {
        this.complainArrayList = complainArrayList;
        this.context = context;
        Log.d("Adapter call", "adapter" + complainArrayList.size());

    }

    public void updateReceiptsList(ArrayList<ComplainData> newlist) {
        complainArrayList = newlist;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return complainArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return complainArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.complainlistsingleviewlayout, null);
            viewHolder.msgText = (TextView) convertView.findViewById(R.id.msgTextview);
            viewHolder.category = (TextView) convertView.findViewById(R.id.category);
            viewHolder.timeAndDate = (TextView) convertView.findViewById(R.id.timeandadte);
            viewHolder.nextButton = (ImageView) convertView.findViewById(R.id.nextbutton);
            viewHolder.statusTag = (ImageView) convertView.findViewById(R.id.statusTag);
            viewHolder.textId = (TextView) convertView.findViewById(R.id.textId);
            viewHolder.addedBy = (TextView) convertView.findViewById(R.id.addedBy);

            ubantuB = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");
            ubantuI = Typeface.createFromAsset(context.getAssets(), "Ubuntu-RI.ttf");
            karlaB = Typeface.createFromAsset(context.getAssets(), "Karla-Bold.ttf");

            viewHolder.msgText.setTypeface(karlaB);
            viewHolder.category.setTypeface(ubantuB);
            viewHolder.timeAndDate.setTypeface(ubantuI);
            viewHolder.addedBy.setTypeface(karlaB);
            viewHolder.textId.setTypeface(ubantuB);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position < complainArrayList.size()) {
            viewHolder.msgText.setText(complainArrayList.get(position).getComplaintDetails());
            viewHolder.category.setText(complainArrayList.get(position).getDescription());
            viewHolder.timeAndDate.setText(complainArrayList.get(position).getDateCreated());
            String str = complainArrayList.get(position).getComplainBy().trim().split(" ")[0];
            viewHolder.addedBy.setText("by " + str + ", " + complainArrayList.get(position).getFlatNbr());
            viewHolder.textId.setText("ID : " + complainArrayList.get(position).getID());
            try {
                int id = Integer.parseInt(complainArrayList.get(position).getStatus());
                viewHolder.statusTag.setImageResource(drawablesStatus[id]);
            } catch (Exception e) {
                int id = 0;
                viewHolder.statusTag.setImageResource(drawablesStatus[id]);
            }

        }

        return convertView;
    }

    private class ViewHolder {
        TextView msgText, category, timeAndDate;
        ImageView nextButton, statusTag;
        TextView textId, addedBy;
    }
}
