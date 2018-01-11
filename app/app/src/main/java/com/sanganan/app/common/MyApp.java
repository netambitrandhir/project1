package com.sanganan.app.common;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;


import com.crashlytics.android.Crashlytics;

import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;
import io.fabric.sdk.android.Fabric;


public class MyApp extends MultiDexApplication {
    private static MyApp instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static MyApp getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Fabric.with(this, new Crashlytics());
        new Instabug.Builder(this, "9ad3843282f9f9137ee9be7949194b3b")
                .setInvocationEvent(InstabugInvocationEvent.NONE)
                .build();

    }

}