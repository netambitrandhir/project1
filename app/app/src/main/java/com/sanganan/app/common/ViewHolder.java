package com.sanganan.app.common;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.sanganan.app.adapters.PostAdapter;

/**
 * Created by Randhir Patel on 12/7/17.
 */
public class ViewHolder {


    public class RecycleTabHolder extends ViewHolder {
        public RecyclerView hLVHide;
    }


    public class ImageViewHolder extends ViewHolder {

        public TextView postContent, flat_name, likecomment, multiplePhotoIndicator, dateAndtime;
        public ImageView profile_pic, comment, like_dislike, feedType;
        public ImageView images;

    }

    public class WithoutImageViewHolder extends ViewHolder {
        public TextView postContent, flat_name, likecomment, multiplePhotoIndicator, dateAndtime;
        public ImageView profile_pic, comment, like_dislike, feedType;
    }


    public class BannerViewHolder extends ViewHolder {
        public SliderLayout mDemoSlider;
        public TextView society_feed_heading;
        public ImageView createPost;

    }
}