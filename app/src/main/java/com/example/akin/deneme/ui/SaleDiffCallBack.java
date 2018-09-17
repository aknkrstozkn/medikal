package com.example.akin.deneme.ui;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.example.akin.deneme.core.model.Sale;

import java.util.List;

public class SaleDiffCallBack extends DiffUtil.Callback {


    private final List<Sale> oldSaleList;
    private final List<Sale> newSaleList;

    public SaleDiffCallBack(List<Sale> oldSaleList, List<Sale> newSaleList) {
        this.oldSaleList = oldSaleList;
        this.newSaleList = newSaleList;
    }

    @Override
    public int getOldListSize() {
        return oldSaleList.size();
    }

    @Override
    public int getNewListSize() {
        return newSaleList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldSaleList.get(oldItemPosition).getPrescription().getId() == newSaleList.get(
                newItemPosition).getPrescription().getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Sale oldSale = oldSaleList.get(oldItemPosition);
        final Sale newSale = newSaleList.get(newItemPosition);

        return oldSale.getPatient().getTc().equals(newSale.getPatient().getTc());
    }




    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

