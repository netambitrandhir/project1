package com.sanganan.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.activities.MainHomePageActivity;
import com.sanganan.app.common.Alphabets;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;

import com.sanganan.app.common.ViewHolder;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.fragments.AdvertiseActivity;
import com.sanganan.app.fragments.AlertDialogLogin;
import com.sanganan.app.fragments.DetailsNeighbourFragment;
import com.sanganan.app.fragments.HomePageOfSociety;
import com.sanganan.app.fragments.PostDetailFragment;

import com.sanganan.app.fragments.UpdateStatusActivity;
import com.sanganan.app.model.BannerData;
import com.sanganan.app.model.PostModel;
import com.sanganan.app.model.TabData;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Handler;

/**
 * Created by Randhir Patel on 24/5/17.
 */

public class PostAdapter extends BaseAdapter implements BaseSliderView.OnSliderClickListener {

    private ArrayList<PostModel> postDatas;
    private Context context;
    private Common common;
    private RequestQueue requestQueue;
    private Typeface worksansM, ubuntuB, ubuntuR;
    private LinearLayoutManager horizontalLayoutManagaerHide;
    private ArrayList<BannerData> listBanner;
    private ArrayList<TabData> tabDataListHide;
    private MyRecycleAdapter tabAdapterHide;
    int topMarginByH, sideMarginByH;
    FirebaseAnalytics mFirebaseAnalytics;
    private HomePageOfSociety fragment;
    LayoutInflater inflater;
    private final int RECYCLE_VIEW = 0;
    private final int BANNER_VIEW = 1;
    private final int POST_VIEW = 2;
    private final int POST_VIEW_NO_IMAGE = 3;
    HashMap<String, BannerData> url_maps = new HashMap<String, BannerData>();

    public PostAdapter(Context context, ArrayList<PostModel> postDatas, Common common, ArrayList<BannerData> listBanner, ArrayList<TabData> tabDataListHide, HomePageOfSociety fragment) {
        this.context = context;
        this.postDatas = postDatas;
        this.common = common;
        this.listBanner = listBanner;
        this.tabDataListHide = tabDataListHide;
        this.fragment = fragment;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        requestQueue = VolleySingleton.getInstance(context).getRequestQueue();
        worksansM = Typeface.createFromAsset(context.getAssets(), "WorkSans-Medium.ttf");
        ubuntuB = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");
        ubuntuR = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
    }

    public void updateReceiptsList(ArrayList<PostModel> newlist) {
        this.postDatas = newlist;
        notifyDataSetChanged();
    }

    public void updateListForRecycleView(ArrayList<PostModel> newlist) {
        this.postDatas = newlist;
        notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        return null == postDatas ? 0 : postDatas.size();

    }

    @Override
    public PostModel getItem(int position) {
        return postDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return RECYCLE_VIEW;
        } else if (position == 1) {
            return BANNER_VIEW;
        } else if (getItem(position).getPhotoPathList().size() > 0) {
            return POST_VIEW;
        } else {
            return POST_VIEW_NO_IMAGE;
        }

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder.RecycleTabHolder recycleViewHolder = null;
        ViewHolder.ImageViewHolder imageViewHolder = null;
        ViewHolder.WithoutImageViewHolder withoutImageViewHolder = null;
        ViewHolder.BannerViewHolder bannerViewHolder = null;


        int type = getItemViewType(position);


