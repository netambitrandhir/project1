package com.sanganan.app.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.sanganan.app.R;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.InsertToMangoDb;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.model.GalleryData;
import com.sanganan.app.model.GalleryParent;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Randhir Patel on 22/6/17.
 */


public class PostGalleryImageActivity extends AppCompatActivity {
    public static final String TAG = "GalleryActivity";
    public static final String EXTRA_NAME = "images";

    private GalleryPagerAdapter _adapter;
    Bundle bundle;
    public int listPosition;
    int imageChoosenPosition;
    ArrayList<GalleryData> listDataGallery;
    private ArrayList<String> _images;
    GalleryParent galleryParent;
    ProgressDialog progressDialog;

    ViewPager _pager;
    String id;
    Common common;
    RequestQueue requestQueue;
    private boolean canDeleteGalleryImage = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_nukad);

        common = new Common(PostGalleryImageActivity.this);
        requestQueue = VolleySingleton.getInstance(PostGalleryImageActivity.this).getRequestQueue();

        bundle = getIntent().getExtras();
        id = bundle.getString("ID");
        _pager = (ViewPager) findViewById(R.id.pager);


        if (Constants.rolesGivenToUser.contains("can_delete_gallery_image")) {
            canDeleteGalleryImage = true;
        }


        if (common.isNetworkAvailable()) {
            getPhotoDetail();
        }

    }

    class GalleryPagerAdapter extends PagerAdapter {

        Context _context;
        LayoutInflater _inflater;
        TextView page_number;
        ImageView share,deleteImage;
        TextView nameTxtView, detailTxtViewId;
        Typeface karlaB, karlaR;
        Common common;
        RequestQueue requestQueue;

        public GalleryPagerAdapter(Context context) {
            _context = context;
            _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            karlaB = Typeface.createFromAsset(context.getAssets(), "Karla-Bold.ttf");
            karlaR = Typeface.createFromAsset(context.getAssets(), "Karla-Regular.ttf");
            common = Common.getNewInstance(context);
            requestQueue = VolleySingleton.getInstance(_context).getRequestQueue();
        }

        @Override
        public int getCount() {
            return _images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = _inflater.inflate(R.layout.pager_gallery_item_nukad, container, false);
            container.addView(itemView);

            final ImageView thumbView = new ImageView(_context);
            thumbView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            page_number = (TextView) itemView.findViewById(R.id.page_number);
            nameTxtView = (TextView) itemView.findViewById(R.id.nameTxtView);
            final TextView numberOfLikes = (TextView) itemView.findViewById(R.id.numberOfLikes);
            nameTxtView.setTypeface(karlaB);
            numberOfLikes.setTypeface(karlaB);
            detailTxtViewId = (TextView) itemView.findViewById(R.id.detailTxtViewId);
            detailTxtViewId.setTypeface(karlaR);
            share = (ImageView) itemView.findViewById(R.id.share);
            final ImageView likeImage = (ImageView) itemView.findViewById(R.id.likeImage);
            deleteImage = (ImageView) itemView.findViewById(R.id.deleteImage);
            /*final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);*/



            final SubsamplingScaleImageView imageView =
                    (SubsamplingScaleImageView) itemView.findViewById(R.id.image);

            int total = listDataGallery.size();
            int number = position + 1;
            page_number.setText(number + "  of   " + total);
            nameTxtView.setText(listDataGallery.get(position).getFirstName() + " , " + listDataGallery.get(position).getFlatNbr());
            detailTxtViewId.setText(listDataGallery.get(position).getPhotoCaption());
            String likeCount = listDataGallery.get(position).getLIKES();

            if (likeCount.equals("0") || likeCount.equals("1")) {
                numberOfLikes.setText(likeCount + " like");
            } else {
                numberOfLikes.setText(likeCount + " likes");
            }

            Glide.with(_context)
                    .load(_images.get(position))
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            imageView.setImage(ImageSource.bitmap(bitmap));
                            thumbView.setImageBitmap(bitmap);
                        }
                    });


            if (listDataGallery.get(position).getIsLIKES().equals("1")) {
                likeImage.setImageDrawable(getResources().getDrawable(R.drawable.liked_fullview));
            } else {
                likeImage.setImageDrawable(getResources().getDrawable(R.drawable.not_like_fullview));
            }

            String photoAddedBy = listDataGallery.get(position).getAddedBy();

            if(photoAddedBy.equalsIgnoreCase(common.getStringValue(Constants.id))|| canDeleteGalleryImage){
                deleteImage.setVisibility(View.VISIBLE);
            }else{
                deleteImage.setVisibility(View.GONE);
            }


            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    //USE DIFFERENT LINK FOR PRODUCTION and DEVELOPMENT
                    //http://52.91.49.255/nukkad_dev/media_view.php?
                    //http://mynukad.com/Nukkad-admin/media_view.php?
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "http://mynukad.com/Nukkad-admin/media_view.php?sid=" + common.getStringValue("ID") + "&mid=" + listDataGallery.get(position).getPhotoId());
                    sendIntent.setType("text/plain");
                    _context.startActivity(sendIntent);
                }
            });

            thumbView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Thumbnail clicked");

                    _pager.setCurrentItem(position);
                }
            });


            likeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (common.isNetworkAvailable()) {
                        GalleryData data = listDataGallery.get(position);
                        if (data.getIsLIKES().equals("1")) {
                            likeImage.setImageDrawable(getResources().getDrawable(R.drawable.not_like_fullview));
                            data.setIsLIKES("0");
                            int count = Integer.parseInt(data.getLIKES());
                            count = count - 1;
                            data.setLIKES(String.valueOf(count));
                            if (count > 1) {
                                numberOfLikes.setText(String.valueOf(count) + " likes");
                            } else {
                                numberOfLikes.setText(String.valueOf(count) + " like");
                            }
                            //  SocietyGalleryFragment.parentList.get(listPosition).setDataArrayList(listDataGallery);
                            apiRequestToUpdate(position, "0");
                        } else {
                            likeImage.setImageDrawable(getResources().getDrawable(R.drawable.liked_fullview));
                            data.setIsLIKES("1");
                            int count = Integer.parseInt(data.getLIKES());
                            count = count + 1;
                            data.setLIKES(String.valueOf(count));
                            if (count > 1) {
                                numberOfLikes.setText(String.valueOf(count) + " likes");
                            } else {
                                numberOfLikes.setText(String.valueOf(count) + " like");
                            }
                            //  SocietyGalleryFragment.parentList.get(listPosition).setDataArrayList(listDataGallery);
                            apiRequestToUpdate(position, "1");
                        }
                    } else {
                        common.showShortToast("No internet...!!");
                    }
                }
            });


            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apiRequestToDelete(position);
                }
            });


            return itemView;
        }

        private void apiRequestToUpdate(int position, String like) {


            String uri = Constants.BaseUrl + "photolike";
            try {
                JSONObject json = new JSONObject();
                json.put("Likes", like);
                json.put("PhotoID", listDataGallery.get(position).getPhotoId());
                json.put("ResidentID", common.getStringValue(Constants.id));

                JsonObjectRequest req = new JsonObjectRequest(uri, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String s = response.toString();
                                    notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                VolleyLog.d("error", error.getMessage());

                            }

                        }) {


                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        return headers;
                    }
                };
                try {
                    req.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(req);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }


        private void apiRequestToDelete(final int position) {


            String uri = Constants.BaseUrl + "gallerypicdelete";
            try {
                JSONObject json = new JSONObject();
                json.put("ID", listDataGallery.get(position).getPhotoId());
                json.put("ResidentID", common.getStringValue(Constants.id));

                JsonObjectRequest req = new JsonObjectRequest(uri, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String s = response.toString();
                                    if(response.optString("Status").equalsIgnoreCase("1")){
                                        InsertToMangoDb insertToMangoDb = new InsertToMangoDb(_context,"photos",listDataGallery.get(position).getPhotoId(),"","");
                                        insertToMangoDb.deleteDataFromServer();
                                        Constants.isAnyNewPostAdded = true;
                                        Constants.isAnyPicAddedOrRemoved = true;
                                        finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                VolleyLog.d("error", error.getMessage());

                            }

                        }) {


                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        return headers;
                    }
                };
                try {
                    req.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(req);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

    }

    private void getPhotoDetail() {
        progressDialog = new ProgressDialog(PostGalleryImageActivity.this);
        progressDialog.setMessage("Please wait Data is loading...");
        progressDialog.show();

        String uri = Constants.BaseUrl + "photodetail";
        try {

            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("ResidentID", common.getStringValue(Constants.id));

            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String s = response.toString();
                                parsedData(response);
                            } catch (Exception e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            VolleyLog.d("error", error.getMessage());
                        }

                    }) {


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Country", "Singapore");
                    return headers;
                }
            };
            try {
                req.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(req);
            } catch (Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();

        }
    }

    private void parsedData(JSONObject response) {

        progressDialog.dismiss();
        listDataGallery = new ArrayList<GalleryData>();
        listDataGallery.clear();
        try {
            if (response.toString().contains("PhotoDetail")) {
                GalleryData data = new GalleryData();
                JSONObject object = response.getJSONObject("PhotoDetail");

                data.setPhotoId(object.optString("PhotoID"));
                data.setPhotoPath(object.optString("PhotoPath"));
                try {
                    data.setPhotoCaption(common.funConvertBase64ToString(object.optString("PhotoCaption")));
                } catch (Exception e) {
                    e.printStackTrace();
                    data.setPhotoCaption(object.optString("PhotoCaption"));
                }
                data.setAddedBy(object.optString("AddedBy"));
                data.setFirstName(object.optString("FirstName"));
                data.setResidentRWAID(object.optString("ResidentRWAID"));
                data.setLIKES(object.optString("LIKES"));
                data.setIsLIKES(object.optString("IsLIKES"));
                data.setFlatNbr(object.optString("FlatNbr"));

                listDataGallery.add(data);


                _images = new ArrayList<>();
                _images.clear();


                for (GalleryData dataObject : listDataGallery) {
                    _images.add(data.getPhotoPath());
                }

                _adapter = new GalleryPagerAdapter(PostGalleryImageActivity.this);
                _pager.setAdapter(_adapter);
                _pager.setCurrentItem(0);
                _pager.setOffscreenPageLimit(1); // how many images to load into memory


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
