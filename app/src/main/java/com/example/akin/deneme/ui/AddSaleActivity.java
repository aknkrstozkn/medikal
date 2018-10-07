package com.example.akin.deneme.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

public class AddSaleActivity extends AppCompatActivity {

    LocationManager locationManager;
    Location location;
    private DataBase db;
    private ExpandableLinearLayout eLayoutPatient, eLayoutRelative, eLayoutPrescription;
    private Relative relative;
    private List<Relativity> relativities;
    private Patient patient;
    private Product product;
    private List<ProductAmount> productAmounts;
    private Prescription prescription;
    private Sale sale, oldSale;
    private Context context = this;
    private Calendar caledar;
    private FloatingActionButton buttonMakeSale, buttonAddProduct, buttonRemoveProduct;
    private CheckBox checkPatientCordinate, checkRelativeCordinate, checkPatientRelative;
    private ListView listProductAmount;
    private int day, mounth, year;
    private String patientCordinate, relativeCordinate;
    //About Relatives Layout
    private EditText editRelativeName, editRelativeTC, editRelativeAddress, editRelativePhoneNumber, editRelativity;
    //About Patients Layout
    private EditText editPatientName, editPatientTC, editPatientAddress, editPatientPhoneNumber;
    //About Product Amount
    private EditText editProductType, editProductBarCode, editProductSerialNumber, editProductAmount;
    //About Prescription
    private Button buttonSetStartDate, buttonSetEndDate, buttonSetRelative;

    public void initializeView() {
        //Views of Prescription Layout
        listProductAmount = findViewById(R.id.listProductAmount);
        buttonSetEndDate = findViewById(R.id.editEndDate);
        buttonSetStartDate = findViewById(R.id.editStartDate);
        editProductAmount = findViewById(R.id.editProductAmount);
        editProductBarCode = findViewById(R.id.editProductBarCode);
        editProductSerialNumber = findViewById(R.id.editProductSerialNumber);
        editProductType = findViewById(R.id.editProductName);

        //Views of Patient
        editPatientAddress = findViewById(R.id.editPatientAddress);
        editPatientTC = findViewById(R.id.editPatientTC);
        editPatientPhoneNumber = findViewById(R.id.editPatientPhone);
        editPatientName = findViewById(R.id.editPatientName);

        //Views of Relative
        editRelativeName = findViewById(R.id.editRelativeName);
        editRelativeTC = findViewById(R.id.editRelativeTC);
        editRelativeAddress = findViewById(R.id.editRelativeAddress);
        editRelativePhoneNumber = findViewById(R.id.editRelativePhone);
        editRelativity = findViewById(R.id.editRelativity);

        //Buttons
        buttonSetRelative = findViewById(R.id.buttonSetRelative);

        //Configure relatives layout
        checkPatientRelative = findViewById(R.id.checkPatientRelative);

        eLayoutRelative = findViewById(R.id.eLayoutRelative);
    }

    //Collecting todays date data
    private void castCalendar(){
        caledar = Calendar.getInstance();
        year = caledar.get(Calendar.YEAR);
        mounth = caledar.get(Calendar.MONTH);
        day = caledar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_detail);

        productAmounts = new ArrayList<>();

        castCalendar();

        db = new DataBase(this);
        try{
            locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
        }catch (Exception e){
            Toast.makeText(context,"Konum alma servisine ulaşılamıyor!",Toast.LENGTH_LONG).show();
            checkRelativeCordinate.setVisibility(View.GONE);
            checkPatientCordinate.setVisibility(View.GONE);
        }


