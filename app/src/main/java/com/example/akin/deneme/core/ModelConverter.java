package com.example.akin.deneme.core;

import android.content.ContentValues;

import com.example.akin.deneme.core.model.Patient;
import com.example.akin.deneme.core.model.Person;
import com.example.akin.deneme.core.model.Prescription;
import com.example.akin.deneme.core.model.Product;
import com.example.akin.deneme.core.model.ProductAmount;
import com.example.akin.deneme.core.model.Relativity;
import com.example.akin.deneme.core.model.Sale;

/**
 * Converting models to database format.
 */
public class ModelConverter {

    protected ContentValues prescription(Prescription prescription) {

        ContentValues values = new ContentValues();

        values.put("id", prescription.getId());
        values.put("sDate", prescription.getsDate());
        values.put("eDate", prescription.geteDate());
        values.put("validity", prescription.getValidity());

        return values;
    }

    protected ContentValues prescriptionProduct(Long prescriptionId, ProductAmount productAmount) {

        ContentValues values = new ContentValues();

        values.put("prescriptionId", prescriptionId);
        values.put("productBarCode", productAmount.getProduct().getBarCode());
        values.put("productAmount", productAmount.getProductAmount());

        return values;
    }

    protected ContentValues patientAndRelative(String patientTC, Relativity relativity) {

        ContentValues values = new ContentValues();

        values.put("patientTC", patientTC);
        values.put("relativeTC", relativity.getRelative().getTc());
        values.put("relativity", relativity.getRelativity());

        return values;
    }

    protected ContentValues person(Person person) {

        ContentValues values = new ContentValues();

        values.put("tc", person.getTc());
        values.put("name", person.getName());
        values.put("address", person.getAddress());
        values.put("cordinate", person.getCordinate());
        values.put("phoneNumber", person.getPhoneNumber());

        return values;
    }


    protected ContentValues sale(Sale sale) {

        ContentValues values = new ContentValues();

        values.put("prescriptionId", sale.getPrescription().getId());
        values.put("patienId", sale.getPatient().getTc());
        values.put("relativeId", sale.getRelative().getTc());
        values.put("date", sale.getDate());

        return values;
    }

    protected ContentValues product(Product product) {

        ContentValues values = new ContentValues();

        values.put("barCode", product.getBarCode());
        values.put("serialNumber", product.getSerialNumber());
        values.put("type", product.getType());

        return values;
    }
}
