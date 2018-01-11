package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.model.HelperModel;
import com.sanganan.app.model.WheelerModel;

import java.util.ArrayList;

/**
 * Created by root on 1/10/16.
 */
public class WheelerAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    Context context;
    Typeface karlaB, wsM, wsL;
    ArrayList<WheelerModel> wheelerDetailList;

    public WheelerAdapter(Context context, ArrayList<WheelerModel> list) {
        this.context = context;
        this.wheelerDetailList = list;
    }

    @Override
    public int getCount() {
        return wheelerDetailList.size();
    }

    @Override
    public Object getItem(int i) {
        return wheelerDetailList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.wheeler_adapterlayout, null);
            viewHolder.wheelerNumber = (TextView) view.findViewById(R.id.vehicleNo);
            viewHolder.wheelerType = (TextView) view.findViewById(R.id.vehicleType);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.wheelerNumber.setText("Vehicle No: " + wheelerDetailList.get(i).getVehicleNumber());
        viewHolder.wheelerType.setText(wheelerDetailList.get(i).getVehicheType() + " Wheeler");

        return view;


    }

    private class ViewHolder {
        TextView wheelerNumber;
        TextView wheelerType;
    }
}
