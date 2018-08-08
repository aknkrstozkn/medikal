package com.example.akin.deneme.core;

import android.content.ContentValues;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {
    private ModelConverter modelConverter;
    private DataConverter dataConverter;

    //table person and its columns
    private String person = "person";
    private String tc = "tc";
    private String name = "name";
    private String address = "address";
    private String cordinate = "cordinate";
    private String phoneNumber = "phoneNumber";
    //table product and its columns
    private String product = "product";
    private String barCode = "barCode";
    private String serialNumber = "serialNumber";
    private String type = "type";
    //table prescription and its columns
    private String prescription = "prescription";
    private String id = "id";
    private String sDate = "sDate";
    private String eDate = "eDate";
    private String validity = "validity";
    // table patientRelative and its columns
    private String patientRelative = "patientRelative";
    private String patientTC = "patientTC";
    private String relativeTC = "relativeTC";
    private String relativity = "relativity";
    //table prescriptionProduct and its columns
    private String prescriptionProduct = "prescriptionProduct";
    private String prescriptionId = "prescriptionId";
    private String productBarCode = "productBarCode";
    private String productAmount = "productAmount";
    //table sale and its columns
    private String sale = "sale";
    private String salePrescriptionId = "salePrescriptionId";
    private String salePatientTC = "salePatientTC";
    private String saleRelativeTC = "saleRelativeTC";
    private String saleDate = "saleDate";

    public DataBase(Context context) {
        super(context, "MedicalDatabase", null, 1);
        modelConverter = new ModelConverter();
        dataConverter = new DataConverter();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTablePerson = "CREATE TABLE " + person + "("
                + tc + " TEXT PRIMARY KEY,"
                + name + " TEXT,"
                + address + " TEXT,"
                + cordinate + " TEXT,"
                + phoneNumber + " TEXT)";
        db.execSQL(createTablePerson);

        String createTableProduct = "CREATE TABLE " + product + "("
                + barCode + " TEXT PRIMARY KEY,"
                + serialNumber + " TEXT,"
                + type + " TEXT)";
        db.execSQL(createTableProduct);

        String createTablePrescription = "CREATE TABLE " + prescription + "("
                + id + " LONG PRIMARY KEY,"
                + sDate + " LONG,"
                + eDate + " LONG,"
                + validity + " INTEGER DEFAULT 1)";
        db.execSQL(createTablePrescription);

        String createTablePatientRelative = "CREATE TABLE " + patientRelative + "("
                + patientTC + " TEXT REFERENCES "+ person + "(" + tc + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + relativeTC + " TEXT REFERENCES "+ person + "(" + tc + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + relativity + " TEXT,"
                + "PRIMARY KEY(" + patientTC + "," + relativeTC + "))";
        db.execSQL(createTablePatientRelative);

        String createTablePrescriptionProduct = "CREATE TABLE " + prescriptionProduct + "("
                + prescriptionId + " LONG REFERENCES " + prescription + "(" + id + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + productBarCode + " TEXT REFERENCES " + product + "(" + barCode + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + productAmount + " INTEGER,"
                + "PRIMARY KEY(" + prescriptionId + "," + productBarCode + "))";
        db.execSQL(createTablePrescriptionProduct);

        String createTableSale = "CREATE TABLE " + sale + "("
                + salePrescriptionId + " LONG REFERENCES " + prescription + "(" + id + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + salePatientTC + " TEXT REFERENCES " + person + "(" + tc + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + saleRelativeTC + " TEXT REFERENCES " + person + "(" + tc +") ON UPDATE CASCADE ON DELETE CASCADE,"
                + saleDate + " LONG,"
                + "PRIMARY KEY(" + salePrescriptionId + "))";
        db.execSQL(createTableSale);

    }

    public void deletePatient(String tc) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQuery = "SELECT * FROM " + "sale" + " WHERE " + "patientTC" + "='" + tc + "'";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.getCount() == 1) {
                    db.delete("person", "tc = ?", new String[]{String.valueOf(tc)});
                }
            }
        }
    }

    public void deleteRelative(String tc) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQuery = "SELECT * FROM " + "sale" + " WHERE " + "relativeTC" + "=" + "'" + tc + "'";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.getCount() == 1) {
                    db.delete("person", "tc = ?", new String[]{String.valueOf(tc)});
                }
            }
        }
    }

    public void addPerson(Person person) {

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQuery = "SELECT * FROM " + "person" + " WHERE " + "tc " + "= " + "'" + person.getTc() + "'";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.getCount() == 0) {
                    db.insert("person", null, modelConverter.person(person));

                }
            }
        }

    }

    public void addPatientRelative(String patientTC, Relativity relativity) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQuery = "SELECT * FROM " + "patientRelative" + " WHERE " + "patientTC " + "= " + "'" + patientTC + "'" + " and relativeTC = " + "'" + relativity.getRelative().getTc() + "'";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.getCount() == 0) {
                    db.insert("patientRelative", null, modelConverter.patientAndRelative(patientTC, relativity));
                }
            }
        }
    }

    public void addProduct(Product product) {
        try (SQLiteDatabase writableDatabase = this.getWritableDatabase()) {
            String selectQuery = "SELECT type FROM " + "product" + " WHERE " + "barCode" + "='" + product.getBarCode() + "'";
            try (Cursor cursor = writableDatabase.rawQuery(selectQuery, null)) {
                if (cursor.getCount() == 0) {
                    writableDatabase.insert("product", null, modelConverter.product(product));
                }
            }
        }
    }


    public void deleteProduct(String barCode) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQuery = "SELECT * FROM " + "prescriptionProduct" + " WHERE " + "productBarCode" + "=" + "'" + barCode + "'";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.getCount() == 1) {
                    db.delete("product", "barCode = ?", new String[]{String.valueOf(barCode)});
                }
            }
        }
    }

    public long addPrescription(Prescription prescription) {
        try (SQLiteDatabase writableDatabase = this.getWritableDatabase();) {
            return writableDatabase.insert("prescription", null, modelConverter.prescription(prescription));
        }
    }

    public void addPrescriptionProduct(long prescriptionId, ProductAmount productAmount) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.insert("prescriptionProduct", null, modelConverter.prescriptionProduct(prescriptionId, productAmount));
        }
    }

    public String addSale(Sale sale) {

        addPerson(sale.getPatient());
        addPerson(sale.getPatient().getRelatives().get(0).getRelative());
        addPatientRelative(sale.getPatient().getTc(), sale.getPatient().getRelatives().get(0));

        long prescriptionId = addPrescription(sale.getPrescription());
        Prescription prescription = sale.getPrescription();
        prescription.setId(prescriptionId);
        sale.setPrescription(prescription);

        for (ProductAmount productAmount : sale.getPrescription().getPrescriptionsProductList()) {
            addProduct(productAmount.getProduct());
            addPrescriptionProduct(sale.getPrescription().getId(), productAmount);
        }
        try(SQLiteDatabase db = this.getWritableDatabase()) {

            String id =String.valueOf(db.insert("sale",null,modelConverter.sale(sale)));
            return String.valueOf(sale.getPrescription().getId()) + " + " + sale.getPatient().getTc() + " + "
                    + sale.getRelative().getTc() + " + " + String.valueOf(sale.getDate()) + " + " + id;
        }


    }

    public String get() {
        String a = "";

        String s = "SELECT * FROM person";
        try (SQLiteDatabase r = this.getReadableDatabase()) {
            try (Cursor c = r.rawQuery(s, null)) {
                c.moveToFirst();
                a += String.valueOf(c.getCount()) + "+";
            }
        }

        s = "SELECT * FROM patientRelative";
        try (SQLiteDatabase r = this.getReadableDatabase()) {
            try (Cursor c = r.rawQuery(s, null)) {
                c.moveToFirst();
                a += String.valueOf(c.getCount()) + "+";
            }
        }
        s = "SELECT * FROM product";
        try (SQLiteDatabase r = this.getReadableDatabase()) {
            try (Cursor c = r.rawQuery(s, null)) {
                c.moveToFirst();
                a += String.valueOf(c.getCount()) + "+";
            }
        }
        s = "SELECT * FROM prescriptionProduct";
        try (SQLiteDatabase r = this.getReadableDatabase()) {
            try (Cursor c = r.rawQuery(s, null)) {
                c.moveToFirst();
                a += String.valueOf(c.getCount()) + "+";
            }
        }
        s = "SELECT * FROM prescription";
        try (SQLiteDatabase r = this.getReadableDatabase()) {
            try (Cursor c = r.rawQuery(s, null)) {
                c.moveToFirst();
                a += String.valueOf(c.getCount()) + "+";
            }
        }
        s = "SELECT * FROM sale";
        try (SQLiteDatabase r = this.getReadableDatabase()) {
            try (Cursor c = r.rawQuery(s, null)) {
                c.moveToFirst();
                a += String.valueOf(c.getCount()) + "+";
            }
        }

        return a;
    }

    public void deletePrescription(long id) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete("prescription", "id", new String[]{String.valueOf(id)});
        }
    }

    public void deleteSale(long prescriptionId) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            deletePatient(getSale(prescriptionId).getPatient().getTc());
            deleteRelative(getSale(prescriptionId).getRelative().getTc());
            for (ProductAmount productAmount : getSale(prescriptionId).getPrescription().getPrescriptionsProductList()) {
                deleteProduct(productAmount.getProduct().getBarCode());
            }
            deletePrescription(getSale(prescriptionId).getPrescription().getId());
        }
    }

    public void updatePerson(String tc, Person person) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update("person", modelConverter.person(person), "tc = ?",
                    new String[]{tc});
        }
    }

    public void updateProduct(String barCode, Product product) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update("product", modelConverter.product(product), "barCode = ?",
                    new String[]{barCode});
        }
    }

    public void updatePrescription(long id, Prescription prescription) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update("prescription", modelConverter.prescription(prescription), "id = ?",
                    new String[]{String.valueOf(id)});
        }
    }

    public void updateSale(long prescriptionId, Sale sale) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update("sale", modelConverter.sale(sale), "prescriptionId = ?",
                    new String[]{String.valueOf(prescriptionId)});
        }
    }

    public void updatePrescriptionProduct(long prescriptionId, String productBarCode, ProductAmount productAmount) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update("prescriptionProduct", modelConverter.prescriptionProduct(prescriptionId, productAmount), "prescriptionId = ? and productBarCode = ?",
                    new String[]{String.valueOf(prescriptionId), productBarCode});
        }
    }

    public void updatePatientRelative(String relativeTC, String patientTC, Relativity relativity) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update("patientRelative", modelConverter.patientAndRelative(patientTC, relativity), "patientTC = ? and relativeTC = ?",
                    new String[]{patientTC, relativeTC});
        }
    }

    public Product getProduct(String barCode) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM " + product + " WHERE " + this.barCode + "=" + "'" + barCode + "'";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                cursor.moveToFirst();
                return dataConverter.product(cursor);
            }
        }
    }

    public Relative getRelative(String tc) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM person WHERE tc = " + "'" + tc + "'";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                cursor.moveToFirst();
                return dataConverter.relative(cursor);
            }
        }
    }

    public List<Relativity> getRelativities(String tc) {
        List<Relativity> relativities = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String relativityQuery = "SELECT relativeTC, relativity FROM patientRelative WHERE patientTC = " + "'" + tc + "'";
            try (Cursor cursor = db.rawQuery(relativityQuery, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        relativities.add(dataConverter.relativity(cursor, getRelative(cursor.getString(cursor.getColumnIndex("relativeTC")))));
                    } while (cursor.moveToNext());
                }
            }
        }
        return relativities;
    }

    public Patient getPatient(String tc) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM person WHERE tc = " + "'" + tc + "'";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                cursor.moveToFirst();
                return dataConverter.patient(cursor, getRelativities(tc));
            }
        }
    }

    public List<Patient> getPatients() {
        List<Patient> patients = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM person";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        patients.add(getPatient(cursor.getString(cursor.getColumnIndex("tc"))));
                    } while (cursor.moveToNext());
                }
            }
        }
        return patients;
    }

    public List<ProductAmount> getProductAmounts(long prescriptionId) {
        List<ProductAmount> productAmounts = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM " + prescriptionProduct + " WHERE " + this.prescriptionId + " = " + prescriptionId;
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        productAmounts.add(dataConverter.productAmount(cursor, getProduct(cursor.getString(cursor.getColumnIndex(productBarCode)))));
                    } while (cursor.moveToNext());
                }
            }
        }
        return productAmounts;
    }

    public Prescription getPrescription(long id) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM " + prescription + " WHERE " + this.id + " = " + id;
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                cursor.moveToFirst();
                return dataConverter.prescription(cursor, getProductAmounts(id));
            }
        }
    }

    public String getId (){
        String a = "";
        long id = 1;
        try(SQLiteDatabase readableDatabase = this.getReadableDatabase()){
            String selectQuery = "SELECT "+sDate+" FROM " + prescription;
            try (Cursor cursor = readableDatabase.rawQuery(selectQuery,null)){
                if(cursor.moveToFirst()){
                    do {
                        a += cursor.getLong(0);
                    }while (cursor.moveToNext());

                }
            }
        }
        try(SQLiteDatabase readableDatabase = this.getReadableDatabase()){
            String selectQuery = "SELECT * FROM " + sale + " WHERE "+ salePrescriptionId + "=" + id;
            try (Cursor cursor = readableDatabase.rawQuery(selectQuery,null)){
                if(cursor.moveToFirst()){
                    do {
                        a += cursor.getLong(cursor.getColumnIndex(salePrescriptionId)) + " + ";
                    }while (cursor.moveToNext());

                }
            }
        }
        return a;
    }

    public Sale getSale(long prescriptionId) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM " + sale + " WHERE = " + salePrescriptionId + "= " + prescriptionId;
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                cursor.moveToFirst();
                return dataConverter.sale(cursor, getPrescription(prescriptionId),
                        getPatient(cursor.getString(cursor.getColumnIndex("patientTC"))),
                        getRelative(cursor.getString(cursor.getColumnIndex("relativeTC"))));
            }
        }
    }

    public List<Sale> getSales() {
        List<Sale> sales = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM " + sale;
            // String selectQuery = "SELECT s.prescriptionId,s.patientTC,s.relativeTC,s.date FROM sale AS s,prescription as p WHERE s.prescriptionId = p.id ORDER BY p.eDate ASC";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        sales.add(dataConverter.sale(cursor,
                                getPrescription(cursor.getLong(cursor.getColumnIndex(salePrescriptionId))),
                                getPatient(cursor.getString(cursor.getColumnIndex(salePatientTC))),
                                getRelative(cursor.getString(cursor.getColumnIndex(saleRelativeTC)))));
                    } while (cursor.moveToNext());
                }
            }
        }
        return sales;
    }

    public List<Sale> getOutOfDatePresSales() {
        List<Sale> sales = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT id FROM prescription AS p, sale AS s WHERE p.validity = 1 and s.prescriptionId = p.id ORDER BY eDate ASC";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        long pTime = getPrescription(cursor.getLong(0)).geteDate();
                        Date date = new Date(pTime);
                        Calendar calendar = Calendar.getInstance();

                        if (date.before(calendar.getTime())) {
                            sales.add(getSale(cursor.getLong(0)));
                        }
                    } while (cursor.moveToNext());
                }
            }
        }
        return sales;
    }

    public List<Sale> getInDatePresSales() {
        List<Sale> sales = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT id FROM prescription AS p, sale AS s WHERE p.validity = 1 and s.prescriptionId = p.id ORDER BY eDate ASC";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        long pTime = getPrescription(cursor.getLong(0)).geteDate();
                        Date date = new Date(pTime);
                        Calendar calendar = Calendar.getInstance();

                        if (date.after(calendar.getTime())) {
                            sales.add(getSale(cursor.getLong(0)));
                        }
                    } while (cursor.moveToNext());
                }
            }
        }
        return sales;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
