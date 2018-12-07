package com.pooproject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Check extends AppCompatActivity {

    private MesNotes note;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        //System.out.println("---------------------------------------------");
        //System.out.println(getIntent().getExtras().getString("idnote"));
        //System.out.println(note);
        //System.out.println("---------------------------------------------");
        if(getIntent().getExtras().getString("idnote")!=null){
            NoteBDD noteBDD = new NoteBDD(getApplicationContext());
            //LinearLayout l = findViewById(R.id.linearLayoutNote);
            noteBDD.open();
            note = noteBDD.getNoteWithId(Integer.parseInt(getIntent().getExtras().getString("idnote")));
            note.setIdLieu(getIntent().getIntExtra("idLieu",-1));
            System.out.println("---------------------------------------------");
            System.out.println(note.getId());
            System.out.println(note.getTitre());
            System.out.println(note.getDesc());
            System.out.println(note.getIdLieu());
            System.out.println("---------------------------------------------");
            setTitle("votre note");
            TextView titre = findViewById(R.id.titre);
            titre.setText(note.getTitre());
            TextView description = findViewById(R.id.description);
            description.setText(note.getDesc());
        }else{
            setTitle("Votre nouvelle note");
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }



    public void onClickSaveNote(View view) {

        EditText titre = findViewById(R.id.titre);
        EditText desc = findViewById(R.id.description);

        //Affichage ou disparition du bouton AfficherLieu s'il y a interaction avec la SearchView
       /* titre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = findViewById(R.id.button);
                button.setVisibility(View.INVISIBLE);
            }
        });

        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = findViewById(R.id.button);
                button.setVisibility(View.INVISIBLE);
            }
        });

        titre.onchangeli(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Button button = findViewById(R.id.button);
                button.setVisibility(View.VISIBLE);
            }
        });*/
        //System.out.println("---------------------------------------------");
        //System.out.println(view.findViewById(R.id.titre).toString());
        //System.out.println(titre.getText().toString());
        //System.out.println("---------------------------------------------");


        if(getIntent().getExtras().getString("idnote")!=null){
            if (titre.getText().length() == 0) {
                Context context = getApplicationContext();
                CharSequence text = "Vous devez ajouter un titre !";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            else {
                if(!(note.getTitre().equals(titre.getText().toString())) || !(note.getDesc().equals(desc.getText().toString()))){
                    note.setTitre(titre.getText().toString());
                    note.setDesc(desc.getText().toString());
                    updateNoteBDD(note.getId(),note);

                    Intent intent = new Intent();
                    intent.putExtra("return", 1);
                    intent.putExtra("title", titre.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    //System.out.println("---------------------------------------------");
                    Intent intent = new Intent();
                    intent.putExtra("return", 2);
                    intent.putExtra("id", note.getId());
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            }
            //setResult(RESULT_OK,);
        }else {
            if (titre.getText().length() == 0) {
                Context context = getApplicationContext();
                CharSequence text = "Vous devez ajouter un titre !";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else {
                insertNoteBDD(titre,desc);
                /*NoteBDD noteBDD = new NoteBDD(this);
                noteBDD.open();
                noteBDD.insertNote(new MesNotes(titre.getText().toString(), desc.getText().toString(), getIntent().getIntExtra("idLieu", -1)));
                noteBDD.close();
*/
                Intent intent = new Intent();
                intent.putExtra("return", 1);
                intent.putExtra("title", titre.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        }

    }

    public void insertNoteBDD(EditText titre, EditText desc){
        NoteBDD noteBDD = new NoteBDD(this);
        noteBDD.open();
        noteBDD.insertNote(new MesNotes(titre.getText().toString(), desc.getText().toString(), getIntent().getIntExtra("idLieu", -1)));
        noteBDD.close();
    }

    public void updateNoteBDD(int id, MesNotes note){
        NoteBDD noteBDD = new NoteBDD(this);
        noteBDD.open();
        noteBDD.updateNote(id, note);
        noteBDD.close();
    }

}
