package com.example.akin.deneme.core.model;

import java.util.List;

public class Prescription {
    /**
     * prescription's key for database.
     */
    private Long id;
    /**
     * Start date of the prescription
     */
    private long date;
    /**
     * current prescription's month duration
     */
    private int duration;
    /**
     *
     */
    private List<ProductAmount> prescriptionsProductList;

    public void setId(Long id) {
        this.id = id;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setPrescriptionsProductList(List<ProductAmount> prescriptionsProductList) {
        this.prescriptionsProductList = prescriptionsProductList;
    }

    public Long getId() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public int getDuration() {
        return duration;
    }

    public List<ProductAmount> getPrescriptionsProductList() {
        return prescriptionsProductList;
    }
}
