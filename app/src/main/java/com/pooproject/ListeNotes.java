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

public class ListeNotes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_note);
        getNotes();
        filter();
    }

    public void getNotes()
    {
        NoteBDD notesBDD = new NoteBDD(this);
        notesBDD.open(); // ouverture de la base
        LinearLayout linearLayout = findViewById(R.id.linearLayoutNote);
        Cursor c = notesBDD.getNotes();
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++){
            //Creation d'un objet virtuel section_note.yml + insertion des informations >> Ajouter au LinearLayout de la page Lieux
            View view = View.inflate(this,R.layout.section_note, null);
            ConstraintLayout clNote = view.findViewById(R.id.clNote);
            clNote.setTag(c.getInt(0));
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
            linearLayout.addView(view);
            c.moveToNext();
        }
        notesBDD.close(); //fermeture de la base
    }

    public void filter(){
        final SearchView searchView1 = findViewById(R.id.searchEditText);
        searchView1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                    searchView1.clearFocus();
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

    public void onClickChangeActivity(int idCheckBox,int id){
        Intent intent = new Intent(this,popUpQuit.class);
        System.out.println(id);
        intent.putExtra("text","Voulez-vous checker cette note ?");
        intent.putExtra("id",id);
        intent.putExtra("idCheckBox",idCheckBox);
        startActivityForResult(intent,952);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 952){
            NoteBDD noteBDD = new NoteBDD(this);
            noteBDD.open();
            noteBDD.removeNoteWithID(data.getIntExtra("id",-1));
            noteBDD.close();
            setContentView(R.layout.liste_note);
            getNotes();
        }
    }

    public void page1 (View view){
        Intent intent = new Intent(this, Lieux.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
