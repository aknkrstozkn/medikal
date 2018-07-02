package com.example.akin.deneme.core.model;

public class Product {

    private String barCode;
    private String type;
    private String serialNumber;

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getBarCode() {
        return barCode;
    }

    public String getType() {
        return type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
}
