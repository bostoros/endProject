package com.pooproject;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "ch1";
    public static final String channelName = "NoteGeolocalised";
    public static NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channelCreation();
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void channelCreation(){
        NotificationChannel channel = new NotificationChannel(channelID,channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(R.color.colorPrimary);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager(){
        if(manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    public NotificationCompat.Builder getChannelNotification(String text, RemoteViews remoteViews){
        return new NotificationCompat.Builder(getApplicationContext(),channelID)
                .setContentTitle("pooProject")
                .setContentText(text)
                .setSmallIcon(R.drawable.smiley_mini)
                .setLights(getResources().getColor(R.color.colorPrimary),0,0)
                .setAutoCancel(true)
                .setCustomBigContentView(remoteViews);
    }
}
