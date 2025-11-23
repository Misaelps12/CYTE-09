package com.cyte_09.ctye_09.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cyte_09.ctye_09.R;
import com.cyte_09.ctye_09.data.db.DbManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DeleteActivity extends AppCompatActivity {

    private TextInputEditText etEmailLogin, etPasswordLogin;
    private CheckBox confirmDelete;
    private Button btnDelete;

    private DbManager dbManager;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete);

        dbManager = new DbManager(this);
        firestore = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmailLogin = findViewById(R.id.etEmailDelete);
        etPasswordLogin = findViewById(R.id.etPasswordDelete);
        confirmDelete = findViewById(R.id.cbDelete);
        btnDelete = findViewById(R.id.btnDeleteAccount);
        btnDelete.setOnClickListener(v -> eliminarCuenta());

        // Autocompletar email desde el menú
        String emailUsuario = getIntent().getStringExtra(MenuActivity.EXTRA_USER_EMAIL);
        if(emailUsuario != null) {
            etEmailLogin.setText(emailUsuario);
            etEmailLogin.setEnabled(false);
        }
    }

    private void eliminarCuenta() {
        String email = etEmailLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!confirmDelete.isChecked()) {
            Toast.makeText(this, "Debe confirmar que desea eliminar la cuenta.", Toast.LENGTH_LONG).show();
            return;
        }

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            Toast.makeText(this, "Error al procesar la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Primero borrar en Firestore
        firestore.collection("usuarios")
                .document(email)
                .delete()
                .addOnSuccessListener(aVoid -> {

                    // Luego borrar en SQLite
                    int filasEliminadas = dbManager.eliminarUsuario(email, hashedPassword);

                    if (filasEliminadas > 0) {
                        Toast.makeText(DeleteActivity.this, "Cuenta eliminada con éxito", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DeleteActivity.this, "Cuenta eliminada de Firebase, pero no se encontró en local", Toast.LENGTH_LONG).show();
                    }

                    Intent intent = new Intent(DeleteActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DeleteActivity.this, "Error al eliminar en Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

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

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}