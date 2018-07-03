package com.example.akin.deneme.core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

public class DataBase extends SQLiteOpenHelper {
    private ModelConverter modelConverter;

    public DataBase(Context context) {
        super(context, "MedicalDatabase", null, 1);
        modelConverter = new ModelConverter();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        List<String> tables = new ArrayList<>();

        String createTablePerson = "CREATE TABLE " + "person" + "("
                + "tc" + " INTEGER PRIMARY KEY,"
                + "name" + " TEXT,"
                + "surname" + " TEXT,"
                + "address" + " TEXT,"
                + "cordinate" + " TEXT,"
                + "phoneNumber" + " INTEGER)";
        db.execSQL(createTablePerson);

        String createTableProduct = "CREATE TABLE " + "product" + "("
                + "barCode" + " TEXT PRIMARY KEY,"
                + "serialNumber" + " TEXT,"
                + "type" + " TEXT)";
        db.execSQL(createTableProduct);

        String createTablePrescription = "CREATE TABLE " + "prescription" + "("
                + "id" + " INTEGER PRIMARY KEY,"
                + "date" + " LONG,"
                + "duration" + " INTEGER)";
        db.execSQL(createTablePrescription);

        String createTablePatientRelative = "CREATE TABLE " + "patientRelative" + "("
                + "patientTC" + " INTEGER REFERENCES person(tc) ON UPDATE CASCADE ON DELETE CASCADE,"
                + "relativeTC" + " INTEGER REFERENCES person(tc) ON UPDATE CASCADE ON DELETE CASCADE,"
                + "relativity" + " TEXT,"
                + "PRIMARY KEY(patientTC,relativeTC))";
        db.execSQL(createTablePatientRelative);

        String createTablePrescriptionProduct = "CREATE TABLE " + "prescriptionProduct" + "("
                + "prescriptionId" + " INTEGER REFERENCES prescription(id) ON UPDATE CASCADE ON DELETE CASCADE,"
                + "productBarCode" + " INTEGER REFERENCES product(barCode) ON UPDATE CASCADE ON DELETE CASCADE,"
                + "productAmount" + " INTEGER,"
                + "PRIMARY KEY(prescriptionId,productBarCode))";
        db.execSQL(createTablePrescriptionProduct);

        String createTableSale = "CREATE TABLE " + "sale" + "("
                + "prescriptionId" + " INTEGER REFERENCES prescription(Id) ON UPDATE CASCADE ON DELETE CASCADE,"
                + "patientTC" + " INTEGER REFERENCES person(tc) ON UPDATE CASCADE ON DELETE CASCADE,"
                + "relativeTC" + " INTEGER REFERENCES person(tc) ON UPDATE CASCADE ON DELETE CASCADE,"
                + "date" + " LONG,"
                + "PRIMARY KEY(prescriptionId))";
        db.execSQL(createTableSale);
    }

    public void deletePerson(Person person) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQuery = "SELECT * FROM " + "sale" + " WHERE " + "tc" + "=" + person.getTc();
            try (Cursor cursor = db.rawQuery(selectQuery, null);) {
                if (cursor.getCount() == 1) {
                    db.delete("person", "tc = ?", new String[]{String.valueOf(person.getTc())});
                }
            }
        }
    }

    /**
     * @param patient
     * @param relative
     */
    public void deletePatientRelative(Patient patient, Relative relative) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQuery = "SELECT * FROM " + "sale" + " WHERE " + "patientTC" + "=" + patient.getTc()
                    + "and relativeTC = " + relative.getTc();
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.getCount() == 1) {
                    db.delete("patientRelative", "patientTC = ? and relativeTC = ?",
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
                    db.insert("patientRelative", null, modelConverter.person(person));
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addPatientRelative(String patientTC, Relativity relativity) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQuery = "SELECT * FROM " + "patientRelative" + " WHERE " + "patientTC " + "= " + patientTC + " and relativeTC = " + relativity.getRelative().getTc();
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.getCount() == 0) {
                    db.insert("patientRelative", null, modelConverter.patientAndRelative(patientTC,relativity));
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
                        db.insert("patientRelative", null, modelConverter.product(product));
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
                    db.delete("product", "barCode = ?", new String[]{String.valueOf(product.getBarCode())});
                }
            }
        }
    }

    public void makeSale(Sale sale) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            addPerson(sale.getPatient());
            addPerson(sale.getPatient().getRelatives().get(0).getRelative());
            addPatientRelative(sale.getPatient().getTc(), sale.getPatient().getRelatives().get(0));
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
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete("sale", "prescriptionId = ?", new String[]{String.valueOf(sale.getPrescription().getId())});
            db.delete("prescriptionProduct", "prescriptionId = ?", new String[]{String.valueOf(sale.getPrescription().getId())});

            for (ProductAmount productAmount : sale.getPrescription().getPrescriptionsProductList()) {
                db.delete("product", "barCode = ?", new String[]{productAmount.getProduct().getBarCode()});
            }

            deletePatientRelative(sale.getPatient(), sale.getRelative());
            deletePerson(sale.getPatient());
            deletePerson(sale.getRelative());
        }
    }

    public void updatePerson(String tc,Person person){
        try(SQLiteDatabase db = this.getWritableDatabase()){
            db.update("person", modelConverter.person(person), "tc = ?",
                    new String[] { tc });
        }
    }

    public void updateProduct(String barCode,Product product){
        try(SQLiteDatabase db = this.getWritableDatabase()){
            db.update("product", modelConverter.product(product), "barCode = ?",
                    new String[] { barCode });
        }
    }

    public void updatePrescription(Long id, Prescription prescription){
        try(SQLiteDatabase db = this.getWritableDatabase()){
            db.update("prescription", modelConverter.prescription(prescription), "id = ?",
                    new String[] { String.valueOf(id) });
        }
    }

    public void updateSale(Long prescriptionId, Sale sale){
        try(SQLiteDatabase db = this.getWritableDatabase()){
            db.update("sale", modelConverter.sale(sale), "prescriptionId = ?",
                    new String[] { String.valueOf(prescriptionId) });
        }
    }

    public void updatePrescriptionProduct(Long prescriptionId, String productBarCode, ProductAmount productAmount){
        try(SQLiteDatabase db = this.getWritableDatabase()){
            db.update("prescriptionProduct", modelConverter.prescriptionProduct(prescriptionId, productAmount), "prescriptionId = ? and productBarCode = ?",
                    new String[] { String.valueOf(prescriptionId), productBarCode });
        }
    }

    public void updatePatientRelative(String relativeTC, String patientTC, Relativity relativity){
        try(SQLiteDatabase db = this.getWritableDatabase()){
            db.update("patientRelative", modelConverter.patientAndRelative(patientTC, relativity), "patientTC = ? and relativeTC = ?",
                    new String[] { patientTC, relativeTC });
        }
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
