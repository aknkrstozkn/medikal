package com.example.akin.deneme.core.model;

public abstract class Person {

    private String name;
    private String tc;
    private String address;
    private String cordinate;
    private String phoneNumber;

    public String getName() {
        return name;
    }

    public String getTc() {
        return tc;
    }

    public String getAddress() {
        return address;
    }

    public String getCordinate() {
        return cordinate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTc(String tc) {
        this.tc = tc;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCordinate(String cordinate) {
        this.cordinate = cordinate;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
