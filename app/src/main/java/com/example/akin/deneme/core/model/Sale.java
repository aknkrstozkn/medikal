package com.example.akin.deneme.core.model;

public class Sale {

    private Prescription prescription;
    private Patient patient;
    private Relative relative;
    private long date;

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setRelative(Relative relative) {
        this.relative = relative;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Patient getPatient() {
        return patient;
    }

    public Relative getRelative() {
        return relative;
    }

    public long getDate() {
        return date;
    }
}
