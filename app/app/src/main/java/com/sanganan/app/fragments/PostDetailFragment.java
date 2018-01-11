package com.sanganan.app.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.activities.AnyNotificationActivity;
import com.sanganan.app.activities.MainHomePageActivity;
import com.sanganan.app.activities.PostGalleryImageActivity;
import com.sanganan.app.adapters.CommentListAdapter;
import com.sanganan.app.adapters.MyRecycleAdapter;
import com.sanganan.app.adapters.PostRecyclerAdapter;
import com.sanganan.app.common.Alphabets;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.InsertToMangoDb;
import com.sanganan.app.common.RecyclerItemClickListener;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.model.Comment;
import com.sanganan.app.model.PostModel;
import com.sanganan.app.utility.ListUtility;
import com.sanganan.app.utility.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 29/5/17.
 */


public class PostDetailFragment extends BaseFragment {


    private View view;
    private ListView commentList;
    private ArrayList<Comment> listOfComment;
    private Bundle bundle;
    private String profile_pic, flatnumber, username, description, feedType, postRefId, postId, commentCount, likesCount, isLIke, photoPath, userId;
    private ImageView profile_picIV, like_dislikeIV, deleteIV, comment, feedTypeIV;
    private TextView flatno_nameTV, title_pageTV, descriptionTV, noOfLikesTV, noOfCommentsTV, commmentTextTV, forwardToDetailPageTV;
    private RecyclerView imagesRecycle;
    private PostRecyclerAdapter postRecyclerAdapter;
    private LinearLayoutManager horizontalLayoutManagaerTop;
    private CommentListAdapter commentListAdapter;
    private Common common;
    private RequestQueue requestQueue;
    String imageArray[];
    private RelativeLayout container_about_post;
    EditText editCommentText;
    Typeface ubuntuB, worksansM, karlaB, ubuntuR, worksansR;
    String forwardToDetailPage;
    ArrayList<String> finalListImage;
    LinearLayout commnetBox;
    int commentCC = 0;
    private boolean canDelete = false;
    private String localUserId;
    private ScrollView scrollcontainer;

