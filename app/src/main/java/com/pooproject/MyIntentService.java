package com.pooproject;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

public class MyIntentService extends IntentService {
    public MyIntentService()
    {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        try {
            Thread.sleep(5000);
            Toast.makeText(this, "service starting", Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }
    }
}
