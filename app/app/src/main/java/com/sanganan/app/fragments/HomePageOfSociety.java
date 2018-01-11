package com.sanganan.app.fragments;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ProgressDialog;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.slider.library.SliderLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.sanganan.app.R;
import com.sanganan.app.activities.MainHomePageActivity;
import com.sanganan.app.activities.StartSearchPage;
import com.sanganan.app.adapters.MyRecycleAdapter;
import com.sanganan.app.adapters.PostAdapter;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.RecyclerItemClickListener;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.firebase.MyFirebaseInstanceIDService;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.interfaces.ICallTOHomePage;
import com.sanganan.app.model.BannerData;
import com.sanganan.app.model.PostModel;
import com.sanganan.app.model.TabData;
import com.sanganan.app.model.WheelerModel;
import com.sanganan.app.sample.SendBirdOpenChatActivity;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.ACTIVITY_SERVICE;


/**
 * Created by Randhir Patel on 24/5/17.
 */


public class HomePageOfSociety extends BaseFragment {

    private static final String appId = "EBCD2CB7-F5CC-4F6C-8EF4-86548DCCDE38"; /* Sample SendBird Application */


    private int TOTAL_COUNT_UPTO_PREVIOUS_LOAD = 0;
    Common comonObj;
    View view;
    ImageView instantActionIcon;
    android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction ft;
    Typeface ubuntuB, ubuntuR;
    int[] imageResources = {
            R.drawable.neighbors, R.drawable.complaints, R.drawable.helpers, R.drawable.gallery, R.drawable.callout, R.drawable.nearby,
            R.drawable.polling_new, R.drawable.classifieds, R.drawable.info, R.drawable.deals};
    String[] tabNames = {"Neighbours_Tab", "Complaints_Tab", "Helpers_Tab", "Gallery_Tab", "Callout_tab", "Nearby_Tab", "Polling_Tab", "Classifieds_Tab", "Societyinfo_tab", "Deals_Tab"};
    TextView title;

    String society;
    TextView relativeLayoutHeading;
    ImageView downarrow;
    boolean isLoggedIn;
    String mApprovalStatus, sUserId, mNickname;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    TextView txt_count;
    ImageView startChat, showTopIV;
    RelativeLayout titleContainer;

    ArrayList<WheelerModel> listVehicle = new ArrayList<>();
    ArrayList<PostModel> postList = new ArrayList<>();
    String token = "";
    private FirebaseAnalytics mFirebaseAnalytics;
    String pollCount, notificationCount, complainCount, classifiedCount, neighbourCount, helperCount, galleryCount, calloutCount;
    ArrayList<BannerData> listBanner = new ArrayList<>();
    SliderLayout mDemoSlider;
    ListView list_item;
    private RecyclerView hLVTop;

    ArrayList<TabData> tabDataList = new ArrayList<>();
    ArrayList<TabData> tabDataListTop = new ArrayList<>();
    ArrayList<TabData> tabDataListHide = new ArrayList<>();
    MyRecycleAdapter tabAdapterTop;
    PostAdapter postAdapter;
    int topMarginByH;
    int sideMarginByH;
    int screenWidth;
    int screenHeight;
    private LinearLayoutManager horizontalLayoutManagaerTop;
    String userid;

    private static final long One_day_millis = 60 * 60 * 24;
    private boolean canCommentOrLike = false;


    boolean userScrolled = false;
    private int mLastFirstVisibleItem;
    private boolean mIsScrollingUp;
    boolean userScrolledActionCompleted = false;
    private boolean loadingCompleted = false;
    public View ftView;
    public boolean isLoading = false;
    private boolean isTabShifted = false;
    int cc = 1;
    private boolean isVisible;

