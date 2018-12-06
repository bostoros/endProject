package com.pooproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MaBaseSQLite extends SQLiteOpenHelper {

    private static final String TABLE_LIEUX = "Lieux";
    private static final String COL_ID = "ID";
    private static final String COL_NOM = "Nom";
    private static final String COL_ADRESSE = "Adresse";
    private static final String COL_LAT = "Latitude";
    private static final String COL_LONG = "Longitude";

    private static final String CREATE_BDD_LIEU = "CREATE TABLE " + TABLE_LIEUX + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NOM + " TEXT NOT NULL, "
            + COL_ADRESSE + " TEXT NOT NULL, "
            + COL_LAT + " TEXT NOT NULL, "
            + COL_LONG +" TEXT NOT NULL);";

    private static final String TABLE_NOTE = "Notes";
    private static final String COL_TITRE = "Titre";
    private static final String COL_DESCRIPTION = "Description";
    private static final String COL_CHECK = "Valider";
    private static final String COL_IDLIEU = "IDLieu";

    private static final String CREATE_BDD_NOTE = "CREATE TABLE " + TABLE_NOTE + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TITRE + " TEXT NOT NULL, "
            + COL_DESCRIPTION + " INTEGER NOT NULL, "
            + COL_CHECK + " TEXT NOT NULL,"
            + COL_IDLIEU + " TEXT NOT NULL);";

    public MaBaseSQLite(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //on créé la table à partir de la requête écrite dans la variable CREATE_BDD
        db.execSQL(CREATE_BDD_NOTE);
        db.execSQL(CREATE_BDD_LIEU);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On peut fait ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0
        db.execSQL("DROP TABLE " + TABLE_LIEUX + ";");
        db.execSQL("DROP TABLE " + TABLE_NOTE + ";");
        onCreate(db);
    }

}