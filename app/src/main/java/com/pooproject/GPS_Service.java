package com.pooproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class GPS_Service extends Service {
    private LocationListener listener;
    private LocationManager manager;
    private LieuxBDD lieuxBDD;
    private NoteBDD noteBDD;
    private ArrayList<liste> listeLieux;
    //private long TIME = 900000;
    private long TIME = 10000;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        if(permissions()) {
            listeLieux = new ArrayList<>();
            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    lieuxBDD = new LieuxBDD(getApplicationContext());
                    noteBDD = new NoteBDD(getApplicationContext());
                    
                    noteBDD.open();
                    lieuxBDD.open();
                    Cursor c = lieuxBDD.getLieux();
                    c.moveToFirst();
                    for (int i = 0; i < c.getCount(); i++) {
                        Double lat = Double.parseDouble(c.getString(3));
                        Double longi = Double.parseDouble(c.getString(4));
                        if(noteBDD.countNotes(c.getInt(0))!=0) {
                            if (createDetection(lat, longi, location.getLatitude(), location.getLongitude())) {
                                if (!isIn(c.getString(0))) {
                                    (new Notifications(getApplicationContext(), c.getInt(0))).cancelNotify();
                                    new Notifications(getApplicationContext(), c.getInt(0));
                                    Calendar cal = new GregorianCalendar();
                                    Date d = cal.getTime();
                                    Long tim = d.getTime();
                                    liste l = new liste(c.getString(0), tim + TIME);
                                    listeLieux.add(l);
                                }
                            }
                        }
                        c.moveToNext();
                    }
                    lieuxBDD.close();
                    noteBDD.close();
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
            manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);
        }
    }

    private boolean isIn(String string) {
        Calendar c = new GregorianCalendar();
        for(liste l : listeLieux)
        {
            if (l.getId().equals(string))
            {
                if (l.getD() > c.getTime().getTime())
                {
                    return true;
                }
                else
                {
                    listeLieux.remove(l);
                    return false;
                }
            }
        }
        return false;
    }

    private boolean createDetection(double lat_dest, double longi_dest, double lat_act, double longi_act)
    {
        //la fonction prend un lieu faisant référence à la position actuelle : lieu_actuel
        //et un lieu : lieu_dest, coorespondant au lieu duquel on approche
        double distance;

        //on calcule la distance entre les 2 lieux à partir des coordonnées passées en paramètre
        distance = Math.acos(Math.sin(Math.toRadians(lat_dest))*Math.sin(Math.toRadians(lat_act))+Math.cos(Math.toRadians(lat_dest))*Math.cos(Math.toRadians(lat_act))*Math.cos(Math.toRadians(longi_dest-longi_act)))*6371;

        //si la distance est < 100 mètres, on lance une ntification avec le nom du lieu et la distance
        if (distance < 0.1)    //detection à 50 m en approche du lieu de destination
        {
            //createNotify("Approche !", "Vous approchez d'un lieu", cursor);
            //supprimer la notification d'approche
            //notif_detection.cancelNotify();
            return true;
        }
        return false;
    }

    public boolean permissions(){
        if(Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                //requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 500);
                stopSelf();
                return false;
            }
        }
        return true;
    }
}
