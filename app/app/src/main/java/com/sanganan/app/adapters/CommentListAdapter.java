package com.sanganan.app.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanganan.app.R;
import com.sanganan.app.common.Alphabets;
import com.sanganan.app.fragments.DetailsNeighbourFragment;
import com.sanganan.app.model.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Randhir Patel on 30/5/17.
 */

public class CommentListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Comment> comments;
    LayoutInflater inflater;
    Typeface karlaBold, karlaRegular, worksansR;

    public CommentListAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Comment getItem(int position) {
        return comments.get(position);
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
            karlaBold = Typeface.createFromAsset(context.getAssets(), "Karla-Bold.ttf");
            karlaRegular = Typeface.createFromAsset(context.getAssets(), "Karla-Regular.ttf");
            worksansR = Typeface.createFromAsset(context.getAssets(), "WorkSans-Regular.ttf");

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.single_comment_view, null);

            viewHolder.flatandname = (TextView) convertView.findViewById(R.id.flatandname);
            viewHolder.flatandname.setTypeface(karlaBold);
            viewHolder.time_added_on = (TextView) convertView.findViewById(R.id.time_added_on);
            viewHolder.time_added_on.setTypeface(worksansR);
            viewHolder.textComment = (TextView) convertView.findViewById(R.id.textComment);
            viewHolder.textComment.setTypeface(karlaRegular);
            viewHolder.imagepic = (CircleImageView) convertView.findViewById(R.id.imagepic);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Comment comment = getItem(position);
        viewHolder.flatandname.setText(comment.getCommentBy() + " ," + comment.getFlatNbr());
        viewHolder.time_added_on.setText(comment.getCreatDate());
        viewHolder.textComment.setText(comment.getPostFeedCommnet());

        try {
            if (comment.getCommentProfilePic().endsWith(".png") || comment.getCommentProfilePic().endsWith(".jpg")) {
                Picasso.with(context).load(comment.getCommentProfilePic()).placeholder(R.drawable.person_placeholder).error(R.drawable.person_placeholder).into(viewHolder.imagepic);
            } else {
                String username = comment.getCommentBy();
                String str = String.valueOf(username.charAt(0));
                str.toLowerCase();
                int positionL = 0;
                for (int i = 0; i < Alphabets.alphabets.size(); i++) {
                    if (str.equalsIgnoreCase(Alphabets.alphabets.get(i))) {
                        positionL = i;
                    }
                }
                viewHolder.imagepic.setImageResource(Alphabets.alphabetsDrawable.get(positionL));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        CircleImageView imagepic;
        TextView flatandname, time_added_on, textComment;
    }
}
