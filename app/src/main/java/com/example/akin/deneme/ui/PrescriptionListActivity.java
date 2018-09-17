package com.example.akin.deneme.ui;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.example.akin.deneme.R;
import com.example.akin.deneme.core.DataBase;
import com.example.akin.deneme.core.model.Sale;

import java.util.List;

public class PrescriptionListActivity extends AppCompatActivity {
    DataBase db;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SearchView searchView;
    private Context context;
    private List<Sale> sales;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_list);
        context = this;
        db = new DataBase(this);
        sales = db.getInDatePresSales();
        if(sales == null || sales.size() == 0){
            Toast.makeText(context,"Herhangi bir reçete satışı bulunmamaktadır", Toast.LENGTH_LONG).show();
            finish();
        }

        Toolbar toolbar = findViewById(R.id.toolbarPrescription);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Geçerli Reçeteler");
        configureRecyclerView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        sales = db.getOutOfDatePresSales();
        configureRecyclerView();
    }

    private void configureRecyclerView(){

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        try {
            mAdapter = new MyAdapter(db.getInDatePresSales(), context);
            mRecyclerView.setAdapter(mAdapter);
        }catch (NullPointerException e){
            Toast errorToast = Toast.makeText(this, "Herhangi bir reçete satışı bulunmamaktadır", Toast.LENGTH_SHORT);
            errorToast.show();
            finish();
        }
        catch (Exception e){
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
