package com.pooproject;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public class Notifications {

    private NotificationHelper notificationHelper;
    private RemoteViews remoteViews;
    private Context context;

    public Notifications(Context context, int idLieu) {

        this.context = context;

        remoteViews = new RemoteViews(context.getPackageName(),R.layout.custom_notification);

        NoteBDD noteBDD = new NoteBDD(context);
        noteBDD.open();
        Cursor cursor = noteBDD.getNotes(idLieu);
        cursor.moveToFirst();

        LieuxBDD lieuxBDD = new LieuxBDD(context);
        lieuxBDD.open();
        MesLieux mesLieux = lieuxBDD.getLieuxWithId(idLieu);
        remoteViews.setTextViewText(R.id.message_notification,"Vous approchez de " + mesLieux.getNom() + ".");

        for (int i=0; i<cursor.getCount(); i++)
        {
            RemoteViews v = new RemoteViews(context.getPackageName(),R.layout.section_note_notification);
            v.setTextViewText(R.id.twNote,cursor.getString(1));

            Intent intent = new Intent("button_clicked");
            intent.putExtra("id",1);
            intent.putExtra("idLieu",idLieu);
            intent.putExtra("idNote",cursor.getInt(0));
            PendingIntent onClickButton = PendingIntent.getBroadcast(context,999,intent,cursor.getInt(0));

            v.setOnClickPendingIntent(R.id.cbNote,onClickButton);
            remoteViews.addView(R.id.notesNotif,v);
            cursor.moveToNext();
        }

        notificationHelper = new NotificationHelper(context);

        createNotify("Vous approchez de " + mesLieux.getNom() + ".");

        noteBDD.close();
    }

    private void createNotify(String textNotification){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            sendOnChannel(textNotification);
        }else{
            if(Build.VERSION.SDK_INT >=24) {

                notification = new Notification.Builder(context)
                        .setContentTitle("pooProject")
                        .setContentText(textNotification)
                        .setSmallIcon(R.drawable.smiley_mini)
                        .setAutoCancel(true)
                        .setVibrate(new long[] { 100, 200, 100, 500, 100, 200 })
                        .setLights(context.getResources().getColor(R.color.colorPrimary), 0, 0)
                        .setCustomBigContentView(remoteViews)
                        .build();
            }else{
                notification = new Notification.Builder(context)
                        .setContentTitle("pooProject")
                        .setContentText(textNotification)
                        .setSmallIcon(R.drawable.smiley_mini)
                        .setAutoCancel(true)
                        .setVibrate(new long[] { 100, 200, 100, 500, 100, 200 })
                        .setLights(context.getResources().getColor(R.color.colorPrimary), 100,100)
                        .build();
                notification.bigContentView=remoteViews;
            }
            notificationManager.notify(1, notification);
        }
    }
    public void cancelNotify(){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void sendOnChannel(String text){
        NotificationCompat.Builder notiBuild = notificationHelper.getChannelNotification(text,remoteViews);
        notificationHelper.getManager().notify(1,notiBuild.build());
    }
}