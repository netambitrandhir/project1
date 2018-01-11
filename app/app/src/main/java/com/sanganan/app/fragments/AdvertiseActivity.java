package com.sanganan.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.sanganan.app.R;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;

/**
 * Created by pranav on 25/1/17.
 */

public class AdvertiseActivity extends Activity {

    View view;
    Common common;
    RequestQueue requestQueue;
    WebView webView;
    TextView society_name;
    Typeface ubuntuB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.society_maintanance);
        common = Common.getNewInstance(this);

        String url = getIntent().getStringExtra("url");

        ubuntuB = Typeface.createFromAsset(this.getAssets(), "Ubuntu-B.ttf");

        society_name = (TextView) findViewById(R.id.society_name);
        society_name.setText("");
        society_name.setTypeface(ubuntuB);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
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

        webView.loadUrl(url);


    }


    public void onBackPressed() {
        if (webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();

    }
}
