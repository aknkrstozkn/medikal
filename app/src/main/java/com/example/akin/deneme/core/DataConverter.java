package com.example.akin.deneme.core;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;

import com.example.akin.deneme.core.model.Patient;
import com.example.akin.deneme.core.model.Prescription;
import com.example.akin.deneme.core.model.Product;
import com.example.akin.deneme.core.model.ProductAmount;
import com.example.akin.deneme.core.model.Relative;
import com.example.akin.deneme.core.model.Relativity;
import com.example.akin.deneme.core.model.Sale;

import java.util.List;

public class DataConverter {

    protected ProductAmount productAmount(Cursor cursor, Product product) {

        ProductAmount productAmount = new ProductAmount();
        try {
            productAmount.setProductAmount(cursor.getInt(cursor.getColumnIndex("productAmount")));
            productAmount.setProduct(product);
        } catch (CursorIndexOutOfBoundsException e) {
            throw e;
        }
        return productAmount;
    }

    protected Product product(Cursor cursor) {

        Product product = new Product();

        try {

            product.setBarCode(cursor.getString(cursor.getColumnIndex("barCode")));
            product.setSerialNumber(cursor.getString(cursor.getColumnIndex("serialNumber")));
            product.setType(cursor.getString(cursor.getColumnIndex("type")));
        } catch (CursorIndexOutOfBoundsException e) {
            throw e;
        }
        return product;
    }

    protected Sale sale(Cursor cursor, Prescription prescription, Patient patient, Relative relative) {

        Sale sale = new Sale();

        try {
            sale.setPatient(patient);
            sale.setRelative(relative);
            sale.setPrescription(prescription);
            sale.setDate(cursor.getLong(cursor.getColumnIndex("saleDate")));
        } catch (CursorIndexOutOfBoundsException e) {
            throw e;
        }
        return sale;
    }

    protected Prescription prescription(Cursor cursor, List<ProductAmount> productAmount) {

        Prescription prescription = new Prescription();

        try {

            prescription.setId(cursor.getLong(cursor.getColumnIndex("id")));
            prescription.setsDate(cursor.getLong(cursor.getColumnIndex("sDate")));
            prescription.seteDate(cursor.getLong(cursor.getColumnIndex("eDate")));
            prescription.setPrescriptionsProductList(productAmount);
            prescription.setValidity(cursor.getInt(cursor.getColumnIndex("validity")));
        } catch (CursorIndexOutOfBoundsException e) {
            throw e;
        }
        return prescription;

    }

    protected Patient patient(Cursor cursor, List<Relativity> relativities) {

        Patient patient = new Patient();

        try {
            patient.setTc(cursor.getString(cursor.getColumnIndex("tc")));
            patient.setName(cursor.getString(cursor.getColumnIndex("name")));
            patient.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            patient.setCordinate(cursor.getString(cursor.getColumnIndex("cordinate")));
            patient.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phoneNumber")));
            patient.setRelatives(relativities);
        } catch (CursorIndexOutOfBoundsException e) {
            throw e;
        }
        return patient;
    }

    protected Relativity relativity(Cursor cursor, Relative relative) {

        Relativity relativity = new Relativity();

        try {
            relativity.setRelative(relative);
            relativity.setRelativity(cursor.getString(cursor.getColumnIndex("relativity")));
        } catch (CursorIndexOutOfBoundsException e) {
            throw e;
        }
        return relativity;

    }

    protected Relative relative(Cursor cursor) {

        Relative relative = new Relative();
        try {
            relative.setTc(cursor.getString(cursor.getColumnIndex("tc")));
            relative.setName(cursor.getString(cursor.getColumnIndex("name")));
            relative.setAddress(cursor.getString(cursor.getColumnIndex("address")));
            relative.setCordinate(cursor.getString(cursor.getColumnIndex("cordinate")));
            relative.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phoneNumber")));
        } catch (CursorIndexOutOfBoundsException e) {
            throw e;
        }
        return relative;
    }


}
