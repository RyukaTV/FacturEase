package com.ryuk.facturease.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "LigneFacture")
public class LigneFacture {

    @DatabaseField(columnName = "id", generatedId = true)
    private int id;

    @DatabaseField
    private String produit, description="";

    @DatabaseField
    private float quantite, prixHT, tva, prixTotalHT, prixTotalTTC;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false,
            index = true)
    private Facture facture;

    public LigneFacture() {}

    public LigneFacture(String produit, float quantite, float prixHT, float tva, Facture facture) {
        this.produit = produit;
        this.quantite = quantite;
        this.prixHT = prixHT;
        this.tva = tva;
        this.prixTotalTTC = prixHT + (prixHT * tva / 100);
        this.facture = facture;
        this.prixTotalHT= prixHT*quantite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduit() {
        return produit;
    }

    public void setProduit(String produit) {
        this.produit = produit;
    }

    public float getQuantite() {
        return quantite;
    }

    public void setQuantite(float quantite) {
        this.quantite = quantite;
    }

    public float getTva() {
        return tva;
    }

    public void setTva(float tva) {
        this.tva = tva;
    }

    public float getPrixTotalHT() {
        return prixTotalHT;
    }


    public float getPrixTotalTTC() {
        return prixTotalTTC;
    }

    public float getPrixHT() {
        return prixHT;
    }

    public void setPrixHT(float prixHT){
        this.prixHT= prixHT;
    }

    public void setPrixTotalHT(float prixTotalHT) {
        this.prixTotalHT = prixTotalHT;
    }

    public void setPrixTotalTTC(float prixTotalTTC) {
        this.prixTotalTTC = prixTotalTTC;
    }
}

