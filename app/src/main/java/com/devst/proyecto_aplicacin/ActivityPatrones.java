package com.devst.proyecto_aplicacin;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityPatrones extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_patrones);

        Button btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityPatrones.this, ActivityMenu.class);
            startActivity(intent);
        });
    }
}