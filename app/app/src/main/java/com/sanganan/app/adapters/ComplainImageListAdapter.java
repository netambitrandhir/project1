package com.sanganan.app.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.sanganan.app.R;
import com.sanganan.app.fragments.AddClassified;
import com.sanganan.app.fragments.AddComplaintActivity;
import com.sanganan.app.fragments.ImageSlideShow;
import com.sanganan.app.fragments.SendNotificationByAdmin;
import com.sanganan.app.fragments.UpdateStatusActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by root on 22/9/16.
 */

public class ComplainImageListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    Context context;
    ArrayList<File> fileList;
    String str;


    public ComplainImageListAdapter(Context context, ArrayList<File> fileList, String str) {
        this.context = context;
        this.fileList = fileList;
        this.str = str;
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
/*
                fileList.remove(fileList.get(position));
*/
                String callerClassName = new Exception().getStackTrace()[1].getClassName();

                if (str.equalsIgnoreCase("Add")) {
                    AddComplaintActivity.pathListOnAws.remove(position);
                    AddComplaintActivity.imageFileList.remove(position);
                } else if (str.equalsIgnoreCase("addClass")) {
                    AddClassified.pathListOnAws.remove(position);
                    AddClassified.imageFileList.remove(position);
                } else if (str.equalsIgnoreCase("addUSA")) {
                    UpdateStatusActivity.pathListOnAws.remove(position);
                    UpdateStatusActivity.imageFileList.remove(position);
                } else {
                    SendNotificationByAdmin.pathListOnAws.remove(position);
                    SendNotificationByAdmin.imageFileList.remove(position);
                }
                notifyDataSetChanged();

            }
        });

     /*   viewHolder.complainimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle1 = new Bundle();
                FragmentActivity activity = (FragmentActivity) (context);
                FragmentManager fm = activity.getSupportFragmentManager();
                ImageSlideShow alertDialog = new ImageSlideShow();
                bundle1.putStringArrayList("imagelist", AddComplaintActivity.pathListOnAws);
                alertDialog.setArguments(bundle1);
                alertDialog.show(fm, "tag_delivery");
            }
        });*/

        return convertView;
    }

    private class ViewHolder {

        ImageView complainimage;
        ImageView cross;

    }
}
