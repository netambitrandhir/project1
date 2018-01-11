package com.sanganan.app.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.sanganan.app.R;
import com.sanganan.app.activities.AnyNotificationActivity;
import com.sanganan.app.activities.MainHomePageActivity;
import com.sanganan.app.activities.NewsFeedNotificationActivity;
import com.sanganan.app.activities.PostGalleryImageActivity;
import com.sanganan.app.activities.StartSearchPage;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.fragments.HomePageOfSociety;
import com.sanganan.app.fragments.PollingFragment;
import com.sanganan.app.fragments.StartAPoll;
import com.sanganan.app.sample.SendBirdOpenChatActivity;

import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

/**
 * Created by Belal on 5/27/2016.
 */


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Common common;
    Intent intent;

    String calloutN, complainN, notificationN;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "ID: " + remoteMessage.getMessageId());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        Log.e("JSON_OBJECT", object.toString());

        common = Common.getNewInstance(getApplicationContext());

        String UrHd2 = object.optString("UrHd2");
        String ID = "";
        if (!UrHd2.equalsIgnoreCase("mynukad " + common.getStringValue(Constants.userRwaName) + " chat")) {
            if (object.has("body")) {
                ID = object.optString("body");
            }
        }


        calloutN = common.getStringValue(Constants.calloutCount);
        complainN = common.getStringValue(Constants.complaintCount);
        notificationN = common.getStringValue(Constants.notificationCount);

        sendNotification(remoteMessage.getNotification().getBody(), ID, UrHd2);
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody, String id, String title) {


        Bundle bundle = new Bundle();
        bundle.putString("ID", id);

        if (title.equalsIgnoreCase("Society Notification")) {
            intent = new Intent(this, AnyNotificationActivity.class);
            bundle.putString("fragmnet", "notification");
            int i = Integer.parseInt(notificationN);
            i = i + 1;
            common.setStringValue(Constants.notificationCount, String.valueOf(i));
        } else if (title.equalsIgnoreCase("Approval Status")) {
            intent = new Intent(this, StartSearchPage.class);
            bundle.putString("fragmnet", "approval");
            Constants.fromWhere = "Blank";
            // common.setStringValue(Constants.approvalStatus,"Y");
        } else if (title.equalsIgnoreCase("Society CallOuts")) {
            intent = new Intent(this, AnyNotificationActivity.class);
            bundle.putString("fragmnet", "callout");
            int i = Integer.parseInt(calloutN);
            i = i + 1;
            common.setStringValue(Constants.calloutCount, String.valueOf(i));
        } else if (title.equalsIgnoreCase("Complaint Status")) {
            intent = new Intent(this, AnyNotificationActivity.class);
            bundle.putString("fragmnet", "complaint");
            int i = Integer.parseInt(complainN);
            i = i + 1;
            common.setStringValue(Constants.complaintCount, String.valueOf(i));
        } else if (title.equalsIgnoreCase("mynukad " + common.getStringValue(Constants.userRwaName) + " chat")) {
            intent = new Intent(this, AnyNotificationActivity.class);
            bundle.putString("fragmnet", "openchat");
        } else if (title.equalsIgnoreCase("mynukad helper attendance")) {
            intent = new Intent(this, AnyNotificationActivity.class);
            bundle.putString("fragmnet", "attendence");
        } else if (title.equalsIgnoreCase("mynukad Classified")) {
            intent = new Intent(this, AnyNotificationActivity.class);
            bundle.putString("fragmnet", "classified");
        } else if (title.equalsIgnoreCase("Society Photo Gallery")) {
            intent = new Intent(this, PostGalleryImageActivity.class);
            bundle.putString("fragmnet", "gallery");
        } else if (title.equalsIgnoreCase("mynukad Poll")) {
            intent = new Intent(this, AnyNotificationActivity.class);
            bundle.putString("fragmnet", "polling");
        } else if (title.equalsIgnoreCase("New Registration")) {
            intent = new Intent(this, AnyNotificationActivity.class);
            bundle.putString("fragmnet", "registration");
        }
        else {
            intent = new Intent(this, NewsFeedNotificationActivity.class);
            bundle.putString("fragmnet", "newsfeed");
        }


        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(PRIORITY_MAX);

        notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder));

        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.mynukad_icon_big);
        notificationBuilder.setColor(Color.TRANSPARENT);
        notificationBuilder.setLargeIcon(icon);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder.setContentIntent(pendingIntent);
                /*setLatestEventInfo(AndroidNotifications.this,
                notificationTitle, notificationMessage, pendingIntent);*/
        notificationManager.notify(0, notificationBuilder.build());
    }


    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return R.drawable.mynukad_icon_48;
        } else {
            return R.mipmap.notification_icon;
        }
    }
}
