package com.example.akin.deneme.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
        Relative relative;
        List<Relativity> relativities;
        Patient patient;
        Product product;
        List<ProductAmount> productAmounts;
        Prescription prescription;
        Sale sale;
        relative = new Relative();
        relativities = new ArrayList<>();
        patient = new Patient();
        product = new Product();
        productAmounts = new ArrayList<>();
        prescription = new Prescription();
        sale = new Sale();

        relative.setName("Akın Kürşat Özkan");
        relative.setTc("16192221284");
        relative.setAddress("Kötekli Mahallesi");
        relative.setPhoneNumber("05458521996");
        relative.setCordinate("dasdas");

        Relativity relativity = new Relativity();
        relativity.setRelative(relative);
        relativity.setRelativity("Oğlu");

        relativities.add(relativity);

        patient.setName("ORHAN ÖZKAN");
        patient.setTc("16195221066");
        patient.setAddress("Müminli Mahallesi");
        patient.setPhoneNumber("05323036820");
        patient.setRelatives(relativities);
        patient.setCordinate("asdasd");

        product.setType("Bez");
        product.setSerialNumber("asdasd");
        product.setBarCode("asdasds");

        ProductAmount productAmount = new ProductAmount();
        productAmount.setProduct(product);
        productAmount.setProductAmount(150);

        productAmounts.add(productAmount);

        Calendar calendar = Calendar.getInstance();

        prescription.seteDate(calendar.getTimeInMillis());
        prescription.setsDate(calendar.getTimeInMillis());
        prescription.setPrescriptionsProductList(productAmounts);

        sale.setRelative(relative);
        sale.setPatient(patient);
        sale.setDate(calendar.getTimeInMillis());
        sale.setPrescription(prescription);
        List<Sale> sales = new ArrayList<>();
        sales.add(sale);
        mAdapter = new MyAdapter(db.getSales());
        mRecyclerView.setAdapter(mAdapter);


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dikkat!");
        builder.setMessage(String.valueOf(db.get()));
        builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {



            }
        });


        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


            }
        });


        builder.show();

    }


}
