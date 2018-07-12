package com.example.akin.deneme.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.akin.deneme.R;
import com.example.akin.deneme.core.model.ProductAmount;
import com.example.akin.deneme.core.model.Sale;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Sale> sales;
    private Context context;
    private SparseBooleanArray expandState;

    public MyAdapter(List<Sale> sales) {
        this.sales = sales;

        for (int i = 0; i < sales.size(); i++) {
            expandState.append(i, false);
        }
    }

    /**
     * class ViewHolder0 extends RecyclerView.ViewHolder {
     * ...
     * public ViewHolder0(View itemView){
     * ...
     * }
     * }
     * <p>
     * class ViewHolder2 extends RecyclerView.ViewHolder {
     * ...
     * public ViewHolder2(View itemView){
     * ...
     * }
     **/
    @Override
    public int getItemViewType(int position) { return super.getItemViewType(position); }

    /**
     * this.context = parent.getContext();
     * return new ViewHolder(LayoutInflater.from(context)
     * .inflate(R.layout.recycler_view_list_row, parent, false));
     **/
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,final int viewType) {
        /**
         switch (viewType) {
         case 0: return new ViewHolder0(...);
         case 2: return new ViewHolder2(...);
         ...
         }
         **/
        this.context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Sale item = sales.get(position);

        holder.setIsRecyclable(false);
        //gets, converts and adds the prescription's end-date to a TextView
        Date eDate = new Date(item.getPrescription().geteDate());
        SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yyyy");
        String newFormat = formatter.format(eDate);
        holder.textViewEDate.setText(newFormat);

        //gets and adds the prescription products and their amounts to a TextView.
        TextView textViewProductAmount = holder.textProductAmount;
        String text = "";
        for (ProductAmount productAmount : item.getPrescription().getPrescriptionsProductList()) {
            text += productAmount.getProductAmount() + " tane " + productAmount.getProduct().getType() + " ";
        }
        textViewProductAmount.setText(text);

        holder.expandableLinearLayout.setInRecyclerView(true);

        //gets, converts and adds the prescription's start-date to a TextView
        TextView textPresDate = holder.expandableLinearLayout.findViewById(R.id.textPresDate);
        Date sDate = new Date(item.getPrescription().getsDate());
        newFormat = formatter.format(sDate);
        textPresDate.setText(newFormat);

        //writes the patient's name to a TextView
        TextView textPatientName = holder.expandableLinearLayout.findViewById(R.id.textPatientName);
        textPatientName.setText(item.getPatient().getName());


        expandState = new SparseBooleanArray();
        holder.expandableLinearLayout.setExpanded(expandState.get(position));
        holder.expandableLinearLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                holder.button.setBackgroundColor(Color.parseColor("#303F9F"));
                createRotateAnimator(holder.button, 0f, 180f).start();
                expandState.put(position, true);
            }

            @Override
            public void onPreClose() {
                holder.button.setBackgroundColor(holder.button.getDrawingCacheBackgroundColor());
                createRotateAnimator(holder.button, 180f, 0f).start();
                expandState.put(position, false);
            }
        });

        holder.button.setRotation(expandState.get(position) ? 180f : 0f);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onClickButton(holder.expandableLinearLayout);
            }
        });


        /**
         switch (holder.getItemViewType()) {
         case 0:
         ViewHolder0 viewHolder0 = (ViewHolder0)holder;
         ...
         break;

         case 2:
         ViewHolder2 viewHolder2 = (ViewHolder2)holder;
         ...
         break;
         **/
    }

    private void onClickButton(final ExpandableLayout expandableLinearLayout) { expandableLinearLayout.toggle(); }

    public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout button;
        public ExpandableLinearLayout expandableLinearLayout;
        public TextView textViewEDate;
        public TextView textProductAmount;

        public ViewHolder(View v) {
            super(v);
            textProductAmount = v.findViewById(R.id.textViewProductAmount);
            textViewEDate = v.findViewById(R.id.textViewEndDate);
            button = v.findViewById(R.id.button);
            expandableLinearLayout = v.findViewById(R.id.expandableLayout);
        }
    }
}