        if (type == RECYCLE_VIEW) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.scrollcontainer_view_single_1, parent, false);
                recycleViewHolder = new ViewHolder().new RecycleTabHolder();
                recycleViewHolder.hLVHide = (RecyclerView) convertView.findViewById(R.id.hideList);

                horizontalLayoutManagaerHide = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                recycleViewHolder.hLVHide.setLayoutManager(horizontalLayoutManagaerHide);
                setHlvHideMargin(recycleViewHolder);

                tabAdapterHide = new MyRecycleAdapter(context, tabDataListHide, topMarginByH, sideMarginByH, fragment, "login");
                recycleViewHolder.hLVHide.setAdapter(tabAdapterHide);

                convertView.setTag(recycleViewHolder);
            } else {
                recycleViewHolder = (ViewHolder.RecycleTabHolder) convertView.getTag();
            }

            tabAdapterHide.notifyDataSetChanged();

            if (tabDataListHide.size() > 0) {
                recycleViewHolder.hLVHide.setVisibility(View.VISIBLE);
            } else {
                recycleViewHolder.hLVHide.setVisibility(View.GONE);
            }


        } else if (type == BANNER_VIEW) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.scrollcontainer_view_single_2, parent, false);
                bannerViewHolder = new ViewHolder().new BannerViewHolder();
                bannerViewHolder.createPost = (ImageView) convertView.findViewById(R.id.createPost);
                bannerViewHolder.mDemoSlider = (SliderLayout) convertView.findViewById(R.id.slider);
                bannerViewHolder.society_feed_heading = (TextView) convertView.findViewById(R.id.society_feed_heading);
                bannerViewHolder.society_feed_heading.setTypeface(ubuntuR);

                setBannerAfterListLoadedFromApi(bannerViewHolder);
                setBannerAccurately(bannerViewHolder);

                convertView.setTag(bannerViewHolder);
            } else {
                bannerViewHolder = (ViewHolder.BannerViewHolder) convertView.getTag();
            }

            if (postDatas.get(position).getIsbanner()) {
                bannerViewHolder.mDemoSlider.setVisibility(View.VISIBLE);
                if (url_maps.size() == 0) {
                    setBannerAfterListLoadedFromApi(bannerViewHolder);
                }
            } else {
                bannerViewHolder.mDemoSlider.setVisibility(View.GONE);
            }


            bannerViewHolder.createPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (common.getBooleanValue(Constants.isLoggedIn) && common.getStringValue("ID").equalsIgnoreCase(common.getStringValue(Constants.userRwa)) && common.getStringValue(Constants.approvalStatus).equalsIgnoreCase("Y")) {

                        Intent intent = new Intent(context, UpdateStatusActivity.class);
                        ((ActionBarActivity) context).startActivityForResult(intent, 18909);
                    } else {
                        AlertDialogLogin dialog = new AlertDialogLogin();
                        dialog.setRetainInstance(true);
                        dialog.show(((ActionBarActivity) context).getSupportFragmentManager(), "tag_alert_login");
                    }
                }
            });

        } else if (type == POST_VIEW) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.scrollcontainer_view_single_3, parent, false);
                imageViewHolder = new ViewHolder().new ImageViewHolder();
                imageViewHolder.postContent = (TextView) convertView.findViewById(R.id.postContent);
                imageViewHolder.postContent.setTypeface(worksansM);
                imageViewHolder.flat_name = (TextView) convertView.findViewById(R.id.flat_name);
                imageViewHolder.flat_name.setTypeface(worksansM);
                imageViewHolder.likecomment = (TextView) convertView.findViewById(R.id.likecomment);
                imageViewHolder.likecomment.setTypeface(worksansM);
                imageViewHolder.multiplePhotoIndicator = (TextView) convertView.findViewById(R.id.multiplePhotoIndicator);
                imageViewHolder.dateAndtime = (TextView) convertView.findViewById(R.id.dateAndtime);
                imageViewHolder.dateAndtime.setTypeface(worksansM);
                imageViewHolder.profile_pic = (ImageView) convertView.findViewById(R.id.profile_pic);
                imageViewHolder.images = (ImageView) convertView.findViewById(R.id.images);
                imageViewHolder.comment = (ImageView) convertView.findViewById(R.id.comment);
                imageViewHolder.like_dislike = (ImageView) convertView.findViewById(R.id.like_dislike);
                imageViewHolder.feedType = (ImageView) convertView.findViewById(R.id.feedType);
                convertView.setTag(imageViewHolder);
            } else {
                imageViewHolder = (ViewHolder.ImageViewHolder) convertView.getTag();
            }


            ////////////////////////////
            final PostModel model = getItem(position);

            if (model.getDescription().isEmpty()) {
                imageViewHolder.postContent.setText("Gallery Pic");
            } else {
                imageViewHolder.postContent.setText(model.getDescription());
            }

            imageViewHolder.flat_name.setText(model.getResidentName() + ", " + model.getResidentFlatNo());
            int countL = Integer.parseInt(model.getLikesCount());
            int countC = Integer.parseInt(model.getCommentCount());
            String sLike = "";
            String sComment = "";

            if (countL > 1) {
                sLike = "likes";
            } else {
                sLike = "like";
            }

            if (countC > 1) {
                sComment = "comments";
            } else {
                sComment = "comment";
            }


            imageViewHolder.likecomment.setText(model.getLikesCount() + " " + sLike + " " + model.getCommentCount() + " " + sComment + " ");

            imageViewHolder.dateAndtime.setText(model.getDateCreated());
            String picLink = model.getProfilePic();

            if (picLink != null) {
                if (picLink.endsWith(".png") || picLink.endsWith(".jpg")) {
                    Picasso.with(context).load(model.getProfilePic()).placeholder(R.drawable.person_placeholder).into(imageViewHolder.profile_pic);
                } else {
                    String str = String.valueOf(model.getResidentName().charAt(0));
                    str.toLowerCase();
                    int positionL = 0;
                    for (int i = 0; i < Alphabets.alphabets.size(); i++) {
                        if (str.equalsIgnoreCase(Alphabets.alphabets.get(i))) {
                            positionL = i;
                        }
                    }
                    imageViewHolder.profile_pic.setImageResource(Alphabets.alphabetsDrawable.get(positionL));
                }
            }

            int drawable = getFeedTypeImage(model.getFeedType());

            imageViewHolder.feedType.setImageResource(drawable);


            if (model.getPhotoPathList().size() > 1) {
                imageViewHolder.multiplePhotoIndicator.setVisibility(View.VISIBLE);
                imageViewHolder.multiplePhotoIndicator.setText(String.valueOf(model.getPhotoPathList().size()));
            } else {
                imageViewHolder.multiplePhotoIndicator.setVisibility(View.GONE);
            }

            final Uri uri = Uri.parse(model.getPhotoPathList().get(0));
            // Picasso.with(context).load(uri).into(imageViewHolder.images);
            /*final ImageView tempIV = imageViewHolder.images;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(context).load(uri).into(tempIV);
                }
            }, 1000);*/

            Glide.with(context)
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewHolder.images);

            if (model.getIsLike().equals("1")) {
                imageViewHolder.like_dislike.setImageResource(R.drawable.like_inlisitng);
            } else {
                imageViewHolder.like_dislike.setImageResource(R.drawable.notlikedbtn);
            }

            imageViewHolder.like_dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle logBundleLike= new Bundle();
                    logBundleLike.putString("society_id", common.getStringValue(Constants.userRwa));
                    logBundleLike.putString("user_id", common.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("Post_liked", logBundleLike);

                    if (postDatas.get(position).getIsLike().equals("1")) {
                        likeApiCall("0", postDatas.get(position).getID(), position);
                    } else {
                        likeApiCall("1", postDatas.get(position).getID(), position);
                    }
                }
            });


            imageViewHolder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new PostDetailFragment();
                    Bundle bundlePostDetail = new Bundle();
                    bundlePostDetail.putString("profile_pic", model.getProfilePic());
                    bundlePostDetail.putString("flatnumber", model.getResidentFlatNo());
                    bundlePostDetail.putString("username", model.getResidentName());
                    bundlePostDetail.putString("description", model.getDescription());
                    bundlePostDetail.putString("feedType", model.getFeedType());
                    bundlePostDetail.putString("postRefId", model.getPostRefID());
                    bundlePostDetail.putString("postId", model.getID());
                    bundlePostDetail.putString("commentCount", model.getCommentCount());
                    bundlePostDetail.putString("likesCount", model.getLikesCount());
                    bundlePostDetail.putString("isLIke", model.getIsLike());
                    bundlePostDetail.putString("pictures", model.getPhotoPath());
                    bundlePostDetail.putStringArrayList("picList", model.getPhotoPathList());
                    bundlePostDetail.putString("UserId", model.getUserId());
                    bundlePostDetail.putString("UserId", model.getUserId());
                    bundlePostDetail.putString("isClickedOn", "comment");
                    fragment.setArguments(bundlePostDetail);
                    ((MainHomePageActivity) context).onButtonClick(fragment, false);
                }
            });

            //////////////////////////////////

        } else if (type == POST_VIEW_NO_IMAGE) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.scrollcontainer_view_single_4, parent, false);
                withoutImageViewHolder = new ViewHolder().new WithoutImageViewHolder();

                withoutImageViewHolder.postContent = (TextView) convertView.findViewById(R.id.postContent);
                withoutImageViewHolder.postContent.setTypeface(worksansM);
                withoutImageViewHolder.flat_name = (TextView) convertView.findViewById(R.id.flat_name);
                withoutImageViewHolder.flat_name.setTypeface(worksansM);
                withoutImageViewHolder.likecomment = (TextView) convertView.findViewById(R.id.likecomment);
                withoutImageViewHolder.likecomment.setTypeface(worksansM);
                withoutImageViewHolder.multiplePhotoIndicator = (TextView) convertView.findViewById(R.id.multiplePhotoIndicator);
                withoutImageViewHolder.dateAndtime = (TextView) convertView.findViewById(R.id.dateAndtime);
                withoutImageViewHolder.dateAndtime.setTypeface(worksansM);
                withoutImageViewHolder.profile_pic = (ImageView) convertView.findViewById(R.id.profile_pic);
                withoutImageViewHolder.comment = (ImageView) convertView.findViewById(R.id.comment);
                withoutImageViewHolder.like_dislike = (ImageView) convertView.findViewById(R.id.like_dislike);
                withoutImageViewHolder.feedType = (ImageView) convertView.findViewById(R.id.feedType);

                convertView.setTag(withoutImageViewHolder);
            } else {
                withoutImageViewHolder = (ViewHolder.WithoutImageViewHolder) convertView.getTag();
            }


            final PostModel model = getItem(position);

            withoutImageViewHolder.postContent.setText(model.getDescription());
            withoutImageViewHolder.flat_name.setText(model.getResidentName() + ", " + model.getResidentFlatNo());
            int countL = Integer.parseInt(model.getLikesCount());
            int countC = Integer.parseInt(model.getCommentCount());
            String sLike = "";
            String sComment = "";

            if (countL > 1) {
                sLike = "likes";
            } else {
                sLike = "like";
            }

            if (countC > 1) {
                sComment = "comments";
            } else {
                sComment = "comment";
            }


            withoutImageViewHolder.likecomment.setText(model.getLikesCount() + " " + sLike + " " + model.getCommentCount() + " " + sComment + " ");

            withoutImageViewHolder.dateAndtime.setText(model.getDateCreated());
            String picLink = model.getProfilePic();

            if (picLink != null) {
                if (picLink.endsWith(".png") || picLink.endsWith(".jpg")) {
                    Picasso.with(context).load(model.getProfilePic()).placeholder(R.drawable.person_placeholder).into(withoutImageViewHolder.profile_pic);
                } else {
                    String str = String.valueOf(model.getResidentName().charAt(0));
                    str.toLowerCase();
                    int positionL = 0;
                    for (int i = 0; i < Alphabets.alphabets.size(); i++) {
                        if (str.equalsIgnoreCase(Alphabets.alphabets.get(i))) {
                            positionL = i;
                        }
                    }
                    withoutImageViewHolder.profile_pic.setImageResource(Alphabets.alphabetsDrawable.get(positionL));
                }
            }

            int drawable = getFeedTypeImage(model.getFeedType());

            withoutImageViewHolder.feedType.setImageResource(drawable);

            if (model.getIsLike().equals("1")) {
                withoutImageViewHolder.like_dislike.setImageResource(R.drawable.like_inlisitng);
            } else {
                withoutImageViewHolder.like_dislike.setImageResource(R.drawable.notlikedbtn);
            }

            withoutImageViewHolder.like_dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle logBundleLike= new Bundle();
                    logBundleLike.putString("society_id", common.getStringValue(Constants.userRwa));
                    logBundleLike.putString("user_id", common.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("Post_liked", logBundleLike);

                    if (postDatas.get(position).getIsLike().equals("1")) {
                        likeApiCall("0", postDatas.get(position).getID(), position);
                    } else {
                        likeApiCall("1", postDatas.get(position).getID(), position);
                    }
                }
            });


            withoutImageViewHolder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new PostDetailFragment();
                    Bundle bundlePostDetail = new Bundle();
                    bundlePostDetail.putString("profile_pic", model.getProfilePic());
                    bundlePostDetail.putString("flatnumber", model.getResidentFlatNo());
                    bundlePostDetail.putString("username", model.getResidentName());
                    bundlePostDetail.putString("description", model.getDescription());
                    bundlePostDetail.putString("feedType", model.getFeedType());
                    bundlePostDetail.putString("postRefId", model.getPostRefID());
                    bundlePostDetail.putString("postId", model.getID());
                    bundlePostDetail.putString("commentCount", model.getCommentCount());
                    bundlePostDetail.putString("likesCount", model.getLikesCount());
                    bundlePostDetail.putString("isLIke", model.getIsLike());
                    bundlePostDetail.putString("pictures", model.getPhotoPath());
                    bundlePostDetail.putStringArrayList("picList", model.getPhotoPathList());
                    bundlePostDetail.putString("UserId", model.getUserId());
                    bundlePostDetail.putString("UserId", model.getUserId());
                    bundlePostDetail.putString("isClickedOn", "comment");
                    fragment.setArguments(bundlePostDetail);
                    ((MainHomePageActivity) context).onButtonClick(fragment, false);
                }
            });
        }
        return convertView;
    }

    private void setHlvHideMargin(ViewHolder.RecycleTabHolder viewHolder) {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((ActionBarActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;

        topMarginByH = screenWidth * 10 / 736;
        sideMarginByH = screenHeight * 14 / 736;

        ViewGroup.MarginLayoutParams layoutParamsMargin = (ViewGroup.MarginLayoutParams) viewHolder.hLVHide.getLayoutParams();
        layoutParamsMargin.setMargins(sideMarginByH, 0, sideMarginByH, topMarginByH);
    }


    private void likeApiCall(final String likeStatus, final String postId, final int position) {
        String uri = Constants.MongoBaseUrl + "postlike.php";
        try {


            StringRequest req = new StringRequest(Request.Method.POST, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject responseObj = new JSONObject(response);
                                String likeStatusAfterResponse = "";
                                dataSetUp(responseObj, likeStatus, position);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d("error", error.getMessage());
                            common.showShortToast(error.getMessage());
                        }

                    }) {


                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("IsLike", likeStatus);
                    params.put("ResidentRWAID", common.getStringValue(Constants.ResidentRWAID));
                    params.put("SocietyFeedID", postId);

                    return params;
                }
            };
            try {
                req.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void dataSetUp(JSONObject response, String likeStatus, int position) {
        String status = "";
        String message = "";
        status = response.optString("Status");
        message = response.optString("Message");
        postDatas.get(position).setIsLike(likeStatus);
        String likeCount = postDatas.get(position).getLikesCount();
        int likes = Integer.parseInt(likeCount);
        if (likeStatus.equals("1")) {
            likes = likes + 1;
        } else {
            likes = likes - 1;
        }
        postDatas.get(position).setLikesCount(String.valueOf(likes));
        notifyDataSetChanged();
    }


    private int getFeedTypeImage(String feedType) {
        int drawable = 0;
        switch (feedType) {
            case "notification":
                drawable = R.drawable.notification_small;
                break;
            case "callout":
                drawable = R.drawable.callout_small;
                break;
            case "poll":
                drawable = R.drawable.polling_small;
                break;
            case "classified":
                drawable = R.drawable.classifieds_small;
                break;
            case "photos":
                drawable = R.drawable.gallery_small;
                break;
            case "createPost":
                drawable = R.drawable.write_post;
                break;
            case "complain":
                drawable = R.drawable.complaints_small;
                break;
            default:
                drawable = R.drawable.write_post;
                break;


        }

        return drawable;

    }


    private void setBannerAfterListLoadedFromApi(ViewHolder.BannerViewHolder viewHolder) {


        url_maps.clear();
        for (int k = 0; k < listBanner.size(); k++) {
            url_maps.put("banner" + k, listBanner.get(k));
        }

        viewHolder.mDemoSlider.removeAllSliders();

        for (String name : url_maps.keySet()) {
            DefaultSliderView textSliderView = new DefaultSliderView(context);
            // initialize a SliderLayout
            textSliderView.description(" ").image(url_maps.get(name)
                    .getBannerImageUrl())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra", name);
            textSliderView.getBundle().putString("urlELink", url_maps.get(name).getExternalLink());
            textSliderView.getBundle().putString("url", url_maps.get(name).getBannerImageUrl());

            viewHolder.mDemoSlider.addSlider(textSliderView);
        }

        if (url_maps.size() > 1) {
            viewHolder.mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
            viewHolder.mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            viewHolder.mDemoSlider.setDuration(5000);
            viewHolder.mDemoSlider.addOnPageChangeListener((ViewPagerEx.OnPageChangeListener) context);

        } else {
            viewHolder.mDemoSlider.stopAutoCycle();
            viewHolder.mDemoSlider.setPagerTransformer(false, new BaseTransformer() {
                @Override
                protected void onTransform(View view, float v) {

                }
            });
        }

    }


    public void setBannerAccurately(ViewHolder.BannerViewHolder viewHolder) {
        Display display = ((ActionBarActivity) context).getWindowManager().getDefaultDisplay();
        android.view.ViewGroup.LayoutParams layoutParams = viewHolder.mDemoSlider.getLayoutParams();
        layoutParams.width = display.getWidth();
        layoutParams.height = (int) (display.getWidth() / (3));
        viewHolder.mDemoSlider.setLayoutParams(layoutParams);
    }


    @Override
    public void onSliderClick(BaseSliderView slider) {

        Object obj = new Object();

        Bundle bundle = slider.getBundle();

        if (common.getBooleanValue(Constants.isLoggedIn)) {
            Bundle logBundleInitial = new Bundle();
            logBundleInitial.putString("banner_url", bundle.getString("url"));
            logBundleInitial.putString("user_id", common.getStringValue(Constants.id));
            mFirebaseAnalytics.logEvent("banner_clicked", logBundleInitial);
        }
        Intent intent = new Intent(context, AdvertiseActivity.class);
        intent.putExtra("url", bundle.getString("urlELink"));
        context.startActivity(intent);
    }


}
