package com.devst.proyecto_aplicacin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuActivity extends AppCompatActivity {

    private String emailUsuarioLogueado;

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

        emailUsuarioLogueado = getIntent().getStringExtra("USER_EMAIL");

        if (emailUsuarioLogueado == null || emailUsuarioLogueado.isEmpty()) {
            // Si falla, avisa inmediatamente al entrar al menú
            Toast.makeText(this, "⚠️ ERROR: No se recibió el email del usuario.", Toast.LENGTH_LONG).show();
            Log.e("MENU_DEBUG", "Email es NULO");
        } else {
            // Si funciona, avisa también para confirmar
            Log.d("MENU_DEBUG", "Email recibido correctamente: " + emailUsuarioLogueado);
        }



            // Obtenemos lo btn
            Button btnControl = findViewById(R.id.btnControl);
            Button btnPatron = findViewById(R.id.btnPatron);
            Button btnInformation = findViewById(R.id.btnInformation);
            Button btnEdit = findViewById(R.id.btnEdit);


            // Crear el Listener
            btnControl.setOnClickListener(view -> {
                Intent intent = new Intent(MenuActivity.this, ControlActivity.class);
                startActivity(intent);
            });

            // Crear el Listener
            btnPatron.setOnClickListener(view -> {
                Intent intent = new Intent(MenuActivity.this, PatronesActivity.class);
                startActivity(intent);
            });

            // Crear el Listener
            btnInformation.setOnClickListener(view -> {
                Intent intent = new Intent(MenuActivity.this, InfoActivity.class);
                startActivity(intent);
            });

            // Crear el Listener
            btnEdit.setOnClickListener(view -> {
                Log.d("DEBUG_MENU", "Intentando abrir EditInfo con email: " + emailUsuarioLogueado);
                Toast.makeText(MenuActivity.this, "Botón Editar presionado", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MenuActivity.this, EditInformationActivity.class);
                intent.putExtra(EditInformationActivity.EXTRA_USER_EMAIL, emailUsuarioLogueado);
                startActivity(intent);
            });
        }
    }
