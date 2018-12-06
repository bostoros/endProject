package com.pooproject;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class Activity_Lieux extends AppCompatActivity {

    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lieux);

        notificationHelper = new NotificationHelper(this);

        //Actions à executer lors de la 1e ouverture de l'appli
        /*SharedPreferences appPref = getSharedPreferences("isFirstTime", 0);
        if(appPref.getBoolean("isFirstTime", true)){

            SharedPreferences.Editor editor = appPref.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();
        }*/

        permissions();
        getLieux();
        filter();
        receiverNotif();
    }

    public void receiverNotif(){
        BroadcastReceiver buttonClickedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(intent.getExtras().getInt("id"));
                NoteBDD noteBDD = new NoteBDD(context);
                noteBDD.open();
                noteBDD.updateCheckWithId(intent.getIntExtra("idNote",-1));
                (new Notifications(context,1)).cancelNotify();
                int count = noteBDD.countNotes(intent.getIntExtra("idLieu",-1));
                noteBDD.close();
                if(count != 0) {
                    new Notifications(context, intent.getIntExtra("idLieu", -1));
                }
            }
        };

        registerReceiver(
                buttonClickedReceiver,
                new IntentFilter("button_clicked")
        );
    }

    //Demande de permission si l'utilisateur ne l'a pas déjà accepté
    public void permissions(){
        if(Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 500);
            }
        }
    }

    //Refu d'ouverture de la creation d'un lieu si l'utilisateur n'a pas la permission de geolocalisé
    public void onClick(View v){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            System.out.println("Cette section requiert l'activation de la localisation.");
            Toast.makeText(this, R.string.askPermission, Toast.LENGTH_LONG).show();
            if(Build.VERSION.SDK_INT >= 23) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(Build.VERSION.SDK_INT >= 23) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 500);
                        }
                    }
                }, 500);
            }
        }else{

            Intent intent = new Intent(this, Activity_Adresse.class);
            startActivity(intent);
        }
    }

    //Fonction de gestion d'actions du bouton TriPar
    public void triPar(String option){
        switch (option){
            case "alphabetique":
                LinearLayout linearLayout = findViewById(R.id.lieuxLayout);
                linearLayout.removeAllViews();
                //getLieuxByLetter();
                break;
        }
    }

    //Fonction de filtre par la barre de recherche
    public void filter(){
        SearchView searchView = findViewById(R.id.searchEditText);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Recréation de l'affichage de la page
                LinearLayout linearLayout = findViewById(R.id.lieuxLayout);
                linearLayout.removeAllViews();
                getLieux();
                //Tri de chaque élément pour vérifier s'il contient le texte dans le SearchView
                for(int i = linearLayout.getChildCount()-1; i>=0;i--){
                    LieuxBDD lieuxBDD = new LieuxBDD(getApplicationContext());
                    lieuxBDD.open();
                    MesLieux mesLieux = lieuxBDD.getLieuxWithId(Integer.parseInt(linearLayout.getChildAt(i).getTag().toString()));
                    if(!mesLieux.getNom().toLowerCase().startsWith(newText.toLowerCase())){
                        linearLayout.removeView(linearLayout.getChildAt(i));
                    }
                    lieuxBDD.close();
                }
                return false;
            }
        });

        //Affichage ou disparition du bouton AfficherLieu s'il y a interaction avec la SearchView
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = findViewById(R.id.btnAddPlace);
                button.setVisibility(View.INVISIBLE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Button button = findViewById(R.id.btnAddPlace);
                button.setVisibility(View.VISIBLE);
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Button button = findViewById(R.id.btnAddPlace);
                button.setVisibility(View.VISIBLE);
            }
        });
    }

    //Fonction d'affichage de l'ensemble des lieux
    public void getLieux()
    {
        LieuxBDD lieuxBDD = new LieuxBDD(this);
        lieuxBDD.open(); // ouverture de la base
        LinearLayout linearLayout = findViewById(R.id.lieuxLayout);
        Cursor c = lieuxBDD.getLieux();
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++){
            //Creation d'un objet virtuel section_lieu.yml + insertion des informations >> Ajouter au LinearLayout de la page Lieux
            View view = View.inflate(this,R.layout.section_lieu, null);
            ConstraintLayout clNote = view.findViewById(R.id.clNote);
            clNote.setTag(c.getInt(0));
            clNote.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    clickCourt(v.getTag().toString());
                }
            });
            Button btnBin= view.findViewById(R.id.btnBin);
            btnBin.setTag(c.getInt(0));
            btnBin.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    onCreateNote(Integer.parseInt((v.getTag().toString())));
                }
            });

            ConstraintLayout clLieu = view.findViewById(R.id.clLieu);
            clLieu.setTag(c.getInt(0));
            clLieu.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    onClickChangeActivity(v.getId(),Integer.parseInt((v.getTag().toString())));
                    return true;
                }
            });
            clLieu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickCourt(v.getTag().toString());
                }
            });
            TextView twNom = view.findViewById(R.id.twNom);
            twNom.setText(c.getString(1));
            TextView twAddr = view.findViewById(R.id.twAddr);
            twAddr.setText(c.getString(2));
            TextView twNotes = view.findViewById(R.id.twNotes);
            NoteBDD noteBDD = new NoteBDD(this);
            noteBDD.open();
            twNotes.setText(String.valueOf(noteBDD.countNotes(c.getInt(0))));
            noteBDD.close();
            linearLayout.addView(view);
            c.moveToNext();
        }
        lieuxBDD.close(); //fermeture de la base
    }

    //Fonction d'affichage de l'ensemble des lieux dans l'ordre alphabétique ( Principe similaire à getLieux() )
    public void getLieuxByLetter(View v)
    {
        LieuxBDD lieuxBDD = new LieuxBDD(this);
        lieuxBDD.open(); // ouverture de la base
        LinearLayout linearLayout = findViewById(R.id.lieuxLayout);
        linearLayout.removeAllViews();
        Cursor c = lieuxBDD.getLieuxByLetter();
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++){
            View view = View.inflate(this,R.layout.section_lieu, null);
            ConstraintLayout clLieu = view.findViewById(R.id.clLieu);
            clLieu.setTag(c.getInt(0));
            clLieu.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    clickLong(v.getTag().toString());
                    return true;
                }
            });
            clLieu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickCourt(v.getTag().toString());
                }
            });
            TextView twNom = view.findViewById(R.id.twNom);
            twNom.setText(c.getString(1));
            twNom.setTextSize(20);
            TextView twAddr = view.findViewById(R.id.twAddr);
            twAddr.setText(c.getString(2));
            twAddr.setTextSize(15);
            TextView twNotes = view.findViewById(R.id.twNotes);
            twNotes.setTextSize(20);
            NoteBDD noteBDD = new NoteBDD(this);
            noteBDD.open();
            twNotes.setText(String.valueOf(noteBDD.countNotes(c.getInt(0))));
            noteBDD.close();
            Button btnBin= view.findViewById(R.id.btnBin);
            btnBin.setTag(c.getInt(0));
            btnBin.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    onCreateNote(Integer.parseInt((v.getTag().toString())));
                }
            });
            linearLayout.addView(view);
            c.moveToNext();
        }
        lieuxBDD.close(); //fermeture de la base
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.lieux);
        getLieux();
        filter();
    }

    //Cette fonction permet de creer une note en cliquant sur le plus
    public void onCreateNote(int id)
    {
        Intent intent = new Intent(this,Activity_Check.class);
        intent.putExtra("idLieu",id);
        startActivityForResult(intent,998);

    }
    //Après un clique long sur un objet section_lieu, ouverture d'une page Adresse
    public void clickLong(String id){

        Intent intent = new Intent(this, Activity_Adresse.class);
        intent.putExtra("id",id);
        intent.putExtra("here",0);
        LieuxBDD lieuxBDD = new LieuxBDD(this);
        lieuxBDD.open();
        MesLieux mesLieux = lieuxBDD.getLieuxWithId(Integer.parseInt(id));
        intent.putExtra("title",mesLieux.getNom());
        intent.putExtra("adresse",mesLieux.getAdresse());
        lieuxBDD.close();
        startActivityForResult(intent,995);

    }

    //Après un clique court sur un objet section_lieu, ouverture d'une page Notes
    public void clickCourt(String id)
    {
        Intent intent = new Intent(this, Activity_Notes.class);
        intent.putExtra("id",Integer.parseInt(id));
        LieuxBDD lieuxBDD = new LieuxBDD(this);
        lieuxBDD.open();
        MesLieux mesLieux = lieuxBDD.getLieuxWithId(Integer.parseInt(id));
        intent.putExtra("title",mesLieux.getNom());
        lieuxBDD.close();
        startActivityForResult(intent, 996);
    }
    public void page1 (View view){

    }

    public void page2 (View view){
        Intent intent = new Intent(this, Activity_ListeNotes.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    //cette fonction ouvre un popup pour confirmer ou infirmer la suppression d'une note lorsque l'utilisateur check
    public void onClickChangeActivity(int idButton,int id){
        Intent intent = new Intent(this,popUpQuit.class);
        System.out.println(id);
        intent.putExtra("text","Que voulez-vous faire ?");
        intent.putExtra("choix",1);
        intent.putExtra("id",id);
        intent.putExtra("idButton",idButton);
        startActivityForResult(intent,994);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 998 && resultCode == RESULT_OK){
            if(data.getIntExtra("return",0)==1){
                setContentView(R.layout.activity_notes);
                //getLieux();
            }
        }
        if(requestCode == 994 && resultCode == RESULT_OK){
            if(data.getIntExtra("return",-1)==1){
                LieuxBDD lieuxBDD = new LieuxBDD(this);
                NoteBDD noteBDD = new NoteBDD(this);
                lieuxBDD.open();
                noteBDD.open();
                noteBDD.updateCheckByLieu(data.getIntExtra("id",-1));
                lieuxBDD.removeLieuWithID(data.getIntExtra("id",-1));
                noteBDD.close();
                lieuxBDD.close();
                setContentView(R.layout.lieux);
                getLieux();
            }
            else if (data.getIntExtra("return",-1)==0)
            {
                clickLong(String.valueOf(data.getIntExtra("id",-1)));
            }
        }
    }
}
