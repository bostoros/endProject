package com.pooproject;

public class MesNotes {
    private int id;
    private String titre;
    private String desc;
    private int idLieu;

    public MesNotes(){

    }

    public MesNotes(String titre, String desc, int idLieu){
        this.titre = titre;
        this.desc = desc;
        this.idLieu = idLieu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDesc(){return desc;}

    public void setDesc(String desc){
        this.desc = desc;
    }

    public int getIdLieu(){return idLieu;}

    public void setIdLieu(int idLieu){
        this.idLieu = idLieu;
    }
}
