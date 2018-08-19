package com.example.akin.deneme.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.akin.deneme.R;
import com.example.akin.deneme.core.DataBase;
import com.example.akin.deneme.core.model.Patient;
import com.example.akin.deneme.core.model.Prescription;
import com.example.akin.deneme.core.model.Product;
import com.example.akin.deneme.core.model.Relative;
import com.example.akin.deneme.core.model.Relativity;
import com.example.akin.deneme.core.model.Sale;

import java.util.ArrayList;
import java.util.List;

public class PatientDetailActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DataBase db;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);
        db = new DataBase(this);
        context = this;

        //Adding some objects to test

        Intent intent = getIntent();

        String id = intent.getStringExtra("tc");
        Patient patient = db.getPatient(id);

        TextView textViewPatientName = findViewById(R.id.textViewPatientName);
        textViewPatientName.setText(patient.getName());

        TextView textViewPatientTC = findViewById(R.id.textViewPatientTC);
        textViewPatientTC.setText("TC: " + patient.getTc());

        TextView textViewPatientPhoneNumber = findViewById(R.id.textViewPatientPhoneNumber);
        textViewPatientPhoneNumber.setText("Tel: " + patient.getPhoneNumber());

        TextView textViewPatientAddress = findViewById(R.id.textViewPatientAddress);
        textViewPatientAddress.setText(patient.getAddress());

        mRecyclerView = findViewById(R.id.recyclerViewRelative);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(patient, context);
        mRecyclerView.setAdapter(mAdapter);
    }
}
