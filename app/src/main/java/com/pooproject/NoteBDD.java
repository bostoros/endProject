package com.pooproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NoteBDD {
    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "pooProject.db";
    private static final String TABLE_NOTE = "Notes";
    private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;
    private static final String COL_TITRE = "Titre";
    private static final int NUM_COL_TITRE = 1;
    private static final String COL_DESC = "Description";
    private static final int NUM_COL_DESC = 2;
    private static final String COL_CHECK = "Valider";
    private static final int NUM_COL_CHECK = 3;
    private static final String COL_IDLIEU = "IDLieu";
    private static final int NUM_COL_IDLIEU = 4;
    private SQLiteDatabase bdd;
    private MaBaseSQLite maBaseSQLite;

    public NoteBDD(Context context){
        maBaseSQLite = new MaBaseSQLite(context, NOM_BDD, null, VERSION_BDD);
    }

    public void open(){
        bdd = maBaseSQLite.getWritableDatabase();
    }

    public void close(){
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }

    public long insertNote(MesNotes note){
        ContentValues values = new ContentValues();
        values.put(COL_TITRE, note.getTitre());
        values.put(COL_DESC, note.getDesc());
        values.put(COL_CHECK,0);
        values.put(COL_IDLIEU,note.getIdLieu());
        return bdd.insert(TABLE_NOTE, null, values);
    }

    public int updateNote(int id, MesNotes note){
        ContentValues values = new ContentValues();
        values.put(COL_TITRE, note.getTitre());
        values.put(COL_DESC, note.getDesc());
        values.put(COL_CHECK,0);
        values.put(COL_IDLIEU,note.getIdLieu());
        return bdd.update(TABLE_NOTE, values, COL_ID + " = " +id, null);
    }
    public void removeNoteWithID(int id){
        if(id != -1)
            bdd.delete(TABLE_NOTE, COL_ID + " = " +id, null);
    }

    public void updateCheckWithId(int id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CHECK,"1");
        bdd.update(TABLE_NOTE,contentValues, COL_ID + " = " +id, null);
    }

    public int updateCheckByLieu(int id){
        ContentValues values = new ContentValues();
        values.put(COL_CHECK,1);
        return bdd.update(TABLE_NOTE, values, COL_IDLIEU + " = " +id, null);
    }

    public MesNotes getNoteWithId(int id){
        Cursor c = bdd.query(TABLE_NOTE, null, COL_ID + " = " + id, null, null, null, null);
        return cursorToNotes(c);
    }

    private MesNotes cursorToNotes(Cursor c){
        if (c.getCount() == 0)
            return null;

        c.moveToFirst();
        MesNotes note = new MesNotes();
        note.setId(c.getInt(NUM_COL_ID));
        note.setTitre(c.getString(NUM_COL_TITRE));
        note.setDesc(c.getString(NUM_COL_DESC));
        note.setIdLieu(c.getInt(NUM_COL_IDLIEU));
        c.close();

        return note;
    }

    public Cursor getNotes(int idLieu){
        return bdd.query(TABLE_NOTE,new String[]{COL_ID, COL_TITRE, COL_DESC},COL_CHECK + " = 0 AND " + COL_IDLIEU + " = " + idLieu,null,null,null,null);
    }

    public Cursor getNotes(){
        return bdd.query(TABLE_NOTE,new String[]{COL_ID, COL_TITRE, COL_DESC},COL_CHECK + " = 0 ",null,null,null,null);
    }

    public int countNotes(int id){
        Cursor cursor = getNotes(id);
        return cursor.getCount();
    }
}
