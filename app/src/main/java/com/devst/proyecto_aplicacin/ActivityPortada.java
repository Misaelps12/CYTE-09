package com.devst.proyecto_aplicacin;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityPortada extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portada);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        Button btnPortada = findViewById(R.id.btnPortada);
        btnPortada.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityPortada.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}