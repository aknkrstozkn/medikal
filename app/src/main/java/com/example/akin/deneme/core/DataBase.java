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

import java.text.SimpleDateFormat;
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
                + id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + sDate + " LONG,"
                + eDate + " LONG,"
                + validity + " INTEGER DEFAULT 1)";
        db.execSQL(createTablePrescription);

        String createTablePatientRelative = "CREATE TABLE " + patientRelative + "("
                + patientTC + " TEXT REFERENCES " + person + "(" + tc + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + relativeTC + " TEXT REFERENCES " + person + "(" + tc + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + relativity + " TEXT,"
                + "PRIMARY KEY(" + patientTC + "," + relativeTC + "))";
        db.execSQL(createTablePatientRelative);

        String createTablePrescriptionProduct = "CREATE TABLE " + prescriptionProduct + "("
                + prescriptionId + " INTEGER REFERENCES " + prescription + "(" + id + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + productBarCode + " TEXT REFERENCES " + product + "(" + barCode + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + productAmount + " INTEGER,"
                + "PRIMARY KEY(" + prescriptionId + "," + productBarCode + "))";
        db.execSQL(createTablePrescriptionProduct);

        String createTableSale = "CREATE TABLE " + sale + "("
                + salePrescriptionId + " INTEGER REFERENCES " + prescription + "(" + id + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + salePatientTC + " TEXT REFERENCES " + person + "(" + tc + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + saleRelativeTC + " TEXT REFERENCES " + person + "(" + tc + ") ON UPDATE CASCADE ON DELETE CASCADE,"
                + saleDate + " LONG,"
                + "PRIMARY KEY(" + salePrescriptionId + "))";
        db.execSQL(createTableSale);

    }

    public void addPerson(Person person) {

            try (SQLiteDatabase db = this.getWritableDatabase()) {
                String selectQuery = "SELECT * FROM " + this.person + " WHERE " + this.tc + " = " + "'" + person.getTc() + "'";
                try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                    if (cursor.getCount() == 0) {
                        db.insert(this.person, null, modelConverter.person(person));

                    }
                }
            }

    }

    public void addPatientRelative(String patientTC, Relativity relativity) {

        try (SQLiteDatabase db = this.getWritableDatabase()) {

            String selectQuery = "SELECT * FROM " + patientRelative + " WHERE " + this.patientTC + " = " + "'" + patientTC + "'" +
                    " and " + relativeTC + " = " + "'" + relativity.getRelative().getTc() + "'";

            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.getCount() == 0) {
                    db.insert(patientRelative, null, modelConverter.patientAndRelative(patientTC, relativity));
                }
            }
        }
    }

    public void addProduct(Product product) {
        try (SQLiteDatabase writableDatabase = this.getWritableDatabase()) {
            String selectQuery = "SELECT * FROM " + this.product + " WHERE " + barCode + " = '" + product.getBarCode() + "'";
            try (Cursor cursor = writableDatabase.rawQuery(selectQuery, null)) {
                if (cursor.getCount() == 0) {
                    writableDatabase.insert(this.product, null, modelConverter.product(product));
                }
            }
        }
    }

    public void addPrescriptionProduct(long prescriptionId, ProductAmount productAmount) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.insert(this.prescriptionProduct, null, modelConverter.prescriptionProduct(prescriptionId, productAmount));
        }
    }

    public long addPrescription(Prescription prescription) {
        try (SQLiteDatabase writableDatabase = this.getWritableDatabase()) {
            return writableDatabase.insert(this.prescription, null, modelConverter.prescription(prescription));
        }
    }

    public void addSale(Sale sale) {
        if(sale.getPatient().getRelatives().isEmpty()){
            addPerson(sale.getPatient());
        }else{
            addPerson(sale.getPatient());
            addPerson(sale.getPatient().getRelatives().get(0).getRelative());
            addPatientRelative(sale.getPatient().getTc(), sale.getPatient().getRelatives().get(0));
        }


        sale.getPrescription().setId(addPrescription(sale.getPrescription()));

        for (ProductAmount productAmount : sale.getPrescription().getPrescriptionsProductList()) {
            addProduct(productAmount.getProduct());
            addPrescriptionProduct(sale.getPrescription().getId(), productAmount);
        }

        try (SQLiteDatabase db = this.getWritableDatabase()) {

            db.insert(this.sale, null, modelConverter.sale(sale));
        }
    }

    public void deletePatient(String tc) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQueryPatient = "SELECT * FROM " + sale + " WHERE " + salePatientTC + " = '" + tc + "'";
            String selectQueryRelative = "SELECT * FROM " + sale + " WHERE " + saleRelativeTC + " = '" + tc + "'";
            try (Cursor cursorPatient = db.rawQuery(selectQueryPatient, null)) {
                try (Cursor cursorRelative = db.rawQuery(selectQueryRelative, null)) {

                    if (cursorPatient.getCount() == 1 && cursorRelative.getCount() == 0) {
                        db.delete(person, this.tc + " = ?", new String[]{String.valueOf(tc)});
                    }
                }
            }
        }
    }

    public void deleteRelative(String tc) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQueryPatient = "SELECT * FROM " + sale + " WHERE " + salePatientTC + " = '" + tc + "'";
            String selectQueryRelative = "SELECT * FROM " + sale + " WHERE " + saleRelativeTC + " = '" + tc + "'";
            try (Cursor cursorPatient = db.rawQuery(selectQueryPatient, null)) {
                try (Cursor cursorRelative = db.rawQuery(selectQueryRelative, null)) {

                    if (cursorRelative.getCount() == 1 && cursorPatient.getCount() == 0) {
                        db.delete(person, this.tc + " = ?", new String[]{String.valueOf(tc)});
                    }
                }
            }
        }
    }
    public void deleteProduct(String barCode) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            String selectQuery = "SELECT * FROM " + prescriptionProduct + " WHERE " + productBarCode + " = " + "'" + barCode + "'";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {

                if (cursor.getCount() == 1) {
                    db.delete(this.product, this.barCode + " = ?", new String[]{String.valueOf(barCode)});
                }
            }
        }
    }

    public void deletePrescription(long id) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(this.prescription, this.id, new String[]{String.valueOf(id)});
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
            db.update(this.person, modelConverter.person(person), this.tc + " = ?",
                    new String[]{tc});
        }
    }

    public void updateProduct(String barCode, Product product) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update(this.product, modelConverter.product(product), this.barCode + " = ?",
                    new String[]{barCode});
        }
    }

    public void updatePrescription(Prescription prescription) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update(this.prescription, modelConverter.prescription(prescription), this.id + " = ?",
                    new String[]{String.valueOf(prescription.getId())});
        }
    }

    public void updateSale(Sale sale) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update(this.sale, modelConverter.sale(sale), this.salePrescriptionId + " = ?",
                    new String[]{String.valueOf(sale.getPrescription().getId())});
        }
    }

    public void updatePrescriptionProduct(long prescriptionId, String productBarCode, ProductAmount productAmount) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update(this.prescriptionProduct, modelConverter.prescriptionProduct(prescriptionId, productAmount),
                    this.prescriptionId + " = ? and " + this.productBarCode + " = ?",
                    new String[]{String.valueOf(prescriptionId), productBarCode});
        }
    }

    public void updatePatientRelative(String relativeTC, String patientTC, Relativity relativity) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.update(this.patientRelative, modelConverter.patientAndRelative(patientTC, relativity),
                    this.patientTC + " = ? and " + this.relativeTC + " = ?",
                    new String[]{patientTC, relativeTC});
        }
    }

    public Product getProduct(String barCode) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM " + product + " WHERE " + this.barCode + " = " + "'" + barCode + "'";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst())
                    return dataConverter.product(cursor);
                else
                    return null;
            }
        }
    }

    public Relative getRelative(String tc) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM " + person + " WHERE " + this.tc + " = " + "'" + tc + "'";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst())
                    return dataConverter.relative(cursor);
                else
                    return null;
            }
        }
    }

    public List<Relativity> getRelativities(String tc) {
        List<Relativity> relativities = new ArrayList<>();
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String relativityQuery = "SELECT " + relativeTC + "," + relativity + " FROM " + patientRelative +
                    " WHERE " + patientTC + " = " + "'" + tc + "'";
            try (Cursor cursor = db.rawQuery(relativityQuery, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        Relative relative = getRelative(
                                cursor.getString(cursor.getColumnIndex("relativeTC")
                                ));

                        relativities.add(dataConverter.relativity(cursor, relative));
                    } while (cursor.moveToNext());
                    return relativities;
                } else
                    return null;
            }
        }

    }

    public Patient getPatient(String tc) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM " + person + " WHERE " + this.tc + " = " + "'" + tc + "'";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst())
                    return dataConverter.patient(cursor, getRelativities(tc));
                else
                    return null;
            }
        }
    }

    public Patient getPatientWithName(String name) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM " + person + " WHERE " + this.name + " = " + "'" + name + "'";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst())
                    return dataConverter.patient(cursor, getRelativities(cursor.getString(cursor.getColumnIndex(tc))));
                else
                    return null;
            }
        }
    }

    public List<Patient> getPatients() {
        List<Patient> patients = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT " + tc + "," + name + "," + address + "," + cordinate + "," + phoneNumber
            + " FROM " + person + " JOIN " + patientRelative + " ON " + tc + " = " + patientTC;
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst()) {

                    do {
                        patients.add(getPatient(cursor.getString(cursor.getColumnIndex("tc"))));
                    } while (cursor.moveToNext());

                    return patients;
                } else
                    return null;
            }
        }

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

                    return productAmounts;
                } else
                    return null;
            }
        }
    }

    public Prescription getPrescription(long id) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM " + prescription + " WHERE " + this.id + " = " + id;
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst())
                    return dataConverter.prescription(cursor, getProductAmounts(id));
                else
                    return null;
            }
        }
    }

    public Sale getSale(long prescriptionId) {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT * FROM " + sale + " WHERE " + salePrescriptionId + " = " + prescriptionId;
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst())
                    return dataConverter.sale(cursor, getPrescription(prescriptionId),
                            getPatient(cursor.getString(cursor.getColumnIndex("salePatientTC"))),
                            getRelative(cursor.getString(cursor.getColumnIndex("saleRelativeTC"))));
                else
                    return null;
            }
        }
    }

    public List<Sale> getSales() {
        List<Sale> sales = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase()) {

            String selectQuery = "SELECT " + salePrescriptionId + "," + salePatientTC +
                    "," + saleRelativeTC + "," + saleDate + " FROM " + sale + "," + prescription +
                    " WHERE " + salePrescriptionId + " = " + id + " ORDER BY " + eDate + " ASC";

            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst()) {

                    do {
                        sales.add(dataConverter.sale(cursor,
                                getPrescription(cursor.getLong(cursor.getColumnIndex(salePrescriptionId))),
                                getPatient(cursor.getString(cursor.getColumnIndex(salePatientTC))),
                                getRelative(cursor.getString(cursor.getColumnIndex(saleRelativeTC)))));
                    } while (cursor.moveToNext());

                    return sales;
                } else
                    return null;
            }
        }

    }

    public List<Sale> getOutOfDatePresSales() {
        List<Sale> sales = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase()) {
            String selectQuery = "SELECT " + id +  " FROM " + prescription + "," + sale +
                    " WHERE " + validity + " = 1 and " + salePrescriptionId + " = " + id +
                    " ORDER BY " + eDate + " ASC";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        long pTime = getPrescription(cursor.getLong(0)).geteDate();
                        Date date = new Date(pTime);
                        Date currentDate = Calendar.getInstance().getTime();
                        if (date.before(currentDate)) {
                            sales.add(getSale((long) cursor.getInt(cursor.getColumnIndex(id))));
                        }
                    } while (cursor.moveToNext());
                    return sales;
                } else
                    return null;
            }
        }
    }

    public List<Sale> getInDatePresSales() {
        List<Sale> sales = new ArrayList<>();

        try (SQLiteDatabase db = this.getReadableDatabase()) {

            String selectQuery = "SELECT " + id + " FROM " + prescription + "," + sale +
                    " WHERE " + validity + " = 1 and " + salePrescriptionId + " = " + id +
                    " ORDER BY " + eDate + " ASC";
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
                    return sales;
                } else
                    return null;
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
