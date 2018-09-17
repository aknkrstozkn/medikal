package com.example.akin.deneme.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
        final Patient patient = db.getPatient(id);

        TextView textViewPatientName = findViewById(R.id.textViewPatientName);
        textViewPatientName.setText(patient.getName());

        TextView textViewPatientTC = findViewById(R.id.textViewPatientTC);
        textViewPatientTC.setText("TC: " + patient.getTc());

        TextView textViewPatientPhoneNumber = findViewById(R.id.textViewPatientPhoneNumber);
        textViewPatientPhoneNumber.setText("Tel: " + patient.getPhoneNumber());
        textViewPatientPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = patient.getPhoneNumber();
                if (number.isEmpty())
                    return;
                String uri = "tel:0" + number;
                Intent intent = new Intent(Intent.ACTION_CALL);
                try {
                    intent.setData(Uri.parse(uri));
                }catch (Exception e){
                    return;
                }
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(intent);
            }
        });

        TextView textViewPatientAddress = findViewById(R.id.textViewPatientAddress);
        textViewPatientAddress.setText(patient.getAddress());

        mRecyclerView = findViewById(R.id.recyclerViewRelative);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(patient, context);
        mRecyclerView.setAdapter(mAdapter);

        Button buttonGPS =  findViewById(R.id.buttonPatientGPS);
        final Uri gmmIntentUri = Uri.parse("google.navigation:q=" + patient.getCordinate());
        buttonGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                Intent i = Intent.createChooser(mapIntent, "choose");
                startActivity(i);

            }
        });

        if(patient.getCordinate() == null)
            buttonGPS.setVisibility(View.INVISIBLE);
    }
}
