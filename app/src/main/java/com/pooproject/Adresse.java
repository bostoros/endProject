package com.pooproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class Adresse extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Double hiddenLat;
    private Double hiddenLong;
    private Double latitude;
    private Double longitude;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    //Création d'un marqueur sur la carte
    private void marker(double lat, double longi){
        //Add a marker in the place and move the camera
        mMap.clear();
        LatLng pos = new LatLng(lat, longi);
        System.out.println(pos);
        mMap.addMarker(new MarkerOptions().position(pos).title("Pos demandée"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 16));
    }

    // Retient l'état de la connexion avec le service
    private boolean mBound = false;
    // Le service en lui-même

    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * La fonction a appeller a chaque fois que l'on clique sur "position actuelle" afin d'afficher la postion actuelle
     */
    public void geo(View v) {
        try {
        Geocoder add = new Geocoder(this);
        List<Address> recup;
        String slat = hiddenLat.toString();
        String slong = hiddenLong.toString();

            double sslat = Double.parseDouble(slat);
            double sslong = Double.parseDouble(slong);
            latitude = sslat;
            longitude = sslong;
            recup = add.getFromLocation(sslat, sslong, 1);
            EditText NomAdresse = findViewById(R.id.NomAdresse);
            String nom = recup.get(0).getAddressLine(0);
            NomAdresse.setText(nom);
            marker(sslat, sslong);
        } catch (Exception e) {
            Intent intent = new Intent(this,popUpOk.class);
            startActivity(intent);
        }
    }

    //Fonction de récupération de la position par Adresse
    public void getPos(View v){
        Geocoder add = new Geocoder(this);
        List<Address> recup;
        try{
            EditText NomAdresse = findViewById(R.id.NomAdresse);
            String nomAdr;
            nomAdr = NomAdresse.getText().toString();
            recup = add.getFromLocationName(nomAdr, 1);
            latitude = recup.get(0).getLatitude();
            longitude = recup.get(0).getLongitude();
            marker(latitude,longitude);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Fonction qui entre les donnees dans la base de donnée
     */
    public void onClickOK(View v)
    {
        //---récupération des coordonnées GPS par le champs Adresse

            getPos(v);

            EditText nom=findViewById(R.id.NomLieu);
            EditText adr = findViewById(R.id.NomAdresse);

        if ((nom.getText().length() == 0) || (adr.getText().length() == 0)) {
            Context context = getApplicationContext();
            CharSequence text = "Vous devez ajouter un titre et une adresse !";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            String name = nom.getText().toString();
            double lati = latitude;
            double longit = longitude;
            String adresse = adr.getText().toString();

            //---creer l'instance pour recuperer la base
            LieuxBDD lieuxBDD = new LieuxBDD(this);
            MesLieux lieu = new MesLieux(name, adresse, lati, longit);
            lieuxBDD.open(); // ouverture de la base
            if (getIntent().getIntExtra("here", 1) == 1) {
                //---Si on est dans un ajout de lieu : insertion
                lieuxBDD.insertLieux(lieu);
            } else {
                //---Si on est dans la modification d'un lieu : update
                lieuxBDD.updateLieux(Integer.parseInt(getIntent().getExtras().getString("id")), lieu);
            }
            lieuxBDD.close(); //fermeture de la base
            //---Retour a la liste des lieux
            // TODO: Pensez a une autre methode afin de ne pas genere de confusion pour l'utilisateur si utilise le bouton retour du telephone
            Intent intent = new Intent(this, Lieux.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.adresse);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // Initialisation de la carte google

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(getIntent().getIntExtra("here",1)==1)
        {
            setTitle(R.string.addPlace);
        } else {
            //--- Si on veut le modifier
            setTitle(R.string.modifyPlace);
            //---Affichage des donnée du lieu que l'on veut modifier
            EditText nom = findViewById(R.id.NomLieu);
            EditText adres = findViewById(R.id.NomAdresse);
            nom.setText(getIntent().getExtras().getString("title"));
            //adres.setText(getIntent().getExtras().getString("adresse"));
            //---Recuperation des coordonnées (dans la bdd) pour les afficher
            LieuxBDD lieuxBDD = new LieuxBDD(this);
            lieuxBDD.open();
            MesLieux lieu = lieuxBDD.getLieuxWithId(Integer.parseInt(getIntent().getExtras().getString("id")));
            adres.setText(lieu.getAdresse());
            latitude = lieu.getLat();
            longitude = lieu.getLong();
            lieuxBDD.close();
            getPos(findViewById(R.id.NomAdresse));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //onCreate
        //---Creation d'un critere pour recupere un provider (provider qui donnera les donnée lie a la localisation
        Criteria cri = new Criteria();
        cri.setAccuracy(Criteria.ACCURACY_COARSE);
        cri.setAltitudeRequired(false);
        cri.setBearingRequired(false);
        cri.setCostAllowed(false);
        cri.setPowerRequirement(Criteria.POWER_LOW);
        //---Creation du LocationManager permettant la récupération des données de localisation
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //---Choix du meilleur provider selon le critere
        String provider = locationManager.getBestProvider(cri, true);

        /**
         * Creation de la classe listener pour le GPS : elle va actualiser les données au fur et a mesure
         * Elle implémente l'interface LocationListener
         * */
        class MyLocationListener implements LocationListener {

            @Override
            public void onLocationChanged(Location location) {
                //---Permet de modifier les coordonnes quand elles changent
                hiddenLat = location.getLatitude();
                hiddenLong = location.getLongitude();
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }
            @Override
            public void onProviderEnabled(String s) {
                Toast.makeText(getApplicationContext(), "GPS enable", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(getApplicationContext(), "GPS disables", Toast.LENGTH_LONG).show();
            }
        }
        //---Récupération de la dernière localisation connue du télépohne
        Location test = locationManager.getLastKnownLocation(provider);
        if (test != null) {
            //---On les met dans les EditText cache
            hiddenLong = test.getLongitude();
            hiddenLat = test.getLatitude();
        }
        //---On créer le listener
        LocationListener locationListener = new MyLocationListener();
        //---On demande la mise a jour de la localisation
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }
}
