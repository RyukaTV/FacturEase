package com.ryuk.facturease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.ryuk.facturease.R;
import com.ryuk.facturease.components.AccordionView;
import com.ryuk.facturease.entities.Client;
import com.ryuk.facturease.entities.Facture;
import com.ryuk.facturease.entities.LigneFacture;
import com.ryuk.facturease.entities.Ville;
import com.ryuk.facturease.services.DatabaseLinker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class ViewFactureActivity extends AppCompatActivity {

    private int idFacture = 0;

    private Spinner spinnerClients;
    private Button validateButton, addLigneButton;
    private ImageButton btnBack;
    private String[] libelles= {"Produit: ", "Quantité:", "Prix HT:", "TVA:", "Total HT:", "Total TTC:", "Description:"};
    private LinearLayout linearLayout;
    private List<List<EditText>> allEditTexts = new ArrayList<>();
    private TextView Titre;
    private final AtomicBoolean canSetMargin= new AtomicBoolean(true);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_facture);

        Intent intent = this.getIntent();
        idFacture = intent.getIntExtra("idFacture", 0);

        DatabaseLinker databaseManager = DatabaseLinker.getInstance(this);
        Facture facture = null;
        List<Client> clients = new ArrayList<>();

        try {
            Dao<Facture, Integer> daoFacture = databaseManager.getDao(Facture.class);
            facture = daoFacture.queryForId(idFacture);

            Dao<Client, Integer> daoClients = databaseManager.getDao(Client.class);
            clients = daoClients.queryForAll();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        addLigneButton= findViewById(R.id.btn_add_ligne);
        spinnerClients = findViewById(R.id.spinner_clients);
        linearLayout = findViewById(R.id.container_infos);
        validateButton = findViewById(R.id.button_validate);
        Titre= findViewById(R.id.TitleTv);
        btnBack = findViewById(R.id.button_back);
        btnBack.bringToFront();
        btnBack.setOnClickListener(v -> finish());
        addLigneButton.setOnClickListener(v -> addNewLigne("Nouveau Produit"));

        ArrayAdapter<Client> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                clients);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClients.setAdapter(adapter);

        if (facture != null) {
            Titre.setText("Facture de " + facture.getClient());
            for (LigneFacture ligne : facture.getLignesFacture()) {
                List<EditText> editTexts = addNewLigne(ligne.getProduit());

                EditText editProduitLocal = editTexts.get(0);
                EditText editQuantiteLocal = editTexts.get(1);
                EditText editPrixHTLocal = editTexts.get(2);
                EditText editTVALocal = editTexts.get(3);
                EditText editTotalHTLocal = editTexts.get(4);
                EditText editTotalTTCLocal = editTexts.get(5);
                EditText editDescriptionLocal = editTexts.get(6);

                editProduitLocal.setText(ligne.getProduit());
                editQuantiteLocal.setText(String.valueOf(ligne.getQuantite()));
                editPrixHTLocal.setText(String.valueOf(ligne.getPrixHT()));
                editTVALocal.setText(String.valueOf(ligne.getTva()));
                editTotalHTLocal.setText(String.valueOf(ligne.getPrixTotalHT()));
                editTotalTTCLocal.setText(String.valueOf(ligne.getPrixTotalTTC()));
                editDescriptionLocal.setText(String.valueOf(ligne.getDescription()));


            }
            int selectionPosition = adapter.getPosition(facture.getClient());
            spinnerClients.setSelection(selectionPosition);
        }else {
            Titre.setText("Nouvelle Facture");
            addNewLigne("Nouveau Produit");
        }
        validateButton.setOnClickListener(v -> updateInfos());
    }

    private void updatePrixTotal(EditText editTextPrixHT, EditText editTextQuantite, EditText editTextTauxTVA, EditText editTextTotalHT, EditText editTextTotalTTC) {
        String prixHTString = editTextPrixHT.getText().toString();
        String quantiteString = editTextQuantite.getText().toString();
        String tauxTVAString = editTextTauxTVA.getText().toString();

        if (!prixHTString.isEmpty() && !quantiteString.isEmpty() && !tauxTVAString.isEmpty()) {
            try {
                double prixHT = Double.parseDouble(prixHTString);
                double quantite = Double.parseDouble(quantiteString);
                double tauxTVA = Double.parseDouble(tauxTVAString);
                double totalHT = prixHT * quantite;
                double totalTTC = totalHT + (totalHT * tauxTVA / 100);

                editTextTotalHT.setText(String.format(Locale.getDefault(), "%.2f", totalHT));
                editTextTotalTTC.setText(String.format(Locale.getDefault(), "%.2f", totalTTC));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            editTextTotalHT.setText("");
            editTextTotalTTC.setText("");
        }
    }

    public void updateInfos() {
        Client client = (Client) spinnerClients.getSelectedItem();
        if (client == null) {
            Toast.makeText(this, "Veuillez sélectionner un client.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseLinker databaseManager = DatabaseLinker.getInstance(this);
        try {
            Dao<Facture, Integer> daoFacture = databaseManager.getDao(Facture.class);
            Dao<LigneFacture, Integer> daoLigne = databaseManager.getDao(LigneFacture.class);

            Facture facture;
            if (idFacture != 0) {
                facture = daoFacture.queryForId(idFacture);

                if (facture != null) {

                    for (LigneFacture ligne : facture.getLignesFacture()) {
                        daoLigne.delete(ligne);
                    }

                    for (List<EditText> editTexts : allEditTexts) {
                        String produit = editTexts.get(0).getText().toString().replace(",", ".");
                        String quantiteString = editTexts.get(1).getText().toString().replace(",", ".");
                        String prixHTString = editTexts.get(2).getText().toString().replace(",", ".");
                        String tvaString = editTexts.get(3).getText().toString().replace(",", ".");
                        String totalhtString = editTexts.get(4).getText().toString().replace(",", ".");
                        String totalttcString = editTexts.get(5).getText().toString().replace(",", ".");
                        String descriptionString = editTexts.get(6).getText().toString();

                        if (produit.isEmpty() || quantiteString.isEmpty() || prixHTString.isEmpty() || tvaString.isEmpty()) {
                            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (descriptionString.isEmpty()){
                            descriptionString= "";
                        }

                        LigneFacture ligne = new LigneFacture();
                        ligne.setProduit(produit);
                        ligne.setQuantite(Float.parseFloat(quantiteString));
                        ligne.setPrixHT(Float.parseFloat(prixHTString));
                        ligne.setDescription(descriptionString);
                        ligne.setTva(Float.parseFloat(tvaString));
                        ligne.setPrixTotalHT(Float.parseFloat(totalhtString));
                        ligne.setPrixTotalTTC(Float.parseFloat(totalttcString));

                        ligne.setFacture(facture);
                        daoLigne.update(ligne);
                        facture.getLignesFacture().add(ligne);
                    }

                    facture.setClient(client);
                    daoFacture.update(facture);
                }
            } else {
                facture = new Facture();
                facture.setClient(client);

                daoFacture.create(facture);

                for (List<EditText> editTexts : allEditTexts) {
                    String produit = editTexts.get(0).getText().toString().replace(",", ".");
                    String quantiteString = editTexts.get(1).getText().toString().replace(",", ".");
                    String prixHTString = editTexts.get(2).getText().toString().replace(",", ".");
                    String tvaString = editTexts.get(3).getText().toString().replace(",", ".");
                    String totalhtString = editTexts.get(4).getText().toString().replace(",", ".");
                    String totalttcString = editTexts.get(5).getText().toString().replace(",", ".");
                    String descriptionString = editTexts.get(6).getText().toString();

                    if (produit.isEmpty() || quantiteString.isEmpty() || prixHTString.isEmpty() || tvaString.isEmpty()) {
                        Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (descriptionString.isEmpty()){
                        descriptionString= "";
                    }

                    LigneFacture nouvelleLigne = new LigneFacture();
                    nouvelleLigne.setProduit(produit);
                    nouvelleLigne.setQuantite(Float.parseFloat(quantiteString));
                    nouvelleLigne.setPrixHT(Float.parseFloat(prixHTString));
                    nouvelleLigne.setDescription(descriptionString);
                    nouvelleLigne.setTva(Float.parseFloat(tvaString));
                    nouvelleLigne.setPrixTotalHT(Float.parseFloat(totalhtString));
                    nouvelleLigne.setPrixTotalTTC(Float.parseFloat(totalttcString));

                    nouvelleLigne.setFacture(facture);
                    daoLigne.create(nouvelleLigne);
                }
                daoFacture.update(facture);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Une erreur est survenue lors de la mise à jour des informations.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    private List<EditText> addNewLigne(String title) {
        AccordionView accordionView = new AccordionView(this);
        if (canSetMargin.get()){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
           params.setMargins(0, 100, 0, 0);
           accordionView.setLayoutParams(params);
        }
        canSetMargin.set(false);
        accordionView.setTitle(title);

        EditText editProduitLocal = new EditText(this);
        EditText editQuantiteLocal = new EditText(this);
        EditText editPrixHTLocal = new EditText(this);
        EditText editTVALocal = new EditText(this);
        EditText editTotalHTLocal = new EditText(this);
        EditText editTotalTTCLocal = new EditText(this);
        EditText editDescriptionLocal= new EditText(this);

        editTotalHTLocal.setFocusable(false);
        editTotalTTCLocal.setFocusable(false);

        editQuantiteLocal.setInputType(InputType.TYPE_CLASS_NUMBER);
        editPrixHTLocal.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTVALocal.setInputType(InputType.TYPE_CLASS_NUMBER);
        List<EditText> editTexts = Arrays.asList(editProduitLocal, editQuantiteLocal, editPrixHTLocal, editTVALocal, editTotalHTLocal, editTotalTTCLocal, editDescriptionLocal);
        if (libelles.length != editTexts.size()) {
            throw new IllegalStateException("La taille de libelles doit correspondre au nombre d'EditTexts.");
        }
        allEditTexts.add(editTexts);
        for (int i = 0; i < libelles.length; i++) {
            TextView tv = new TextView(this);
            tv.setText(libelles[i]);
            accordionView.addContent(tv);
            accordionView.addContent(editTexts.get(i));
        }

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePrixTotal(editPrixHTLocal, editQuantiteLocal, editTVALocal, editTotalHTLocal, editTotalTTCLocal);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        editPrixHTLocal.addTextChangedListener(watcher);
        editQuantiteLocal.addTextChangedListener(watcher);
        editTVALocal.addTextChangedListener(watcher);

        linearLayout.addView(accordionView);
        return editTexts;
    }
}
