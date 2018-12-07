package com.pooproject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

public class Activity_Notes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        setTitle(getIntent().getExtras().getString("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getNotes();
        filter();
    }

    //Fonction d'affichage de l'ensemble des notes
    public void getNotes(){
        NoteBDD noteBDD = new NoteBDD(getApplicationContext());
        LinearLayout l = findViewById(R.id.linearLayoutNote);
        noteBDD.open();
        Cursor c = noteBDD.getNotes(getIntent().getIntExtra("id",-1));
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++){
            //Creation d'un objet virtuel section_note.yml + insertion des informations >> Ajouter au LinearLayout de la page Notes
            final View view = View.inflate(this,R.layout.section_note, null);
            ConstraintLayout clNote = view.findViewById(R.id.clNote);
            clNote.setTag(c.getInt(0));
            ConstraintLayout clInfosNotes = view.findViewById(R.id.clInfosNotes);
            clInfosNotes.setTag(c.getInt(0));
            TextView twNote = view.findViewById(R.id.twNote);
            twNote.setText(c.getString(1));
            TextView twNote1 = view.findViewById(R.id.twNote1);
            twNote1.setText(c.getString(2));
            CheckBox cbNote = view.findViewById(R.id.cbNote);
            cbNote.setId((int)(Math.random()*100000000));
            cbNote.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    onClickChangeActivity(v.getId(),Integer.parseInt(((ConstraintLayout)v.getParent()).getTag().toString()));
                }
            });
            l.addView(view);
            c.moveToNext();
        }
    }

    //Fonction de filtre par la barre de recherche
    public void filter(){
        //Definition des actions lors de la modification du SearchView
        SearchView searchView = findViewById(R.id.searchEditTextNote);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //Recréation de l'affichage de la page
                LinearLayout linearLayout = findViewById(R.id.linearLayoutNote);
                linearLayout.removeAllViews();
                getNotes();
                //Tri de chaque élément pour vérifier s'ils contiennent le texte dans le SearchView
                for(int i = linearLayout.getChildCount()-1; i>=0;i--){
                    NoteBDD noteBDD = new NoteBDD(getApplicationContext());
                    noteBDD.open();
                    MesNotes mesNotes = noteBDD.getNoteWithId(Integer.parseInt(linearLayout.getChildAt(i).getTag().toString()));
                    if(!mesNotes.getTitre().toLowerCase().contains(newText.toLowerCase())){
                        linearLayout.removeView(linearLayout.getChildAt(i));
                    }
                    noteBDD.close();
                }
                return false;
            }
        });
    }

    //Ouverture d'un popUp de confirmation : Checker une note
    public void onClickChangeActivity(int idCheckBox,int id){
        Intent intent = new Intent(this,popUpQuit.class);
        intent.putExtra("text","Voulez-vous supprimer cette note?");
        intent.putExtra("id",id);
        intent.putExtra("idCheckBox",idCheckBox);
        startActivityForResult(intent,997);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    //Ouverture d'une page de création de note pour visualiser la note
    public void onClickViewNote(View view){
        Intent intent = new Intent(this,Activity_Check.class);
        intent.putExtra("idnote",view.getTag().toString());
        intent.putExtra("idLieu",getIntent().getIntExtra("id",-1));
        startActivityForResult(intent,998);
    }

    //Analyse des retours de différentes pages
    //Code 998 : Creation d'une note
    //Code 997 : Demande pour checker une note
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Si les informations reçu sont corrects (RESULT_OK // return = 1) => Recréation de la page avec la nouvelle note
        if(requestCode == 998 && resultCode == RESULT_OK){
            if(data.getIntExtra("return",0)==1){
                setContentView(R.layout.activity_notes);
                getNotes();
            }
        }else{

        }if(requestCode == 998 && resultCode == RESULT_CANCELED){
            if(data.getIntExtra("return",-1)==2){
                NoteBDD noteBDD = new NoteBDD(this);
                noteBDD.open();
                noteBDD.updateCheckWithId(data.getIntExtra("id",-1));
                noteBDD.close();
            }
        }
        //Si les informations reçu sont corrects (RESULT_OK // return = 1) => Suppression de la note + Recréation de la page
        if(requestCode == 997 && resultCode == RESULT_OK){
            if(data.getIntExtra("return",-1)==0){
                CheckBox checkBox = findViewById(data.getIntExtra("idCheckBox", 0));
                checkBox.setChecked(false);
            }else if(data.getIntExtra("return",-1)==1){
                NoteBDD noteBDD = new NoteBDD(this);
                noteBDD.open();
                noteBDD.updateCheckWithId(data.getIntExtra("id",-1));
                //noteBDD.removeNoteWithID(data.getIntExtra("id",-1));
                noteBDD.close();
            }
        }
        setContentView(R.layout.activity_notes);
        getNotes();
    }
}
