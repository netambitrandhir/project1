package com.sanganan.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sanganan.app.R;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.interfaces.DrawerLocker;

/**
 * Created by pranav on 3/8/16.
 */
public class SocietyInfoFragment extends Activity {

    View view;
    Common common;
    RequestQueue requestQueue;
    WebView webView;
    TextView society_name;
    Typeface ubuntuB;
    String societyName;

    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.society_maintanance);
        common = Common.getNewInstance(this);
        ubuntuB = Typeface.createFromAsset(this.getAssets(), "Ubuntu-B.ttf");
        society_name = (TextView) findViewById(R.id.society_name);
        society_name.setTypeface(ubuntuB);


        webView = (WebView) findViewById(R.id.webview);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if(common.getBooleanValue(Constants.isLoggedIn)) {
            Bundle logBundleInitial = new Bundle();
            logBundleInitial.putString("society_id", common.getStringValue(Constants.userRwa));
            logBundleInitial.putString("user_id", common.getStringValue(Constants.id));
            mFirebaseAnalytics.logEvent("society_info", logBundleInitial);
        }

        // webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith("mailto:") || url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });


        if (common.getStringValue(Constants.approvalStatus).equalsIgnoreCase("Y") && common.getBooleanValue(Constants.isLoggedIn)) {
            if(common.getStringValue("ID").equals(common.getStringValue(Constants.userRwa))) {
                societyName = common.getStringValue(Constants.userRwaName);
                webView.loadUrl(common.getStringValue(Constants.SocietyPrivateinfo));
                society_name.setText(societyName);
            }else{
                societyName = common.getStringValue("SocietyName");
                webView.loadUrl(common.getStringValue(Constants.SocietyPublicInfo));
                society_name.setText(societyName);
            }
        } else {
            societyName = common.getStringValue("SocietyName");
            webView.loadUrl(common.getStringValue(Constants.SocietyPublicInfo));
            society_name.setText(societyName);
        }


    }


    public void onBackPressed() {
        if (webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();

    }
}
