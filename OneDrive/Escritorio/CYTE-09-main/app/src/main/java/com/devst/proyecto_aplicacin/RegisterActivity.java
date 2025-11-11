package com.devst.proyecto_aplicacin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

// -- Importamos los componentes de Material Design que usaste
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

// --  Clases del paquete de base de datos
import com.devst.proyecto_aplicacin.DB.DbManager;
import com.devst.proyecto_aplicacin.DB.Usuario;

// -- Librerías de seguridad para hashear contraseñas
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class RegisterActivity extends AppCompatActivity {

    // -- Elementos de la interfaz de usuario
    TextInputEditText etNombre, etApellido, etTelefono, etEmail, etPassword, etConfirmPassword;
    MaterialButton btnRegister;
    TextView tvLoginLink;
    TextInputLayout tilEmail, tilConfirmPassword;

    // --- Impotaciones de seguridad
    DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // -- Inicializa el administrador de base de datos
        dbManager = new DbManager(this);

        // -- Vincula cada variable con su elemento visual
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etTelefono = findViewById(R.id.etTelefono);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        tilEmail = findViewById(R.id.textInputLayoutEmail);
        tilConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);

        // -- Acción del botón de registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        // -- Acción del texto “¿Ya tienes cuenta?”
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Metodo que valida los datos del formulario y registra al usuario si todo esta correcto
     */

    private void registrarUsuario() {

        // -- Obtiene los valores ingresados por el usuario
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String correo = etEmail.getText().toString().trim();
        String contrasena = etPassword.getText().toString().trim();
        String confirmarContrasena = etConfirmPassword.getText().toString().trim();

        // -- Limpia errores previos
        etEmail.setError(null);
        etConfirmPassword.setError(null);

        // -- Verifica que ningún campo esté vacío
        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() ||
                correo.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            if (nombre.isEmpty()) etNombre.setError("Campo requerido");
            return;
        }

        // -- Valida que el formato del correo sea correcto
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etEmail.setError("Formato de correo no válido");
            return;
        }

        // -- Valida que las contraseñas coincidan
        if (!contrasena.equals(confirmarContrasena)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            return;
        }

        // -- Se encripta la contraseña antes de guardarla para mayor seguridad
        String contrasenaHasheada = hashPassword(contrasena);
        if (contrasenaHasheada == null) {
            Toast.makeText(this, "Error crítico al procesar contraseña", Toast.LENGTH_LONG).show();
            return;
        }


        // -- Crea un nuevo Usuario con la contraseña hasheada
        Usuario nuevoUsuario = new Usuario(nombre, apellido, telefono, correo, contrasenaHasheada);

        // -- Inserta el usuario en la base de datos
        long id = dbManager.insertarUsuario(nuevoUsuario);

        // -- Si la inserción falla (por ejemplo, correo duplicado)
        if (id == -1) {
            etEmail.setError("El correo ya está en uso");
            Toast.makeText(this, "Error: El correo ya está registrado.", Toast.LENGTH_LONG).show();
        } else {
            // -- Registro exitoso
            Toast.makeText(this, "¡Usuario registrado con éxito!", Toast.LENGTH_LONG).show();

            // -- Redirige al usuario a la pantalla de inicio de sesión
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Aplica el algoritmo SHA-256 para cifrar la contraseña antes de guardarla.
     * Retorna una cadena hexadecimal que representa el hash.
     */
    private String hashPassword(String password) {
        try {

            // -- Crea un objeto MessageDigest para usar SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // -- Convierte la contraseña en bytes y calcula el hash
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // -- Convierte el hash en una cadena hexadecimal legible
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // -- Devuelve el hash en texto plano
            return hexString.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            // -- En caso de error, retorna null
            return null;
        }
    }
}