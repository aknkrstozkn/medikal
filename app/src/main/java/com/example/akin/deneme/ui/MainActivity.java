package com.example.akin.deneme.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DataBase(this);

        Button butOutOfDatePres = findViewById(R.id.buttonOutOfDatePres);
        List<Sale> outOfDateSales = db.getOutOfDatePresSales();

        if(outOfDateSales == null){
            butOutOfDatePres.setClickable(false);
            butOutOfDatePres.setText("GÜNÜ GEÇMİŞ REÇETELER (" + 0 + ")");
        }else{
            butOutOfDatePres.setText("GÜNÜ GEÇMİŞ REÇETELER (" + outOfDateSales.size() + ")");
        }
        butOutOfDatePres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OutOfDatePresActivity.class);
                startActivity(intent);
            }
        });

        Button button = findViewById(R.id.buttonSales);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddSaleActivity.class);
                startActivity(intent);
            }
        });

        Button buttonValid = findViewById(R.id.buttonValidPres);
        List<Sale> validSales = db.getInDatePresSales();

        if(validSales == null){
            buttonValid.setClickable(false);
            buttonValid.setText("REÇETELER (" + 0 + ")");
        }else{
            buttonValid.setText("REÇETELER (" + validSales.size() + ")");
        }
        buttonValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PrescriptionListActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton buttonAddSale = findViewById(R.id.floatingButtonAddSale);
        buttonAddSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddSaleActivity.class);
                startActivity(intent);
            }
        });



    }


}
