package com.sanganan.app.fragments;

import android.annotation.TargetApi;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;

import android.media.Image;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
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


/**
 * Created by Randhir Patel on 24/5/17.
 */


public class HomePageLogOutFragment extends BaseFragment implements BaseSliderView.OnSliderClickListener {

    private static final String appId = "EBCD2CB7-F5CC-4F6C-8EF4-86548DCCDE38"; /* Sample SendBird Application */
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

    TextView title, society_feed_heading, textMessage;
    View view1, view2;
    String society;
    TextView relativeLayoutHeading;
    ImageView downarrow, createPost;
    boolean isLoggedIn;
    String mApprovalStatus, sUserId, mNickname;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    TextView txt_count;
    ImageView startChat;
    RelativeLayout titleContainer;
    ImageView imageSnackbar;
    ArrayList<WheelerModel> listVehicle = new ArrayList<>();

    String token = "";
    private FirebaseAnalytics mFirebaseAnalytics;
    ArrayList<BannerData> listBanner = new ArrayList<>();
    SliderLayout mDemoSlider;

    private RecyclerView hLVTop;
    private RecyclerView hLVHide;
    ArrayList<TabData> tabDataList = new ArrayList<>();
    ArrayList<TabData> tabDataListTop = new ArrayList<>();
    ArrayList<TabData> tabDataListHide = new ArrayList<>();
    MyRecycleAdapter tabAdapterTop;
    MyRecycleAdapter tabAdapterHide;


    int topMarginByH;
    int sideMarginByH;
    int screenWidth;
    int screenHeight;
    private LinearLayoutManager horizontalLayoutManagaerTop;
    private LinearLayoutManager horizontalLayoutManagaerHide;
    String userid;
    RelativeLayout relative;
    ImageView posthideContainer;
    public View ftView;
    public boolean isLoading = false;


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
        view = inflater.inflate(R.layout.homepage_society, container, false);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);


        intializeView();


        if (isLoggedIn && mApprovalStatus.equalsIgnoreCase("Y") && comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa))) {
            userid = comonObj.getStringValue(Constants.id);
            sUserId = comonObj.getStringValue(Constants.id);
            mNickname = comonObj.getStringValue(Constants.FirstName);
            posthideContainer.setVisibility(View.GONE);

        } else {
            userid = comonObj.getStringValue(Constants.id);
            posthideContainer.setVisibility(View.VISIBLE);
        }


        if (comonObj.isNetworkAvailable()) {
            if (tabDataList.size() == 0) {
                updateUserInfo();
            } else {
                setSnackBar();
            }


        } else {
            comonObj.showShortToast("no internet");
        }

        final Bundle bundle = getArguments();

        ((DrawerLocker) getActivity()).setDrawerEnabled(true);
        ((MainHomePageActivity) getActivity()).closeDrawer();

        title = (TextView) getActivity().findViewById(R.id.toolbar_title);

        society = comonObj.getStringValue("SocietyName");

        title.setText(society);
        title.setLines(1);
        title.setHorizontallyScrolling(true);
        title.setMarqueeRepeatLimit(-1);
        //  At this point the view is not scrolling!
        //  Get scrolling to start
        title.setSelected(true);

        relativeLayoutHeading = (TextView) getActivity().findViewById(R.id.toolbarTitleHeading);
        downarrow = (ImageView) getActivity().findViewById(R.id.downarrow);


        TextView titleDone = (TextView) getActivity().findViewById(R.id.title_done);
        titleDone.setVisibility(View.GONE);

        ImageView addNew = (ImageView) getActivity().findViewById(R.id.addnew);
        addNew.setVisibility(View.GONE);


        instantActionIcon = (ImageView) getActivity().findViewById(R.id.alert);
        instantActionIcon.setImageResource(R.drawable.notification_new);


        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggedIn && mApprovalStatus.equalsIgnoreCase("Y") && comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa))) {
                    Intent intent = new Intent(getActivity(), UpdateStatusActivity.class);
                    startActivityForResult(intent, 18909);
                } else {
                    AlertDialogLogin dialog = new AlertDialogLogin();
                    dialog.setRetainInstance(true);
                    dialog.show(getFragmentManager(), "tag_alert_login");
                }
            }
        });


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

