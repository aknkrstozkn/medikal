package com.example.akin.deneme.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.akin.deneme.R;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Sale> sales = new ArrayList<>();
    private Context context;
    private SparseBooleanArray expandState;
    private List<Relativity> relativities = new ArrayList<>();
    private final int TYPE_PRESCRIPTION = 1;
    private final int TYPE_RELATIVE = 2;

    public MyAdapter(List<Sale> sales) {
        this.sales = sales;
        expandState = new SparseBooleanArray();

        for (int i = 0; i < sales.size(); i++) expandState.append(i, false);
    }

    public MyAdapter(Patient patient) {

        this.relativities = patient.getRelatives();
        expandState = new SparseBooleanArray();

        for (int i = 0; i < relativities.size(); i++) expandState.append(i, false);
    }

    @Override
    public int getItemViewType(int position) {
        if (sales.size() != 0) {
            Log.i("SALE TIPI",String.valueOf(TYPE_PRESCRIPTION));
            return TYPE_PRESCRIPTION;
        } else if (relativities.size() != 0) {
            Log.i("RECEGE TIPI",String.valueOf(TYPE_RELATIVE));
            return TYPE_RELATIVE;
        }
        Log.i("DUSMEDI!!!!!!","ITEM SAYISI DUSMEDI!!!");
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
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder.getItemViewType() == TYPE_PRESCRIPTION) {

            final ViewHolderPrescriptions viewHolderPrescriptions = (ViewHolderPrescriptions) holder;
            Sale item = sales.get(position);

            viewHolderPrescriptions.setIsRecyclable(false);
            //gets, converts and adds the prescription's end-date to a TextView
            Date eDate = new Date(item.getPrescription().geteDate());
            SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy", Locale.getDefault());
            String newFormat = formatter.format(eDate);
            viewHolderPrescriptions.textViewEDate.setText(newFormat);

            //gets and adds the prescription products and their amounts to a TextView.
            TextView textViewProductAmount = viewHolderPrescriptions.textProductAmount;
            String text = "";
            for (ProductAmount productAmount : item.getPrescription().getPrescriptionsProductList()) {
                text += productAmount.getProductAmount() + " tane " + productAmount.getProduct().getType() + " ";
            }
            textViewProductAmount.setText(text);

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
        } else if(holder.getItemViewType() == TYPE_RELATIVE){

            final ViewHolderRelativities viewHolderRelativities = (ViewHolderRelativities) holder;
            Relativity relativity = relativities.get(position);

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

            TextView textViewRelativeAddress = viewHolderRelativities.expandableLinearLayout.findViewById(R.id.textViewRelativeAddress);
            textViewRelativeAddress.setText(relativity.getRelative().getAddress());

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

        }
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
        if (!sales.isEmpty()) {
            Log.i("RECETE SAYISI",String.valueOf(sales.size()));
            return sales.size();
        } else if (!relativities.isEmpty()) {
            Log.i("RELATIVE SAYISI",String.valueOf(relativities.size()));
            return relativities.size();
        }
        Log.i("AMK DUSMEDI!!!","DUSMEDI!!!!!!");
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

}