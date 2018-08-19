package com.example.akin.deneme.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.example.akin.deneme.R;
import com.example.akin.deneme.core.DataBase;
import com.example.akin.deneme.core.model.Patient;
import com.example.akin.deneme.core.model.Prescription;
import com.example.akin.deneme.core.model.Product;
import com.example.akin.deneme.core.model.ProductAmount;
import com.example.akin.deneme.core.model.Relative;
import com.example.akin.deneme.core.model.Relativity;
import com.example.akin.deneme.core.model.Sale;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AddSaleActivity extends AppCompatActivity {

    private DataBase db;
    private ExpandableLinearLayout eLayoutPatient, eLayoutRelative, eLayoutPrescription;
    private Relative relative;
    private List<Relativity> relativities;
    private Patient patient;
    private Product product;
    private List<ProductAmount> productAmounts;
    private Prescription prescription;
    private Sale sale;
    private Context context = this;
    private Calendar caledar;
    private Button buttonSetStartDate, buttonSetEndDate;
    private FloatingActionButton buttonMakeSale, buttonAddProduct, buttonRemoveProduct;
    private CheckBox checkPatientCordinate, checkRelativeCordinate, checkPatientRelative;
    private ListView listProductAmount;
    private int day, mounth, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_detail);

        productAmounts = new ArrayList<>();
        listProductAmount = findViewById(R.id.listProductAmount);
        eLayoutRelative = findViewById(R.id.eLayoutRelative);

        caledar = Calendar.getInstance();
        year = caledar.get(Calendar.YEAR);
        mounth = caledar.get(Calendar.MONTH);
        day = caledar.get(Calendar.DAY_OF_MONTH);

        db = new DataBase(this);


        final Button buttonSetRelative = findViewById(R.id.buttonSetRelative);
        checkPatientRelative = findViewById(R.id.checkPatientRelative);
        checkPatientRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!checkPatientRelative.isChecked()){
                    eLayoutRelative.setVisibility(View.VISIBLE);
                    buttonSetRelative.setVisibility(View.VISIBLE);
                }else{
                    eLayoutRelative.setVisibility(View.INVISIBLE);
                    buttonSetRelative.setVisibility(View.INVISIBLE);
                }

            }
        });


        buttonSetEndDate = findViewById(R.id.editEndDate);
        buttonSetEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dpd = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                month += 1;
                                buttonSetEndDate.setText(dayOfMonth + "/" + month + "/" + year);
                            }
                        }, year, mounth, day);

                dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Seç", dpd);
                dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", dpd);
                dpd.show();
            }
        });

        buttonSetStartDate = findViewById(R.id.editStartDate);
        buttonSetStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dpd = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                month += 1;
                                buttonSetStartDate.setText(dayOfMonth + "/" + month + "/" + year);
                            }
                        }, year, mounth, day);

                dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Seç", dpd);
                dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", dpd);
                dpd.show();
            }
        });

        buttonAddProduct = findViewById(R.id.floatingButtonAddProduct);
        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productAmounts.add(getProductAmount(getProduct()));
                ArrayList<String> stringList = new ArrayList<>();
                for (ProductAmount productAmount : productAmounts) {
                    stringList.add(productAmount.getProductAmount() + " tane " + productAmount.getProduct().getType());
                }

                String[] myStringArray = stringList.toArray(new String[stringList.size()]);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_list_item_1, myStringArray);
                listProductAmount.setAdapter(adapter);

            }
        });

        buttonRemoveProduct = findViewById(R.id.floatingButtonRemoveProduct);
        buttonRemoveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productAmounts.remove(productAmounts.size() - 1);
                ArrayList<String> stringList = new ArrayList<>();
                for (ProductAmount productAmount : productAmounts) {
                    stringList.add(productAmount.getProductAmount() + " tane " + productAmount.getProduct().getType());
                }

                String[] myStringArray = stringList.toArray(new String[stringList.size()]);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_list_item_1, myStringArray);
                listProductAmount.setAdapter(adapter);

            }
        });

        buttonMakeSale = findViewById(R.id.floatingButtonMakeSale);
        buttonMakeSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPatientRelative.isChecked()){

                    relativities = new ArrayList<>();
                    patient = getPatient(relativities);
                    product = getProduct();
                    prescription = getPrescription(productAmounts);
                    sale = getSale(prescription, patient);

                }else{
                    relative = getRelative();
                    relativities = getRelativities(relative);
                    patient = getPatient(relativities);
                    product = getProduct();
                    prescription = getPrescription(productAmounts);
                    sale = getSale(prescription, patient, relative);
                }


                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Dikkat!");
                builder.setMessage("Bilgiler kaydedilecektir, emin misiniz?");
                builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        db.addSale(sale);
                    }
                });

                builder.show();
            }
        });


    }

    public void buttonPatient(View view) {
        eLayoutPatient = findViewById(R.id.eLayoutPatient);
        eLayoutPatient.toggle(); // toggle expand and collapse
    }

    public void buttonRelative(View view) {
        eLayoutRelative = findViewById(R.id.eLayoutRelative);
        eLayoutRelative.toggle(); // toggle expand and collapse
    }

    public void buttonPrescription(View view) {
        eLayoutPrescription = findViewById(R.id.eLayoutPrescription);
        eLayoutPrescription.toggle(); // toggle expand and collapse
    }

    private Patient getPatient(List<Relativity> relativities) {

        Patient patient = new Patient();
        EditText editPatientName = findViewById(R.id.editPatientName);
        EditText editPatientTC = findViewById(R.id.editPatientTC);
        EditText editPatientPhone = findViewById(R.id.editPatientPhone);
        EditText editPatientAddress = findViewById(R.id.editPatientAddress);

        patient.setRelatives(relativities);
        patient.setPhoneNumber(editPatientPhone.getText().toString());
        patient.setAddress(editPatientAddress.getText().toString());
        patient.setTc(editPatientTC.getText().toString());
        patient.setName(editPatientName.getText().toString().toUpperCase());

        return patient;
    }

    private Relative getRelative() {

        Relative relative = new Relative();
        EditText editRelativeName = findViewById(R.id.editRelativeName);
        EditText editRelativeTC = findViewById(R.id.editRelativeTC);
        EditText editRelativePhone = findViewById(R.id.editRelativePhone);
        EditText editRelativeAddress = findViewById(R.id.editRelativeAddress);

        relative.setAddress(editRelativeAddress.getText().toString());
        relative.setTc(editRelativeTC.getText().toString());
        relative.setName(editRelativeName.getText().toString().toUpperCase());
        relative.setPhoneNumber(editRelativePhone.getText().toString());

        return relative;
    }

    private List<Relativity> getRelativities(Relative relative) {
        List<Relativity> relativities = new ArrayList<>();
        Relativity relativity = new Relativity();
        EditText editRelativity = findViewById(R.id.editRelativity);

        relativity.setRelative(relative);
        relativity.setRelativity(editRelativity.getText().toString());
        relativities.add(relativity);

        return relativities;
    }

    private Product getProduct() {

        Product product = new Product();

        EditText editProductName = findViewById(R.id.editProductName);
        EditText editProductSerialNumber = findViewById(R.id.editProductSerialNumber);
        EditText editProductBarcode = findViewById(R.id.editProductBarCode);

        product.setType(editProductName.getText().toString());
        product.setBarCode(editProductBarcode.getText().toString());
        product.setSerialNumber(editProductSerialNumber.getText().toString());

        return product;
    }

    private ProductAmount getProductAmount(Product product) {

        ProductAmount productAmount = new ProductAmount();
        EditText editProductAmount = findViewById(R.id.editProductAmount);

        productAmount.setProduct(product);
        productAmount.setProductAmount(Integer.parseInt(editProductAmount.getText().toString()));


        return productAmount;
    }

    private Prescription getPrescription(List<ProductAmount> productAmounts) {

        Prescription prescription = new Prescription();
        prescription.setPrescriptionsProductList(productAmounts);

        try {

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date myDate = df.parse(buttonSetStartDate.getText().toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(myDate);
            prescription.setsDate(calendar.getTimeInMillis());

            myDate = df.parse(buttonSetEndDate.getText().toString());
            calendar.setTime(myDate);
            prescription.seteDate(calendar.getTimeInMillis());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return prescription;
    }

    private Sale getSale(Prescription prescription, Patient patient, Relative relative) {

        Sale sale = new Sale();

        sale.setDate(caledar.getTimeInMillis());
        sale.setPrescription(prescription);
        sale.setPatient(patient);
        sale.setRelative(relative);

        return sale;
    }
    private Sale getSale(Prescription prescription, Patient patient){
        return getSale(prescription, patient, null);
    }
}
