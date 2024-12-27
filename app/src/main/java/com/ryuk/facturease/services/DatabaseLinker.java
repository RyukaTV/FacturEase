package com.ryuk.facturease.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.stmt.query.In;
import com.ryuk.facturease.entities.Client;
import com.ryuk.facturease.entities.Facture;
import com.ryuk.facturease.entities.LigneFacture;
import com.ryuk.facturease.entities.Ville;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseLinker extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "bdd.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseLinker instance = null;

    private DatabaseLinker( Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseLinker getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseLinker(context);
        }
        return instance;
    }

    // Appelé lorsque la base de données est créé
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Client.class);
            TableUtils.createTable(connectionSource, Ville.class);
            TableUtils.createTable(connectionSource, Facture.class);
            TableUtils.createTable(connectionSource, LigneFacture.class);


            Dao<Ville, Integer> daoVille = this.getDao( Ville.class );
            daoVille.create(new Ville("Ajouter une ville", null));
            Ville vl=new Ville("Clermont-Ferrand", 63000);
            daoVille.create(vl);
            daoVille.create(new Ville("Lyon", 69000));

            Dao<Client, Integer> daoClient= this.getDao(Client.class);
            Client gab= new Client("JUILLARD", "Gabin","0768002097", "aaa", vl);
            daoClient.create(gab);

            Facture facture = new Facture();
            facture.setClient(gab);
            Dao<Facture, Integer> factureDao = this.getDao(Facture.class);
            factureDao.create(facture);

            LigneFacture ligne1 = new LigneFacture("Produit A", 2, 100, 20, facture);
            LigneFacture ligne2 = new LigneFacture("Produit B", 1, 50, 20, facture);
            LigneFacture ligne3 = new LigneFacture("Produit C", 1, 50, 20, facture);

            Dao<LigneFacture, Integer> ligneFactureDao = this.getDao(LigneFacture.class);
            ligneFactureDao.create(ligne1);
            ligneFactureDao.create(ligne2);
            ligneFactureDao.create(ligne3);

            facture.addLigneFacture(ligne1);
            facture.addLigneFacture(ligne2);
            facture.addLigneFacture(ligne3);
            Log.i( "DATABASE", "onCreate invoked" );
        } catch( Exception exception ) {
            Log.e( "DATABASE", "Can't create Database", exception );
        }
    }

    // Appelée lorsque l'application est mise à jour (quand le numéro de version est changée)
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Client.class, true );
            TableUtils.dropTable(connectionSource, Ville.class, true );
            TableUtils.dropTable(connectionSource, Facture.class, true);
            TableUtils.dropTable(connectionSource, LigneFacture.class, true);

            onCreate( database, connectionSource);
            Log.i( "DATABASE", "onUpgrade invoked" );
        } catch( Exception exception ) {
            Log.e( "DATABASE", "Can't upgrade Database", exception );
        }
    }
}