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
 * Created by root on 5/10/16.
 */

public class AdminAdapter extends BaseAdapter {
    ArrayList<String> listAdminActions;
    Context context;
    ImageView imageIcon;
    TextView nameCat, pendingUsers;
    Typeface ubantuMedium;
    ArrayList arr;
    int userIPS;

    public AdminAdapter(Context context, ArrayList<String> listAdminActions, ArrayList arr, int userIPS) {
        this.listAdminActions = listAdminActions;
        this.context = context;
        this.arr = arr;
        this.userIPS = userIPS;
        ubantuMedium = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");

    }

    @Override
    public int getCount() {
        return listAdminActions.size();
    }

    @Override
    public Object getItem(int position) {
        return listAdminActions.get(position);
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
            pendingUsers = (TextView) convertView.findViewById(R.id.pendingUsers);
            nameCat.setTypeface(ubantuMedium);
            pendingUsers.setTypeface(ubantuMedium);

        }
        if (listAdminActions.get(position).equalsIgnoreCase("Approve Society Members")) {
            pendingUsers.setVisibility(View.VISIBLE);
            pendingUsers.setText(" (" + userIPS + " New)");
        } else {
            pendingUsers.setVisibility(View.GONE);
        }
        imageIcon.setImageResource((int)arr.get(position));
        nameCat.setText(listAdminActions.get(position));


        return convertView;
    }
}
