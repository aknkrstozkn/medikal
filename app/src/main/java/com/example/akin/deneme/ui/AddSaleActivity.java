package com.example.akin.deneme.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.example.akin.deneme.R;
import com.example.akin.deneme.core.DataBase;
import com.example.akin.deneme.core.model.Patient;
import com.example.akin.deneme.core.model.Prescription;
import com.example.akin.deneme.core.model.Product;
import com.example.akin.deneme.core.model.ProductAmount;
import com.example.akin.deneme.core.model.Sale;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddSaleActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DataBase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsale);
        db = new DataBase(this);

        //Adding some objects to test
        List<Sale> sales = new ArrayList<>();
        List<Prescription> prescriptions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setType("Bez");

            Patient patient = new Patient();
            patient.setName("AKIN KURSAT OZKAN");
            patient.setTc("16192221284");

            Sale sale = new Sale();
            sale.setPatient(patient);

            Product product1 = new Product();
            product1.setType("Sonda");

            ProductAmount productAmount = new ProductAmount();
            productAmount.setProduct(product);
            productAmount.setProductAmount(240);

            ProductAmount productAmount1 = new ProductAmount();
            productAmount1.setProduct(product1);
            productAmount1.setProductAmount(120);

            List<ProductAmount> productAmountList = new ArrayList<>();
            productAmountList.add(productAmount);
            productAmountList.add(productAmount1);

            Prescription prescription = new Prescription();
            Calendar calendar = Calendar.getInstance();
            sale.setDate(calendar.getTimeInMillis());
            prescription.seteDate(calendar.getTimeInMillis());
            prescription.setsDate(calendar.getTimeInMillis());
            prescription.setValidity(1);
            prescription.setPrescriptionsProductList(productAmountList);

            sale.setPrescription(prescription);

            sales.add(sale);
        }

        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(sales);
        mRecyclerView.setAdapter(mAdapter);

    }
}
