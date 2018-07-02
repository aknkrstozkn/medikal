package com.example.akin.deneme.core.model;

import java.util.HashMap;
import java.util.List;

public class Patient extends Person {

    private List<Relativity> relatives;

    public List<Relativity> getRelatives() {
        return relatives;
    }

    public void setRelatives(List<Relativity> relatives) {
        this.relatives = relatives;
    }
}
