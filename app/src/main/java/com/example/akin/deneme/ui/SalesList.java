package com.example.akin.deneme.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.akin.deneme.R;
import com.example.akin.deneme.core.DataBase;
import com.example.akin.deneme.core.model.Sale;

import java.util.List;

public class SalesList extends AppCompatActivity {

    private DataBase db;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SearchView searchView;
    private Context context;
    private List<Sale> sales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_list);

        Toolbar toolbar = findViewById(R.id.toolbarSales);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Satışlar");

        context = this;

        db = new DataBase(this);
        //sales = db.getSales();
        final RelativeLayout spinner = findViewById(R.id.pbHeaderProgress);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                sales = db.getSales();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                spinner.setVisibility(View.GONE);
                configureRecyclerView();
            }
        }.execute();

        //configureRecyclerView();
    }

    private void configureRecyclerView() {

        mRecyclerView = findViewById(R.id.recyclerViewSales);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        try {
            mAdapter = new MyAdapter(sales, context);
            mRecyclerView.setAdapter(mAdapter);
        } catch (NullPointerException e) {
            Toast errorToast = Toast.makeText(this, "Herhangi bir reçete satışı bulunmamaktadır", Toast.LENGTH_SHORT);
            errorToast.show();
        } catch (Exception e) {
            throw e;
        }
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
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }
}
