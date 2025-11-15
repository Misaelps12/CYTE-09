package com.devst.proyecto_aplicacin;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.devst.proyecto_aplicacin.DB.DbManager;
import com.devst.proyecto_aplicacin.DB.Modelo.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity {

    // -- Elementos de la interfaz de usuario
    TextInputEditText etEmailLogin, etPasswordLogin;
    TextInputLayout tilEmailLogin, tilPasswordLogin;

    TextView tvEliminarCuenta, tvForgotPassword;
    MaterialButton btnLogin, btnRegister;
    CheckBox cbRemember;

    // --- Impotaciones de seguridad
    DbManager dbManager;
    SharedPreferences sharedPreferences;

    // -- Variable de sesion
    private static final String PREFS_NAME = "MisPreferencias";
    private static final String KEY_REMEMBER_ME = "recordarSesion";
    private static final String KEY_EMAIL = "emailGuardado";

    private static final String KEY_PASSWORD = "contraseñaGuardada";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // -- Enlaces de los elementos del layout
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        tilEmailLogin = findViewById(R.id.tilEmailLogin);
        tilPasswordLogin = findViewById(R.id.tilPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        cbRemember = findViewById(R.id.cbRemember);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvEliminarCuenta = findViewById(R.id.tvDeleteAccount);


        // Inicializa el manejador de base de datos y las preferencias
        dbManager = new DbManager(this);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Carga el estado guardado del "Recordar sesión"
        cargarPreferencias();

        // Listener del botón de inicio de sesión
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarLogin();
            }
        });

        // Listener del botón de registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirige a la actividad de registro
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // --- Listener de eliminar cuenta
        tvEliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, DeleteActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Carga las preferencias almacenadas.
     * Si el usuario eligió "Recordar sesión", el correo se completa automáticamente.
     */

    private void cargarPreferencias() {
        boolean recordar = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
        if (recordar) {
            String emailGuardado = sharedPreferences.getString(KEY_EMAIL, "");
            etEmailLogin.setText(emailGuardado);
            cbRemember.setChecked(true);
        }
    }



    /**
     * Valida los campos de correo y contraseña, y verifica las credenciales con la base de datos.
     */
    private void validarLogin(){
        // -- Limpia errores previos
        tilEmailLogin.setError(null);
        tilPasswordLogin.setError(null);

        // -- Obtiene los valores ingresados
        String email = etEmailLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        // -- Validaciones básicas de campos vacíos

        if (email.isEmpty()) {
            tilEmailLogin.setError("El correo es requerido");
            return;
        }
        if (password.isEmpty()) {
            tilPasswordLogin.setError("La contraseña es requerida");
            return;
        }

        // -- Encripta la contraseña con SHA-256 antes de compararla
        String passwordHasheada = hashPassword(password);
        if (passwordHasheada == null) {
            Toast.makeText(this, "Error al procesar la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Busca el usuario en la base de datos por su email
        Usuario usuario = dbManager.getUsuarioPorEmail(email);

        if (usuario == null) {
            // Si no existe el usuario
            tilEmailLogin.setError("Usuario no encontrado");
            return;
        }

        // Compara la contraseña encriptada con la almacenada
        if (usuario.getPassword().equals(passwordHasheada)) {
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

            // -- Guarda o elimina preferencias según el estado del checkbox
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (cbRemember.isChecked()) {
                editor.putBoolean(KEY_REMEMBER_ME, true);
                editor.putString(KEY_EMAIL, email);
                editor.putString(KEY_PASSWORD, passwordHasheada);
            } else {
                editor.remove(KEY_REMEMBER_ME);
                editor.remove(KEY_EMAIL);
                editor.remove(KEY_PASSWORD);
            }

            // -- Aplica los cambios
            editor.apply();

            // -- Redirige a la pantalla principal (MenuActivity)
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            String emailIngresado = etEmailLogin.getText().toString().trim();
            intent.putExtra("USER_EMAIL", email);
            startActivity(intent);
            // --  Cierra la actividad actual
            finish();

        } else {
            tilPasswordLogin.setError("Contraseña incorrecta");
        }
    }


    /**
     * Aplica un algoritmo SHA-256 a la contraseña ingresada.
     * Retorna la cadena en formato hexadecimal.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}