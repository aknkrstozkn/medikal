package com.example.akin.deneme.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.akin.deneme.R;
import com.example.akin.deneme.core.DataBase;
import com.example.akin.deneme.core.model.Patient;
import com.example.akin.deneme.core.model.Prescription;
import com.example.akin.deneme.core.model.Product;
import com.example.akin.deneme.core.model.ProductAmount;
import com.example.akin.deneme.core.model.Relative;
import com.example.akin.deneme.core.model.Relativity;
import com.example.akin.deneme.core.model.Sale;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class OutOfDatePresActivity extends AppCompatActivity {
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
            mAdapter = new MyAdapter(db.getOutOfDatePresSales());
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
