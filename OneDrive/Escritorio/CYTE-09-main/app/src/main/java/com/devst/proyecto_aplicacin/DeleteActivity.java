package com.devst.proyecto_aplicacin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.devst.proyecto_aplicacin.DB.DbManager;
import com.google.android.material.textfield.TextInputEditText;

import java.nio.charset.StandardCharsets; // Importación necesaria para UTF_8
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class DeleteActivity extends AppCompatActivity {

    // -- Declaramos las variables
    private TextInputEditText etEmailLogin;
    private TextInputEditText etPasswordLogin;
    private CheckBox confitmDelete;
    private Button btnDelete;


    // -- Declaramos el manager de la base de datos
    private DbManager dbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete);

        // -- Inicializamos la DBManger
        dbManager = new DbManager(this);

        // -- Configuramos de insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // -- Vinculamos las vistas
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        confitmDelete = findViewById(R.id.confitmDelete);
        btnDelete = findViewById(R.id.btnDelete);


        // -- Configuramos el listener del botón eliminar
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarCuenta();
            }
        });
    }

    /**
     * Lógica para obtener las credenciales, verificar la confirmación y ejecutar la eliminación.
     */

    private void eliminarCuenta(){

        //-- obtendo los datos ingresados
        String email = etEmailLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        // -- Validaciones basicas
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!confitmDelete.isChecked()) {
            Toast.makeText(this, "Debe confirmar que está seguro para eliminar la cuenta.", Toast.LENGTH_LONG).show();
            return;
        }

        // -- hasheamos la contraseña
        String hashedPassword = hashPassword(password);

        if (hashedPassword == null) {
            Toast.makeText(this, "Error al procesar la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        //--- Ejecutamos la eliminacion de la fila
        int filasEliminadas = dbManager.eliminarUsuario(email, hashedPassword);

        if (filasEliminadas > 0) {
            Toast.makeText(this, "Cuenta eliminada con éxito", Toast.LENGTH_LONG).show();

            // ⚠️ CORRECCIÓN: Usar banderas para limpiar la pila y evitar doble llamada
            Intent intent = new Intent(DeleteActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); // Única llamada necesaria

            finish();
        } else {
            // Falla porque las credenciales (hash) no coincidieron o el usuario no existe.
            Toast.makeText(this, "Error al eliminar la cuenta. Credenciales no válidas.", Toast.LENGTH_LONG).show();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Usar UTF_8 garantiza la consistencia del hash con el registro
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Este error ocurre si el algoritmo (SHA-256) no es compatible (muy raro en Android)
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            // Manejar otros posibles errores de codificación
            e.printStackTrace();
            return null;
        }
    }
}