    private void fixAutoStart() {

        String manufacturer = "xiaomi";
        if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER) && !comonObj.pref.contains("autostarted")) {
            //this will open auto start screen where user can enable permission for your app
            XiomiDialog dialog = new XiomiDialog();
            dialog.setRetainInstance(true);
            dialog.show(getFragmentManager(), "tag_alert_xiaomi");

        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (tabDataList.size() == 0) {
            view = inflater.inflate(R.layout.homepage_society_new, container, false);
        }
        ftView = inflater.inflate(R.layout.lazy_loading_footer_view, null);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);


        intializeView();

        if (isLoggedIn) {
            userid = comonObj.getStringValue(Constants.id);
            sUserId = comonObj.getStringValue(Constants.id);
            mNickname = comonObj.getStringValue(Constants.FirstName);
        } else {

        }

        if (comonObj.isNetworkAvailable()) {
            if (tabDataList.size() == 0) {
                updateUserInfo();
            } else {
                getNotificationCount();
            }
        } else {
            comonObj.showShortToast("no internet");
        }


        ///  userdetails,,,postList,,,,notificationCount,,,bannerList  ///

        final Bundle bundle = getArguments();

        ((DrawerLocker) getActivity()).setDrawerEnabled(true);
        ((MainHomePageActivity) getActivity()).closeDrawer();

        title = (TextView) getActivity().findViewById(R.id.toolbar_title);

        society = comonObj.getStringValue("SocietyName");

        title.setText(society);
        title.setLines(1);
        title.setHorizontallyScrolling(true);
        title.setMarqueeRepeatLimit(-1);
        title.setSelected(true);

        relativeLayoutHeading = (TextView) getActivity().findViewById(R.id.toolbarTitleHeading);
        downarrow = (ImageView) getActivity().findViewById(R.id.downarrow);


        TextView titleDone = (TextView) getActivity().findViewById(R.id.title_done);
        titleDone.setVisibility(View.GONE);

        ImageView addNew = (ImageView) getActivity().findViewById(R.id.addnew);
        addNew.setVisibility(View.GONE);


        instantActionIcon = (ImageView) getActivity().findViewById(R.id.alert);
        instantActionIcon.setImageResource(R.drawable.notification_new);


        instantActionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoggedIn & comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa)) && mApprovalStatus.equalsIgnoreCase("Y")) {
                    Fragment fragment = new Notification();
                    activityCallback.onButtonClick(fragment, false);
                    relativeLayoutHeading.setVisibility(View.GONE);
                    downarrow.setVisibility(View.GONE);

                } else {
                    AlertDialogLogin dialog = new AlertDialogLogin();
                    dialog.setRetainInstance(true);
                    dialog.show(getFragmentManager(), "tag_alert_login");
                }
            }
        });


        showTopIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTopIV.setVisibility(View.GONE);
                isVisible = false;
                list_item.smoothScrollToPosition(0);
            }
        });


        list_item.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                // If scroll state is touch scroll then set userScrolledActionCompleted
                // true
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }


                if (view.getId() == list_item.getId()) {
                    final int currentFirstVisibleItem = list_item.getFirstVisiblePosition();


                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        mIsScrollingUp = false;
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        mIsScrollingUp = true;
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }

            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {


                // Now check if userScrolledActionCompleted is true and also check if
                // the item is end then update list view and set
                // userScrolledActionCompleted to false
                // + " Child count: " + list_item.getChildCount());


                if (list_item.getFirstVisiblePosition() >= 10 && !isVisible && !mIsScrollingUp) {
                    showTopIV.setVisibility(View.VISIBLE);
                    isVisible = true;
                } else if (list_item.getFirstVisiblePosition() < 10 && isVisible) {
                    showTopIV.setVisibility(View.GONE);
                    isVisible = false;
                }


                if (list_item.getFirstVisiblePosition() == 0) {
                    if (tabDataListTop.size() > 5) {
                        for (int k = tabDataListTop.size() - 1; k >= 5; k--) {
                            tabDataListTop.remove(k);
                            System.out.println("Loop: " + k);
                        }
                        System.out.println("Notify upper");
                        tabAdapterTop.notifyDataSetChanged();
                    }
                } else if (tabDataListTop.size() <= 5) {
                    tabDataListTop.clear();
                    tabDataListTop.addAll(tabDataList);
                    tabAdapterTop.notifyDataSetChanged();
                    System.out.println("Notify lower");
                }

                if (userScrolled && !mIsScrollingUp && !loadingCompleted
                        && firstVisibleItem + visibleItemCount == totalItemCount && !(list_item.getFirstVisiblePosition() < 12)) {

                    userScrolled = false;
                    userScrolledActionCompleted = false;
                    cc = cc + 1;
                    loadPostDataFromAPI(String.valueOf(cc));
                }
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    View v = list_item.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {

                        return;
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem) {
                    View v = list_item.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        // reached the top:

                        // Setup refresh listener which triggers new data loading

                        return;
                    }
                }
            }

        });


        //Copy rest as above*******************************

        titleContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StartSearchPage.class);
                Bundle bundleXYZ = new Bundle();
                bundleXYZ.putString("fromclass", HomePageOfSociety.class.getName());
                intent.putExtras(bundleXYZ);
                Constants.fromWhere = "MainPage";
                startActivity(intent);
                getActivity().finish();
            }
        });

        startChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isLoggedIn & comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa)) & mApprovalStatus.equalsIgnoreCase("Y")) {
                    SendBird.init(appId, getActivity());
                    connect();

                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

                    Bundle logBundleChat = new Bundle();
                    logBundleChat.putString("society_id", comonObj.getStringValue(Constants.userRwa));
                    logBundleChat.putString("user_id", comonObj.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("chat_clicked", logBundleChat);

                } else {
                    AlertDialogLogin dialog = new AlertDialogLogin();
                    dialog.setRetainInstance(true);
                    dialog.show(getFragmentManager(), "tag_alert_login");
                }
            }
        });

        if (bundle != null) {
            if (bundle.containsKey("notification")) {
                if (isLoggedIn & comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa))
                        && comonObj.getStringValue(Constants.approvalStatus).equalsIgnoreCase("Y")) {

                    Fragment fragment = new Notification();
                    activityCallback.onButtonClickNoBack(fragment);
                    relativeLayoutHeading.setVisibility(View.GONE);
                    downarrow.setVisibility(View.GONE);

                }
            }
        }


        list_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundlePostDetail = new Bundle();
                PostModel model = postList.get(position);
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

                Fragment fragment = new PostDetailFragment();
                fragment.setArguments(bundlePostDetail);
                activityCallback.onButtonClick(fragment, false);

            }
        });

        return view;
    }

    public void onItemListClicked(String tabName) {

        switch (tabName) {

            case "Societyinfo_tab": {
                if (comonObj.isNetworkAvailable()) {

                    Intent intent = new Intent(getActivity(), SocietyInfoFragment.class);
                    startActivityForResult(intent, 9450);
                    relativeLayoutHeading.setVisibility(View.GONE);

                } else {
                    comonObj.showShortToast("You need to connect to internet.");
                }
                break;
            }

            case "Nearby_Tab": {
                NearbyShops.alist.clear();
                Fragment fragment1 = new NearbyShops();
                activityCallback.onButtonClick(fragment1, false);
                relativeLayoutHeading.setVisibility(View.GONE);
                downarrow.setVisibility(View.GONE);
                break;
            }

            case "Neighbours_Tab": {
                if (isLoggedIn) {

                    Bundle logBundleNeighbourNO = new Bundle();
                    logBundleNeighbourNO.putString("society_id", comonObj.getStringValue(Constants.userRwa));
                    logBundleNeighbourNO.putString("user_id", comonObj.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("myNeighbors_clicked", logBundleNeighbourNO);


                }


                if (isLoggedIn & comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa)) & mApprovalStatus.equalsIgnoreCase("Y")) {
                    Fragment fragment1 = new YourNeighbours();
                    activityCallback.onButtonClick(fragment1, false);
                    relativeLayoutHeading.setVisibility(View.GONE);
                    downarrow.setVisibility(View.GONE);

                    Bundle logBundleNeighbourOpen = new Bundle();
                    logBundleNeighbourOpen.putString("society_id", comonObj.getStringValue(Constants.userRwa));
                    logBundleNeighbourOpen.putString("user_id", comonObj.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("myNeighbors_clicked", logBundleNeighbourOpen);

                } else {
                    AlertDialogLogin dialog = new AlertDialogLogin();
                    dialog.setRetainInstance(true);
                    dialog.show(getFragmentManager(), "tag_alert_login");

                    Bundle logBundleNeighbour = new Bundle();
                    logBundleNeighbour.putString("society_id", comonObj.getStringValue("ID"));
                    mFirebaseAnalytics.logEvent("myNeighbors_clicked_NL", logBundleNeighbour);
                }
                break;
            }

            case "Complaints_Tab": {

                if (isLoggedIn) {
                    Bundle logBundleComplaintsDefault = new Bundle();
                    logBundleComplaintsDefault.putString("society_id", comonObj.getStringValue(Constants.userRwa));
                    logBundleComplaintsDefault.putString("user_id", comonObj.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("complaints_clicked", logBundleComplaintsDefault);
                }

                if (isLoggedIn & comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa)) & mApprovalStatus.equalsIgnoreCase("Y")) {

                    Fragment fragment1 = new Complains();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("home", true);
                    fragment1.setArguments(bundle);
                    activityCallback.onButtonClick(fragment1, false);

                    Bundle logBundleComplaintsopen = new Bundle();
                    logBundleComplaintsopen.putString("society_id", comonObj.getStringValue("ID"));
                    logBundleComplaintsopen.putString("user_id", comonObj.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("complaints_clicked", logBundleComplaintsopen);

                    relativeLayoutHeading.setVisibility(View.GONE);
                    downarrow.setVisibility(View.GONE);
                } else {

                    Bundle logBundleComplaintsClicked = new Bundle();
                    logBundleComplaintsClicked.putString("society_id", comonObj.getStringValue("ID"));
                    mFirebaseAnalytics.logEvent("complaints_clicked_NL", logBundleComplaintsClicked);

                    AlertDialogLogin dialog = new AlertDialogLogin();
                    dialog.setRetainInstance(true);
                    dialog.show(getFragmentManager(), "tag_alert_login");
                }
                break;
            }

            case "Helpers_Tab": {
                if (!isLoggedIn) {
                    //

                    Bundle logBundleCommunityNL = new Bundle();
                    logBundleCommunityNL.putString("society_id", comonObj.getStringValue("ID"));
                    mFirebaseAnalytics.logEvent("community_helper_clicked_NL", logBundleCommunityNL);
                }

                if (isLoggedIn & comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa)) & mApprovalStatus.equalsIgnoreCase("Y")) {
                    Fragment fragment1 = new CommunityHelperFragment();
                    activityCallback.onButtonClick(fragment1, false);
                    relativeLayoutHeading.setVisibility(View.GONE);
                    downarrow.setVisibility(View.GONE);
                } else {
                    AlertDialogLogin dialog = new AlertDialogLogin();
                    dialog.setRetainInstance(true);
                    dialog.show(getFragmentManager(), "tag_alert_login");
                }
                break;
            }

            case "Polling_Tab": {

                if (isLoggedIn & comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa)) & mApprovalStatus.equalsIgnoreCase("Y")) {
                    Fragment fragment1 = new PollingFragment();//Polling
                    activityCallback.onButtonClick(fragment1, false);
                    relativeLayoutHeading.setVisibility(View.GONE);
                    downarrow.setVisibility(View.GONE);

                    Bundle logBundlePolls = new Bundle();
                    logBundlePolls.putString("society_id", comonObj.getStringValue("ID"));
                    logBundlePolls.putString("user_id", comonObj.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("polls_clicked", logBundlePolls);

                } else {
                    AlertDialogLogin dialog = new AlertDialogLogin();
                    dialog.setRetainInstance(true);
                    dialog.show(getFragmentManager(), "tag_alert_login");

                    Bundle logBundlePollsNL = new Bundle();
                    logBundlePollsNL.putString("society_id", comonObj.getStringValue("ID"));
                    mFirebaseAnalytics.logEvent("polls_clicked_NL", logBundlePollsNL);
                }
                break;
            }

            case "Gallery_Tab": {
                if (isLoggedIn) {
                    Bundle logBundleGallery = new Bundle();
                    logBundleGallery.putString("society_id", comonObj.getStringValue("ID"));
                    logBundleGallery.putString("user_id", comonObj.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("gallery_clicked", logBundleGallery);
                } else {
                    Bundle logBundleGalleryNL = new Bundle();
                    logBundleGalleryNL.putString("society_id", comonObj.getStringValue("ID"));
                    mFirebaseAnalytics.logEvent("gallery_clicked_NL", logBundleGalleryNL);
                }
                if (isLoggedIn & comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa)) & mApprovalStatus.equalsIgnoreCase("Y")) {
                    Fragment fragment1 = new SocietyGalleryFragment();
                    activityCallback.onButtonClick(fragment1, false);
                    relativeLayoutHeading.setVisibility(View.GONE);
                    downarrow.setVisibility(View.GONE);
                } else {
                    AlertDialogLogin dialog = new AlertDialogLogin();
                    dialog.setRetainInstance(true);
                    dialog.show(getFragmentManager(), "tag_alert_login");
                }
                break;
            }

            case "Callout_tab": {
                if (isLoggedIn) {
                    Bundle logBundleCallout = new Bundle();
                    logBundleCallout.putString("society_id", comonObj.getStringValue("ID"));
                    logBundleCallout.putString("user_id", comonObj.getStringValue(Constants.id));
                    mFirebaseAnalytics.logEvent("call_out_clicked_NL", logBundleCallout);
                } else {
                    Bundle logBundleCalloutNL = new Bundle();
                    logBundleCalloutNL.putString("society_id", comonObj.getStringValue("ID"));
                    mFirebaseAnalytics.logEvent("call_out_clicked", logBundleCalloutNL);
                }
                if (isLoggedIn & comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa)) & mApprovalStatus.equalsIgnoreCase("Y")) {
                    Fragment fragment1 = new CallOut();//call-out
                    activityCallback.onButtonClick(fragment1, false);
                    relativeLayoutHeading.setVisibility(View.GONE);
                    downarrow.setVisibility(View.GONE);
                } else {
                    AlertDialogLogin dialog = new AlertDialogLogin();
                    dialog.setRetainInstance(true);
                    dialog.show(getFragmentManager(), "tag_alert_login");
                }
                break;
            }
            case "Classifieds_Tab": {
                Bundle logBundleClassified = new Bundle();
                logBundleClassified.putString("society_id", comonObj.getStringValue("ID"));
                mFirebaseAnalytics.logEvent("Classified_clicked", logBundleClassified);

                if (isLoggedIn & comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa)) & mApprovalStatus.equalsIgnoreCase("Y")) {
                    Fragment fragment1 = new Classified();
                    activityCallback.onButtonClick(fragment1, false);
                    relativeLayoutHeading.setVisibility(View.GONE);
                    downarrow.setVisibility(View.GONE);
                } else {
                    AlertDialogLogin dialog = new AlertDialogLogin();
                    dialog.setRetainInstance(true);
                    dialog.show(getFragmentManager(), "tag_alert_login");
                }
                break;
            }

            case "Deals_Tab": {
                Fragment fragment = new ViewDealsFragment();
                activityCallback.onButtonClick(fragment, false);
                relativeLayoutHeading.setVisibility(View.GONE);
                downarrow.setVisibility(View.GONE);
            }
        }
    }

    private void intializeView() {

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        ubuntuR = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-R.ttf");

        comonObj = Common.getNewInstance(getActivity());

        isLoggedIn = comonObj.getBooleanValue(Constants.isLoggedIn);
        mApprovalStatus = comonObj.getStringValue(Constants.approvalStatus);
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        txt_count = (TextView) getActivity().findViewById(R.id.txt_count);
        mDemoSlider = (SliderLayout) view.findViewById(R.id.slider);
        hLVTop = (RecyclerView) view.findViewById(R.id.showList);
        list_item = (ListView) view.findViewById(R.id.list_item);
        list_item.setScrollingCacheEnabled(false);

        horizontalLayoutManagaerTop
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        startChat = (ImageView) view.findViewById(R.id.startChat);
        showTopIV = (ImageView) view.findViewById(R.id.showTop);
        titleContainer = (RelativeLayout) getActivity().findViewById(R.id.titleContainer);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());


        //margin set by code

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
        screenHeight = displaymetrics.heightPixels;


        int topMarginByW = screenWidth * 10 / 414;
        int sideMarginByW = screenHeight * 14 / 414;
        topMarginByH = screenWidth * 10 / 736;
        sideMarginByH = screenHeight * 14 / 736;

      /*  ViewGroup.MarginLayoutParams layoutParamsMargin = (ViewGroup.MarginLayoutParams) hLVHide.getLayoutParams();
        layoutParamsMargin.setMargins(sideMarginByH, 0, sideMarginByH, topMarginByH);*/

        ViewGroup.MarginLayoutParams layoutParamsMarginTop = (ViewGroup.MarginLayoutParams) hLVTop.getLayoutParams();
        layoutParamsMarginTop.setMargins(sideMarginByH, sideMarginByH, sideMarginByH, topMarginByH);


        txt_count.setVisibility(View.GONE);

