package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.model.CalloutData;
import com.sanganan.app.model.NearByShopSearch;

import java.util.ArrayList;

/**
 * Created by raj on 1/5/2017.
 */

public class CalliDialogAdapter extends BaseAdapter {

    ArrayList<String> numberArrayList;
    LayoutInflater inflater;
    Context context;
    Typeface karlaBold;

    public CalliDialogAdapter(Context context, ArrayList<String> numberArrayList) {
        this.context = context;
        this.numberArrayList = numberArrayList;
    }
    @Override
    public int getCount() {
        return numberArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return numberArrayList.get(position);
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

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.calli_dialogadapter, null);

            viewHolder.numberTxtView = (TextView) convertView.findViewById(R.id.numberTxtView);
            viewHolder.numberTxtView.setTypeface(karlaBold);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.numberTxtView.setText("+91-"+numberArrayList.get(position));

        return convertView;
    }

    public class ViewHolder {
        LinearLayout rl;
        TextView numberTxtView;
    }
}
