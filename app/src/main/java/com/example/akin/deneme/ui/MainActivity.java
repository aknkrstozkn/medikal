package com.example.akin.deneme.ui;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.akin.deneme.R;
import com.example.akin.deneme.core.DataBase;
import com.example.akin.deneme.core.model.Patient;
import com.example.akin.deneme.core.model.Sale;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    DataBase db;
    Button butOutOfDatePres, button, buttonValid;
    FloatingActionButton buttonAddSale;
    List<Sale> outOfDateSales, validSales;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SearchView searchView;
    private Context context;
    private List<Sale> sales;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean r;
        for(int result : grantResults){
            if (result != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(context,"Bütün izinleri vermelisiniz!",Toast.LENGTH_LONG).show();
                finish();
            }

        }

    }

    public void checkPermissions() {
        // Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CALL_PHONE},
                    0);
        } else {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarPatient);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hasta Ara, TC ya da İSİM");
        context = this;
        checkPermissions();
        db = new DataBase(this);

        if (db.getSales() != null && db.getSales().size() == 50)
            finish();

        butOutOfDatePres = findViewById(R.id.buttonOutOfDatePres);
        checkOutOfDatePres();

        butOutOfDatePres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OutOfDatePresActivity.class);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    throw e;
                }
            }
        });

        button = findViewById(R.id.buttonSales);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SalesList.class);
                startActivity(intent);
            }
        });

        buttonValid = findViewById(R.id.buttonValidPres);
        checkValidDatePres();

        buttonValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PrescriptionListActivity.class);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    throw e;
                }
            }
        });

        buttonAddSale = findViewById(R.id.floatingButtonAddSale);
        buttonAddSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddSaleActivity.class);
                startActivity(intent);
            }
        });


    }

    private void checkOutOfDatePres() {
        outOfDateSales = db.getOutOfDatePresSales();

        if (outOfDateSales == null) {

            butOutOfDatePres.setText("GÜNÜ GEÇMİŞ REÇETELER (" + 0 + ")");
        } else {
            butOutOfDatePres.setText("GÜNÜ GEÇMİŞ REÇETELER (" + outOfDateSales.size() + ")");
        }
    }

    private void checkValidDatePres() {
        validSales = db.getInDatePresSales();

        if (validSales == null) {
            buttonValid.setText("REÇETELER (" + 0 + ")");
        } else {
            buttonValid.setText("REÇETELER (" + validSales.size() + ")");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOutOfDatePres();
        checkValidDatePres();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        /**  searchView.setActivated(true);
         searchView.setQueryHint("Isim");
         searchView.onActionViewExpanded();
         searchView.setIconified(false);
         searchView.clearFocus(); **/

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, PatientDetailActivity.class);
                Patient tcPatient = db.getPatient(query);
                Patient namePatient = db.getPatientWithName(query.toUpperCase());
                String tc;
                if (tcPatient != null) {

                    tc = tcPatient.getTc();
                } else if (namePatient != null) {

                    tc = namePatient.getTc();
                } else {
                    Toast toast = Toast.makeText(context, "Aradığınız Hasta Bulunamadı..." + "\n"
                            + "Lütfen doğru aradığınızdan emin olun.", Toast.LENGTH_LONG);
                    toast.show();
                    return false;
                }
                intent.putExtra("tc", tc);
                startActivity(intent);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });
        return true;
    }


    protected boolean isAlwaysExpanded() {
        return true;
    }
}
