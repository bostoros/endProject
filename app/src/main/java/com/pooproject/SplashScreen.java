package com.pooproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class SplashScreen extends Activity {

    private ProgressBar firstBar = null;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    private static int SPLASH_TIME_OUT = 2500;

    private ArrayList<MesLieux> liste=new ArrayList<>();
    // Retient l'état de la connexion avec le service
    private boolean mBound = false;
    // Le service en lui-même
    // Interface de connexion au service

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, GPS_Service.class);
        startService(mIntent);
        mBound = true;
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
       super.onStop();
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firstBar = findViewById(R.id.progressBar3);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progressStatus < 500){
                    // Update the progress status
                    progressStatus +=1;

                    // Try to sleep the thread for 20 milliseconds
                    try{
                        Thread.sleep(50);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            firstBar.setProgress(progressStatus);
                        }
                    });
                }
            }
        }).start(); // Start the operation

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent j = new Intent(SplashScreen.this, Activity_Lieux.class);
                startActivity(j);
                finish();

            }
        }, SPLASH_TIME_OUT);
    }
}
