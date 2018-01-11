package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.sanganan.app.R;
import com.sanganan.app.model.Category;

import java.util.ArrayList;


/**
 * Created by pranav on 22/6/16.
 */
public class ShopCategoryHLAdapter extends BaseAdapter {
    Context ctx;
    private LayoutInflater inflater;
    ArrayList<Category> aList;
    Typeface karla_regular;

    public ShopCategoryHLAdapter(Context context, ArrayList<Category> aList) {
        this.ctx = context;
        this.aList = aList;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        karla_regular = Typeface.createFromAsset(context.getAssets(), "Karla-Regular.ttf");


    }

    @Override
    public int getCount() {
        return aList.size();
    }

    @Override
    public Object getItem(int position) {
        return aList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder mHolder;

        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.shopcategorysingleviewlayout, null);
            mHolder.category = (TextView) convertView.findViewById(R.id.textCategory);
            mHolder.category.setTypeface(karla_regular);

            convertView.setTag(mHolder);



        }
        else{
            mHolder = (ViewHolder) convertView.getTag();

        }

        mHolder.category.setText(aList.get(position).getName());


        return convertView;
    }

    private class ViewHolder {
        TextView category;

    }
}
