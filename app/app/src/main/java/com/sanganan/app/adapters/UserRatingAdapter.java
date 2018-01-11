package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.common.Alphabets;
import com.sanganan.app.common.RandomColorAllocator;
import com.sanganan.app.model.UserComment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by root on 20/10/16.
 */

public class UserRatingAdapter extends BaseAdapter {

    Context context;
    ArrayList<UserComment> commentList;
    LayoutInflater inflater;
    Typeface karlaB, karlaR, ubantuB;
    RandomColorAllocator colorAllocator;


    public UserRatingAdapter(Context context, ArrayList<UserComment> commentList) {
        this.commentList = commentList;
        this.context = context;

    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();

                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.single_user_detail_comment, null);

                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.picture);
                viewHolder.colorback = (ImageView) convertView.findViewById(R.id.colorback);
                viewHolder.comment = (TextView) convertView.findViewById(R.id.comment);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.tvId = (TextView) convertView.findViewById(R.id.tvId);
                viewHolder.rated = (TextView) convertView.findViewById(R.id.rated);
                viewHolder.ratingtv = (TextView) convertView.findViewById(R.id.ratingtv);
                viewHolder.timeduration = (TextView) convertView.findViewById(R.id.timeduration);
                viewHolder.relativeLayoutTop = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutTop1);
                viewHolder.relativeLayoutBelow = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutBelow);
                viewHolder.textNameInitial = (TextView) convertView.findViewById(R.id.textNameInitial);

                karlaB = Typeface.createFromAsset(context.getAssets(), "Karla-Bold.ttf");
                karlaR = Typeface.createFromAsset(context.getAssets(), "Karla-Regular.ttf");
                ubantuB = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");

                viewHolder.comment.setTypeface(karlaR);
                viewHolder.rated.setTypeface(karlaB);
                viewHolder.ratingtv.setTypeface(ubantuB);
                viewHolder.timeduration.setTypeface(karlaB);
                viewHolder.tvId.setTypeface(karlaB);
                viewHolder.tvName.setTypeface(karlaB);
                viewHolder.textNameInitial.setTypeface(karlaR);

                colorAllocator = new RandomColorAllocator(getCount());

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.comment.setText(commentList.get(position).getCommentText());
            viewHolder.tvName.setText(commentList.get(position).getUserName());
            viewHolder.tvId.setText(commentList.get(position).getUsrFlatNumber());
            String rating = commentList.get(position).getRatingByUser();

            if (rating.length() >= 3) {
                rating = rating.substring(0, 3);
                viewHolder.ratingtv.setText(rating);
            } else if (!rating.isEmpty()) {
                viewHolder.ratingtv.setText(rating);
            } else {
                viewHolder.ratingtv.setText("NR");
            }
            Double d = 0.0;
            if (!rating.isEmpty()) {
                d = Double.parseDouble(rating);
            }
            if (d >= 3.0) {
                viewHolder.ratingtv.setBackgroundResource(R.drawable.green_rate_rect);
            }

            viewHolder.timeduration.setText(commentList.get(position).getAgo());

            String pickLink = commentList.get(position).getUserProfilePic();

            if (pickLink.endsWith(".png") || pickLink.endsWith(".jpg")) {

                viewHolder.relativeLayoutTop.setVisibility(View.VISIBLE);
                viewHolder.relativeLayoutBelow.setVisibility(View.INVISIBLE);
                Picasso.with(context).load(pickLink).placeholder(R.drawable.galleryplacholder).into(viewHolder.imageView);
            } else {
                viewHolder.relativeLayoutTop.setVisibility(View.INVISIBLE);
                viewHolder.relativeLayoutBelow.setVisibility(View.VISIBLE);

                String name = commentList.get(position).getUserName();
                int positionH = 0;
                if (name.isEmpty()) {
                    position = 0;
                } else {
                    String str = String.valueOf(name.charAt(0));


                    for (int j = 0; j < Alphabets.alphabets.size(); j++) {
                        if (str.equalsIgnoreCase(Alphabets.alphabets.get(j))) {
                            positionH = j;
                        }
                    }
                }
                viewHolder.colorback.setImageResource(Alphabets.alphabetsDrawable.get(positionH));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {

        ImageView imageView, colorback;
        TextView comment, tvName, tvId, rated, ratingtv, timeduration, textNameInitial;
        RelativeLayout relativeLayoutTop, relativeLayoutBelow;
    }
}
