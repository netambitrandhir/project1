package com.sanganan.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sanganan.app.R;
import com.sanganan.app.fragments.EditHelperActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by root on 22/9/16.
 */

public class EditCommunityHelperAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    Context context;
    ArrayList<String> fileList;


    public EditCommunityHelperAdapter(Context context, ArrayList<String> fileList) {
        this.context = context;
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.complain_h_list_adapter, null);

            viewHolder.complainimage = (ImageView) convertView.findViewById(R.id.complainimage);
            viewHolder.cross = (ImageView) convertView.findViewById(R.id.cross);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Picasso.with(context).load(fileList.get(position)).placeholder(R.drawable.galleryplacholder).into(viewHolder.complainimage);

        viewHolder.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditHelperActivity.pathListOnAws.remove(position);
                notifyDataSetChanged();

            }
        });

        return convertView;
    }

    private class ViewHolder {

        ImageView complainimage;
        ImageView cross;

    }
}
