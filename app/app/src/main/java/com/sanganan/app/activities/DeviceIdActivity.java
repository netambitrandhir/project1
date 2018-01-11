package com.sanganan.app.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sanganan.app.R;

import java.util.ArrayList;

/**
 * Created by root on 27/2/17.
 */

public class DeviceIdActivity extends Activity {


    String permissionArray[] = {Manifest.permission.READ_PHONE_STATE};

    private static String DEVICE_ID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndHandlePermission();
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            DEVICE_ID = telephonyManager.getDeviceId();
            Log.d("DEVICE_ID",DEVICE_ID);

        }


    }


    private void checkAndHandlePermission() {

        int j = 1;
        ArrayList<String> listPermissionDenied = new ArrayList<String>();
        for (int i = 0; i < permissionArray.length; i++) {
            if (ActivityCompat.checkSelfPermission(this, permissionArray[i]) != PackageManager.PERMISSION_GRANTED) {

                listPermissionDenied.add(permissionArray[i]);
                j = j * 0;

            }
        }
        if (j == 0) {
            String array[] = new String[listPermissionDenied.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = listPermissionDenied.get(i);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(array, 4);
            }
        } else {
            //DO what ever you want to if have permission
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            DEVICE_ID = telephonyManager.getDeviceId();
            Log.d("DEVICE_ID",DEVICE_ID);

        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 4) {
            int k = 1;
            int l = 0;
            for (int i = 0; i < grantResults.length; i++) {
                k = k * grantResults[i];
                l = l + grantResults[i];
            }
            if (k != 0 && k != -1 && l == permissionArray.length) {

                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                DEVICE_ID = telephonyManager.getDeviceId();
                Log.d("DEVICE_ID",DEVICE_ID);

            } else {
                checkAndHandlePermission();
                //showShortToast "You need to give all permissions"
            }
        }
    }
}
