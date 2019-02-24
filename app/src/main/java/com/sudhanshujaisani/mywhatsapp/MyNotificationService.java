package com.sudhanshujaisani.mywhatsapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyNotificationService extends FirebaseMessagingService {
    String channelId="MyChannelId";
    String channelName="My Channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,channelId);
        builder.setContentTitle("New Friend Request");
        builder.setContentText("You have a new friend request");
        builder.setSmallIcon(R.drawable.images);

        Intent intent =new Intent(this,UsersActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationChannel channel=new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);


        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
       manager.createNotificationChannel(channel);
        manager.notify(1001,builder.build());
    }
}
