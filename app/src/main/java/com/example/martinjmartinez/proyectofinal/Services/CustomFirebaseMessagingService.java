package com.example.martinjmartinez.proyectofinal.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.martinjmartinez.proyectofinal.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class CustomFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Intent intent = new Intent("notificationDialog");

        intent.putExtra("message", remoteMessage.getNotification().getBody());
        intent.putExtra("title", remoteMessage.getNotification().getTitle());
        intent.putExtra("tag", remoteMessage.getNotification().getTag());
        intent.putExtra("notification", true);
        intent.putExtra("deviceId", remoteMessage.getData().get("deviceId"));

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
