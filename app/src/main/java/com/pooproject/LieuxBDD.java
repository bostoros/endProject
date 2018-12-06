package com.pooproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LieuxBDD {
    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "pooProject.db";

    private static final String TABLE_LIEUX = "Lieux";

    private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;
    private static final String COL_NOM = "Nom";
    private static final int NUM_COL_NOM = 1;
    private static final String COL_ADDR = "Adresse";
    private static final int NUM_COL_ADDR = 2;
    private static final String COL_LAT = "Latitude";
    private static final int NUM_COL_LAT = 3;
    private static final String COL_LONG = "Longitude";
    private static final int NUM_COL_LONG = 4;


    private SQLiteDatabase bdd;

    private MaBaseSQLite maBaseSQLite;

    public LieuxBDD(Context context){
        //On créer la BDD et sa table
        maBaseSQLite = new MaBaseSQLite(context, NOM_BDD, null, VERSION_BDD);
    }
    public void upDate()
    {
        maBaseSQLite.onUpgrade(bdd,1,2);
    }

    public void open(){
        //on ouvre la BDD en écriture
        bdd = maBaseSQLite.getWritableDatabase();
    }

    public void close(){
        //on ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }

    public long insertLieux(MesLieux lieu){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_NOM, lieu.getNom());
        values.put(COL_LAT, lieu.getLat());
        values.put(COL_LONG, lieu.getLong());
        values.put(COL_ADDR, lieu.getAdresse());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_LIEUX, null, values);
    }

    public int updateLieux(int id, MesLieux lieu){
        //La mise à jour d'un livre dans la BDD fonctionne plus ou moins comme une insertion
        //il faut simple préciser quelle livre on doit mettre à jour grâce à l'ID
        ContentValues values = new ContentValues();
        values.put(COL_NOM, lieu.getNom());
        values.put(COL_LAT, lieu.getLat());
        values.put(COL_LONG, lieu.getLong());
        values.put(COL_ADDR, lieu.getAdresse());
        return bdd.update(TABLE_LIEUX, values, COL_ID + " = " +id, null);
    }

    public int removeLieuWithID(int id){
        //Suppression d'un livre de la BDD grâce à l'ID
        return bdd.delete(TABLE_LIEUX, COL_ID + " = " +id, null);
    }

    public MesLieux getLieuxWithId(int id){
        //Récupère dans un Cursor les valeur correspondant à un livre contenu dans la BDD (ici on sélectionne le livre grâce à son titre)
        Cursor c = bdd.query(TABLE_LIEUX, new String[] {COL_ID, COL_NOM,COL_ADDR,COL_LAT,COL_LONG}, COL_ID + " LIKE \"" + id +"\"", null, null, null, null);
        return cursorToLieux(c);
    }

    //Cette méthode permet de convertir un cursor en un livre
    private MesLieux cursorToLieux(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        //On créé un livre
        MesLieux lieu = new MesLieux();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        lieu.setId(c.getInt(NUM_COL_ID));
        lieu.setNom(c.getString(NUM_COL_NOM));
        lieu.setLat(Double.parseDouble(c.getString(NUM_COL_LAT)));
        lieu.setLong(Double.parseDouble(c.getString(NUM_COL_LONG)));
        lieu.setAdresse(c.getString(NUM_COL_ADDR));
        //On ferme le cursor
        c.close();

        //On retourne le lieu
        return lieu;
    }

    public Cursor getLieux(){
        return bdd.query(TABLE_LIEUX, new String[] {COL_ID, COL_NOM,COL_ADDR,COL_LAT,COL_LONG}, null, null, null, null, null);
    }

    public Cursor getLieuxByLetter(){
        return bdd.query(TABLE_LIEUX, new String[] {COL_ID, COL_NOM,COL_ADDR,COL_LAT,COL_LONG}, null, null, null, null, COL_NOM);
    }
}
