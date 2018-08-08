package com.example.akin.deneme.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.akin.deneme.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

public class SaleDetailActivity extends AppCompatActivity {

    ExpandableLinearLayout eLayoutPatient, eLayoutRelative, eLayoutPrescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sale_detail);

    }
/**
    public void buttonPatient(View view){
        eLayoutPatient = findViewById(R.id.expandableLayout1);
        eLayoutPatient.toggle(); // toggle expand and collapse
    }

    public void buttonRelative(View view) {
        eLayoutRelative = findViewById(R.id.expandableLayout2);
        eLayoutRelative.toggle(); // toggle expand and collapse
    }

    public void buttonPrescription(View view) {
        eLayoutPrescription = findViewById(R.id.expandableLayout3);
        eLayoutPrescription.toggle(); // toggle expand and collapse
    }
**/
}
