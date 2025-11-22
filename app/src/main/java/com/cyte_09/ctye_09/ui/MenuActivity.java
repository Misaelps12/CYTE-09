package com.cyte_09.ctye_09.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cyte_09.ctye_09.R;

public class MenuActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "USER_EMAIL";

    private String emailUsuarioLogueado;

    private CardView cardControl;
    private CardView cardPatrones;
    private CardView cardInfo;
    private CardView cardInfoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Activar Edge-to-Edge
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- Obtener email del usuario registrado
        emailUsuarioLogueado = getIntent().getStringExtra(EXTRA_USER_EMAIL);

        if (emailUsuarioLogueado == null || emailUsuarioLogueado.isEmpty()) {
            Toast.makeText(this, "⚠️ ERROR: No se recibió el email del usuario.", Toast.LENGTH_LONG).show();
            Log.e("MENU_DEBUG", "Email es NULO");
        } else {
            Log.d("MENU_DEBUG", "Email recibido correctamente: " + emailUsuarioLogueado);
        }

        // --- Referencias a los CardViews
        cardControl = findViewById(R.id.cardControl);
        cardPatrones = findViewById(R.id.cardPatrones);
        cardInfo = findViewById(R.id.cardInfo);
        cardInfoUsuario = findViewById(R.id.cardInfoUsuario);

        // --- Listeners
        cardControl.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, ControlActivity.class);
            intent.putExtra(EXTRA_USER_EMAIL, emailUsuarioLogueado);
            startActivity(intent);
        });

        cardPatrones.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, PatronesActivity.class);
            intent.putExtra(EXTRA_USER_EMAIL, emailUsuarioLogueado);
            startActivity(intent);
        });

        cardInfo.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, InfoActivity.class);
            intent.putExtra(EXTRA_USER_EMAIL, emailUsuarioLogueado);
            startActivity(intent);
        });

        cardInfoUsuario.setOnClickListener(view -> {
            Log.d("MENU_DEBUG", "Intentando abrir EditInformationActivity con email: " + emailUsuarioLogueado);
            Toast.makeText(MenuActivity.this, "Botón Editar presionado", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MenuActivity.this, EditInformationActivity.class);
            intent.putExtra(EditInformationActivity.EXTRA_USER_EMAIL, emailUsuarioLogueado);
            startActivity(intent);
        });
    }
}
