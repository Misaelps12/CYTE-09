package com.cyte_09.ctye_09.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cyte_09.ctye_09.data.db.DbManager;
import com.cyte_09.ctye_09.data.modelo.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import com.cyte_09.ctye_09.R;

public class EditInformationActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "USER_EMAIL";

    private TextInputEditText etNombre, etApellido, etTelefono, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnAceptar;
    private TextInputLayout tilConfirmPassword;

    private DbManager dbManager;
    private FirebaseFirestore firebaseDb;
    private String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_information);

        dbManager = new DbManager(this);
        firebaseDb = FirebaseFirestore.getInstance();

        // Ajustar padding según sistema de barras
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Recibir email
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_USER_EMAIL)) {
            currentEmail = intent.getStringExtra(EXTRA_USER_EMAIL);
            Log.d("DEBUG_EDIT", "Email recibido para editar: " + currentEmail);
        } else {
            Toast.makeText(this, "Error: Sesión no válida.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // IDs corregidos según tu XML
        etNombre = findViewById(R.id.etNombreEdit);
        etApellido = findViewById(R.id.etApellidoEdit);
        etTelefono = findViewById(R.id.etTelefonoEdit);
        etEmail = findViewById(R.id.etEmailEdit);
        etPassword = findViewById(R.id.etNuevaPassword);
        etConfirmPassword = findViewById(R.id.etConfirmNuevaPassword);
        btnAceptar = findViewById(R.id.btnGuardarCambios);
        tilConfirmPassword = findViewById(R.id.tilConfirmNuevaPass);

        cargarDatosFirebase(currentEmail);

        btnAceptar.setOnClickListener(v -> guardarCambios());
    }

    // Cargar datos desde Firebase
    private void cargarDatosFirebase(String email) {
        firebaseDb.collection("usuarios")
                .document(email)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        etNombre.setText(document.getString("nombre"));
                        etApellido.setText(document.getString("apellido"));
                        etTelefono.setText(document.getString("telefono"));
                        etEmail.setText(email);
                        etEmail.setEnabled(false);

                        // Sincronizar con SQLite
                        Usuario u = new Usuario(
                                document.getString("nombre"),
                                document.getString("apellido"),
                                document.getString("telefono"),
                                email,
                                document.getString("password")
                        );
                        dbManager.actualizarInformacion(u);
                    } else {
                        cargarDatosSQLite(email);
                    }
                })
                .addOnFailureListener(e -> cargarDatosSQLite(email));
    }

    // Cargar datos desde SQLite (fallback)
    private void cargarDatosSQLite(String email) {
        Usuario usuario = dbManager.getUsuarioPorEmail(email);

        if (usuario != null) {
            etNombre.setText(usuario.getNombre());
            etApellido.setText(usuario.getApellido());
            etTelefono.setText(usuario.getTelefono());
            etEmail.setText(usuario.getCorreo());
            etEmail.setEnabled(false);
        } else {
            Toast.makeText(this, "Error al cargar datos.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // Guardar cambios en Firebase y SQLite
    private void guardarCambios() {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String nuevaPass = etPassword.getText().toString().trim();
        String confirmarPass = etConfirmPassword.getText().toString().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        String passwordHash;

        if (!nuevaPass.isEmpty() || !confirmarPass.isEmpty()) {
            if (!nuevaPass.equals(confirmarPass)) {
                tilConfirmPassword.setError("Las contraseñas no coinciden");
                return;
            } else {
                tilConfirmPassword.setError(null);
            }
            passwordHash = hashPassword(nuevaPass);
            dbManager.actualizarPassword(currentEmail, passwordHash);
        } else {
            passwordHash = null;
        }

        // Datos para Firebase
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", nombre);
        data.put("apellido", apellido);
        data.put("telefono", telefono);
        if (passwordHash != null) data.put("password", passwordHash);

        firebaseDb.collection("usuarios")
                .document(currentEmail)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    // Guardar en SQLite
                    Usuario u = new Usuario(nombre, apellido, telefono, currentEmail, passwordHash);
                    dbManager.actualizarInformacion(u);
                    Toast.makeText(this, "Información actualizada.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar en Firebase.", Toast.LENGTH_SHORT).show();
                });
    }

    // Hash SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
