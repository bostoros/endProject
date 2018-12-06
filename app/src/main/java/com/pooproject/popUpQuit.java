package com.pooproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class popUpQuit extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupquit);

        TextView textView = findViewById(R.id.textViewPopUpQuit);
        textView.setText(getIntent().getExtras().getString("text"));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width),(int)(height*0.22));

        if(getIntent().getIntExtra("choix",0)==1)
        {
            Button buttonno = findViewById(R.id.buttonno);
            buttonno.setText("Modifier");

            Button buttonyes = findViewById(R.id.buttonyes);
            buttonyes.setText("Supprimer");
        }
    }
    public void onClickYes(View v){
        Intent intent = new Intent();
        intent.putExtra("return",1);
        intent.putExtra("id",getIntent().getExtras().getInt("id"));
        setResult(RESULT_OK,intent);
        finish();
    }

    public void onClickNo(View v){
        Intent intent = new Intent();
        intent.putExtra("return",0);
        //intent.putExtra("id",getIntent().getExtras().getInt("id"));
        intent.putExtra("idCheckBox",getIntent().getExtras().getInt("idCheckBox"));
        setResult(RESULT_OK,intent);
        finish();
    }

}
