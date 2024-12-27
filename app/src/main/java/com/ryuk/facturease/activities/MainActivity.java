package com.ryuk.facturease.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.ryuk.facturease.R;
import com.ryuk.facturease.entities.Client;
import com.ryuk.facturease.entities.Facture;
import com.ryuk.facturease.services.DatabaseLinker;
import com.ryuk.facturease.services.Service;

import java.sql.SQLException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TableLayout containerClients;
    private SearchView searchView;
    private List<Client> allClients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        containerClients = findViewById(R.id.container_factures);
        searchView = findViewById(R.id.searchClient);

        Service.swapActivityOnClick(findViewById(R.id.button_ajout_client), this, ViewClientActivity.class, false);
        Service.swapActivityOnClick(findViewById(R.id.btnRecap), this, FactureActivity.class, true);
        Service.swapActivityOnClick(findViewById(R.id.btnAjouter), this, ViewFactureActivity.class, false);

        createClients();
        setupSearch();
    }

    private void createClients() {
        containerClients.removeAllViews();
        DatabaseLinker databaseManager = DatabaseLinker.getInstance(this);
        try {
            Dao<Client, Integer> daoClient = databaseManager.getDao(Client.class);
            allClients = daoClient.queryForAll(); // Store all clients for filtering

            for (Client client : allClients) {
                addClientRow(client);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void addClientRow(Client client) {
        TableRow row = new TableRow(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setWeightSum(8);

        TableRow.LayoutParams param = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                4f
        );

        TextView labelNom = new TextView(this);
        labelNom.setLayoutParams(param);
        labelNom.setText(client.getNom());
        row.addView(labelNom);

        TextView labelPrenom = new TextView(this);
        labelPrenom.setLayoutParams(param);
        labelPrenom.setText(client.getPrenom());
        row.addView(labelPrenom);

        TableRow.LayoutParams paramButton = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        );

        ImageButton editClient = new ImageButton(this);
        editClient.setLayoutParams(paramButton);
        editClient.setBackground(null);
        editClient.setImageResource(R.drawable.edit);
        row.addView(editClient);
        editClient.setOnClickListener(v -> {
            editClient.setImageTintMode(PorterDuff.Mode.DARKEN);
            Intent monIntent = new Intent(MainActivity.this, ViewClientActivity.class);
            monIntent.putExtra("idClient", client.getId());
            startActivity(monIntent);
        });

        ImageButton deleteClient = new ImageButton(this);
        deleteClient.setLayoutParams(paramButton);
        deleteClient.setImageResource(R.drawable.delete);
        deleteClient.setBackground(null);
        row.addView(deleteClient);
        deleteClient.setOnClickListener(v -> {
            try {
                Dao<Client, Integer> daoClient = DatabaseLinker.getInstance(MainActivity.this).getDao(Client.class);
                Dao<Facture, Integer> daoFacture = DatabaseLinker.getInstance(MainActivity.this).getDao(Facture.class);
                DeleteBuilder<Facture, Integer> deleteBuilder = daoFacture.deleteBuilder();
                deleteBuilder.where().eq("client_id", client.getId());
                deleteBuilder.delete();
                daoClient.delete(client);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            ((ViewGroup) row.getParent()).removeView(row);
        });

        containerClients.addView(row);
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterClients(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterClients(newText);
                return false;
            }
        });
    }

    private void filterClients(String query) {
        query = query.toLowerCase().trim();
        containerClients.removeAllViews();


        for (Client client : allClients) {
            String clientNom = client.getNom().toLowerCase();
            String clientPrenom= client.getPrenom().toLowerCase();

            if (clientNom.contains(query) || clientPrenom.contains(query)
                    || (clientNom + " " + clientPrenom).contains(query)) {
                addClientRow(client); // Ajouter la ligne filtrée
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        containerClients.removeAllViews();
        createClients();
    }

}