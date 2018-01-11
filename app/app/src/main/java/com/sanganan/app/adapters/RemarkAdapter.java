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
import com.sanganan.app.model.ComplainRemark;
import com.sanganan.app.model.Remark;

import java.util.ArrayList;

/**
 * Created by pranav on 15/12/16.
 */

public class RemarkAdapter extends BaseAdapter {

    Context context;
    ArrayList<Remark> remarkList;
    LayoutInflater inflater;
    Typeface karlaBold, karlaRegular, uItalic;

    public RemarkAdapter(Context context, ArrayList<Remark> remarkList) {
        this.context = context;
        this.remarkList = remarkList;
    }

    @Override
    public int getCount() {
        return remarkList.size();
    }

    @Override
    public Object getItem(int position) {
        return remarkList.get(position);
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
            karlaRegular = Typeface.createFromAsset(context.getAssets(), "Karla-Regular.ttf");
            uItalic = Typeface.createFromAsset(context.getAssets(), "Ubuntu-RI.ttf");

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_remark_view, null);

            viewHolder.time = (TextView) convertView.findViewById(R.id.time_field);
            viewHolder.time.setTypeface(karlaBold);
            viewHolder.nameFlatField = (TextView) convertView.findViewById(R.id.nameFlatField);
            viewHolder.nameFlatField.setTypeface(karlaBold);
            viewHolder.remarkText = (TextView) convertView.findViewById(R.id.remarkText);
            viewHolder.remarkText.setTypeface(karlaRegular);


            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String name = remarkList.get(position).getName();
        name = name.trim();
        name = name.split(" ")[0];

        viewHolder.time.setText(remarkList.get(position).getRemarkDate());
        viewHolder.nameFlatField.setText(name + ", " + remarkList.get(position).getFlat());
        viewHolder.remarkText.setText(remarkList.get(position).getRemark());

        return convertView;
    }

    private class ViewHolder {
        TextView time, remarkText, nameFlatField;
    }
}