        initializeView();
        /**
         editPatientTC.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
        if (putPatient())
        Toast.makeText(context, "Böyle bir hasta bulunmamaktadır!", Toast.LENGTH_LONG).show();

        return true;
        }
        return false;
        }
        });
         **/
        checkPatientCordinate = findViewById(R.id.checkPatientCordinate);
        checkPatientCordinate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPatientCordinate.isChecked()) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AddSaleActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                        return;
                    }
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                        return;
                    }

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location == null)
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location == null)
                        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                    try {
                        patientCordinate = location.getLatitude() + "," + location.getLongitude();
                    } catch (NullPointerException e) {
                        Toast.makeText(context, "Konum alınamadı!... " +
                                        "\n Konumunuz Kapalı yada Telefonunuz çekmiyor olabilir.",
                                Toast.LENGTH_LONG).show();
                        checkPatientCordinate.setChecked(false);
                    } catch (Exception e) {
                        Toast.makeText(context, "Yeni Konum alınamadı!... " +
                                        "\n Alınan konum hatalı ,Konumunuz Kapalı ya da Telefonunuz çekmiyor olabilir.",
                                Toast.LENGTH_LONG).show();
                        checkPatientCordinate.setChecked(false);
                    }


                }
            }
        });

        checkRelativeCordinate = findViewById(R.id.checkRelativeCordinate);
        checkRelativeCordinate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(AddSaleActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                        return;
                    }
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                        return;
                    }
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location == null)
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location == null)
                        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    try {
                        relativeCordinate = location.getLatitude() + "," + location.getLongitude();
                    } catch (NullPointerException e) {

                        Toast.makeText(context, "Konum alınamadı!... " +
                                        "\n Konumunuz Kapalı ya da Telefonunuz çekmiyor olabilir.",
                                Toast.LENGTH_LONG).show();
                        checkRelativeCordinate.setChecked(false);
                    } catch (Exception e) {

                        Toast.makeText(context, "Yeni Konum alınamadı!... " +
                                        "\n Alınan konum hatalı ,Konumunuz Kapalı ya da Telefonunuz çekmiyor olabilir.",
                                Toast.LENGTH_LONG).show();
                        checkRelativeCordinate.setChecked(false);
                    }
                }
            }
        });


        editPatientTC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int tcLenght = s.length();
                if (tcLenght == 11) {
                    if (putPatient())
                        Toast.makeText(context, "Hasta Bilgileri Kolay-Yükleme ile geldi", Toast.LENGTH_LONG).show();
                }
            }
        });

        editRelativeTC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 11) {
                    if(putRelative())
                        Toast.makeText(context,
                                "Bilgiler Yüklendi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button buttonSetRelative = findViewById(R.id.buttonSetRelative);
        checkPatientRelative = findViewById(R.id.checkPatientRelative);
        checkPatientRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkPatientRelative.isChecked()) {
                    eLayoutRelative.setVisibility(View.VISIBLE);
                    buttonSetRelative.setVisibility(View.VISIBLE);
                } else {
                    eLayoutRelative.setVisibility(View.GONE);
                    buttonSetRelative.setVisibility(View.GONE);
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
                                if (!buttonSetStartDate.getText().toString().isEmpty()) {
                                    try {
                                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                        Date sDate = df.parse(buttonSetStartDate.getText().toString());
                                        Date eDate = df.parse(String.format("%d/%d/%d", dayOfMonth, month, year));
                                        if (sDate.after(eDate)) {
                                            Toast.makeText(context, "Reçetenin bitiş günü, başlangıç gününden önce olamaz",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    } catch (Exception e) {
                                    }
                                }

                                buttonSetEndDate.setText(String.format("%d/%d/%d", dayOfMonth, month, year));
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
                                if (!buttonSetEndDate.getText().toString().isEmpty()) {
                                    try {
                                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                        Date sDate = df.parse(String.format("%d/%d/%d", dayOfMonth, month, year));
                                        Date eDate = df.parse(buttonSetEndDate.getText().toString());
                                        if (sDate.after(eDate)) {
                                            Toast.makeText(context, "Reçetenin bitiş günü, başlangıç gününden önce olamaz",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    } catch (Exception e) {
                                    }
                                }

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
                ProductAmount productA = getProductAmount(getProduct());
                if (productA == null)
                    return;
                productAmounts.add(productA);
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
                if (productAmounts.size() == 0)
                    return;
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

        editPatientTC.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus)
                if (editPatientTC.getText().length() < 11) {
                    editPatientTC.setError("TC numarası 11 haneli olmalı");
                }
                else
                    editPatientTC.setError(null);
            }
        });

        editRelativeTC.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                if (editRelativeTC.getText().length() < 11) {
                    editRelativeTC.setError("TC numarası 11 haneli olmalı");
                }
                else
                    editRelativeTC.setError(null);
            }
        });

        editPatientPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                if (editPatientPhoneNumber.getText().length() < 10) {
                    editPatientPhoneNumber.setError("GSM 10 haneli olmalı");
                }
                else
                    editPatientPhoneNumber.setError(null);
            }
        });

        editRelativePhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                if (editRelativePhoneNumber.getText().length() < 10) {
                    editRelativePhoneNumber.setError("GSM 10 haneli olmalı");
                }
                else
                    editRelativePhoneNumber.setError(null);
            }
        });

        buttonMakeSale = findViewById(R.id.floatingButtonMakeSale);
        buttonMakeSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativities = new ArrayList<>();
                if (!checkPatientRelative.isChecked()) {
                    if(controlRelative()) {
                        Toast.makeText(context,
                                "Lütfen bilgilerin hepsini (doğru) doldurun!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    relative = getRelative();
                    relativities = getRelativities(relative);

                }
                if (controlPatient() || controlPrescription() || editPatientTC.getError() != null
                        || editPatientPhoneNumber.getError() != null) {
                    Toast toast = Toast.makeText(context, "Lütfen bilgilerin hepsini (doğru) doldurun!", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                } else if (listProductAmount.getChildCount() == 0) {
                    Toast toast = Toast.makeText(context, "Lütfen listeye ürün ekleyin!", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                patient = getPatient(relativities);
                product = getProduct();
                prescription = getPrescription(productAmounts);
                sale = getSale(prescription, patient);

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Dikkat!");
                builder.setMessage("Bilgiler kaydedilecektir, emin misiniz?");
                builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        db.addSale(sale);
                        finish();
                    }
                });

                builder.show();
            }
        });


    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Konum kapalı.")
                .setCancelable(false)
                .setPositiveButton("Konumu Aç", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        checkPatientCordinate.setClickable(false);
                        checkRelativeCordinate.setClickable(false);
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean putPatient() {
        String tc = editPatientTC.getText().toString();
        patient = db.getPatient(tc);
        if (patient == null)
            return false;

        editPatientName.setText(patient.getName());
        editPatientAddress.setText(patient.getAddress());
        editPatientPhoneNumber.setText(patient.getPhoneNumber());
        return true;
    }


    private boolean putRelative() {
        patient = db.getPatient(editPatientTC.getText().toString());
        String tc = editRelativeTC.getText().toString();
        String relativity = "";
        if (patient == null)
            return false;
        relative = db.getRelative(tc);
        relativities = db.getRelativities(patient.getTc());
        if (relativities == null || relative == null)
            return false;

        for (Relativity relativity1 : relativities) {
            if (relativity1.getRelative().getTc().equals(relative.getTc()))
                relativity = relativity1.getRelativity();
        }
        if (relativity.isEmpty())
            return false;

        editRelativeName.setText(relative.getName());
        editRelativeAddress.setText(relative.getAddress());
        editRelativePhoneNumber.setText(relative.getPhoneNumber());
        editRelativity.setText(relativity);
        return true;
    }

    public boolean controlPatient() {
        String phoneNumber = editPatientPhoneNumber.getText().toString();
        String address = editPatientAddress.getText().toString();
        String tc = editPatientTC.getText().toString();
        String name = editPatientName.getText().toString().toUpperCase();

        if (phoneNumber.isEmpty() || address.isEmpty() || tc.isEmpty() || name.isEmpty())
            return true;

        return false;
    }

    public boolean controlRelative() {

        String address = editRelativeAddress.getText().toString();
        String tc = editRelativeTC.getText().toString();
        String name = editRelativeName.getText().toString().toUpperCase();
        String phoneNumber = editRelativePhoneNumber.getText().toString();
        String relativity = editRelativity.getText().toString();

        if (address.isEmpty() || tc.isEmpty() || name.isEmpty() ||
                phoneNumber.isEmpty() || relativity.isEmpty())
            return true;
        return false;
    }

    public boolean controlPrescription() {
        String sDate = buttonSetStartDate.getText().toString();
        String eDate = buttonSetEndDate.getText().toString();

        if (eDate.isEmpty() || sDate.isEmpty())
            return true;
        return false;
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

        patient.setRelatives(relativities);
        String phoneNumber = editPatientPhoneNumber.getText().toString();
        patient.setPhoneNumber(phoneNumber);
        String address = editPatientAddress.getText().toString();
        patient.setAddress(address);
        String tc = editPatientTC.getText().toString();
        patient.setTc(tc);
        String name = editPatientName.getText().toString().toUpperCase();
        patient.setName(name);
        if (checkPatientCordinate.isChecked())
            patient.setCordinate(patientCordinate);


        return patient;
    }

    private Relative getRelative() {

        Relative relative = new Relative();

        String address = editRelativeAddress.getText().toString();
        relative.setAddress(address);
        String tc = editRelativeTC.getText().toString();
        relative.setTc(tc);
        String name = editRelativeName.getText().toString().toUpperCase();
        relative.setName(name);
        String phoneNumber = editRelativePhoneNumber.getText().toString();
        relative.setPhoneNumber(phoneNumber);
        if (checkRelativeCordinate.isChecked())
            relative.setCordinate(relativeCordinate);

        return relative;
    }

    private List<Relativity> getRelativities(Relative relative) {
        List<Relativity> relativities = new ArrayList<>();
        Relativity relativity = new Relativity();

        relativity.setRelative(relative);
        relativity.setRelativity(editRelativity.getText().toString());
        relativities.add(relativity);

        return relativities;
    }

    private Product getProduct() {

        Product product = new Product();

        String type = editProductType.getText().toString();
        String barCode = editProductBarCode.getText().toString();
        String serialNumber = editProductSerialNumber.getText().toString();

        if (type.isEmpty() || barCode.isEmpty() || serialNumber.isEmpty())
            return null;

        product.setType(type);
        product.setBarCode(barCode);
        product.setSerialNumber(serialNumber);

        return product;
    }

    private ProductAmount getProductAmount(Product product) {

        ProductAmount productAmount = new ProductAmount();

        String amount = editProductAmount.getText().toString();

        if (product == null || amount.isEmpty()) {
            Toast toast = Toast.makeText(context, "Lütfen ürün bilgilerini tam girin!", Toast.LENGTH_LONG);
            toast.show();
            return null;
        }

        try {
            productAmount.setProductAmount(Integer.parseInt(amount));
        } catch (NumberFormatException e) {
            Toast toast = Toast.makeText(context, "Lütfen adedi doğru girin!", Toast.LENGTH_LONG);
            toast.show();
            return null;
        }
        productAmount.setProduct(product);

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

        castCalendar();

        sale.setDate(caledar.getTimeInMillis());
        sale.setPrescription(prescription);
        sale.setPatient(patient);
        sale.setRelative(relative);

        return sale;
    }

    private Sale getSale(Prescription prescription, Patient patient) {
        return getSale(prescription, patient, null);
    }
}
