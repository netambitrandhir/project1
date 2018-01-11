package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.model.CalloutData;
import com.sanganan.app.model.TimeSheet;

import java.util.ArrayList;

/**
 * Created by root on 21/3/17.
 */

public class AttendenceListAdapter extends BaseAdapter {

    Context context;
    ArrayList<TimeSheet> timeSheets;
    LayoutInflater inflater;
    Typeface UbuntuR;
    String text = "";

    public AttendenceListAdapter(Context context, ArrayList<TimeSheet> timeSheets) {
        this.context = context;
        this.timeSheets = timeSheets;
    }

    @Override
    public int getCount() {
        return timeSheets.size();
    }

    @Override
    public TimeSheet getItem(int position) {
        return timeSheets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            UbuntuR = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_attendence_time_view, null);

            viewHolder.time_text = (TextView) convertView.findViewById(R.id.time_text);
            viewHolder.time_text.setTypeface(UbuntuR);


            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        text = "";
        if(getItem(position).getIs_in().equals("1")){
            text = "Check-in at "+getItem(position).getTime();
            viewHolder.time_text.setTextColor(context.getResources().getColor(R.color.light_green));
            viewHolder.time_text.setText(text);
        }
        else{
            text = "Check-out at "+getItem(position).getTime();
            viewHolder.time_text.setTextColor(context.getResources().getColor(R.color.text_red));
            viewHolder.time_text.setText(text);
        }



        return convertView;
    }

    private class ViewHolder {
        TextView time_text;
    }
}
