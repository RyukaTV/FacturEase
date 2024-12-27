package com.ryuk.facturease.entities;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Client")
public class Client {

    @DatabaseField( columnName = "id", generatedId = true )
    private int id;

    @DatabaseField
    private String nom;

    @DatabaseField
    private String prenom;

    @DatabaseField
    private String telephone;

    @DatabaseField
    private String adresse;

    @DatabaseField( canBeNull = true, foreign = true, foreignAutoCreate = true, foreignAutoRefresh= true, maxForeignAutoRefreshLevel=2 )
    private Ville ville;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Facture> Factures;

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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Ville getVille() {
        return ville;
    }

    public void setVille(Ville ville) {
        this.ville = ville;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public ForeignCollection<Facture> getFactures() {
        return Factures;
    }

    public void setFactures(ForeignCollection<Facture> factures) {
        this.Factures = factures;
    }

    public Client() {
    }

    public Client(String nom, String prenom, String telephone, String adresse, Ville ville) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.adresse = adresse;
        this.ville = ville;
    }

    @Override
    public String toString() {
        return nom + " " + prenom;
    }

    @Override
    public boolean equals(Object otherClient) {
        boolean same = false;
        if (otherClient instanceof Client) {
            same = this.getId() == ((Client)otherClient).getId();
        }
        return same;
    }
}
