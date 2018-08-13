package com.example.akin.deneme.core;

import android.content.ContentValues;

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
        try {
            values.put("sDate", prescription.getsDate());
            values.put("eDate", prescription.geteDate());
            values.put("validity", 1);
        } catch (Exception e) {
            throw e;
        }

        return values;
    }

    protected ContentValues prescriptionProduct(Long prescriptionId, ProductAmount productAmount) {

        ContentValues values = new ContentValues();
        try {
            values.put("prescriptionId", prescriptionId);
            values.put("productBarCode", productAmount.getProduct().getBarCode());
            values.put("productAmount", productAmount.getProductAmount());
        } catch (Exception e) {
            throw e;
        }
        return values;
    }

    protected ContentValues patientAndRelative(String patientTC, Relativity relativity) {

        ContentValues values = new ContentValues();
        try {
            values.put("patientTC", patientTC);
            values.put("relativeTC", relativity.getRelative().getTc());
            values.put("relativity", relativity.getRelativity());
        } catch (Exception e) {
            throw e;
        }
        return values;
    }

    protected ContentValues person(Person person) {

        ContentValues values = new ContentValues();
        try {
            values.put("tc", person.getTc());
            values.put("name", person.getName());
            values.put("address", person.getAddress());
            values.put("cordinate", person.getCordinate());
            values.put("phoneNumber", person.getPhoneNumber());
        } catch (Exception e) {
            throw e;
        }
        return values;
    }


    protected ContentValues sale(Sale sale) {

        ContentValues values = new ContentValues();
        try {
            values.put("salePrescriptionId", sale.getPrescription().getId());
            values.put("salePatientTC", sale.getPatient().getTc());

            if(sale.getRelative() == null){
                values.putNull("saleRelativeTC");
            }else{
                values.put("saleRelativeTC", sale.getRelative().getTc());
            }

            values.put("saleDate", sale.getDate());
        } catch (Exception e) {
            throw e;
        }
        return values;
    }

    protected ContentValues product(Product product) {

        ContentValues values = new ContentValues();
        try {
            values.put("barCode", product.getBarCode());
            values.put("serialNumber", product.getSerialNumber());
            values.put("type", product.getType());
        } catch (Exception e) {
            throw e;
        }
        return values;
    }
}
