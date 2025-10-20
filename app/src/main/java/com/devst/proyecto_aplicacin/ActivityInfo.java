package com.devst.proyecto_aplicacin;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_info); // Enlaza con su XML

        Button btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityInfo.this, ActivityMenu.class);
            startActivity(intent);
        });
    }
}
