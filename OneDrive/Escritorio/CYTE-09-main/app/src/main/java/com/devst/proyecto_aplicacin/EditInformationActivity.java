package com.devst.proyecto_aplicacin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.devst.proyecto_aplicacin.DB.DbManager;
import com.devst.proyecto_aplicacin.DB.Modelo.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Activity para permitir al usuario ver y modificar su información de perfil.
 * Requiere el correo del usuario logueado pasado por Intent.
 */
public class EditInformationActivity extends AppCompatActivity {

    // Constante para la clave del email en el Intent
    public static final String EXTRA_USER_EMAIL = "extra_user_email";

    // -- Elementos de la interfaz de usuario (Vistas)
    private TextInputEditText etNombre, etApellido, etTelefono, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnAceptar;
    private TextInputLayout tilConfirmPassword;

    // --- Variables de Datos y Seguridad
    private DbManager dbManager; // Administrador de la base de datos
    private String currentEmail; // Para almacenar el email del usuario actual logueado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_information);

        // -- Inicializa el administrador de base de datos
        dbManager = new DbManager(this);

        // -- Configura los Insets del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // -- Obtiene el email del usuario logueado desde el Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_USER_EMAIL)) {
            currentEmail = intent.getStringExtra(EXTRA_USER_EMAIL);

            Toast.makeText(this, "Email recibido: " + currentEmail, Toast.LENGTH_LONG).show();
            Log.d("DEBUG_EDIT", "Email recibido para editar: " + currentEmail);

        } else {
            Toast.makeText(this, "Error: Sesión no válida.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // -- Vincula las variables con los IDs de las vistas
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etTelefono = findViewById(R.id.etTelefono);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnAceptar = findViewById(R.id.btnAceptar);
        tilConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);

        // -- Carga los datos del usuario actual
        cargarDatosUsuario(currentEmail);

        // -- Configura el Listener del botón Aceptar
        btnAceptar.setOnClickListener(v -> guardarCambios());
    }

    // ---------------------------------------------------------------------------------------------
    // --- LÓGICA DE CARGA DE DATOS ---
    // ---------------------------------------------------------------------------------------------

    private void cargarDatosUsuario(String email) {
        Usuario usuario = dbManager.getUsuarioPorEmail(email);

        if (usuario != null) {
            etNombre.setText(usuario.getNombre());
            etApellido.setText(usuario.getApellido());
            etTelefono.setText(usuario.getTelefono());
            etEmail.setText(usuario.getCorreo());
            etEmail.setEnabled(false); // El email no se puede editar
        } else {
            Toast.makeText(this, "Error al cargar datos del usuario.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // --- LÓGICA DE GUARDADO DE CAMBIOS ---
    // ---------------------------------------------------------------------------------------------

    private void guardarCambios() {
        String nuevoNombre = etNombre.getText().toString().trim();
        String nuevoApellido = etApellido.getText().toString().trim();
        String nuevoTelefono = etTelefono.getText().toString().trim();
        String nuevaContrasena = etPassword.getText().toString().trim();
        String confirmarContrasena = etConfirmPassword.getText().toString().trim();

        // -- Validaciones básicas
        if (nuevoNombre.isEmpty() || nuevoApellido.isEmpty() || nuevoTelefono.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos de información personal.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean huboCambios = false;

        // -- Actualización de Contraseña (si aplica)
        if (!nuevaContrasena.isEmpty() || !confirmarContrasena.isEmpty()) {
            if (!nuevaContrasena.equals(confirmarContrasena)) {
                tilConfirmPassword.setError("Las contraseñas no coinciden.");
                return;
            } else {
                tilConfirmPassword.setError(null); // Limpiar error previo
            }

            String contrasenaHasheada = hashPassword(nuevaContrasena);
            if (contrasenaHasheada != null && dbManager.actualizarPassword(currentEmail, contrasenaHasheada)) {
                huboCambios = true;
            } else {
                Toast.makeText(this, "Error al actualizar la contraseña.", Toast.LENGTH_SHORT).show();
            }
        }

        // -- Actualización de Información Personal
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setNombre(nuevoNombre);
        usuarioActualizado.setApellido(nuevoApellido);
        usuarioActualizado.setTelefono(nuevoTelefono);
        usuarioActualizado.setCorreo(currentEmail);

        if (dbManager.actualizarInformacion(usuarioActualizado)) {
            huboCambios = true;
        }

        // -- Resultado Final
        if (huboCambios) {
            Toast.makeText(this, "✅ Información actualizada con éxito.", Toast.LENGTH_SHORT).show();
            finish(); // Vuelve al menú
        } else {
            Toast.makeText(this, "⚠️ No se detectaron cambios para guardar.", Toast.LENGTH_SHORT).show();
            // Opcional: finish(); // Descomenta si quieres que se cierre aunque no haya cambios.
        }
    }

    // ---------------------------------------------------------------------------------------------
    // --- FUNCIÓN DE HASHING (SHA-256) ---
    // ---------------------------------------------------------------------------------------------

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            Log.e("HASHING", "Error al hashear contraseña", e);
            return null;
        }
    }
}