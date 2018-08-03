package com.example.akin.deneme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.akin.deneme.R;
import com.example.akin.deneme.core.DataBase;

public class MainActivity extends AppCompatActivity {
    DataBase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DataBase(getApplicationContext());

        final Button butOutOfDatePres = findViewById(R.id.buttonOutOfDatePres);
        int presAmount = db.getOutOfDatePres().size();
        butOutOfDatePres.setText(butOutOfDatePres.getText() + String.valueOf(presAmount) + " )");

        butOutOfDatePres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddSaleActivity.class);
                startActivity(intent);
            }
        });

        Button button = findViewById(R.id.buttonSales);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SaleDetailActivity.class);
                startActivity(intent);
            }
        });

        Button buttonValid = findViewById(R.id.buttonValidPres);
        buttonValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PatientDetailActivity.class);
                startActivity(intent);
            }
        });

    }



}
