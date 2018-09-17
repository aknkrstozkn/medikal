package com.example.akin.deneme.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.renderscript.Sampler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akin.deneme.R;
import com.example.akin.deneme.core.DataBase;
import com.example.akin.deneme.core.model.Patient;
import com.example.akin.deneme.core.model.ProductAmount;
import com.example.akin.deneme.core.model.Relativity;
import com.example.akin.deneme.core.model.Sale;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MyAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final int TYPE_PRESCRIPTION = 1;
    private final int TYPE_RELATIVE = 2;
    private final int TYPE_SALE = 3;
    private final int TYPE_PATIENT = 4;
    private List<Sale> sales = new ArrayList<>();
    private List<Sale> salesFiltered = new ArrayList<>();
    private Context context;
    private List<Patient> patients = new ArrayList<>();
    private List<Patient> patientsFiltered = new ArrayList<>();
    private SparseBooleanArray expandState;
    private List<Relativity> relativities = new ArrayList<>();
    private DataBase db;


    public MyAdapter(List<T> items, Context context) {

        this.context = context;
        db = new DataBase(context);
        expandState = new SparseBooleanArray();
        if (!items.isEmpty()) {

            if (items.get(0) instanceof Sale) {

                this.sales = (List<Sale>) items;
                salesFiltered = (List<Sale>) items;
                for (int i = 0; i < salesFiltered.size(); i++) expandState.append(i, false);
            } else if (items.get(0) instanceof Patient) {

                this.patients = (List<Patient>) items;
                patientsFiltered = (List<Patient>) items;
                for (int i = 0; i < patients.size(); i++) expandState.append(i, false);
            }
        }

    }

    public MyAdapter(Patient patient, Context context) {
        this.context = context;
        this.relativities = patient.getRelatives();
        expandState = new SparseBooleanArray();

        if(relativities != null)
        for (int i = 0; i < relativities.size(); i++) expandState.append(i, false);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();
                FilterResults results = new FilterResults();
                int value = getItemViewType(0);

                if (value == TYPE_PRESCRIPTION || value == TYPE_SALE){
                    List<Sale> filteredSales = new ArrayList<>();

                    if (query.isEmpty()) {
                        filteredSales = sales;
                    } else {
                        for (Sale sale : sales) {
                            if (sale.getPatient().getName().toLowerCase().contains(query.toLowerCase()) || sale.getPatient().getTc().toLowerCase().contains(query.toLowerCase())) {
                                filteredSales.add(sale);
                            }
                        }
                    }
                    results.count = filteredSales.size();
                    results.values = filteredSales;
                }else{
                    List<Patient> filteredPatients = new ArrayList<>();

                    if (query.isEmpty()) {
                        filteredPatients = patients;
                    } else {
                        for (Patient patient : patients) {
                            if (patient.getName().toLowerCase().contains(query.toLowerCase()) || patient.getTc().toLowerCase().contains(query.toLowerCase())) {
                                filteredPatients.add(patient);
                            }
                        }
                    }
                    results.count = filteredPatients.size();
                    results.values = filteredPatients;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                salesFiltered = (ArrayList<Sale>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    /**
     * public interface MyAdapterListener {
     * void onSelected(String item);
     * }
     **/


    @Override
    public int getItemViewType(int position) {
        if (context instanceof SalesList) {

            return TYPE_SALE;
        } else if (context instanceof OutOfDatePresActivity || context instanceof PrescriptionListActivity) {

            return TYPE_PRESCRIPTION;
        } else if (context instanceof PatientDetailActivity) {

            return TYPE_RELATIVE;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        this.context = parent.getContext();
        switch (viewType) {
            case TYPE_RELATIVE:
                return new ViewHolderRelativities(LayoutInflater.from(context).inflate(R.layout.patient_relative_detail, parent, false));
            case TYPE_PRESCRIPTION:
                return new ViewHolderPrescriptions(LayoutInflater.from(context).inflate(R.layout.row, parent, false));
            case TYPE_SALE:
                return new ViewHolderSales(LayoutInflater.from(context).inflate(R.layout.rowsale, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {
            case TYPE_PRESCRIPTION:
                final ViewHolderPrescriptions viewHolderPrescriptions = (ViewHolderPrescriptions) holder;
                final Sale item = salesFiltered.get(position);

                viewHolderPrescriptions.setIsRecyclable(false);
                //gets, converts and adds the prescription's end-date to a TextView
                Date eDate = new Date(item.getPrescription().geteDate());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String newFormat = formatter.format(eDate);
                viewHolderPrescriptions.textViewEDate.setText(newFormat);

                //gets and adds the prescription products and their amounts to a TextView.
                TextView textViewProductAmount = viewHolderPrescriptions.textProductAmount;
                String text = "";
                List<ProductAmount> productAmounts = item.getPrescription().getPrescriptionsProductList();
                if(productAmounts != null) {
                    for (ProductAmount productAmount : productAmounts) {
                        text += productAmount.getProductAmount() + " tane " + productAmount.getProduct().getType() + " ";
                    }
                    textViewProductAmount.setText(text);
                }
                viewHolderPrescriptions.expandableLinearLayout.setInRecyclerView(true);

                //gets, converts and adds the prescription's start-date to a TextView
                TextView textPresDate = viewHolderPrescriptions.expandableLinearLayout.findViewById(R.id.textPresDate);
                Date sDate = new Date(item.getPrescription().getsDate());
                newFormat = formatter.format(sDate);
                textPresDate.setText(newFormat);

                //writes the patient's name to a TextView
                TextView textPatientName = viewHolderPrescriptions.expandableLinearLayout.findViewById(R.id.textPatientName);
                textPatientName.setText(item.getPatient().getName());

                viewHolderPrescriptions.expandableLinearLayout.setExpanded(expandState.get(position));
                viewHolderPrescriptions.expandableLinearLayout.setListener(new ExpandableLayoutListenerAdapter() {
                    @Override
                    public void onPreOpen() {
                        createRotateAnimator(viewHolderPrescriptions.button, 0f, 180f).start();
                        expandState.put(position, true);
                    }

                    @Override
                    public void onOpened() {
                        viewHolderPrescriptions.button.setBackgroundColor(Color.parseColor("#303F9F"));
                        super.onOpened();


                    }

                    @Override
                    public void onClosed() {
                        viewHolderPrescriptions.button.setBackgroundColor(viewHolderPrescriptions.button.getDrawingCacheBackgroundColor());
                        super.onClosed();

                    }

                    @Override
                    public void onPreClose() {
                        createRotateAnimator(viewHolderPrescriptions.button, 180f, 0f).start();
                        expandState.put(position, false);
                    }

                });

                viewHolderPrescriptions.button.setRotation(expandState.get(position) ? 180f : 0f);
                viewHolderPrescriptions.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        onClickButton(viewHolderPrescriptions.expandableLinearLayout);

                    }
                });

                RelativeLayout buttonPresSale = viewHolderPrescriptions.expandableLinearLayout.findViewById(R.id.buttonSale);
                buttonPresSale.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, SaleDetailActivity.class);
                        intent.putExtra("salePrescriptionId",item.getPrescription().getId());
                        context.startActivity(intent);
                    }
                });

                RelativeLayout buttonPresPatient = viewHolderPrescriptions.
                        expandableLinearLayout.findViewById(R.id.buttonPatient);
                buttonPresPatient.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PatientDetailActivity.class);
                        intent.putExtra("tc",item.getPatient().getTc());
                        context.startActivity(intent);
                    }
                });

                break;
            case TYPE_RELATIVE:

                final ViewHolderRelativities viewHolderRelativities = (ViewHolderRelativities) holder;
                final Relativity relativity = relativities.get(position);

                viewHolderRelativities.setIsRecyclable(false);

                Button button = viewHolderRelativities.button;
                button.setText(relativity.getRelativity() + " " + relativity.getRelative().getName());

                viewHolderRelativities.expandableLinearLayout.setInRecyclerView(true);

                TextView textViewRelativeTC = viewHolderRelativities.expandableLinearLayout.findViewById(R.id.textViewRelativeTC);
                textViewRelativeTC.setText("TC: " + relativity.getRelative().getTc());

                TextView textViewRelativeName = viewHolderRelativities.expandableLinearLayout.findViewById(R.id.textViewRelativeName);
                textViewRelativeName.setText(relativity.getRelative().getName());

                TextView textViewRelativePhoneNumber = viewHolderRelativities.expandableLinearLayout.findViewById(R.id.textViewRelativePhoneNumber);
                textViewRelativePhoneNumber.setText("Tel: " + relativity.getRelative().getPhoneNumber());
                textViewRelativePhoneNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String number = relativity.getRelative().getPhoneNumber();
                        if (number.isEmpty())
                            return;
                        String uri = "tel:0" + number;
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        try {
                            intent.setData(Uri.parse(uri));
                        }catch (Exception e){
                            return;
                        }
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        context.startActivity(intent);
                    }
                });

                TextView textViewRelativeAddress = viewHolderRelativities.expandableLinearLayout.findViewById(R.id.textViewRelativeAddress);
                textViewRelativeAddress.setText(relativity.getRelative().getAddress());

                Button buttonRelativeGPS = viewHolderRelativities.expandableLinearLayout.findViewById(R.id.buttonRelativeGPS);
                final Uri gmmIntentUri = Uri.parse("google.navigation:q=" + relativity.getRelative().getCordinate());
                buttonRelativeGPS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        Intent i = Intent.createChooser(mapIntent, "choose");
                        context.startActivity(i);
                    }
                });

                if(relativity.getRelative().getCordinate() == null)
                    buttonRelativeGPS.setVisibility(View.INVISIBLE);

                viewHolderRelativities.expandableLinearLayout.setExpanded(expandState.get(position));
                viewHolderRelativities.expandableLinearLayout.setListener(new ExpandableLayoutListenerAdapter() {
                    @Override
                    public void onPreOpen() {
                        expandState.put(position, true);
                    }

                    @Override
                    public void onOpened() {
                        super.onOpened();


                    }

                    @Override
                    public void onClosed() {
                        super.onClosed();

                    }

                    @Override
                    public void onPreClose() {
                        expandState.put(position, false);
                    }

                });

                viewHolderRelativities.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        onClickButton(viewHolderRelativities.expandableLinearLayout);
                    }
                });



                break;
            case TYPE_SALE:
                final ViewHolderSales viewHolderSales = (ViewHolderSales) holder;
                final Sale sale = salesFiltered.get(position);

                RelativeLayout buttonSale = viewHolderSales.button;

                TextView textSaleDate = buttonSale.findViewById(R.id.textViewSaleDate);
                Date saleDate = new Date(sale.getDate());
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String date = format.format(saleDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(saleDate);
                String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
                String minute = String.valueOf(calendar.get(Calendar.MINUTE));
                textSaleDate.setText(hour + ":" + minute + "\n" +
                        date);

                TextView textSalePatientName = buttonSale.findViewById(R.id.textViewSalePatientName);
                textSalePatientName.setText(sale.getPatient().getName());

                TextView textProductTypes = buttonSale.findViewById(R.id.textViewProductTypes);
                String productTypes = "";
                List<ProductAmount> productAmountss = sale.getPrescription().getPrescriptionsProductList();
                if(productAmountss != null) {
                    for (ProductAmount productAmount :productAmountss) {
                        productTypes += productAmount.getProduct().getType() + " ";
                    }
                    textProductTypes.setText(productTypes);
                }

                textSalePatientName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PatientDetailActivity.class);
                        intent.putExtra("tc",sale.getPatient().getTc());
                        context.startActivity(intent);
                    }
                });

                textProductTypes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, SaleDetailActivity.class);
                        intent.putExtra("salePrescriptionId" +
                                "",sale.getPrescription().getId());
                        context.startActivity(intent);
                    }
                });

                textProductTypes.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        deleteSaleItem(sale, viewHolderSales);
                        return false;
                    }
                });

                textSalePatientName.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        deleteSaleItem(sale, viewHolderSales);
                        return false;
                    }
                });

                buttonSale.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        deleteSaleItem(sale, viewHolderSales);
                        return false;
                    }
                });

                break;
        }
    }

    public void deleteSaleItem(final Sale sale, final ViewHolderSales viewHolderSales){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Dikkat!");
        builder.setMessage("Bu satış silinecektir!");
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //first of all we should delete sale from database
                db.deleteSale(sale.getPrescription().getId());
                //we need a new list to compare old one
                List<Sale> sales = new ArrayList<>();
                sales.addAll(salesFiltered);
                //we cant use positon parameter here because he is not updating itself with list
                //so we need to use viewHolderSales.getAdapterPosition()
                sales.remove(viewHolderSales.getAdapterPosition());
                //updating recyclerview with DiffUtil.Callback because this is faster than notifyDataSetChanged() method
                updateSaleListItems(sales);
            }
        });

        builder.show();
    }

    public void updateSaleListItems(List<Sale> sales) {
        final SaleDiffCallBack diffCallback = new SaleDiffCallBack(this.salesFiltered, sales);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.salesFiltered.clear();
        this.salesFiltered.addAll(sales);
        diffResult.dispatchUpdatesTo(this);

    }


    private void onClickButton(final ExpandableLayout expandableLinearLayout) {
        expandableLinearLayout.toggle();
    }

    public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    @Override
    public int getItemCount() {
        int type = getItemViewType(0);
        if(type == TYPE_SALE || type == TYPE_PRESCRIPTION){
            if(salesFiltered != null)
                return salesFiltered.size();
        }else if(type == TYPE_RELATIVE){
            if(relativities != null)
                return relativities.size();
        }
        return 0;
    }


    public class ViewHolderPrescriptions extends RecyclerView.ViewHolder {

        public RelativeLayout button;
        public ExpandableLinearLayout expandableLinearLayout;
        public TextView textViewEDate;
        public TextView textProductAmount;

        public ViewHolderPrescriptions(View v) {
            super(v);
            textProductAmount = v.findViewById(R.id.textViewProductAmount);
            textViewEDate = v.findViewById(R.id.textViewEndDate);
            button = v.findViewById(R.id.button);
            expandableLinearLayout = v.findViewById(R.id.expandableLayout);
        }
    }

    public class ViewHolderRelativities extends RecyclerView.ViewHolder {

        public Button button;
        public ExpandableLinearLayout expandableLinearLayout;

        public ViewHolderRelativities(View v) {
            super(v);
            button = v.findViewById(R.id.buttonExpandRelative);
            expandableLinearLayout = v.findViewById(R.id.expandableRelative);
        }
    }

    public class ViewHolderSales extends RecyclerView.ViewHolder {

        public RelativeLayout button;

        public ViewHolderSales(View v) {
            super(v);
            button = v.findViewById(R.id.buttonSale);
        }
    }


}