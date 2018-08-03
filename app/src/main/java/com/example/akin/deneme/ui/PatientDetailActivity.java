package com.example.akin.deneme.ui;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);
        db = new DataBase(this);

        //Adding some objects to test

        Product product = new Product();
        product.setType("Bez");

        Patient patient = new Patient();
        patient.setName("AKIN KURSAT OZKAN");
        patient.setTc("16192221284");
        patient.setAddress("Kötekli Mahallesi 265. Sokak Fatma Altaş Apt. Kat 1 Daire 7");
        patient.setPhoneNumber("05458529996");

        List<Relativity> relativities = new ArrayList<>();

        for (int i = 0; i < 10; i++){

            Relative relative = new Relative();
            relative.setName("ORHAN ÖZKAN");
            relative.setTc("16195221066");
            relative.setAddress("Müminli Mahallesi Toki Konutlaru No 6 ADANA/SARIÇAM");
            relative.setPhoneNumber("05323036820");

            Relativity relativity = new Relativity();
            relativity.setRelative(relative);
            relativity.setRelativity("Babası");

            relativities.add(relativity);
        }

        patient.setRelatives(relativities);

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

        mAdapter = new MyAdapter(patient);
        mRecyclerView.setAdapter(mAdapter);
    }
}
