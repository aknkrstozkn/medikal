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
import com.google.gson.internal.bind.SqlDateTypeAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {
    private ModelConverter modelConverter;
    private DataConverter dataConverter;

    public DataBase(Context context) {
        super(context, "MedicalDatabase", null, 1);
        modelConverter = new ModelConverter();
        dataConverter = new DataConverter();
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
        db.execSQL(createTablePerson);

        String createTableProduct = "CREATE TABLE " + "product" + "("
                + "barCode" + " TEXT PRIMARY KEY,"
                + "serialNumber" + " TEXT,"
                + "type" + " TEXT)";
        db.execSQL(createTableProduct);

        String createTablePrescription = "CREATE TABLE " + "prescription" + "("
                + "id" + " INTEGER PRIMARY KEY,"
                + "sDate" + " LONG,"
                + "eDate" + " LONG,"
                + "validity" + " INTEGER DEFAULT 1)";
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

    public void deletePatient(String tc) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQuery = "SELECT * FROM " + "sale" + " WHERE " + "patientTC" + "=" + tc;
            try (Cursor cursor = db.rawQuery(selectQuery, null);) {
                if (cursor.getCount() == 1) {
                    db.delete("person", "tc = ?", new String[]{String.valueOf(tc)});
                }
            }
        }
    }

    public void deleteRelative(String tc) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQuery = "SELECT * FROM " + "sale" + " WHERE " + "relativeTC" + "=" + tc;
            try (Cursor cursor = db.rawQuery(selectQuery, null);) {
                if (cursor.getCount() == 1) {
                    db.delete("person", "tc = ?", new String[]{String.valueOf(tc)});
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
                    db.insert("patientRelative", null, modelConverter.product(product));
                    return true;
                }
            }
        }
        return false;
    }



    public void deleteProduct(String barCode) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQuery = "SELECT * FROM " + "prescriptionProduct" + " WHERE " + "productBarCode" + "=" + barCode;
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.getCount() == 1) {
                    db.delete("product", "barCode = ?", new String[]{String.valueOf(barCode)});
                }
            }
        }
    }

    public long addPrescription(Prescription prescription){
        try (SQLiteDatabase db = this.getWritableDatabase()){
            return db.insert("prescription", null, modelConverter.prescription(prescription));

        }
    }

    public void addPrescriptionProduct(long prescriptionId, ProductAmount productAmount){
        try (SQLiteDatabase db = this.getWritableDatabase()){
            db.insert("prescriptionProduct", null, modelConverter.prescriptionProduct(prescriptionId,productAmount));
        }
    }

    public void addSale(Sale sale) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            addPerson(sale.getPatient());
            addPerson(sale.getPatient().getRelatives().get(0).getRelative());
            addPatientRelative(sale.getPatient().getTc(), sale.getPatient().getRelatives().get(0));
            sale.getPrescription().setId(addPrescription(sale.getPrescription()));

            for (ProductAmount productAmount : sale.getPrescription().getPrescriptionsProductList()) {
                addProduct(productAmount.getProduct());
                addPrescriptionProduct(sale.getPrescription().getId(),productAmount);
            }
            db.insert("sale", null, modelConverter.sale(sale));
        }
    }

    public void deletePrescription(Long id){
        try (SQLiteDatabase db = this.getWritableDatabase()){
            db.delete("prescription","id", new String[] {String.valueOf(id)});
        }
    }

    public void deleteSale(Long prescriptionId) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            deletePatient(getSale(prescriptionId).getPatient().getTc());
            deleteRelative(getSale(prescriptionId).getRelative().getTc());
            for (ProductAmount productAmount: getSale(prescriptionId).getPrescription().getPrescriptionsProductList()) {
                deleteProduct(productAmount.getProduct().getBarCode());
            }
            deletePrescription(getSale(prescriptionId).getPrescription().getId());
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

    public Product getProduct(String barCode){
        try(SQLiteDatabase db = this.getReadableDatabase()){
            String selectQuery = "SELECT * FROM " + "product" + " WHERE " + "barCode" + "=" + barCode;
            try(Cursor cursor = db.rawQuery(selectQuery, null)){
                cursor.moveToFirst();
                return dataConverter.product(cursor);
            }
        }
    }

    public Relative getRelative(String tc){
        try(SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM person WHERE tc = " + tc ;
            try(Cursor cursor = db.rawQuery(selectQuery, null)){
                cursor.moveToFirst();
                return dataConverter.relative(cursor);
            }
        }
    }

    public List<Relativity> getRelativities(String tc){
        List<Relativity> relativities = new ArrayList<>();
        try(SQLiteDatabase db= this.getReadableDatabase()){
            String relativityQuery = "SELECT relativeTC, relativity FROM patientRelative WHERE patientTC = " + tc;
            try(Cursor cursor = db.rawQuery(relativityQuery, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        relativities.add(dataConverter.relativity(cursor,getRelative(cursor.getString(cursor.getColumnIndex("relativeTC")))));
                    } while (cursor.moveToNext());
                }
            }
        }
        return relativities;
    }

    public Patient getPatient(String tc){
        try(SQLiteDatabase db= this.getReadableDatabase()){
            String patientQuery = "SELECT * FROM person WHERE tc = " + tc;
            try(Cursor cursor = db.rawQuery(patientQuery, null)) {
                return dataConverter.patient(cursor,getRelativities(tc));
            }
        }
    }

    public List<Patient> getPatients(){
        List<Patient> patients = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM person";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if(cursor.moveToFirst()){
                    do {
                        patients.add(getPatient(cursor.getString(cursor.getColumnIndex("tc"))));
                    }while (cursor.moveToNext());
                }
            }
        }
        return patients;
    }

    public List<ProductAmount> getProductAmounts(Long prescriptionId){
        List<ProductAmount> productAmounts = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()){
            String selectQuery = "SELECT * FROM prescriptionProduct WHERE prescriptionId = " + prescriptionId;
            try (Cursor cursor = db.rawQuery(selectQuery, null)){
                if (cursor.moveToFirst()){
                    do {
                        productAmounts.add(dataConverter.productAmount(cursor, getProduct(cursor.getString(cursor.getColumnIndex("barCode")))));
                    }while (cursor.moveToNext());
                }
            }
        }
        return productAmounts;
    }

    public Prescription getPrescription(Long id){
        try(SQLiteDatabase db = this.getReadableDatabase()){
            String selectQuery = "SELECT * FROM prescription WHERE id = " + id;
            try(Cursor cursor = db.rawQuery(selectQuery, null)){
                cursor.moveToFirst();
                return dataConverter.prescription(cursor, getProductAmounts(id));
            }
        }
    }

    public Sale getSale(Long prescriptionId){
        try(SQLiteDatabase db = this.getReadableDatabase()){
            String selectQuery = "SELECT * FROM sale WHERE = prescriptionId = " + prescriptionId;
            try (Cursor cursor = db.rawQuery(selectQuery, null)){
                cursor.moveToFirst();
                return dataConverter.sale(cursor, getPrescription(prescriptionId),
                        getPatient(cursor.getString(cursor.getColumnIndex("patientTC"))),
                        getRelative(cursor.getString(cursor.getColumnIndex("relativeTC"))));
            }
        }
    }

    public List<Sale> getSales(){
        List<Sale> sales = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase()){
            String selectQuery = "SELECT s.prescriptionId,s.patientTC,s.relativeTC,s.date FROM sale AS s,prescription as p WHERE s.prescriptionId = p.id ORDER BY p.eDate ASC";
            try (Cursor cursor = db.rawQuery(selectQuery, null)){
                if(cursor.moveToFirst()){
                    do {
                        sales.add(dataConverter.sale(cursor,
                                getPrescription(cursor.getLong(cursor.getColumnIndex("prescriptionId"))),
                                getPatient(cursor.getString(cursor.getColumnIndex("patientTC"))),
                                getRelative(cursor.getString(cursor.getColumnIndex("relativeTC")))));
                    }while (cursor.moveToNext());
                }
            }
        }
        return sales;
    }

    public List<Prescription> getOutOfDatePres(){
        List<Prescription> prescriptions = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase()){
            String selectQuery = "SELECT id FROM prescription WHERE validity = 1 ORDER BY eDate ASC";
            try (Cursor cursor = db.rawQuery(selectQuery, null)){
                if(cursor.moveToFirst()){
                    do {
                        long pTime = getPrescription(cursor.getLong(0)).geteDate();
                        Date date = new Date(pTime);
                        Calendar calendar = Calendar.getInstance();

                        if(date.before(calendar.getTime())){
                            prescriptions.add(getPrescription(cursor.getLong(0)));
                        }
                    }while (cursor.moveToNext());
                }
            }
        }
        return prescriptions;
    }

    public List<Prescription> getValidPres(){
        List<Prescription> prescriptions = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase()){
            String selectQuery = "SELECT id FROM prescription WHERE validity = 1 ORDER BY eDate ASC";
            try (Cursor cursor = db.rawQuery(selectQuery, null)){
                if(cursor.moveToFirst()){
                    do {
                        long pTime = getPrescription(cursor.getLong(0)).geteDate();
                        Date date = new Date(pTime);
                        Calendar calendar = Calendar.getInstance();

                        if(date.after(calendar.getTime())){
                            prescriptions.add(getPrescription(cursor.getLong(0)));
                        }
                    }while (cursor.moveToNext());
                }
            }
        }
        return prescriptions;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
