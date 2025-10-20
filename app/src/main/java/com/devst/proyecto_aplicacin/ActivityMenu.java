package com.devst.proyecto_aplicacin;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_menu);

        Button btnControl = findViewById(R.id.btnControl);
        btnControl.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityMenu.this, ActivityControl.class);
            startActivity(intent);
        });

        Button btnPatron = findViewById(R.id.btnPatron);
        btnPatron.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityMenu.this, ActivityPatrones.class);
            startActivity(intent);
        });
    }
}