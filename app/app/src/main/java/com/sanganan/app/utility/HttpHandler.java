package com.sanganan.app.utility;


import android.graphics.Bitmap;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.util.HashMap;
import java.util.Map;

public class HttpHandler {
    private static HttpHandler obj = null;

    HttpClient httpclient;
    BasicCookieStore cookieStore;
    HttpContext httpContext;
    Map<String, Bitmap> mobileIconMap;
    Map<String, Bitmap> mobileBgMap;
    Map<String, Bitmap> mobileSummaryBgMap;

    public Map<String, Bitmap> getMobileSummaryBgMap() {
        return mobileSummaryBgMap;
    }


    public void setMobileSummaryBgMap(Map<String, Bitmap> mobileSummaryBgMap) {
        this.mobileSummaryBgMap = mobileSummaryBgMap;
    }


    private HttpHandler() {
        httpclient = new DefaultHttpClient();
        cookieStore = new BasicCookieStore();
        httpContext = new BasicHttpContext();
        mobileIconMap = new HashMap<String, Bitmap>();
        mobileBgMap = new HashMap<String, Bitmap>();
        mobileSummaryBgMap = new HashMap<String, Bitmap>();
    }


    public static HttpHandler getInstance() {
        if (obj == null) {
            obj = new HttpHandler();
        }

        return obj;
    }

    public HttpClient getHttpClient() {
        return httpclient;
    }

    public BasicCookieStore getBasicCookieStore() {
        return cookieStore;
    }

    public HttpContext getHttpContext() {
        return httpContext;
    }


    public Map<String, Bitmap> getMobileIconMap() {
        return mobileIconMap;
    }


    public void setMobileIconMap(Map<String, Bitmap> mobileIconMap) {
        this.mobileIconMap = mobileIconMap;
    }


    public Map<String, Bitmap> getMobileBgMap() {
        return mobileBgMap;
    }


    public void setMobileBgMap(Map<String, Bitmap> mobileBgMap) {
        this.mobileBgMap = mobileBgMap;
    }


}
