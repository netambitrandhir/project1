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
import com.sanganan.app.model.ClassifiedModel;

import java.util.ArrayList;

/**
 * Created by pranav on 30/11/16.
 */

public class CalloutListAdapter extends BaseAdapter {

    Context context;
    ArrayList<CalloutData> calloutArrayList;
    LayoutInflater inflater;
    Typeface karlaBold, uRegular, uItalic;

    public CalloutListAdapter(Context context, ArrayList<CalloutData> calloutArrayList) {
        this.context = context;
        this.calloutArrayList = calloutArrayList;
    }

    @Override
    public int getCount() {
        return calloutArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return calloutArrayList.get(position);
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
            karlaBold = Typeface.createFromAsset(context.getAssets(), "Karla-Bold.ttf");
            uRegular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
            uItalic = Typeface.createFromAsset(context.getAssets(), "Ubuntu-RI.ttf");

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_callout_view, null);

            viewHolder.callOutField = (TextView) convertView.findViewById(R.id.callOutField);
            viewHolder.callOutField.setTypeface(karlaBold);
            viewHolder.callOutBy = (TextView) convertView.findViewById(R.id.callOutBy);
            viewHolder.callOutBy.setTypeface(uRegular);
            viewHolder.callOutDate = (TextView) convertView.findViewById(R.id.callOutDate);
            viewHolder.callOutDate.setTypeface(uItalic);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.callOutField.setText(calloutArrayList.get(position).getDescription());
        viewHolder.callOutDate.setText(calloutArrayList.get(position).getDateSent());
        String byNameFlat = "by " + calloutArrayList.get(position).getFirstName() + ", " + calloutArrayList.get(position).getFlatNbr();
        viewHolder.callOutBy.setText(byNameFlat);

        return convertView;
    }

    private class ViewHolder {
        TextView callOutField, callOutBy, callOutDate;
    }
}
