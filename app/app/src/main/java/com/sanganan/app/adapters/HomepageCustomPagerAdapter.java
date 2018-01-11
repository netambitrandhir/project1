package com.sanganan.app.adapters;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sanganan.app.R;

import java.util.ArrayList;

/**
 * Created by root on 13/10/16.
 */
public class HomepageCustomPagerAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    int [] list;
    View itemView;
    ImageView imageView;

    public HomepageCustomPagerAdapter(Context context, int [] list) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list=list;
    }


    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
       return view== ((LinearLayout) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        itemView=layoutInflater.inflate(R.layout.custom_pager_adapter_layout,container,false);
        imageView= (ImageView) itemView.findViewById(R.id.imageView);
        imageView.setImageResource(list[position]);
        container.addView(itemView);

        return itemView;
    }
}
