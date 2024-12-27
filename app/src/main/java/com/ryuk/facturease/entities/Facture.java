package com.ryuk.facturease.entities;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Facture")
public class Facture {

    @DatabaseField(columnName = "id", generatedId = true)
    private int id;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<LigneFacture> lignesFacture;


    @DatabaseField( canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh= true, maxForeignAutoRefreshLevel=2)
    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ForeignCollection<LigneFacture> getLignesFacture() {
        return lignesFacture;
    }

    public void setLignesFacture(ForeignCollection<LigneFacture> lignesFacture) {
        this.lignesFacture = lignesFacture;
    }

    public void addLigneFacture(LigneFacture ligne) {
        lignesFacture.add(ligne);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
