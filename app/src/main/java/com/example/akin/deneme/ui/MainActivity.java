package com.example.akin.deneme.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarPatient);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hasta Ara, TC YADA İSİM");

        context = this;
        db = new DataBase(this);

        butOutOfDatePres = findViewById(R.id.buttonOutOfDatePres);
        checkOutOfDatePres();

        butOutOfDatePres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OutOfDatePresActivity.class);
                startActivity(intent);
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
                startActivity(intent);
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
            butOutOfDatePres.setClickable(false);
            butOutOfDatePres.setText("GÜNÜ GEÇMİŞ REÇETELER (" + 0 + ")");
        } else {
            butOutOfDatePres.setText("GÜNÜ GEÇMİŞ REÇETELER (" + outOfDateSales.size() + ")");
        }
    }

    private void checkValidDatePres() {
        validSales = db.getInDatePresSales();

        if (validSales == null) {
            buttonValid.setClickable(false);
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
