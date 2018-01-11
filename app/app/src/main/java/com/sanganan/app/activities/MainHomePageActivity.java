package com.sanganan.app.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.gson.Gson;
import com.sanganan.app.R;
import com.sanganan.app.adapters.NavDrawerListAdapter;
import com.sanganan.app.common.Alphabets;
import com.sanganan.app.common.AndroidBug5497Workaround;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.firebase.MyFirebaseInstanceIDService;
import com.sanganan.app.fragments.AdminFragment;
import com.sanganan.app.fragments.AlertDialogLogin;
import com.sanganan.app.fragments.BaseFragment;
import com.sanganan.app.fragments.CommunityHelperDetails;
import com.sanganan.app.fragments.FavouriteFragment;
import com.sanganan.app.fragments.HomePageLogOutFragment;
import com.sanganan.app.fragments.HomePageOfSociety;
import com.sanganan.app.fragments.MyProfileFragment;
import com.sanganan.app.fragments.NearbyShops;
import com.sanganan.app.fragments.NotificationDetailsFragment;
import com.sanganan.app.fragments.SignInFragment;
import com.sanganan.app.interfaces.DrawerLocker;
import com.sanganan.app.interfaces.ToolbarListner;

import com.sanganan.app.model.NavDrawerItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;


/**
 * Created by Randhir Patel on 24/5/17.
 */


public class MainHomePageActivity extends ActionBarActivity implements NavigationView.OnNavigationItemSelectedListener, ToolbarListner, DrawerLocker, ViewPagerEx.OnPageChangeListener {
    ActionBarDrawerToggle toggle;
    public static DrawerLayout drawer;
    Toolbar mToolbar;
    ListView listView;
    public android.support.v4.app.FragmentManager fragmentManager;
    private static long backPressed = 0;
    public static NavDrawerListAdapter navDrawerListAdapter;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    Common comonObj;
    public static ArrayList<NavDrawerItem> navDrawerItems;
    private String[] navMenuTitles;
    Typeface ubantuBold, karlaR;
    int[] myImageList = new int[]{R.drawable.home, R.drawable.favourites, R.drawable.admin, R.drawable.my_profile,
            R.drawable.appnotifications,
            R.drawable.login, R.drawable.about_app, R.drawable.t_condition, R.drawable.contact_us, R.drawable.share};
    Fragment fragment;
    SharedPreferences preferences;
    TextView relativeLayoutHeading;
    ImageView downarrow;

    boolean isLoggedIn;
    TextView nukkad, sanganan, appVersion;
    RequestQueue requestQueue;
    String token = "";
    String mApprovalStatus = "";
    TextView txt_count;
    SharedPreferences.OnSharedPreferenceChangeListener spfListner;
    public static boolean no_need_to_refresh = false;


    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(comonObj.getStringValue(Constants.userRwaName));
        Crashlytics.setUserEmail(comonObj.getStringValue(Constants.email));
        Crashlytics.setUserName(comonObj.getStringValue(Constants.FirstName));
        Crashlytics.setString("Phone Number", comonObj.getStringValue(Constants.Phone));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Doing this to open notification when app closed
        Bundle bundle = getIntent().getExtras();
        comonObj = Common.getNewInstance(this);
        isLoggedIn = comonObj.getBooleanValue(Constants.isLoggedIn);
        no_need_to_refresh = false;
        Constants.fromWhere = "Blank";

        //To start logging user info open below code snippet and make sure logUser method data not be null
       /* Fabric.with(this,new Crashlytics());
        logUser();*/


