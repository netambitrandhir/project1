package com.sanganan.app.common;

import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by pranav on 29/12/16.
 */

public class DateFormatter {




    private String timeconversion(String tt) {
        long time = Long.valueOf(tt);
        String result = "";
        Date date = new Date(time * 1000L);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm aaa");
        format.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        result = format.format(date);
        Log.d("date", format.format(date));

        return result;
    }

}