    int indicator = 0;//if 0 ---- not yet commented    if 1----start view from bottom as comment added at bottom of view
    private FirebaseAnalytics mFirebaseAnalytics;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.postdetail, container, false);
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);


        TextView title_done = (TextView) getActivity().findViewById(R.id.title_done);
        title_done.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);


        bundle = getArguments();

        profile_pic = bundle.getString("profile_pic");
        flatnumber = bundle.getString("flatnumber");
        username = bundle.getString("username");
        description = bundle.getString("description");
        feedType = bundle.getString("feedType");
        postRefId = bundle.getString("postRefId");
        postId = bundle.getString("postId");
        commentCount = bundle.getString("commentCount");
        likesCount = bundle.getString("likesCount");
        isLIke = bundle.getString("isLIke");
        photoPath = bundle.getString("pictures");
        finalListImage = bundle.getStringArrayList("picList");
        userId = bundle.getString("UserId");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());


        initializeView();

        Bundle logBundleComment= new Bundle();
        logBundleComment.putString("society_id", common.getStringValue(Constants.userRwa));
        logBundleComment.putString("user_id", common.getStringValue(Constants.id));
        mFirebaseAnalytics.logEvent("Post_Details", logBundleComment);




        localUserId = common.getStringValue(Constants.id);

        commentCC = Integer.parseInt(commentCount);


        if (bundle.containsKey("isClickedOn")) {
            editCommentText.setFocusableInTouchMode(true);
            editCommentText.requestFocus();
            editCommentText.performClick();
        }


        canDelete = checkCanUserDeletePost();


        int drawable = getFeedTypeImage(feedType);

        feedTypeIV.setImageResource(drawable);
        forwardToDetailPageTV.setText(forwardToDetailPage);

        if (feedType.equals("createPost")) {
            forwardToDetailPageTV.setVisibility(View.GONE);
        }


        if (canDelete) {
            deleteIV.setVisibility(View.VISIBLE);
        } else {
            deleteIV.setVisibility(View.INVISIBLE);
        }


        deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (common.isNetworkAvailable()) {
                    deleteDataFromServer();
                } else {
                    common.showShortToast("No network");
                }
            }
        });


        if (common.isNetworkAvailable()) {
            getCommentDataFromAPi();
        }

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (common.isNetworkAvailable()) {
                    if (!editCommentText.getText().toString().isEmpty()) {
                        commentApi();
                        Bundle logBundleComment= new Bundle();
                        logBundleComment.putString("society_id", common.getStringValue(Constants.userRwa));
                        logBundleComment.putString("user_id", common.getStringValue(Constants.id));
                        mFirebaseAnalytics.logEvent("Post_Comment", logBundleComment);
                    }
                }
            }
        });

        like_dislikeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Bundle logBundleLike= new Bundle();
                logBundleLike.putString("society_id", common.getStringValue(Constants.userRwa));
                logBundleLike.putString("user_id", common.getStringValue(Constants.id));
                mFirebaseAnalytics.logEvent("Post_liked", logBundleLike);

                int cntLike = Integer.parseInt(likesCount);
                like_dislikeIV.setEnabled(false);

                if (isLIke.equals("1")) {
                    cntLike = cntLike - 1;
                    likesCount = String.valueOf(cntLike);
                    if (Integer.parseInt(likesCount) < 2) {
                        noOfLikesTV.setText(likesCount + " like");
                    } else {
                        noOfLikesTV.setText(likesCount + " likes");
                    }
                    like_dislikeIV.setImageResource(R.drawable.favorite_helper);
                    isLIke = "other";
                    likeApiCall("0");
                } else if (isLIke.equals("0")) {
                    cntLike = cntLike + 1;
                    likesCount = String.valueOf(cntLike);
                    if (Integer.parseInt(likesCount) < 2) {
                        noOfLikesTV.setText(likesCount + " like");
                    } else {
                        noOfLikesTV.setText(likesCount + " likes");
                    }
                    like_dislikeIV.setImageResource(R.drawable.favorite_selectednew);
                    isLIke = "other";
                    likeApiCall("1");
                }

                like_dislikeIV.setEnabled(true);

            }
        });

        forwardToDetailPageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardToFeedType(feedType, postRefId);
            }
        });


        imagesRecycle.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        DialogFragment fragment = new ImageSlideShow();
                        Bundle bundle1 = new Bundle();
                        bundle1.putStringArrayList("imagelist", finalListImage);
                        bundle1.putInt("position", position);
                        fragment.setArguments(bundle1);
                        fragment.setRetainInstance(true);
                        fragment.show(getFragmentManager(), "tag_slide_show");
                    }
                })
        );


        profile_picIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle1 = new Bundle();
                Fragment fragment = new DetailsNeighbourFragment();
                bundle1.putString("ID", userId);
                fragment.setArguments(bundle1);
                activityCallback.onButtonClick(fragment, false);
            }
        });


        return view;
    }

    private boolean checkCanUserDeletePost() {

        if ((userId.equals(common.getStringValue(Constants.id)) || (Constants.rolesGivenToUser.contains("can_delete_post"))) && feedType.equals("createPost")) {
            return true;
        } else {
            return false;
        }
    }

    private int getFeedTypeImage(String feedType) {
        int drawable = 0;

        switch (feedType) {
            case "notification":
                drawable = R.drawable.notification_small;
                forwardToDetailPage = "view notice >";
                break;
            case "callout":
                drawable = R.drawable.callout_small;
                forwardToDetailPage = "view callout >";
                break;
            case "poll":
                drawable = R.drawable.polling_small;
                forwardToDetailPage = "view poll >";
                break;
            case "classified":
                drawable = R.drawable.classifieds_small;
                forwardToDetailPage = "view classified >";
                break;
            case "photos":
                drawable = R.drawable.gallery_small;
                forwardToDetailPage = "view pic >";
                break;
            case "createPost":
                drawable = R.drawable.write_post;
                forwardToDetailPage = "";
                break;
            case "complain":
                drawable = R.drawable.complaints_small;
                forwardToDetailPage = "view complaint >";
                break;
            default:
                drawable = R.drawable.write_post;
                forwardToDetailPage = "view createPost >";
                break;

        }

        return drawable;

    }

    private void forwardToFeedType(String feedType, String postRefId) {

        Bundle bundle = new Bundle();
        bundle.putString("ID", postRefId);
        Intent intent;
        if (feedType.equals("photos")) {
            intent = new Intent(getActivity(), PostGalleryImageActivity.class);
        } else {
            intent = new Intent(getActivity(), AnyNotificationActivity.class);
        }
        switch (feedType) {
            case "notification":
                bundle.putString("fragmnet", "notification");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case "callout":
                bundle.putString("fragmnet", "callout");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case "poll":
                bundle.putString("fragmnet", "polling");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case "classified":
                bundle.putString("fragmnet", "classified");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case "photos":
                bundle.putString("fragmnet", "photos");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case "createPost":

                break;
            case "complain":
                bundle.putString("fragmnet", "complaint");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:

                break;

        }


    }

    private void initializeView() {

        common = Common.getNewInstance(getActivity());
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        worksansM = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Medium.ttf");
        karlaB = Typeface.createFromAsset(getActivity().getAssets(), "Karla-Bold.ttf");
        ubuntuR = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-R.ttf");
        worksansR = Typeface.createFromAsset(getActivity().getAssets(), "WorkSans-Regular.ttf");


        if (!photoPath.isEmpty()) {
            imageArray = photoPath.split(",");
        } else {
            imageArray = new String[0];
        }
        scrollcontainer = (ScrollView) view.findViewById(R.id.scrollcontainer);
        profile_picIV = (ImageView) view.findViewById(R.id.profile_pic);
        like_dislikeIV = (ImageView) view.findViewById(R.id.like_dislike);
        deleteIV = (ImageView) view.findViewById(R.id.deletePost);
        feedTypeIV = (ImageView) view.findViewById(R.id.feedType);
        commnetBox = (LinearLayout) view.findViewById(R.id.commnetBox);
        flatno_nameTV = (TextView) view.findViewById(R.id.flatno_name);
        flatno_nameTV.setTypeface(ubuntuB);
        title_pageTV = (TextView) view.findViewById(R.id.textViewtitle);
        title_pageTV.setTypeface(ubuntuB);
        descriptionTV = (TextView) view.findViewById(R.id.description);
        descriptionTV.setTypeface(worksansM);
        noOfLikesTV = (TextView) view.findViewById(R.id.noOfLikes);
        noOfLikesTV.setTypeface(karlaB);
        forwardToDetailPageTV = (TextView) view.findViewById(R.id.forwardToDetailPage);
        forwardToDetailPageTV.setTypeface(karlaB);
        noOfCommentsTV = (TextView) view.findViewById(R.id.noOfComments);
        noOfCommentsTV.setTypeface(karlaB);
        commmentTextTV = (TextView) view.findViewById(R.id.commmentText);
        commmentTextTV.setTypeface(ubuntuR);
        container_about_post = (RelativeLayout) view.findViewById(R.id.container_about_post);
        imagesRecycle = (RecyclerView) view.findViewById(R.id.imagesRecycle);
        comment = (ImageView) view.findViewById(R.id.comment);
        editCommentText = (EditText) view.findViewById(R.id.editCommentText);
        editCommentText.setTypeface(worksansR);
        commentList = (ListView) view.findViewById(R.id.commentList);


/*
        editCommentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                int n=editCommentText.getLineCount();
                if(n>=2)
                {
                    int h = editCommentText.getHeight();
                    int finalHeight = n*h;
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, finalHeight);
                    editCommentText.setLayoutParams(lp);
                }

            }
        });*/


    }

    public void getCommentDataFromAPi() {
        common.showSpinner(getActivity());
        String uri = Constants.MongoBaseUrl + "showfeedcomment.php";
        try {


            StringRequest req = new StringRequest(Request.Method.POST, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject responseObj = new JSONObject(response);
                                setUpUiAfterResponse(responseObj);
                                editCommentText.setText("");
                            } catch (Exception ex) {
                                ex.printStackTrace();
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
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("SocietyFeedID", postId);
                    return params;
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

    public void setUpUiAfterResponse(JSONObject response) {
        common.hideSpinner();
        String status = "";

        try {
            listOfComment = new ArrayList<>();
            listOfComment.clear();
            status = response.optString("Status");
            if (status.equals("1")) {
                JSONArray array = response.getJSONArray("PostComment");
                for (int i = 0; i < array.length(); i++) {
                    Comment comment = new Comment();
                    JSONObject object = array.getJSONObject(i);
                    String SocietyFeedID = object.optString("SocietyFeedID");
                    String ResidentRWAID = object.optString("ResidentRWAID");
                    String PostFeedCommnet;
                    try {
                        PostFeedCommnet = common.funConvertBase64ToString(object.optString("PostFeedCommnet"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        PostFeedCommnet = object.optString("PostFeedCommnet");

                    }
                    String CommentBy = object.optString("CommentBy");
                    String FlatNbr = object.optString("FlatNbr");
                    String CreatDate = object.optString("CreatDate");
                    String IsActive = object.optString("IsActive");
                    String CommentProfilePic = object.optString("CommentProfilePic");


                    comment.setSocietyFeedID(SocietyFeedID);
                    comment.setResidentRWAID(ResidentRWAID);
                    comment.setPostFeedCommnet(PostFeedCommnet);
                    comment.setCommentBy(CommentBy);
                    comment.setFlatNbr(FlatNbr);
                    comment.setCreatDate(CreatDate);
                    comment.setIsActive(IsActive);
                    comment.setCommentProfilePic(CommentProfilePic);

                    listOfComment.add(comment);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        commentCount = String.valueOf(listOfComment.size());

        if(indicator==1){
            setNameAndPostUI();
            scrollcontainer.post(new Runnable() {
                public void run() {
                    scrollcontainer.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

        commentListAdapter = new CommentListAdapter(getActivity(), listOfComment);
        commentList.setAdapter(commentListAdapter);
        ListUtility.setListViewHeightBasedOnItems(commentList);
        //scrollcontainer.smoothScrollTo(0, view.getBottom());

        if (listOfComment.size() > 0) {
            commnetBox.setVisibility(View.VISIBLE);
            commentList.setSelection(listOfComment.size() - 1);
        } else {
            commnetBox.setVisibility(View.INVISIBLE);
        }


        if (indicator == 0) {
            setNameAndPostUI();
        }

    }

    private void setNameAndPostUI() {
        try {
            if (profile_pic.endsWith(".png") || profile_pic.endsWith(".jpg")) {
                Picasso.with(getActivity()).load(profile_pic).placeholder(R.drawable.person_placeholder).error(R.drawable.person_placeholder).into(profile_picIV);
            } else {
                String str = String.valueOf(username.charAt(0));
                str.toLowerCase();
                int positionL = 0;
                for (int i = 0; i < Alphabets.alphabets.size(); i++) {
                    if (str.equalsIgnoreCase(Alphabets.alphabets.get(i))) {
                        positionL = i;
                    }
                }
                profile_picIV.setImageResource(Alphabets.alphabetsDrawable.get(positionL));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isLIke.equals("1")) {
            like_dislikeIV.setImageResource(R.drawable.favorite_selectednew);
        } else {
            like_dislikeIV.setImageResource(R.drawable.favorite_helper);
        }

        String firstName = username.split(" ")[0];
        flatno_nameTV.setText(firstName + ", " + flatnumber);

        descriptionTV.setText(description);

        if (Integer.parseInt(likesCount) < 2) {
            noOfLikesTV.setText(likesCount + " like");
        } else {
            noOfLikesTV.setText(likesCount + " likes");
        }

        if (Integer.parseInt(commentCount) <= 1) {
            noOfCommentsTV.setText(commentCount + " comment");
        } else {
            noOfCommentsTV.setText(commentCount + " comments");
        }


        horizontalLayoutManagaerTop = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);


        if (imageArray.length > 0) {
            imagesRecycle.setVisibility(View.VISIBLE);
            android.view.ViewGroup.LayoutParams layoutParams = imagesRecycle.getLayoutParams();
            int height = layoutParams.height;
            android.view.ViewGroup.MarginLayoutParams layoutMarginParams = (ViewGroup.MarginLayoutParams) imagesRecycle.getLayoutParams();
            android.view.ViewGroup.MarginLayoutParams layoutMarginParamsContainer = (ViewGroup.MarginLayoutParams) container_about_post.getLayoutParams();
            int leftMarginContainer = layoutMarginParamsContainer.leftMargin;
            int leftMArgin = layoutMarginParams.leftMargin;
            int totalMargin = leftMArgin + leftMarginContainer;


            postRecyclerAdapter = new PostRecyclerAdapter(getActivity(), imageArray, height, totalMargin);
            imagesRecycle.setLayoutManager(horizontalLayoutManagaerTop);
            imagesRecycle.setAdapter(postRecyclerAdapter);
        } else {
            imagesRecycle.setVisibility(View.GONE);
        }
    }

    public void commentApi() {

        indicator = 1;
        common.showSpinner(getActivity());
        String uri = Constants.MongoBaseUrl + "feedcomment.php";
        try {


            StringRequest req = new StringRequest(Request.Method.POST, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject responseObj = new JSONObject(response);
                                String status = responseObj.optString("Status");
                                if (status.equals("1")) {
                                    Constants.isAnyNewPostAdded = true;
                                    getCommentDataFromAPi();


                                }


                            } catch (Exception ex) {
                                ex.printStackTrace();
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
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("SocietyFeedID", postId);
                    params.put("CommentProfilePic", common.getStringValue(Constants.ProfilePic));
                    params.put("ResidentRWAID", common.getStringValue(Constants.ResidentRWAID));
                    params.put("PostFeedCommnet", common.StringToBase64StringConvertion(editCommentText.getText().toString()));
                    params.put("CommentBy", common.getStringValue(Constants.FirstName));
                    params.put("FlatNbr", common.getStringValue(Constants.flatNumber));
                    return params;
                }
            };
            try {
                req.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void likeApiCall(final String likeStatus) {
        String uri = Constants.MongoBaseUrl + "postlike.php";
        try {


            StringRequest req = new StringRequest(Request.Method.POST, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject responseObj = new JSONObject(response);
                                String likeStatusAfterResponse = responseObj.optString("Status");
                                if (likeStatusAfterResponse.equals("1")) {
                                    isLIke = likeStatus;
                                    Constants.isAnyNewPostAdded = true;
                                }

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


    @Override
    public void onResume() {
        super.onResume();
        if (Constants.isAnyNewPostAdded) {
            getActivity().onBackPressed();
        }
    }


    public void deleteDataFromServer() {
        common.showSpinner(getActivity());

        String uri = Constants.MongoBaseUrl + "onlypostdelete.php";
        try {


            StringRequest req = new StringRequest(Request.Method.POST, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject object = new JSONObject(response);

                                String status = object.optString("Status");
                                String Message = object.optString("Message");
                                common.hideSpinner();
                                if (status.equals("1")) {
                                    Constants.isAnyNewPostAdded = true;
                                    getActivity().onBackPressed();
                                    common.showShortToast(Message);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                common.hideSpinner();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d("error", error.getMessage());
                            common.hideSpinner();
                        }

                    }) {


                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ID", postId);

                    return params;

                }
            };
            try {
                req.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(req);
            } catch (Exception e) {
                e.printStackTrace();
                common.hideSpinner();
            }
        } catch (Exception e) {
            e.printStackTrace();
            common.hideSpinner();

        }


    }

}
