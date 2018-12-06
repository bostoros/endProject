package com.pooproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Check extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        setTitle("Votre nouvelle note");
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

        if (titre.getText().length() == 0) {
            Context context = getApplicationContext();
            CharSequence text = "Vous devez ajouter un titre !";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            NoteBDD noteBDD = new NoteBDD(this);
            noteBDD.open();
            noteBDD.insertNote(new MesNotes(titre.getText().toString(), desc.getText().toString(), getIntent().getIntExtra("idLieu", -1)));
            noteBDD.close();

            Intent intent = new Intent();
            intent.putExtra("return", 1);
            intent.putExtra("title", titre.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}