/*

        hLVTop.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        onItemListClicked(tabDataListTop.get(position).getTabName());
                    }
                })
        );

        hLVHide.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        onItemListClicked(tabDataListHide.get(position).getTabName());
                    }
                })
        );
*/


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
                AlertDialogLogin dialog = new AlertDialogLogin();
                dialog.setRetainInstance(true);
                dialog.show(getFragmentManager(), "tag_alert_login");
            }
        }
    }

    private void intializeView() {


        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = layoutInflater.inflate(R.layout.lazy_loading_footer_view, null);

        ubuntuB = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        ubuntuR = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-R.ttf");

        comonObj = Common.getNewInstance(getActivity());
        posthideContainer = (ImageView) view.findViewById(R.id.posthideContainer);
        textMessage = (TextView) view.findViewById(R.id.textMessage);
        isLoggedIn = comonObj.getBooleanValue(Constants.isLoggedIn);
        mApprovalStatus = comonObj.getStringValue(Constants.approvalStatus);
        requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();
        txt_count = (TextView) getActivity().findViewById(R.id.txt_count);
        mDemoSlider = (SliderLayout) view.findViewById(R.id.slider);
        hLVTop = (RecyclerView) view.findViewById(R.id.showList);
        hLVHide = (RecyclerView) view.findViewById(R.id.hideList);

        society_feed_heading = (TextView) view.findViewById(R.id.society_feed_heading);
        society_feed_heading.setTypeface(ubuntuR);
        view1 = view.findViewById(R.id.view1);
        view2 = view.findViewById(R.id.view2);
        createPost = (ImageView) view.findViewById(R.id.createPost);

        horizontalLayoutManagaerTop
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        horizontalLayoutManagaerHide
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        startChat = (ImageView) view.findViewById(R.id.startChat);
        titleContainer = (RelativeLayout) getActivity().findViewById(R.id.titleContainer);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        imageSnackbar = (ImageView) view.findViewById(R.id.image_container);
        relative = (RelativeLayout) view.findViewById(R.id.bannerimagelayout);

        //margin set by code

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = displaymetrics.widthPixels;
        screenHeight = displaymetrics.heightPixels;


        int topMarginByW = screenWidth * 10 / 414;
        int sideMarginByW = screenHeight * 14 / 414;
        topMarginByH = screenWidth * 10 / 736;
        sideMarginByH = screenHeight * 14 / 736;

        ViewGroup.MarginLayoutParams layoutParamsMargin = (ViewGroup.MarginLayoutParams) hLVHide.getLayoutParams();
        layoutParamsMargin.setMargins(sideMarginByH, 0, sideMarginByH, topMarginByH);

        ViewGroup.MarginLayoutParams layoutParamsMarginTop = (ViewGroup.MarginLayoutParams) hLVTop.getLayoutParams();
        layoutParamsMarginTop.setMargins(sideMarginByH, sideMarginByH, sideMarginByH, topMarginByH);


        txt_count.setVisibility(View.GONE);


        ViewGroup.LayoutParams paramRelative = relative.getLayoutParams();
        paramRelative.height = screenHeight * 150 / 736;
        paramRelative.width = screenWidth;
        relative.setLayoutParams(paramRelative);

        ViewGroup.MarginLayoutParams relativeMargin = (ViewGroup.MarginLayoutParams) relative.getLayoutParams();
        relativeMargin.setMargins(0, 0, 0, topMarginByH);

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


            } else {
                // comonObj.showShortToast("No banner image");
            }

            setBannerAfterListLoadedFromApi();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setBannerAfterListLoadedFromApi() {

        HashMap<String, BannerData> url_maps = new HashMap<String, BannerData>();

        for (int k = 0; k < listBanner.size(); k++) {
            url_maps.put("banner" + k, listBanner.get(k));
        }


        for (String name : url_maps.keySet()) {
            DefaultSliderView textSliderView = new DefaultSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView.description(" ").image(url_maps.get(name)
                    .getBannerImageUrl())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra", name);
            textSliderView.getBundle().putString("urlELink", url_maps.get(name).getExternalLink());
            textSliderView.getBundle().putString("url", url_maps.get(name).getBannerImageUrl());

            mDemoSlider.addSlider(textSliderView);
        }

        if (url_maps.size() > 1) {
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mDemoSlider.setDuration(5000);
            mDemoSlider.addOnPageChangeListener((ViewPagerEx.OnPageChangeListener) getActivity());

        } else {
            mDemoSlider.stopAutoCycle();
            mDemoSlider.setPagerTransformer(false, new BaseTransformer() {
                @Override
                protected void onTransform(View view, float v) {

                }
            });
        }


        fixAutoStart();
    }

    private void updateUserInfo() {
        comonObj.showSpinner(getActivity());

        String uri = Constants.BaseUrl + "userdetails";
        try {


            JSONObject json = new JSONObject();
            if (isLoggedIn) {
                json.put("ID", userid);
            } else {
                json.put("RWAID", comonObj.getStringValue("ID"));
            }
            JsonObjectRequest req = new JsonObjectRequest(uri, json,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                JSONObject object = response.getJSONObject("User");
                                comonObj.hideSpinner();

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

                                setSnackBar();

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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setSnackBar() {

        String approvalStatus = comonObj.getStringValue(Constants.approvalStatus);


        if (isLoggedIn && approvalStatus.equalsIgnoreCase("Y")) {
            imageSnackbar.setVisibility(View.GONE);
        } else if (!isLoggedIn) {
            imageSnackbar.setVisibility(View.VISIBLE);
            imageSnackbar.setImageResource(R.drawable.bottom_info);
            imageSnackbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SignInFragment.class);
                    startActivity(intent);
                }
            });
        } else if (approvalStatus.equals("N") || approvalStatus.equals("P")) {
            imageSnackbar.setVisibility(View.VISIBLE);
            imageSnackbar.setImageResource(R.drawable.bottom_approval);
            imageSnackbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ApprovalPendingFragment.class);
                    startActivity(intent);
                }
            });
        }

        setTabAndBannerAfterResponse();
    }

    private void setTabAndBannerAfterResponse() {

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
            tabAdapterTop = new MyRecycleAdapter(getActivity(), tabDataListTop, topMarginByH, sideMarginByH, HomePageLogOutFragment.this, "logout");
            hLVTop.setLayoutManager(horizontalLayoutManagaerTop);
            hLVTop.setAdapter(tabAdapterTop);
            // hLVTop.addItemDecoration(new DividerItemDecoration(getActivity()));


            tabAdapterHide = new MyRecycleAdapter(getActivity(), tabDataListHide, topMarginByH, sideMarginByH, HomePageLogOutFragment.this, "logout");
            hLVHide.setLayoutManager(horizontalLayoutManagaerHide);
            hLVHide.setAdapter(tabAdapterHide);
            // hLVHide.addItemDecoration(new DividerItemDecoration(getActivity()));

        } else {
            ///write code for size less than 5
            tabDataListTop.clear();
            for (int i = 0; i < tabDataList.size(); i++) {
                tabDataListTop.add(tabDataList.get(i));
            }
            tabAdapterTop = new MyRecycleAdapter(getActivity(), tabDataListTop, topMarginByH, sideMarginByH, HomePageLogOutFragment.this, "logout");
            hLVTop.setLayoutManager(horizontalLayoutManagaerTop);
            hLVTop.setAdapter(tabAdapterTop);

            hLVHide.setVisibility(View.GONE);
        }


        if (Constants.rolesGivenToUser.contains("Society_banner")) {
            relative.setVisibility(View.VISIBLE);
            if (listBanner.size() == 0) {
                loadBannerFromApi();
            } else {
                setBannerAfterListLoadedFromApi();
            }
        } else {
            relative.setVisibility(View.GONE);
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
      /*  if (Constants.isAnyNewPostAdded) {
            postList.clear();
            cc = 1;
            userScrolledActionCompleted = false;
            loadingCompleted = false;
            loadPostDataFromAPI("1");
        }*/

        if (isLoggedIn) {
            MyFirebaseInstanceIDService firebaseInstanceId = new MyFirebaseInstanceIDService();
            firebaseInstanceId.onTokenRefresh();
            token = comonObj.getStringValue("token");

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

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


    @Override
    public void onSliderClick(BaseSliderView slider) {


        Object obj = new Object();

        Bundle bundle = slider.getBundle();

        if (isLoggedIn) {
            Bundle logBundleInitial = new Bundle();
            logBundleInitial.putString("banner_url", bundle.getString("url"));
            logBundleInitial.putString("user_id", comonObj.getStringValue(Constants.id));
            mFirebaseAnalytics.logEvent("banner_clicked", logBundleInitial);
        }
        Intent intent = new Intent(getActivity(), AdvertiseActivity.class);
        intent.putExtra("url", bundle.getString("urlELink"));
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDemoSlider.stopAutoCycle();
    }

}


