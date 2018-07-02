package com.example.akin.deneme.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.print.PrintAttributes;
import android.support.annotation.CheckResult;

import com.example.akin.deneme.core.model.*;

import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    private ModelConverter modelConverter;
    private SQLiteDatabase dbW;

    public DataBase(Context context) {
        super(context, "MedicalDatabase", null, 1);
        modelConverter = new ModelConverter();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTablePerson = "CREATE TABLE " + "person" + "("
                + "tc" + " INTEGER PRIMARY KEY,"
                + "name" + " TEXT,"
                + "surname" + " TEXT,"
                + "address" + " TEXT,"
                + "cordinate" + " TEXT,"
                + "phoneNumber" + " INTEGER)";

        String createTableProduct = "CREATE TABLE " + "product" + "("
                + "barCode" + " TEXT PRIMARY KEY,"
                + "serialNumber" + " TEXT,"
                + "type" + " TEXT)";
        String createTablePrescription = "CREATE TABLE " + "prescription" + "("
                + "id" + " INTEGER PRIMARY KEY,"
                + "date" + " LONG,"
                + "duration" + " INTEGER)";

        String createTablePatientRelative = "CREATE TABLE " + "patientRelative" + "("
                + "patientTC" + " INTEGER REFERENCES person(tc),"
                + "relativeTC" + " INTEGER REFERENCES person(tc),"
                + "relativity" + " TEXT,"
                + "PRIMARY KEY(patientTC,relativeTC))";


        String createTablePrescriptionProduct = "CREATE TABLE " + "prescriptionProduct" + "("
                + "prescriptionId" + " INTEGER REFERENCES prescription(id),"
                + "productBarCode" + " INTEGER REFERENCES product(barCode),"
                + "productAmount" + " INTEGER,"
                + "PRIMARY KEY(prescriptionId,productBarCode))";

        String createTableSale = "CREATE TABLE " + "sale" + "("
                + "prescriptionId" + " INTEGER REFERENCES prescription(Id),"
                + "patientTC" + " INTEGER REFERENCES person(tc),"
                + "relativeTC" + " INTEGER REFERENCES person(tc),"
                + "date" + " LONG,"
                + "PRIMARY KEY(prescriptionId))";

        db.execSQL(createTablePerson);
        db.execSQL(createTableProduct);
        db.execSQL(createTablePrescription);
        db.execSQL(createTablePatientRelative);
        db.execSQL(createTablePrescriptionProduct);
        db.execSQL(createTableSale);

    }

    public void deletePerson(Person person) {

        try (SQLiteDatabase db = this.getWritableDatabase()) {

            String selectQuery = "SELECT * FROM " + "sale" + " WHERE " + "tc" + "=" + person.getTc();

            try (Cursor cursor = db.rawQuery(selectQuery, null);) {

                if (cursor.getCount() == 1) {

                    dbW.delete("person", "tc = ?", new String[]{String.valueOf(person.getTc())});
                }
            }
        }
    }

    public void deletePatientRelative(Patient patient, Relative relative) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {

            String selectQuery = "SELECT * FROM " + "sale" + " WHERE " + "patientTC" + "=" + patient.getTc()
                    + "and relativeTC = " + relative.getTc();

            try (Cursor cursor = db.rawQuery(selectQuery, null)) {

                if (cursor.getCount() == 1) {

                    dbW.delete("patientRelative", "patientTC = ? and relativeTC = ?",
                            new String[]{String.valueOf(patient.getTc()), String.valueOf(relative.getTc())});
                }
            }
        }
    }

    public boolean addPerson(Person person) {

        try (SQLiteDatabase db = this.getWritableDatabase()) {

            String selectQuery = "SELECT * FROM " + "person" + " WHERE " + "tc" + "=" + person.getTc();

            try (Cursor cursor = db.rawQuery(selectQuery, null)) {

                if (cursor.getCount() == 0) {

                    dbW.insert("patientRelative", null, modelConverter.person(person));

                    return true;
                }
            }
        }
        return false;
    }

    public boolean addPatientRelative(Patient patient) {

        try (SQLiteDatabase db = this.getWritableDatabase()) {

            String selectQuery = "SELECT * FROM " + "patientRelative" + " WHERE " + "patientTC " + "= " + patient.getTc() + " and relativeTC = " + patient.getRelatives().get(0).getRelative().getTc();

            try (Cursor cursor = db.rawQuery(selectQuery, null)) {

                if (cursor.getCount() == 0) {

                    dbW.insert("patientRelative", null, modelConverter.patientAndRelative(patient));

                    return true;
                }
            }
        }
        return false;
    }

    public boolean addProduct(Product product) {

        try (SQLiteDatabase db = this.getWritableDatabase()) {

            String selectQuery = "SELECT * FROM " + "product" + " WHERE " + "barCode" + "=" + product.getBarCode();

            try (Cursor cursor = db.rawQuery(selectQuery, null)) {

                if (cursor.getCount() == 0) {

                    if (cursor.getCount() == 0)
                        dbW.insert("patientRelative", null, modelConverter.product(product));

                    return true;
                }
            }
        }
        return false;
    }

    public void deleteProduct(Product product) {

        try (SQLiteDatabase db = this.getWritableDatabase()) {

            String selectQuery = "SELECT * FROM " + "prescriptionProduct" + " WHERE " + "productBarCode" + "=" + product.getBarCode();

            try (Cursor cursor = db.rawQuery(selectQuery, null)) {

                if (cursor.getCount() == 1) {

                    dbW.delete("product", "barCode = ?", new String[]{String.valueOf(product.getBarCode())});
                }
            }
        }
    }

    public void makeSale(Sale sale) {

        try(SQLiteDatabase db = this.getWritableDatabase()) {

            addPerson(sale.getPatient());
            addPerson(sale.getPatient().getRelatives().get(0).getRelative());
            addPatientRelative(sale.getPatient());
            Long prescriptionId = db.insert("prescription", null, modelConverter.prescription(sale.getPrescription()));
            sale.getPrescription().setId(prescriptionId);

            for (ProductAmount productAmount : sale.getPrescription().getPrescriptionsProductList()) {

                addProduct(productAmount.getProduct());
                db.insert("prescriptionProduct", null, modelConverter.prescriptionProduct(prescriptionId, productAmount));
            }

            db.insert("sale", null, modelConverter.sale(sale));
        }
    }

    public void deleteSale(Sale sale) {

        dbW = this.getWritableDatabase();

        dbW.delete("sale", "prescriptionId = ?", new String[]{String.valueOf(sale.getPrescription().getId())});
        dbW.delete("prescriptionProduct", "prescriptionId = ?", new String[]{String.valueOf(sale.getPrescription().getId())});

        for (ProductAmount productAmount : sale.getPrescription().getPrescriptionsProductList()) {

            dbW.delete("product", "barCode = ?", new String[]{productAmount.getProduct().getBarCode()});
        }

        deletePatientRelative(sale.getPatient(), sale.getRelative());
        deletePerson(sale.getPatient());
        deletePerson(sale.getRelative());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
