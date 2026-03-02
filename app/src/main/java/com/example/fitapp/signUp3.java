package com.example.fitapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
public class signUp3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3);
    }

    public void clickedGetStartedBtn(View view) {
        Intent si = new Intent(this, FragmentsActivity.class);
        startActivity(si);
    }
}