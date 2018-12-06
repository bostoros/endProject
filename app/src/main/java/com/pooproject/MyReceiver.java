package com.pooproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent i ) {
        Intent intent = new Intent( ctx, GPS_Service.class );
        ctx.startService(intent);
    }
}
