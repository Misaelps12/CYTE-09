package com.devst.proyecto_aplicacin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ActivityMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtenemos lo btn
        Button btnControl = findViewById(R.id.btnControl);
        Button btnPatron = findViewById(R.id.btnPatron);
        Button btnInformation = findViewById(R.id.btnInformation);

        // Crear el Listener
        btnControl.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityMenu.this, ActivityControl.class);
            startActivity(intent);
        });

        // Crear el Listener
        btnPatron.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityMenu.this, ActivityPatrones.class);
            startActivity(intent);
        });

        // Crear el Listener
        btnInformation.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityMenu.this, ActivityInfo.class);
            startActivity(intent);
        });
    }
}