package com.example.akin.deneme.core;

import android.database.Cursor;

import com.example.akin.deneme.core.model.Patient;
import com.example.akin.deneme.core.model.Person;
import com.example.akin.deneme.core.model.Prescription;
import com.example.akin.deneme.core.model.Product;
import com.example.akin.deneme.core.model.ProductAmount;
import com.example.akin.deneme.core.model.Relative;
import com.example.akin.deneme.core.model.Relativity;
import com.example.akin.deneme.core.model.Sale;

import java.util.ArrayList;
import java.util.List;

public class DataConverter {

    protected ProductAmount productAmount(Cursor cursor, Product product) {

        ProductAmount productAmount = new ProductAmount();

        productAmount.setProductAmount(cursor.getInt(cursor.getColumnIndex("productAmount")));
        productAmount.setProduct(product);

        return productAmount;
    }

    protected Product product(Cursor cursor) {

        Product product = new Product();

        product.setBarCode(cursor.getString(0));
        product.setSerialNumber(cursor.getString(1));
        product.setType(cursor.getString(2));

        return product;
    }

    protected Sale sale(Cursor cursor, Prescription prescription, Patient patient, Relative relative) {

        Sale sale = new Sale();
        cursor.moveToFirst();

        sale.setPatient(patient);
        sale.setRelative(relative);
        sale.setPrescription(prescription);
        sale.setDate(cursor.getLong(cursor.getColumnIndex("date")));

        return sale;
    }

    protected Prescription prescription(Cursor cursor, ArrayList<ProductAmount> productAmount) {

        Prescription prescription = new Prescription();
        cursor.moveToFirst();

        prescription.setId(cursor.getLong(0));
        prescription.setDate(cursor.getLong(1));
        prescription.setDuration(cursor.getInt(2));
        prescription.setPrescriptionsProductList(productAmount);

        return prescription;

    }

    protected Patient patient(Cursor cursor, ArrayList<Relativity>  relativities) {

        Patient patient = new Patient();
        cursor.moveToFirst();

        patient.setTc(cursor.getString(0));
        patient.setName(cursor.getString(1));
        patient.setSurname(cursor.getString(2));
        patient.setAddress(cursor.getString(3));
        patient.setCordinate(cursor.getString(4));
        patient.setPhoneNumber(cursor.getString(5));
        patient.setRelatives(relativities);

        return patient;
    }

    protected Relativity relativity(Cursor cursor, Relative relative){

        Relativity relativity = new Relativity();

        relativity.setRelative(relative);
        relativity.setRelativity(cursor.getColumnName(cursor.getColumnIndex("relativity")));

        return relativity;

    }

    protected Relative relative(Cursor cursor) {

        Relative relative = new Relative();
        cursor.moveToFirst();

        relative.setTc(cursor.getString(0));
        relative.setName(cursor.getString(1));
        relative.setSurname(cursor.getString(2));
        relative.setAddress(cursor.getString(3));
        relative.setCordinate(cursor.getString(4));
        relative.setPhoneNumber(cursor.getString(5));

        return relative;
    }


}