        if (bundle != null) {
            if (bundle.containsKey("ID") && bundle.containsKey("fragmnet")) {
                if (!bundle.getString("fragmnet").equalsIgnoreCase("approval")) {
                    if (bundle.getString("fragmnet").equalsIgnoreCase("newsfeed")) {
                        Intent intent = new Intent(this, NewsFeedNotificationActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else if(bundle.getString("fragmnet").equalsIgnoreCase("gallery")){
                        Intent intent = new Intent(this, PostGalleryImageActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    else  {
                        Intent intent = new Intent(this, AnyNotificationActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {
                    if (comonObj.getStringValue(Constants.approvalStatus).equalsIgnoreCase("N") || comonObj.getStringValue(Constants.approvalStatus).equalsIgnoreCase("P")) {
                        comonObj.setStringValue(Constants.approvalStatus, "Y");
                      //  startActivity(getIntent());
                    } else {
                        comonObj.setStringValue(Constants.approvalStatus, "N");
                      //  startActivity(getIntent());

                    }
                }
            }
        }

        setContentView(R.layout.activity_main);
        //its a android bug and fixed by assist class ...
        // AndroidBug5497Workaround.assistActivity(this);


        ubantuBold = Typeface.createFromAsset(getApplicationContext().getAssets(), "Ubuntu-B.ttf");
        karlaR = Typeface.createFromAsset(getApplicationContext().getAssets(), "Karla-Regular.ttf");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        txt_count = (TextView) findViewById(R.id.txt_count);

        if (comonObj.getStringValue(Constants.notificationCount).equalsIgnoreCase("0")) {
            txt_count.setVisibility(View.GONE);
        } else {
            txt_count.setVisibility(View.VISIBLE);
        }
      /*  txt_count_callout = (TextView)findViewById(R.id.txt_count_callout);
        txt_count_complain = (TextView)findViewById(R.id.txt_count_complain);*/


        preferences = comonObj.pref;


        spfListner = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch (key) {
                    case "notificationCount": {
                        if (comonObj.getStringValue(Constants.notificationCount).equalsIgnoreCase("0")) {
                            txt_count.setVisibility(View.GONE);
                        } else {
                            txt_count.setVisibility(View.VISIBLE);
                            txt_count.setText(comonObj.getStringValue(Constants.notificationCount));
                        }
                        break;
                    }
                    case "ApprovalStatus": {
                        Intent intent = getIntent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    default:
                        break;
                }
            }
        };


        relativeLayoutHeading = (TextView) findViewById(R.id.toolbarTitleHeading);
        downarrow = (ImageView) findViewById(R.id.downarrow);


        String title = comonObj.getStringValue("SocietyName");


        TextView tv1 = (TextView) findViewById(R.id.toolbar_title);
        tv1.setLines(1);
        tv1.setHorizontallyScrolling(true);
        tv1.setMarqueeRepeatLimit(-1);
        tv1.setSelected(true);
        tv1.setTypeface(ubantuBold);
        tv1.setText(title);

        if(title.length()>16){
            tv1.setWidth(250);
        }

        mApprovalStatus = comonObj.getStringValue(Constants.approvalStatus);

        requestQueue = VolleySingleton.getInstance(MainHomePageActivity.this).getRequestQueue();


        listView = (ListView) findViewById(R.id.list_slidermenu);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.post(new Runnable() {
            @Override
            public void run() {
                // To display hamburger icon in toolbar
                toggle.syncState();
            }
        });
        drawer.setDrawerListener(toggle);


        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);


        navDrawerItems = new ArrayList<NavDrawerItem>();
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], myImageList[0]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], myImageList[1]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], myImageList[2]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], myImageList[3]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], myImageList[4]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], myImageList[5]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], myImageList[6]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], myImageList[7]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], myImageList[8]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], myImageList[9]));

        if (isLoggedIn) {
            navDrawerItems.remove(5);
            navDrawerItems.add(5, new NavDrawerItem(navMenuTitles[10], myImageList[5]));
        } else {
            navDrawerItems.remove(5);
            navDrawerItems.add(5, new NavDrawerItem(navMenuTitles[5], myImageList[5]));
        }


        navDrawerListAdapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        listView.setAdapter(navDrawerListAdapter); ///////////////////////////////////////
        setListViewHeightBasedOnItems(listView);


        drawer.post(new Runnable() {
            @Override
            public void run() {

                // ActionBarDrawerToggle.syncState();
                toggle.syncState();
            }
        });
        drawer.setDrawerListener(toggle);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(navDrawerItems.get(position).title);
            }
        });

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (navDrawerItems.size() == 10 && !no_need_to_refresh) {
                    if (!checkIsAdmin() && !haveFavourites()) {
                        navDrawerItems.remove(1);
                        navDrawerItems.remove(1);
                    } else if (!checkIsAdmin()) {
                        navDrawerItems.remove(2);
                    } else if (!haveFavourites()) {
                        navDrawerItems.remove(1);
                    }
                    no_need_to_refresh = true;
                    navDrawerListAdapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
                    listView.setAdapter(navDrawerListAdapter);
                    setListViewHeightBasedOnItems(listView);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //comonObj.showShortToast("Drawer is opened");
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header = navigationView.getHeaderView(0);


        nukkad = (TextView) header.findViewById(R.id.header1);
        sanganan = (TextView) header.findViewById(R.id.header2);
        appVersion = (TextView) header.findViewById(R.id.header3);

        nukkad.setTypeface(ubantuBold);
        sanganan.setTypeface(karlaR);
        appVersion.setTypeface(karlaR);

        PackageInfo pInfo = null;
        String version = "";

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            appVersion.setText("App Version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            appVersion.setText("App Version 2.7");
        }


            if (isLoggedIn && mApprovalStatus.equalsIgnoreCase("Y") && comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa)) ) {
            fragment = new HomePageOfSociety();
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction tx1 = fragmentManager.beginTransaction();
            tx1.replace(R.id.container_body, fragment);
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            tx1.addToBackStack("Home");
            tx1.commit();
        } else {
            fragment = new HomePageLogOutFragment();
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction tx1 = fragmentManager.beginTransaction();
            tx1.replace(R.id.container_body, fragment);
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            tx1.addToBackStack("Home");
            tx1.commit();
        }
        Alphabets alphabets = new Alphabets();///neede to initialize variables and drawables


    }

    private boolean haveFavourites() {

        boolean canKeepFavourites = false;

        if (Constants.rolesGivenToUser.contains("Helpers_Tab") || Constants.rolesGivenToUser.contains("Nearby_Tab")) {
            canKeepFavourites = true;
        }


        return canKeepFavourites;

    }

    public boolean checkIsAdmin() {
        boolean isAdmin = false;

        if (Constants.rolesGivenToUser.contains("Can_Approve_Member") || Constants.rolesGivenToUser.contains("Can_Send_Notification")) {
            isAdmin = true;
        }

        return isAdmin;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDrawerEnabled(true);
        if (isLoggedIn) {
            MyFirebaseInstanceIDService firebaseInstanceId = new MyFirebaseInstanceIDService();
            firebaseInstanceId.onTokenRefresh();
            token = comonObj.getStringValue("token");

        }
        preferences.registerOnSharedPreferenceChangeListener(spfListner);
        mApprovalStatus = comonObj.getStringValue(Constants.approvalStatus);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.toogleBtn) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 9001) {
            onBackPressed();
            Fragment fragmentShop = new NearbyShops();
            onButtonClick(fragmentShop, false);
        } else if (requestCode == CommunityHelperDetails.REQUEST_CODE_EDIT_HELPER) {
            if (resultCode == 123) {
                onBackPressed();
                Fragment fragment = new CommunityHelperDetails();
                Bundle bundle = data.getExtras();
                fragment.setArguments(bundle);
                onButtonClick(fragment, false);
            } else if (resultCode == 1234) {
                onBackPressed();
            }
        }
    }


    @Override
    public void onButtonClick(Fragment newfragment, Boolean isCommingBack) {

        Common.hideSoftKeyboard(this);
        fragment = (BaseFragment) newfragment;
        fragmentManager = getSupportFragmentManager();
        if (isCommingBack) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);

            try {
                fragmentTransaction.commit();
            } catch (IllegalStateException ignored) {
            }
        } else {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit,
                    R.anim.slide_in_left, R.anim.slide_in_right);
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());

            try {
                fragmentTransaction.commit();
            } catch (IllegalStateException ignored) {
            }

        }
    }


    @Override
    public void onButtonClickNoBack(Fragment newfragment) {
        fragment = (BaseFragment) newfragment;
        fragmentManager = getSupportFragmentManager();
        fragment = (BaseFragment) newfragment;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit,
                R.anim.slide_in_left, R.anim.slide_in_right);
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }


    private void selectItem(String title) {

        mApprovalStatus = comonObj.getStringValue(Constants.approvalStatus);
        closeDrawer();
        switch (title) {

            case "Home": {

                if (isLoggedIn && mApprovalStatus.equalsIgnoreCase("Y") && comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa)) ) {
                    fragment = new HomePageOfSociety();
                    fragmentManager = getSupportFragmentManager();
                    FragmentTransaction tx1 = fragmentManager.beginTransaction();
                    tx1.replace(R.id.container_body, fragment);
                    tx1.commit();
                    relativeLayoutHeading.setVisibility(View.GONE);
                    downarrow.setVisibility(View.GONE);
                }else{
                    fragment = new HomePageLogOutFragment();
                    fragmentManager = getSupportFragmentManager();
                    FragmentTransaction tx1 = fragmentManager.beginTransaction();
                    tx1.replace(R.id.container_body, fragment);
                    tx1.addToBackStack("Home");
                    tx1.commit();
                    relativeLayoutHeading.setVisibility(View.GONE);
                    downarrow.setVisibility(View.GONE);
                }
                break;
            }

            case "Favourites": {
                if (isLoggedIn & comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa)) & mApprovalStatus.equalsIgnoreCase("Y")) {
                    fragment = new FavouriteFragment();
                    fragmentManager = getSupportFragmentManager();
                    FragmentTransaction tx1 = fragmentManager.beginTransaction();
                    tx1.replace(R.id.container_body, fragment);
                    tx1.addToBackStack("Favourite");
                    tx1.commit();
                    relativeLayoutHeading.setVisibility(View.GONE);
                    downarrow.setVisibility(View.GONE);
                }else{
                    AlertDialogLogin dialog = new AlertDialogLogin();
                    dialog.setRetainInstance(true);
                    dialog.show(getSupportFragmentManager(), "tag_alert_login");
                }

                break;
            }

            case "Admin": {

                if (isLoggedIn & comonObj.getStringValue("ID").equalsIgnoreCase(comonObj.getStringValue(Constants.userRwa)) & mApprovalStatus.equalsIgnoreCase("Y")) {
                    fragment = new AdminFragment();
                    fragmentManager = getSupportFragmentManager();
                    FragmentTransaction tx1 = fragmentManager.beginTransaction();
                    tx1.replace(R.id.container_body, fragment);
                    tx1.addToBackStack("Admin");
                    tx1.commit();
                    relativeLayoutHeading.setVisibility(View.GONE);
                    downarrow.setVisibility(View.GONE);
                } else {
                    closeDrawer();

                }
                break;
            }

            case "My Profile": {
                if (isLoggedIn) {
                    fragment = new MyProfileFragment();
                    fragmentManager = getSupportFragmentManager();
                    FragmentTransaction tx1 = fragmentManager.beginTransaction();
                    tx1.replace(R.id.container_body, fragment);
                    tx1.addToBackStack("profile");
                    tx1.commit();
                    relativeLayoutHeading.setVisibility(View.GONE);
                    downarrow.setVisibility(View.GONE);
                }else{
                    AlertDialogLogin dialog = new AlertDialogLogin();
                    dialog.setRetainInstance(true);
                    dialog.show(getSupportFragmentManager(), "tag_alert_login");
                }

                break;
            }

            case "App Feeds": {

                if (isLoggedIn && mApprovalStatus.equalsIgnoreCase("Y")) {
                    Intent intent = new Intent(MainHomePageActivity.this, NewsFeedNotificationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fragmnet", "listNotificationNFeed");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    AlertDialogLogin dialog = new AlertDialogLogin();
                    dialog.setRetainInstance(true);
                    dialog.show(getSupportFragmentManager(), "tag_alert_login");
                }

                break;
            }

            case "Login/Signup": {

                Intent intent = new Intent(this, SignInFragment.class);
                startActivity(intent);

                break;
            }

            case "Logout": {
                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                preferences = MainHomePageActivity.this.getSharedPreferences(Constants.preference, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean(Constants.isLoggedIn, false);

                                editor.remove(Constants.id);
                                editor.remove(Constants.Phone);
                                editor.remove(Constants.email);
                                editor.remove(Constants.Password);
                                editor.remove(Constants.FirstName);
                                editor.remove(Constants.MiddleName);
                                editor.remove(Constants.LastName);
                                editor.remove(Constants.ProfilePic);
                                editor.remove(Constants.Gender);
                                editor.remove(Constants.Occupation);
                                editor.remove(Constants.AddedOn);
                                editor.remove(Constants.isActive);
                                editor.remove(Constants.userRwa);
                                editor.remove(Constants.ResidentRWAID);
                                editor.remove(Constants.flatId);
                                editor.remove(Constants.flatNumber);
                                editor.remove(Constants.ResidentRWAID);
                                editor.remove(Constants.userRwaName);
                                editor.remove(Constants.isPhonePublic);
                                editor.remove(Constants.aboutMe);
                                editor.remove(Constants.VehicleList);

                                editor.remove(Constants.notificationCount);
                                editor.remove(Constants.pollCount);
                                editor.remove(Constants.complaintCount);
                                editor.remove(Constants.classifiedCount);
                                editor.remove(Constants.calloutCount);
                                editor.remove(Constants.galleryCount);
                                editor.remove(Constants.neighborsCount);
                                editor.remove(Constants.helperCount);


                                editor.remove(Constants.notificationLastSeenTime);
                                editor.remove(Constants.pollLastSeenTime);
                                editor.remove(Constants.complaintLastSeenTime);
                                editor.remove(Constants.classifiedLastSeenTime);
                                editor.remove(Constants.calloutLastSeenTime);
                                editor.remove(Constants.galleryLastSeenTime);
                                editor.remove(Constants.neighborsLastSeenTime);
                                editor.remove(Constants.helperLastSeenTime);
                                editor.remove("Count");
                                editor.remove("token");
                                token = "";
                                callApi();
                                boolean b = editor.commit();
                                if (b) {
                                    navDrawerItems.remove(4);
                                    navDrawerItems.add(4, new NavDrawerItem(navMenuTitles[4], myImageList[4]));

                                }
                                navDrawerListAdapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
                                listView.setAdapter(navDrawerListAdapter); ///////////////////////////////////////
                                setListViewHeightBasedOnItems(listView);
                                isLoggedIn = false;
                                closeDrawer();
                                finish();
                                Intent intent = new Intent(MainHomePageActivity.this, MainHomePageActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            }

            case "About App": {
                Intent intent = new Intent(this, AboutApp.class);
                startActivity(intent);
                break;

            }
            case "Terms & Conditions": {
                Intent intent = new Intent(this, TermsANDCondition.class);
                startActivity(intent);
                break;
            }
            case "Contact Us": {
                Intent intent = new Intent(this, ContactUs.class);
                startActivity(intent);
                break;
            }

            case "Share App": {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Download #mynukad app from apple or google store,please click this link on mobile device: http://mynukad.com/mynukaddownload.html");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                break;
            }


        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawer.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(enabled);
    }

    @Override
    public void onBackPressed() {

        fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 1) {
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager()
                    .getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);

            fragmentManager.popBackStack();

        } else if (fragmentManager.getBackStackEntryCount() == 1) {

            if (backPressed + 2000 > System.currentTimeMillis()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
                finish();
            } else {
                new Common(this).showShortToast("Press one more time to go back");
                backPressed = System.currentTimeMillis();
            }


        }

    }

    public void openDrawer() {
        drawer.openDrawer(Gravity.RIGHT);
    }

    public void closeDrawer() {
        drawer.closeDrawer(Gravity.LEFT);
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int numberOfItems = listAdapter.getCount();
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + 30;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;
        } else {
            return false;
        }
    }

    private void callApi() {

        String url = Constants.BaseUrl + "devicetoken";
        try {
            JSONObject json = new JSONObject();
            json.put("UserID", comonObj.getStringValue(Constants.id));
            json.put("DeviceUniqueID", token);
            json.put("DeviceType", "A");

            JsonObjectRequest req = new JsonObjectRequest(url, json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String message = response.optString("Message");
                                String Status = response.optString("Status");
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
    protected void onPause() {
        super.onPause();
        preferences.unregisterOnSharedPreferenceChangeListener(spfListner);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
