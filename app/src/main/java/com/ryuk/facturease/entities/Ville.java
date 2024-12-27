package com.ryuk.facturease.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Ville")
public class Ville {

    @DatabaseField( columnName = "id", generatedId = true )
    private int id;

    @DatabaseField
    private String nom;

    @DatabaseField
    private Integer PostalCode;

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

    public int getPostalCode(){
        return this.PostalCode;
    }

    public void setPostalCode(int postalCode) {
        PostalCode = postalCode;
    }

    public Ville() {
    }

    public Ville(String nom, Integer PostalCode) {
        this.nom = nom;
        this.PostalCode= PostalCode;
    }

    @Override
    public String toString() {
        if (PostalCode == null){
            return nom;
        }
        return nom + " (" + PostalCode + ")";
    }

    @Override
    public boolean equals(Object otherVille) {
        boolean same = false;
        if (otherVille instanceof Ville) {
            same = this.getId() == ((Ville)otherVille).getId();
        }
        return same;
    }
}
