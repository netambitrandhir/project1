package com.sanganan.app.adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.sanganan.app.R;
import com.sanganan.app.common.Alphabets;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.model.PostModel;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Randhir Patel on 10/7/17.
 */

public class MyRecyclerPostAdapter extends RecyclerView.Adapter<MyRecyclerPostAdapter.MyViewHolder> {

    private ArrayList<PostModel> postList;
    private Context context;
    Typeface worksansM;
    Common common;
    RequestQueue requestQueue;

    public MyRecyclerPostAdapter(Context context, ArrayList<PostModel> postList) {
        this.postList = postList;
        this.context = context;
        common = Common.getNewInstance(context);
        requestQueue = VolleySingleton.getInstance(context).getRequestQueue();

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView postContent, flat_name, likecomment, multiplePhotoIndicator, dateAndtime;
        ImageView  comment, like_dislike, feedType;
        ImageView images;
        CircleImageView profile_pic;
        RelativeLayout imagecontainer;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.postContent = (TextView) itemView.findViewById(R.id.postContent);
            this.flat_name = (TextView) itemView.findViewById(R.id.flat_name);
            this.likecomment = (TextView) itemView.findViewById(R.id.likecomment);
            this.imagecontainer = (RelativeLayout) itemView.findViewById(R.id.imagecontainer);
            this.multiplePhotoIndicator = (TextView) itemView.findViewById(R.id.multiplePhotoIndicator);
            this.dateAndtime = (TextView) itemView.findViewById(R.id.dateAndtime);
            this.profile_pic = (CircleImageView) itemView.findViewById(R.id.profile_pic);
            this.images = (ImageView) itemView.findViewById(R.id.images);
            this.comment = (ImageView) itemView.findViewById(R.id.comment);
            this.like_dislike = (ImageView) itemView.findViewById(R.id.like_dislike);
            this.feedType = (ImageView) itemView.findViewById(R.id.feedType);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_single_view, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        TextView postContent = holder.postContent;
        TextView flat_name = holder.flat_name;
        TextView likecomment = holder.likecomment;
        TextView dateAndtime = holder.dateAndtime;
        CircleImageView profile_pic = holder.profile_pic;
        ImageView feedType = holder.feedType;
        ImageView like_dislike  = holder.like_dislike;
        TextView multiplePhotoIndicator = holder.multiplePhotoIndicator;
        ImageView images = holder.images;
        RelativeLayout imagecontainer = holder.imagecontainer;


        worksansM = Typeface.createFromAsset(context.getAssets(), "WorkSans-Medium.ttf");

        postContent.setTypeface(worksansM);
        flat_name.setTypeface(worksansM);
        likecomment.setTypeface(worksansM);
        dateAndtime.setTypeface(worksansM);

        PostModel model = postList.get(position);


        postContent.setText(model.getDescription());
        flat_name.setText(model.getResidentName() + ", " + model.getResidentFlatNo());
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


        likecomment.setText(model.getLikesCount() + " " + sLike + " " + model.getCommentCount() + " " + sComment + " ");

        dateAndtime.setText(model.getDateCreated());
        String picLink = model.getProfilePic();

        if (picLink != null) {
            if (picLink.endsWith(".png") || picLink.endsWith(".jpg")) {
                Picasso.with(context).load(model.getProfilePic()).placeholder(R.drawable.person_placeholder).into(profile_pic);
            } else {
                String str = String.valueOf(model.getResidentName().charAt(0));
                str.toLowerCase();
                int positionL = 0;
                for (int i = 0; i < Alphabets.alphabets.size(); i++) {
                    if (str.equalsIgnoreCase(Alphabets.alphabets.get(i))) {
                        positionL = i;
                    }
                }
                profile_pic.setImageResource(Alphabets.alphabetsDrawable.get(positionL));
            }
        }

        int drawable = getFeedTypeImage(model.getFeedType());

        feedType.setImageResource(drawable);

        if (model.getPhotoPathList().size() > 0) {
            imagecontainer.setVisibility(View.VISIBLE);
            if (model.getPhotoPathList().size() > 1) {
                multiplePhotoIndicator.setVisibility(View.VISIBLE);
                multiplePhotoIndicator.setText(String.valueOf(model.getPhotoPathList().size()));
            } else {
                multiplePhotoIndicator.setVisibility(View.GONE);
            }
            Uri uri = Uri.parse(model.getPhotoPathList().get(0));
            images.setImageURI(uri);
            // Picasso.with(context).load(model.getPhotoPathList().get(0)).into(viewHolder.images);
        } else {
            imagecontainer.setVisibility(View.GONE);
        }

        if (model.getIsLike().equals("1")) {
            like_dislike.setImageResource(R.drawable.like_inlisitng);
        } else {
            like_dislike.setImageResource(R.drawable.notlikedbtn);
        }

        like_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postList.get(position).getIsLike().equals("1")) {
                    likeApiCall("0", postList.get(position).getID(), position);
                } else {
                    likeApiCall("1", postList.get(position).getID(), position);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return postList.size();
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

    private void likeApiCall(final String likeStatus, final String postId, final int position) {
        String uri = "http://54.174.234.86/phpmongodb/postlike.php";
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
                            //common.showShortToast(error.getMessage());
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
        postList.get(position).setIsLike(likeStatus);
        String likeCount = postList.get(position).getLikesCount();
        int likes = Integer.parseInt(likeCount);
        if (likeStatus.equals("1")) {
            likes = likes + 1;
        } else {
            likes = likes - 1;
        }
        postList.get(position).setLikesCount(String.valueOf(likes));
        notifyDataSetChanged();
    }


}
