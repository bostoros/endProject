package com.pooproject;

public class MesLieux {
    private int id;
    private String nom;
    private double lat;
    private double longi;
    private String adresse;

    public MesLieux(){}

    public MesLieux(String nom,String addr, double lat, double longi){
        this.nom = nom;
        this.lat = lat;
        this.longi = longi;
        this.adresse = addr;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;

    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse(){return adresse;}

    public void setAdresse(String addr){this.adresse = addr;}

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLong() {
        return longi;
    }

    public void setLong(double longi) {
        this.longi = longi;
    }

    public String toString(){
        return "ID : "+id+"\nNom : "+nom+"\nAdresse : "+adresse+"\nLatitude : "+lat+"\nLongitude : "+longi;
    }
}
