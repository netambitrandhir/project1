package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.model.ClassifiedModel;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

/**
 * Created by pranav on 30/11/16.
 */

public class ClassifiedListAdapter extends BaseAdapter {

    Context context;
    ArrayList<ClassifiedModel> classifiedArrayList;
    LayoutInflater inflater;
    Typeface karlaBold, umedium, umi;


    public ClassifiedListAdapter(Context context, ArrayList<ClassifiedModel> classifiedArrayList) {
        this.classifiedArrayList = classifiedArrayList;
        this.context = context;

    }

    @Override
    public int getCount() {
        return classifiedArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return classifiedArrayList.get(position);
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
            umedium = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");
            umi = Typeface.createFromAsset(context.getAssets(), "Ubuntu-MI.ttf");

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_classified_view, null);
            viewHolder.classifiedpic = (ImageView) convertView.findViewById(R.id.classifiedpic);
            viewHolder.classifiedtitle = (TextView) convertView.findViewById(R.id.classifiedtitle);
            viewHolder.classifiedtitle.setTypeface(karlaBold);
            viewHolder.classifiedpostedby = (TextView) convertView.findViewById(R.id.classifiedpostedby);
            viewHolder.classifiedpostedby.setTypeface(umedium);
            viewHolder.classifiedpostedon = (TextView) convertView.findViewById(R.id.classifiedpostedon);
            viewHolder.classifiedpostedon.setTypeface(umi);
            viewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout);


            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.classifiedtitle.setText(classifiedArrayList.get(position).getTitle());
        String postedByText = "by " + classifiedArrayList.get(position).getFirstName() + ", " + classifiedArrayList.get(position).getFlatNbr();
        viewHolder.classifiedpostedby.setText(postedByText);

        String postedOnText = classifiedArrayList.get(position).getAddedOn();
        viewHolder.classifiedpostedon.setText(postedOnText);

        if (!classifiedArrayList.get(position).getImage1().isEmpty()) {
            Picasso.with(context).load(classifiedArrayList.get(position).getImage1()).placeholder(R.drawable.classifiedplaceholder).resize(1000, 1000).into(viewHolder.classifiedpic);
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView classifiedpic;
        TextView classifiedtitle, classifiedpostedby, classifiedpostedon;
        RelativeLayout relativeLayout;
    }
}
