package com.cyte_09.ctye_09.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.cyte_09.ctye_09.R;
import com.cyte_09.ctye_09.data.db.DbManager;
import com.cyte_09.ctye_09.data.modelo.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etEmailLogin, etPasswordLogin;
    TextInputLayout tilEmailLogin, tilPasswordLogin;

    TextView tvEliminarCuenta, tvForgotPassword;
    MaterialButton btnLogin, btnRegister;
    CheckBox cbRemember;

    DbManager dbManager;
    SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "MisPreferencias";
    private static final String KEY_REMEMBER_ME = "recordarSesion";
    private static final String KEY_EMAIL = "emailGuardado";
    private static final String KEY_PASSWORD = "contraseñaGuardada";

    FirebaseFirestore firebaseDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        tilEmailLogin = findViewById(R.id.tilEmailLogin);
        tilPasswordLogin = findViewById(R.id.tilPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        cbRemember = findViewById(R.id.cbRemember);
        tvEliminarCuenta = findViewById(R.id.tvDeleteAccount);

        dbManager = new DbManager(this);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        firebaseDb = FirebaseFirestore.getInstance();

        cargarPreferencias();

        btnLogin.setOnClickListener(v -> validarLogin());

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        tvEliminarCuenta.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, DeleteActivity.class))
        );
    }

    private void cargarPreferencias() {
        boolean recordar = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
        if (recordar) {
            String emailGuardado = sharedPreferences.getString(KEY_EMAIL, "");
            etEmailLogin.setText(emailGuardado);
            cbRemember.setChecked(true);
        }
    }

    private void validarLogin() {
        tilEmailLogin.setError(null);
        tilPasswordLogin.setError(null);

        String email = etEmailLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        if (email.isEmpty()) {
            tilEmailLogin.setError("El correo es requerido");
            return;
        }
        if (password.isEmpty()) {
            tilPasswordLogin.setError("La contraseña es requerida");
            return;
        }

        String passwordHasheada = hashPassword(password);

        if (passwordHasheada == null) {
            Toast.makeText(this, "Error al procesar la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseDb.collection("usuarios")
                .document(email)
                .get()
                .addOnSuccessListener(document -> manejarLoginFirebase(document, email, passwordHasheada))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al conectar con Firebase", Toast.LENGTH_SHORT).show();
                    validarConSQLite(email, passwordHasheada);
                });
    }

    private void manejarLoginFirebase(DocumentSnapshot document, String email, String passwordHasheada) {
        if (document.exists()) {
            String passwordFirebase = document.getString("password");

            if (passwordFirebase != null && passwordFirebase.equals(passwordHasheada)) {

                guardarPreferencias(email, passwordHasheada);

                Toast.makeText(this, "Inicio de sesión exitoso (Firebase)", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                intent.putExtra("USER_EMAIL", email);
                startActivity(intent);
                finish();

            } else {
                tilPasswordLogin.setError("Contraseña incorrecta");
            }
        } else {
            validarConSQLite(email, passwordHasheada);
        }
    }

    private void validarConSQLite(String email, String passwordHasheada) {

        Usuario usuario = dbManager.getUsuarioPorEmail(email);

        if (usuario == null) {
            tilEmailLogin.setError("Usuario no encontrado");
            return;
        }

        if (usuario.getPassword().equals(passwordHasheada)) {

            guardarPreferencias(email, passwordHasheada);

            Toast.makeText(this, "Inicio de sesión exitoso (SQLite)", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            intent.putExtra("EXTRA_USER_EMAIL", email);
            startActivity(intent);
            finish();

        } else {
            tilPasswordLogin.setError("Contraseña incorrecta");
        }
    }

    private void guardarPreferencias(String email, String passwordHash) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (cbRemember.isChecked()) {
            editor.putBoolean(KEY_REMEMBER_ME, true);
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_PASSWORD, passwordHash);
        } else {
            editor.remove(KEY_REMEMBER_ME);
            editor.remove(KEY_EMAIL);
            editor.remove(KEY_PASSWORD);
        }

        editor.apply();
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
