package com.sanganan.app.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.sanganan.app.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


/**
 * Created by aksoni on 12/24/2015.
 */
public class Common {
    private Context mcontext;
    private static Common mInstance = null;
    public static boolean debug = true;
    public SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    Dialog loadDialog;

    public Common(Context ctx) {
        this.mcontext = ctx;
        pref = mcontext.getSharedPreferences(Constants.preference, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static synchronized Common getNewInstance(Context ctx) {
        mInstance = new Common(ctx);
        return mInstance;
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);

            inputMethodManager.hideSoftInputFromWindow(activity
                    .getCurrentFocus().getWindowToken(), 0);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }

    public void showSoftKeyboard(Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    public static float getDistance(double startLati, double startLongi,
                                    double goalLati, double goalLongi) {
        Location locationA = new Location("point A");
        locationA.setLatitude(startLati);
        locationA.setLongitude(startLongi);
        Location locationB = new Location("point B");
        locationB.setLatitude(goalLati);
        locationB.setLongitude(goalLongi);
        float distance = (float) (locationA.distanceTo(locationB) * 0.000621371);
        return distance;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mcontext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();

        return activeNetworkInfo != null;
    }

    public void showShortToast(String output) {
        Toast.makeText(mcontext, output, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String output) {
        Toast.makeText(mcontext, output, Toast.LENGTH_LONG).show();
    }

    public boolean getBooleanValue(String key) {
        return pref.getBoolean(key, false);
    }

    public void removeKey(String key) {
        editor.remove(key);
        editor.commit();
    }

    public String getStringValue(String key) {
        String str = pref.getString(key, "");
        if (str.equalsIgnoreCase("null")) {
            str = "";
        }
        return str;
    }

    public void setStringValue(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void setIntValue(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }


    public int getIntValue(String key) {
        return pref.getInt(key, -1);
    }


    public String convertFromUnix1(String unix_time) throws NullPointerException, IllegalArgumentException {
        long time = Long.valueOf(unix_time);
        String result = "";
        Date date = new Date(time * 1000L);
        SimpleDateFormat format = new SimpleDateFormat("d MMM, hh:mm a");
        format.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        result = format.format(date);
        Log.d("date", format.format(date));

        return result;
    }

    public String convertFromUnix2(String unix_time) throws NullPointerException, IllegalArgumentException {
        long time = Long.valueOf(unix_time);
        String result = "";
        Date date = new Date(time * 1000L);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        format.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        result = format.format(date);
        Log.d("date", format.format(date));

        return result;
    }

    public String convertFromUnix3(String unix_time) throws NullPointerException, IllegalArgumentException {
        long time = Long.valueOf(unix_time);
        String result = "";
        Date date = new Date(time * 1000L);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        result = format.format(date);
        Log.d("date", format.format(date));

        return result;
    }

    public String convertFromUnix4(String unix_time) throws NullPointerException, IllegalArgumentException {
        long time = Long.valueOf(unix_time);
        String result = "";
        Date date = new Date(time * 1000L);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a, d MMM, yy");
        format.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        result = format.format(date);
        Log.d("date", format.format(date));

        return result;
    }


    public long getUnixTime() {
        long unixTime = System.currentTimeMillis() / 1000L;
        return unixTime;
    }

    public String funConvertBase64ToString(String base64String) {
        byte[] data = Base64.decode(base64String, Base64.NO_WRAP);
        String text = null;
        try {
            text = new String(data, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public String StringToBase64StringConvertion(String text) {
        String base64Result = "";
        if (text != null) {
            byte[] data = new byte[0];
            try {
                data = text.getBytes("UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            base64Result = Base64.encodeToString(data, Base64.NO_WRAP);
        }
        return base64Result;
    }


    public void showSpinner(Context context) {
        Log.e("BaseActivity", "showSpinner");
        if (loadDialog != null) {
            if (loadDialog.isShowing())
                loadDialog.dismiss();
        }
        loadDialog = new Dialog(context, R.style.TransparentDialogTheme);
        loadDialog.setContentView(R.layout.spinner_rotate);
        loadDialog.setCanceledOnTouchOutside(false);

        ImageView ivLoader = (ImageView) loadDialog.findViewById(R.id.ivSpinner);

        Animation animRotate = AnimationUtils.loadAnimation(context, R.anim.rotate);
        ivLoader.startAnimation(animRotate);
        loadDialog.show();
    }

    public void hideSpinner() {
        if (loadDialog != null && loadDialog.isShowing())
            loadDialog.dismiss();

    }

}
