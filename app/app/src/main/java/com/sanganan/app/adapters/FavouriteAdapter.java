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

import java.util.ArrayList;

/**
 * Created by root on 27/9/16.
 */
public class FavouriteAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> arrayList;
    int arr[];
    ImageView imageIcon;
    TextView nameCat;
    Typeface monster;

    public FavouriteAdapter(Context context, ArrayList<String> arrayList, int arr[]) {
        this.context = context;
        this.arrayList = arrayList;
        this.arr = arr;
        monster = Typeface.createFromAsset(context.getAssets(), "Montserrat-Bold.otf");
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_view_fav_basecategory, null);

            imageIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
            nameCat = (TextView) convertView.findViewById(R.id.textNameCategory);
            nameCat.setTypeface(monster);

        }

        imageIcon.setImageResource(arr[position]);
        nameCat.setText(arrayList.get(position));

        return convertView;
    }
}
