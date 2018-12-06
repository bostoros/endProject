package com.pooproject;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Button_listener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(intent.getFlags());
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(intent.getExtras().getInt("id"));
        NoteBDD noteBDD = new NoteBDD(context);
        noteBDD.open();
        noteBDD.updateCheckWithId(intent.getIntExtra("idNote",-1));
        (new Notifications(context,1)).cancelNotify();
        int count = noteBDD.countNotes(intent.getIntExtra("idLieu",-1));
        noteBDD.close();
        if(count != 0)
            new Notifications(context,intent.getIntExtra("idLieu",-1));
    }
}
