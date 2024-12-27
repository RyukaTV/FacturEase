package com.ryuk.facturease.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.j256.ormlite.dao.Dao;
import com.ryuk.facturease.R;
import com.ryuk.facturease.entities.Facture;
import com.ryuk.facturease.entities.LigneFacture;
import com.ryuk.facturease.services.DatabaseLinker;
import com.ryuk.facturease.services.Service;

import java.sql.SQLException;
import java.util.List;

public class FactureActivity extends AppCompatActivity {

    private TableLayout containerFactures;
    private List<Facture> allFactures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facture);

        containerFactures= findViewById(R.id.container_factures);
        Service.swapActivityOnClick(findViewById(R.id.btnProfil), this, MainActivity.class, true);
        Service.swapActivityOnClick(findViewById(R.id.btnAjouter), this, ViewFactureActivity.class, false);
        createFactures();
    }


    private void createFactures() {
        containerFactures.removeAllViews();
        DatabaseLinker databaseManager = DatabaseLinker.getInstance(this);
        try {
            Dao<Facture, Integer> daoFacture= databaseManager.getDao(Facture.class);
            allFactures = daoFacture.queryForAll();

            for (Facture facture : allFactures) {
                addClientRow(facture);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void addClientRow(Facture facture) {
        TableRow rowClient = new TableRow(this);
        rowClient.setGravity(Gravity.CENTER_VERTICAL);
        rowClient.setWeightSum(8);

        TableRow.LayoutParams paramClient = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                8f
        );
        TextView labelNomPrenom = new TextView(this);
        labelNomPrenom.setLayoutParams(paramClient);
        labelNomPrenom.setText(facture.getClient().getNom()+" "+facture.getClient().getPrenom());
        labelNomPrenom.setTypeface(null, Typeface.BOLD); // Texte en gras
        rowClient.addView(labelNomPrenom);

        ImageButton editFacture = new ImageButton(this);
        editFacture.setLayoutParams(paramClient);
        editFacture.setBackground(null);
        editFacture.setImageResource(R.drawable.edit);
        rowClient.addView(editFacture);
        editFacture.setOnClickListener(v -> {
            editFacture.setImageTintMode(PorterDuff.Mode.DARKEN);
            Intent monIntent = new Intent(this, ViewFactureActivity.class);
            monIntent.putExtra("idFacture", facture.getId());
            startActivity(monIntent);
        });

        ImageButton deleteFacture = new ImageButton(this);
        deleteFacture.setLayoutParams(paramClient);
        deleteFacture.setImageResource(R.drawable.delete);
        deleteFacture.setBackground(null);
        rowClient.addView(deleteFacture);
        deleteFacture.setOnClickListener(v -> {
            try {
                Dao<Facture, Integer> daoFacture = DatabaseLinker.getInstance(this).getDao(Facture.class);
                daoFacture.delete(facture);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            createFactures();
        });

        containerFactures.addView(rowClient);

        TableRow rowDetail = new TableRow(this);
        rowDetail.setGravity(Gravity.CENTER_VERTICAL);
        rowDetail.setWeightSum(8);
        TableRow.LayoutParams paramDetail = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        );

        for (String index: new String[]{"Produit", "Quantité", "Prix HT", "TVA", "TotalHT", "TotalTTC"}) {
            TextView labelDetail = new TextView(this);
            labelDetail.setLayoutParams(paramDetail);
            labelDetail.setTypeface(null, Typeface.BOLD_ITALIC);
            labelDetail.setText(index);
            rowDetail.addView(labelDetail);
        }
        containerFactures.addView(rowDetail);

        for (LigneFacture ligne : facture.getLignesFacture()) {
            TableRow rowLigne = new TableRow(this);
            rowLigne.setGravity(Gravity.CENTER_VERTICAL);
            rowLigne.setWeightSum(8);
            TableRow.LayoutParams paramLigne = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
            );

            TextView labelProduit = new TextView(this);
            labelProduit.setLayoutParams(paramLigne);
            labelProduit.setText(ligne.getProduit());
            rowLigne.addView(labelProduit);

            TextView labelQuantite = new TextView(this);
            labelQuantite.setLayoutParams(paramLigne);
            labelQuantite.setText(String.valueOf(ligne.getQuantite()));
            rowLigne.addView(labelQuantite);

            TextView labelPrixHT = new TextView(this);
            labelPrixHT.setLayoutParams(paramLigne);
            labelPrixHT.setText(String.valueOf(ligne.getPrixHT()));
            rowLigne.addView(labelPrixHT);

            TextView labelTVA = new TextView(this);
            labelTVA.setLayoutParams(paramLigne);
            labelTVA.setText(String.valueOf(ligne.getTva()));
            rowLigne.addView(labelTVA);

            TextView labelPrixTotalHT = new TextView(this);
            labelPrixTotalHT.setLayoutParams(paramLigne);
            labelPrixTotalHT.setText(String.valueOf(ligne.getPrixTotalHT()));
            rowLigne.addView(labelPrixTotalHT);

            TextView labelPrixTotalTTC = new TextView(this);
            labelPrixTotalTTC.setLayoutParams(paramLigne);
            labelPrixTotalTTC.setText(Html.fromHtml("<u>"+ligne.getPrixTotalTTC()+"</u>"));
            rowLigne.addView(labelPrixTotalTTC);
            containerFactures.addView(rowLigne);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        containerFactures.removeAllViews();
        createFactures();
    }
}