/*
        ViewGroup.LayoutParams paramRelative = relative.getLayoutParams();
        paramRelative.height = screenHeight * 150 / 736;
        paramRelative.width = screenWidth;
        relative.setLayoutParams(paramRelative);

        ViewGroup.MarginLayoutParams relativeMargin = (ViewGroup.MarginLayoutParams) relative.getLayoutParams();
        relativeMargin.setMargins(0, 0, 0, topMarginByH);*/

        //list,text_head,view1,view2


        Constants.isAnyHelperEditedorAdded = false;
        Constants.isAnyCallOutCreatedOrRemoved = false;
        Constants.isAnyPollCreatedOrRemoved = false;
        Constants.isAnyClassifiedCreatedOrRemoved = false;
        Constants.isAnyPicAddedOrRemoved = false;
        Constants.isComplainStatusChangedOrEdited = false;
        Constants.positionListEdited = 0;
        SocietyGalleryFragment.parentList.clear();
    }

    private void loadBannerFromApi() {


        String uri = Constants.BaseUrl + "bannerimage";
        try {


            JSONObject json = new JSONObject();
            json.put("RWAID", comonObj.getStringValue("ID"));

            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                saveBanner(response);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void saveBanner(JSONObject response) {

        try {

            String status = response.optString("Status");
            if (status.equalsIgnoreCase("1")) {
                JSONArray array = response.getJSONArray("Banner Image");

                listBanner.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    BannerData data = new BannerData();

                    String ID = obj.optString("ID");
                    data.setID(ID);
                    String RWAID = obj.optString("RWAID");
                    data.setRWAID(ID);
                    String BannerImageUrl = obj.optString("BannerImageUrl");
                    data.setBannerImageUrl(BannerImageUrl);
                    String BannerImageSmallUrl = obj.optString("BannerImageSmallUrl");
                    data.setBannerImageUrl(BannerImageSmallUrl);
                    String DateUploded = obj.optString("DateUploded");
                    data.setDateUploded(DateUploded);
                    String StartDate = obj.optString("StartDate");
                    data.setStartDate(StartDate);
                    String EndDate = obj.optString("EndDate");
                    data.setEndDate(EndDate);
                    String ExternalLink = obj.optString("ExternalLink");
                    data.setExternalLink(ExternalLink);
                    String Addedby = obj.optString("Addedby");
                    data.setAddedby(Addedby);
                    String IsActive = obj.optString("IsActive");
                    data.setIsActive(IsActive);

                    listBanner.add(data);

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateUserInfo() {

        comonObj.showSpinner(getActivity());
        String uri = Constants.BaseUrl + "userdetails";
        try {


            JSONObject json = new JSONObject();

            json.put("ID", userid);

            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONObject object = response.getJSONObject("User");

                                if (isLoggedIn) {
                                    String appStatus = object.optString("ApprovalStatus");
                                    String latitude = object.optString("Latitude");
                                    String longitude = object.optString("Longitude");
                                    String chatUrlForUser = object.optString("ChatUrl");
                                    comonObj.setStringValue(Constants.approvalStatus, appStatus);
                                    comonObj.setStringValue(Constants.LATITUDE, latitude);
                                    comonObj.setStringValue(Constants.LONGITUDE, longitude);
                                    comonObj.setStringValue("chat", chatUrlForUser);
                                    isLoggedIn = comonObj.getBooleanValue(Constants.isLoggedIn);
                                    mApprovalStatus = comonObj.getStringValue(Constants.approvalStatus);

                                    if (object.has("Vehicles")) {
                                        JSONArray arrayVehicle = object.getJSONArray("Vehicles");
                                        listVehicle.clear();
                                        for (int i = 0; i < arrayVehicle.length(); i++) {
                                            WheelerModel wheelerModel = new WheelerModel();
                                            wheelerModel.setID(arrayVehicle.getJSONObject(i).optString("ID"));
                                            wheelerModel.setVehicheType(arrayVehicle.getJSONObject(i).optString("VehicleType"));
                                            wheelerModel.setVehicleNumber(arrayVehicle.getJSONObject(i).optString("VehicleNbr"));
                                            listVehicle.add(wheelerModel);
                                        }

                                    }

                                    Gson gson = new Gson();
                                    String jsonVehicles = gson.toJson(listVehicle);
                                    Log.d("TAG", "jsonCars = " + jsonVehicles);
                                    comonObj.setStringValue(Constants.VehicleList, jsonVehicles);
                                }

                                JSONArray array = object.getJSONArray("Function");
                                Constants.rolesGivenToUser.clear();
                                for (int ro = 0; ro < array.length(); ro++) {
                                    JSONObject roleObj = array.getJSONObject(ro);
                                    String functionName = roleObj.optString("FunctionName");
                                    Constants.rolesGivenToUser.add(functionName);
                                }

                                setTabDataInitially();
                                setTabAndBannerAfterResponse();


                            } catch (JSONException e) {
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

    private void setTabAndBannerAfterResponse() {

        if (Constants.rolesGivenToUser.contains("Society_banner")) {
            if (listBanner.size() == 0) {
                loadBannerFromApi();
            } else {
                fixAutoStart();
            }
        }

        getNotificationCount();

        if (Constants.rolesGivenToUser.contains("Can_Chat")) {
            startChat.setVisibility(View.VISIBLE);
        } else {
            startChat.setVisibility(View.GONE);
        }

    }

    private void setTabDataInitially() {
        tabDataList.clear();
        for (int i = 0; i < tabNames.length; i++) {
            if (Constants.rolesGivenToUser.contains(tabNames[i])) {

                TabData data = new TabData();
                data.setIsTabActive(true);
                data.setTabImage(imageResources[i]);
                data.setTabData("0");
                data.setTabName(tabNames[i]);
                tabDataList.add(data);

            }
        }

        if (tabDataList.size() > 5) {
            tabDataListTop.clear();
            for (int i = 0; i < 5; i++) {
                tabDataListTop.add(tabDataList.get(i));
            }
            tabDataListHide.clear();
            for (int i = 5; i < tabDataList.size(); i++) {
                tabDataListHide.add(tabDataList.get(i));
            }
            tabAdapterTop = new MyRecycleAdapter(getActivity(), tabDataListTop, topMarginByH, sideMarginByH, HomePageOfSociety.this, "login");
            hLVTop.setLayoutManager(horizontalLayoutManagaerTop);
            hLVTop.setAdapter(tabAdapterTop);

        } else {
            ///write code for size less than 5

            tabDataListTop.clear();
            for (int i = 0; i < tabDataList.size(); i++) {
                tabDataListTop.add(tabDataList.get(i));
            }

            tabAdapterTop = new MyRecycleAdapter(getActivity(), tabDataListTop, topMarginByH, sideMarginByH, HomePageOfSociety.this, "login");
            hLVTop.setLayoutManager(horizontalLayoutManagaerTop);
            hLVTop.setAdapter(tabAdapterTop);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onResume() {

        super.onResume();

        instantActionIcon.setVisibility(View.VISIBLE);
        title.setText(society);
        title.setSelected(true);
        title.setTypeface(ubuntuB);
        relativeLayoutHeading.setVisibility(View.VISIBLE);
        downarrow.setVisibility(View.VISIBLE);
        //setSnackBar();

        if (Constants.isAnyNewPostAddedFroMPostAdd) {
            postList.clear();
            Constants.isAnyNewPostAddedFroMPostAdd = false;
            cc = 1;
            userScrolled = false;
            loadingCompleted = false;
            loadPostDataFromAPI("1");
        }

        if (isLoggedIn) {
            MyFirebaseInstanceIDService firebaseInstanceId = new MyFirebaseInstanceIDService();
            firebaseInstanceId.onTokenRefresh();
            token = comonObj.getStringValue("token");

        } else {

        }
    }

    /* @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

     }
 */
    private void connect() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("chat starting...");
        progressDialog.show();
        SendBird.connect(sUserId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), "Unable to start chat....", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                } else {
                    progressDialog.dismiss();
                    Intent intent = new Intent(getActivity(), SendBirdOpenChatActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("channel_url", comonObj.getStringValue("chat"));
                    intent.putExtras(bundle1);
                    startActivity(intent);

                }

                String nickname = mNickname;

                SendBird.updateCurrentUserInfo(nickname, comonObj.getStringValue(Constants.ProfilePic), new SendBird.UserInfoUpdateHandler() {
                    @Override
                    public void onUpdated(SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(), "User info not updated...", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        comonObj.setStringValue("user_id", sUserId);
                        comonObj.setStringValue("nickname", mNickname);


                    }
                });

                if (FirebaseInstanceId.getInstance().getToken() == null) return;

                SendBird.registerPushTokenForCurrentUser(FirebaseInstanceId.getInstance().getToken(), true, new SendBird.RegisterPushTokenWithStatusHandler() {
                    @Override
                    public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        });
    }

    private void getNotificationCount() {

        String uri = Constants.BaseUrl + "allcount";
        try {

            JSONObject json = new JSONObject();
            json.put("RWAID", comonObj.getStringValue(Constants.userRwa));
            String timeLocal = String.valueOf(comonObj.getUnixTime() - (15 * One_day_millis));

            if (comonObj.pref.contains(Constants.notificationLastSeenTime)) {
                json.put("notification", comonObj.getStringValue(Constants.notificationLastSeenTime));
            } else {
                json.put("notification", timeLocal);
                comonObj.setStringValue(Constants.notificationLastSeenTime, timeLocal);
            }

            if (comonObj.pref.contains(Constants.classifiedLastSeenTime)) {
                json.put("classified", comonObj.getStringValue(Constants.classifiedLastSeenTime));

            } else {
                json.put("classified", timeLocal);
                comonObj.setStringValue(Constants.classifiedLastSeenTime, timeLocal);
            }

            if (comonObj.pref.contains(Constants.pollLastSeenTime)) {
                json.put("polling", comonObj.getStringValue(Constants.pollLastSeenTime));
            } else {
                json.put("polling", timeLocal);
                comonObj.setStringValue(Constants.pollLastSeenTime, timeLocal);
            }

            if (comonObj.pref.contains(Constants.complaintLastSeenTime)) {
                json.put("complaints", comonObj.getStringValue(Constants.complaintLastSeenTime));
            } else {
                json.put("complaints", timeLocal);
                comonObj.setStringValue(Constants.complaintLastSeenTime, timeLocal);
            }

            if (comonObj.pref.contains(Constants.neighborsLastSeenTime)) {
                json.put("neighbors", comonObj.getStringValue(Constants.neighborsLastSeenTime));
            } else {
                json.put("neighbors", timeLocal);
                comonObj.setStringValue(Constants.neighborsLastSeenTime, timeLocal);

            }

            if (comonObj.pref.contains(Constants.helperLastSeenTime)) {
                json.put("helper", comonObj.getStringValue(Constants.helperLastSeenTime));
            } else {
                json.put("helper", timeLocal);
                comonObj.setStringValue(Constants.helperLastSeenTime, timeLocal);
            }

            if (comonObj.pref.contains(Constants.calloutLastSeenTime)) {
                json.put("callout", comonObj.getStringValue(Constants.calloutLastSeenTime));
            } else {
                json.put("callout", timeLocal);
                comonObj.setStringValue(Constants.calloutLastSeenTime, timeLocal);
            }

            if (comonObj.pref.contains(Constants.galleryLastSeenTime)) {
                json.put("photogallery", comonObj.getStringValue(Constants.galleryLastSeenTime));
            } else {
                json.put("photogallery", timeLocal);
                comonObj.setStringValue(Constants.galleryLastSeenTime, timeLocal);
            }


            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onResponse(JSONObject response) {

                            notificationCount = response.optString("Notificationcount");
                            pollCount = response.optString("Pollcount");
                            complainCount = response.optString("Complaintcount");
                            classifiedCount = response.optString("Classifiedscount");
                            neighbourCount = response.optString("Neighbors");
                            helperCount = response.optString("Helpercount");
                            galleryCount = response.optString("Photogallery");
                            calloutCount = response.optString("Calloutcount");


                            if (notificationCount.equalsIgnoreCase("0")) {
                                txt_count.setVisibility(View.GONE);
                                txt_count.setText(notificationCount);
                            } else {
                                txt_count.setVisibility(View.VISIBLE);
                                txt_count.setText(notificationCount);
                            }

                            tabDataList.clear();
                            for (int i = 0; i < tabNames.length; i++) {
                                if (Constants.rolesGivenToUser.contains(tabNames[i])) {

                                    TabData data = new TabData();
                                    data.setIsTabActive(true);
                                    data.setTabImage(imageResources[i]);
                                    data.setTabName(tabNames[i]);

                                    if (tabNames[i].equalsIgnoreCase("Polling_Tab")) {
                                        data.setTabData(pollCount);
                                    } else if (tabNames[i].equalsIgnoreCase("Callout_tab")) {
                                        data.setTabData(calloutCount);
                                    } else if (tabNames[i].equalsIgnoreCase("Neighbours_Tab")) {
                                        data.setTabData(neighbourCount);
                                    } else if (tabNames[i].equalsIgnoreCase("Gallery_Tab")) {
                                        data.setTabData(galleryCount);
                                    } else if (tabNames[i].equalsIgnoreCase("Classifieds_Tab")) {
                                        data.setTabData(classifiedCount);
                                    } else if (tabNames[i].equalsIgnoreCase("Complaints_Tab")) {
                                        data.setTabData(complainCount);
                                    } else if (tabNames[i].equalsIgnoreCase("Helpers_Tab")) {
                                        data.setTabData(helperCount);
                                    } else {
                                        data.setTabData("0");
                                    }
                                    tabDataList.add(data);
                                }
                            }


                            comonObj.setStringValue(Constants.notificationCount, notificationCount);
                            comonObj.setStringValue(Constants.pollCount, pollCount);
                            comonObj.setStringValue(Constants.classifiedCount, classifiedCount);
                            comonObj.setStringValue(Constants.galleryCount, galleryCount);
                            comonObj.setStringValue(Constants.calloutCount, calloutCount);
                            comonObj.setStringValue(Constants.neighborsCount, neighbourCount);
                            comonObj.setStringValue(Constants.helperCount, helperCount);
                            comonObj.setStringValue(Constants.complaintCount, complainCount);


                            if (tabDataList.size() > 5) {
                                tabDataListTop.clear();
                                for (int i = 0; i < 5; i++) {
                                    tabDataListTop.add(tabDataList.get(i));
                                }
                                tabDataListHide.clear();
                                for (int i = 5; i < tabDataList.size(); i++) {
                                    tabDataListHide.add(tabDataList.get(i));
                                }
                                tabAdapterTop.notifyDataSetChanged();


                            } else {
                                ///write code for size less than 5

                                tabDataListTop.clear();
                                for (int i = 0; i < tabDataList.size(); i++) {
                                    tabDataListTop.add(tabDataList.get(i));
                                }

                                tabAdapterTop.notifyDataSetChanged();

                            }


                            if (Constants.isAnyNewPostAdded || postList.size() == 0) {
                                postList.clear();
                                cc = 1;
                                if (postList.size() > 0) {
                                    postList.clear();
                                }
                                userScrolled = false;
                                loadingCompleted = false;
                                loadPostDataFromAPI("1");
                            } else {
                                postAdapter.notifyDataSetChanged();
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

    private void loadPostDataFromAPI(final String pageNo) {


        if (cc != 1) {
            list_item.addFooterView(ftView, null, false);
        } else {
            comonObj.showSpinner(getActivity());
        }
        String uri = Constants.MongoBaseUrl + "onlinefind.php";
        try {


            StringRequest req = new StringRequest(Request.Method.POST, uri,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                if (cc == 1) {
                                    if (tabDataList.size() > 5) {
                                        PostModel model1 = new PostModel();
                                        model1.setTabData(true);
                                        postList.add(model1);
                                    } else {
                                        PostModel model1 = new PostModel();
                                        model1.setTabData(false);
                                        postList.add(model1);
                                    }

                                    if (listBanner.size() > 0) {
                                        PostModel model2 = new PostModel();
                                        model2.setIsbanner(true);
                                        postList.add(model2);
                                    } else {
                                        PostModel model2 = new PostModel();
                                        model2.setIsbanner(false);
                                        postList.add(model2);
                                    }
                                }

                                JSONObject responseObj = new JSONObject(response);
                                postDataManipulation(responseObj);
                                Constants.isAnyNewPostAdded = false;

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                comonObj.hideSpinner();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d("error", error.getMessage());

//                            list_item.setVisibility(View.GONE);
                            if (cc == 1) {
                                if (tabDataList.size() > 5) {
                                    PostModel model1 = new PostModel();
                                    model1.setTabData(true);
                                    postList.add(model1);
                                } else {
                                    PostModel model1 = new PostModel();
                                    model1.setTabData(false);
                                    postList.add(model1);
                                }

                                if (listBanner.size() > 0) {
                                    PostModel model2 = new PostModel();
                                    model2.setIsbanner(true);
                                    postList.add(model2);
                                } else {
                                    PostModel model2 = new PostModel();
                                    model2.setIsbanner(false);
                                    postList.add(model2);
                                }
                            }
                            postAdapter = new PostAdapter(getActivity(), postList, comonObj, listBanner, tabDataListHide, HomePageOfSociety.this);
                            list_item.setAdapter(postAdapter);
                            comonObj.hideSpinner();
                        }

                    }) {


                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("RWAID", comonObj.getStringValue("ID"));
                    if (isLoggedIn && mApprovalStatus.equalsIgnoreCase("Y") && comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa))) {
                        params.put("ResidentRWAID", comonObj.getStringValue(Constants.ResidentRWAID));
                        canCommentOrLike = true;
                    } else {
                        canCommentOrLike = false;
                    }
                    params.put("limit", pageNo);
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

    private void postDataManipulation(JSONObject response) {

        if (cc == 1) {
            comonObj.hideSpinner();

        }

        list_item.setVisibility(View.VISIBLE);
        String status = "";
        try {

            status = response.optString("Status");
            if (status.equals("1")) {
                JSONArray responseArray = response.getJSONArray("PostFeed");

                for (int i = 0; i < responseArray.length(); i++) {
                    PostModel model = new PostModel();
                    JSONObject object = responseArray.getJSONObject(i);
                    String ID = object.optString("ID");
                    String Description = "";
                    try {
                        Description = comonObj.funConvertBase64ToString(object.optString("Description"));
                    } catch (Exception e) {
                        Description = object.optString("Description");
                    }
                    String FeedType = object.optString("FeedType");
                    String LikesCount = object.optString("LikesCount");
                    String CommentCount = object.optString("CommentCount");
                    String PostRefID = object.optString("PostRefID");
                    String ResidentRwaID = object.optString("ResidentRwaID");
                    String RWAID = object.optString("RWAID");
                    String ResidentName = object.optString("ResidentName");
                    String ResidentFlatNo = object.optString("ResidentFlatNo");
                    String DateCreated = String.valueOf(object.optInt("DateCreated"));
                    String ProfilePic = object.optString("ProfilePic");
                    String isLike = object.optString("IsLike");
                    String PhotoPath = object.optString("PhotoPath");
                    String userId = object.optString("UserId");
                    ArrayList<String> listOfPhotos = new ArrayList<>();
                    if (PhotoPath.endsWith(".jpg") || PhotoPath.endsWith("png")) {
                        String postImageArray[] = PhotoPath.split(",");
                        for (int kl = 0; kl < postImageArray.length; kl++) {
                            listOfPhotos.add(postImageArray[kl]);
                        }
                    } else {
                        PhotoPath = "";
                        listOfPhotos.clear();
                    }

                    if (!DateCreated.isEmpty()) {
                        DateCreated = comonObj.convertFromUnix4(DateCreated);
                    }

                    model.setID(ID);
                    model.setUserId(userId);
                    model.setDescription(Description);
                    model.setFeedType(FeedType);
                    model.setLikesCount(LikesCount);
                    model.setCommentCount(CommentCount);
                    model.setPostRefID(PostRefID);
                    model.setResidentRwaID(ResidentRwaID);
                    model.setResidentFlatNo(ResidentFlatNo);
                    model.setRWAID(RWAID);
                    model.setResidentName(ResidentName);
                    model.setDateCreated(DateCreated);
                    model.setProfilePic(ProfilePic);
                    model.setIsLike(isLike);
                    model.setPhotoPath(PhotoPath);
                    model.setPhotoPathList(listOfPhotos);

                    postList.add(model);
                }

                if (cc == 1) {
                    postAdapter = new PostAdapter(getActivity(), postList, comonObj, listBanner, tabDataListHide, HomePageOfSociety.this);
                    list_item.setAdapter(postAdapter);
                    TOTAL_COUNT_UPTO_PREVIOUS_LOAD = postList.size();

                } else {
                    if (TOTAL_COUNT_UPTO_PREVIOUS_LOAD == postList.size()) {
                    } else {
                        postAdapter.updateReceiptsList(postList);
                        list_item.requestLayout();
                    }
                    TOTAL_COUNT_UPTO_PREVIOUS_LOAD = postList.size();
                }

            } else {
                if (cc == 1) {
                    postAdapter = new PostAdapter(getActivity(), postList, comonObj, listBanner, tabDataListHide, HomePageOfSociety.this);
                    list_item.setAdapter(postAdapter);
                    TOTAL_COUNT_UPTO_PREVIOUS_LOAD = postList.size();
                }
                loadingCompleted = true;

            }
            if (list_item.getFooterViewsCount() != 0) {
                list_item.removeFooterView(ftView);
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

        userScrolledActionCompleted = true;


    }


/*

    public void doSomethingMemoryIntensive() {

        // Before doing something that requires a lot of memory,
        // check to see whether the device is in a low memory state.
        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();

        System.out.print(memoryInfo.lowMemory);

        if (!memoryInfo.lowMemory) {
            // Do memory intensive work ...
        }
    }

    // Get a MemoryInfo object for the device's current memory status.
    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }
*/


}