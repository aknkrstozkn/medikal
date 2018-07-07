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
    private long sDate;
    /**
     * current prescription's month duration
     */
    private long eDate;
    private int validity;

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setsDate(long sDate) {
        this.sDate = sDate;
    }

    public void seteDate(long eDate) {
        this.eDate = eDate;
    }

    public void setPrescriptionsProductList(List<ProductAmount> prescriptionsProductList) {
        this.prescriptionsProductList = prescriptionsProductList;
    }

    public long getsDate() {
        return sDate;
    }

    public long geteDate() {
        return eDate;
    }

    public List<ProductAmount> getPrescriptionsProductList() {
        return prescriptionsProductList;
    }

    /**
     *
     */

    private List<ProductAmount> prescriptionsProductList;


}
