package com.cyte_09.ctye_09.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.cyte_09.ctye_09.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        int duracionSplash = 3000;

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, PortadaActivity.class);
            startActivity(intent);
            finish();
        }, duracionSplash);
    }
}
