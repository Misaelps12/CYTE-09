package com.cyte_09.ctye_09.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import com.cyte_09.ctye_09.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.cyte_09.ctye_09.data.db.DbManager;
import com.cyte_09.ctye_09.data.modelo.Usuario;

import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText etNombre, etApellido, etTelefono, etEmail, etPassword, etConfirmPassword;
    MaterialButton btnRegister;
    TextView tvLoginLink;
    TextInputLayout tilEmail, tilConfirmPassword;
    DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbManager = new DbManager(this);

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etTelefono = findViewById(R.id.etTelefono);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        tilEmail = findViewById(R.id.tilEmail);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        btnRegister.setOnClickListener(v -> registrarUsuario());

        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registrarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String correo = etEmail.getText().toString().trim();
        String contrasena = etPassword.getText().toString().trim();
        String confirmarContrasena = etConfirmPassword.getText().toString().trim();

        etEmail.setError(null);
        etConfirmPassword.setError(null);

        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() ||
                correo.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {

            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();

            if (nombre.isEmpty()) etNombre.setError("Campo requerido");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etEmail.setError("Formato de correo no válido");
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            return;
        }

        String contrasenaHasheada = hashPassword(contrasena);
        if (contrasenaHasheada == null) {
            Toast.makeText(this, "Error al procesar contraseña", Toast.LENGTH_LONG).show();
            return;
        }

        Usuario nuevoUsuario = new Usuario(nombre, apellido, telefono, correo, contrasenaHasheada);
        long id = dbManager.insertarUsuario(nuevoUsuario);

        if (id == -1) {
            etEmail.setError("El correo ya está en uso");
            Toast.makeText(this, "Error: El correo ya está registrado.", Toast.LENGTH_LONG).show();
        } else {
            guardarEnFirebase(nuevoUsuario);
            Toast.makeText(this, "¡Usuario registrado con éxito!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        }
    }

    private void guardarEnFirebase(Usuario usuario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("nombre", usuario.getNombre());
        data.put("apellido", usuario.getApellido());
        data.put("telefono", usuario.getTelefono());
        data.put("correo", usuario.getCorreo());
        data.put("password", usuario.getPassword());

        db.collection("usuarios")
                .document(usuario.getCorreo())
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Usuario guardado correctamente en Firebase"))
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error al guardar usuario", e);
                    Toast.makeText(this, "Error al guardar en Firebase", Toast.LENGTH_LONG).show();
                });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
