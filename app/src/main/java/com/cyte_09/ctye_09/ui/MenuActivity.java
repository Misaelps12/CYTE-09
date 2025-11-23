package com.cyte_09.ctye_09.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.cyte_09.ctye_09.R;
import com.cyte_09.ctye_09.data.db.DbManager;
import com.cyte_09.ctye_09.data.modelo.Usuario;

public class MenuActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "USER_EMAIL";

    private String emailUsuarioLogueado;
    private DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // -- Edge to edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbManager = new DbManager(this);

        // -- Obtener email del usuario
        emailUsuarioLogueado = getIntent().getStringExtra(EXTRA_USER_EMAIL);

        if (emailUsuarioLogueado == null || emailUsuarioLogueado.isEmpty()) {
            Toast.makeText(this, "⚠️ ERROR: No se recibió el email del usuario.", Toast.LENGTH_LONG).show();
            Log.e("MENU_DEBUG", "Email es NULO");
            return;
        } else {
            Log.d("MENU_DEBUG", "Email recibido correctamente: " + emailUsuarioLogueado);
        }

        // -- Obtener información del usuario desde SQLite
        Usuario usuario = dbManager.getUsuarioPorEmail(emailUsuarioLogueado);

        if (usuario == null) {
            Toast.makeText(this, "Usuario no encontrado en la base de datos", Toast.LENGTH_LONG).show();
        }

        // -- Referencias de las cards
        CardView cardControl = findViewById(R.id.cardControl);
        CardView cardPatrones = findViewById(R.id.cardPatrones);
        CardView cardInfo = findViewById(R.id.cardInfo);
        CardView cardInfoUsuario = findViewById(R.id.cardInfoUsuario);

        // -- Listeners
        cardControl.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ControlActivity.class);
            intent.putExtra(EXTRA_USER_EMAIL, emailUsuarioLogueado);
            startActivity(intent);
        });

        cardPatrones.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, PatronesActivity.class);
            intent.putExtra(EXTRA_USER_EMAIL, emailUsuarioLogueado);
            startActivity(intent);
        });

        cardInfo.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, InformationActivity.class);
            intent.putExtra(EXTRA_USER_EMAIL, emailUsuarioLogueado);
            startActivity(intent);
        });

        cardInfoUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, EditInformationActivity.class);
            intent.putExtra(EXTRA_USER_EMAIL, emailUsuarioLogueado);
            startActivity(intent);
        });
    }
}
