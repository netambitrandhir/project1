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
import com.sanganan.app.activities.StartSearchPage;
import com.sanganan.app.fragments.NearbyShops;
import com.sanganan.app.fragments.Notification;
import com.sanganan.app.model.Category;

import java.util.ArrayList;

/**
 * Created by root on 19/9/16.
 */

public class FiltersNotificationAdapter extends BaseAdapter {
    Context context;
    ArrayList<Category> arrayListCategory;
    LayoutInflater inflater;
    Typeface karlaB;


    public FiltersNotificationAdapter(Context context, ArrayList<Category> arrayListCategory) {
        this.context = context;
        this.arrayListCategory = arrayListCategory;
        karlaB = Typeface.createFromAsset(context.getAssets(), "Karla-Bold.ttf");

    }


    @Override
    public int getCount() {
        return arrayListCategory.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayListCategory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(StartSearchPage.LAYOUT_INFLATER_SERVICE);
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.filter_list_child, null);
            viewHolder.categoryName = (TextView) convertView.findViewById(R.id.categoryName);
            viewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.rl_container);
            viewHolder.imageCheckUncheck = (ImageView) convertView.findViewById(R.id.checkuncheck);
            viewHolder.categoryName.setTypeface(karlaB);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.categoryName.setText(arrayListCategory.get(position).getName());

        if (arrayListCategory.get(position).isChecked() == true) {
            viewHolder.imageCheckUncheck.setImageResource(R.drawable.check_mark);
        } else {

            viewHolder.imageCheckUncheck.setImageResource(R.drawable.white_button);
        }


        viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayListCategory.get(position).isChecked() == true) {
                    arrayListCategory.get(position).setChecked(false);
                    Notification.alistNotification.remove(arrayListCategory.get(position));
                } else {
                    arrayListCategory.get(position).setChecked(true);
                    Notification.alistNotification.add(arrayListCategory.get(position));
                }

                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView categoryName;
        ImageView imageCheckUncheck;
        RelativeLayout relativeLayout;
    }
}

