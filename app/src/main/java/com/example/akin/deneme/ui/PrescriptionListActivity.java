package com.example.akin.deneme.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.akin.deneme.R;
import com.example.akin.deneme.core.DataBase;

public class PrescriptionListActivity extends AppCompatActivity {
    DataBase db;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_list);

        db = new DataBase(this);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mLayoutManager);

        try {
            mAdapter = new MyAdapter(db.getInDatePresSales());
            mRecyclerView.setAdapter(mAdapter);
        }catch (NullPointerException e){
            Toast errorToast = Toast.makeText(this, "Herhangi bir reçete satışı bulunmamaktadır", Toast.LENGTH_SHORT);
            errorToast.show();
        }
        catch (Exception e){
            throw e;
        }
    }
}
