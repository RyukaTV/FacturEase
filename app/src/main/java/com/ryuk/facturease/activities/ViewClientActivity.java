package com.ryuk.facturease.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.j256.ormlite.dao.Dao;
import com.ryuk.facturease.R;
import com.ryuk.facturease.entities.Client;
import com.ryuk.facturease.entities.Ville;
import com.ryuk.facturease.services.DatabaseLinker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ViewClientActivity extends AppCompatActivity {

    private int idClient = 0;

    private EditText editNom;
    private EditText editPrenom;
    private EditText editTelephone;
    private EditText editAdresse;
    private Spinner spinnerVilles;
    private Button validateButton;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_client);

        Intent intent = this.getIntent();
        idClient = intent.getIntExtra("idClient", 0);

        DatabaseLinker databaseManager = DatabaseLinker.getInstance(this);
        Client client = null;
        List<Ville> villes = new ArrayList<>();

        try {
            Dao<Client, Integer> daoClient = databaseManager.getDao( Client.class );
            client = daoClient.queryForId(idClient);

            Dao<Ville, Integer> daoVille = databaseManager.getDao( Ville.class );
            villes = daoVille.queryForAll();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        editNom = findViewById(R.id.edit_nom);
        editPrenom = findViewById(R.id.edit_prixht);
        editTelephone = findViewById(R.id.edit_telephone);
        editAdresse = findViewById(R.id.edit_adresse);
        spinnerVilles = findViewById(R.id.spinner_clients);
        validateButton = findViewById(R.id.button_validate);
        btnBack = findViewById(R.id.button_back);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        ArrayAdapter<Ville> adapter = new ArrayAdapter<Ville>(this,
                android.R.layout.simple_spinner_item,
                villes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVilles.setAdapter(adapter);
        if (villes.size() > 1) {
            spinnerVilles.setSelection(1);
        }
        spinnerVilles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Ville selectedVille = (Ville) parent.getItemAtPosition(position);
                if (selectedVille.getNom().equals("Ajouter une ville")) {
                    // Ouvrir une boîte de dialogue pour ajouter une ville
                    showAddVilleDialog(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (client != null) {
            editNom.setText(client.getNom());
            editPrenom.setText(client.getPrenom());
            editTelephone.setText(client.getTelephone());
            editAdresse.setText(client.getAdresse());

            int selectionPosition= adapter.getPosition(client.getVille());
            spinnerVilles.setSelection(selectionPosition);
        }

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfos();
            }
        });
    }

    private void showAddVilleDialog(ArrayAdapter<Ville> adapter) {
        // Créer une boîte de dialogue avec un champ de texte pour saisir le nom et le code postal
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajouter une ville");

        // Créer les champs de saisie pour la ville et le code postal
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_ville, null);
        builder.setView(customLayout);

        EditText editTextVille = customLayout.findViewById(R.id.editTextVille);
        EditText editTextCodePostal = customLayout.findViewById(R.id.editTextCodePostal);

        // Définir les boutons de la boîte de dialogue
        builder.setPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String villeNom = editTextVille.getText().toString();
                String codePostal = editTextCodePostal.getText().toString();

                if (!villeNom.isEmpty() && !codePostal.isEmpty()) {
                    DatabaseLinker linker= DatabaseLinker.getInstance(ViewClientActivity.this);
                    try {
                        Dao<Ville, Integer> daoVille = linker.getDao(Ville.class);
                        if (villeExiste(daoVille, villeNom, codePostal)) {
                            Toast.makeText(ViewClientActivity.this, "La ville existe déjà : " + villeNom, Toast.LENGTH_SHORT).show();
                        } else {
                            // Si la ville n'existe pas, on l'ajoute
                            Ville nouvelleVille = new Ville(villeNom, Integer.valueOf(codePostal));
                            daoVille.create(nouvelleVille);
                            adapter.add(nouvelleVille);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(ViewClientActivity.this, "Ville ajoutée : " + villeNom, Toast.LENGTH_SHORT).show();
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        Toast.makeText(ViewClientActivity.this, "Erreur lors de l'ajout de la ville", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ViewClientActivity.this, "Veuillez saisir le nom de la ville et le code postal.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Afficher la boîte de dialogue
        builder.create().show();
    }

    private boolean villeExiste(Dao<Ville, Integer> daoVille, String villeNom, String codePostal) {
        try {
            // Recherche par nom et code postal
            List<Ville> result = daoVille.queryBuilder()
                    .where()
                    .eq("nom", villeNom)
                    .and()
                    .eq("postalcode", Integer.valueOf(codePostal))
                    .query();
            return !result.isEmpty(); // Si la liste n'est pas vide, la ville existe
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // En cas d'erreur SQL, retourner false
        }
    }

    public void updateInfos() {
        String nom = editNom.getText().toString();
        String prenom = editPrenom.getText().toString();
        String telephone = editTelephone.getText().toString();
        String adresse = editAdresse.getText().toString();
        Ville ville = (Ville)spinnerVilles.getSelectedItem();

        DatabaseLinker databaseManager = DatabaseLinker.getInstance(this);
        Client client = null;
        Dao<Client, Integer> daoClient = null;
        try {
            daoClient = databaseManager.getDao(Client.class);

            if (idClient != 0) {
                client = daoClient.queryForId(idClient);
                client.setNom(nom);
                client.setPrenom(prenom);
                client.setTelephone(telephone);
                client.setAdresse(adresse);
                client.setVille(ville);

                daoClient.update(client);
            }
            else {
                client = new Client();
                client.setNom(nom);
                client.setPrenom(prenom);
                client.setTelephone(telephone);
                client.setAdresse(adresse);
                client.setVille(ville);

                daoClient.create(client);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

}
