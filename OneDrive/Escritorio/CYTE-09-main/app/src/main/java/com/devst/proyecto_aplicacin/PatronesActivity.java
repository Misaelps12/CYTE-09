package com.devst.proyecto_aplicacin;

import android.Manifest;

import android.app.AlertDialog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.devst.proyecto_aplicacin.DB.DbManager;
import com.devst.proyecto_aplicacin.DB.Modelo.Patrones;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class PatronesActivity extends AppCompatActivity {

    Button btnPatron1, btnPatron2, btnPatron3, btnPatron4;

    // Variables Bluetooth
    BluetoothAdapter btAdapter;
    BluetoothSocket btSocket;
    OutputStream outputStream;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrones); // Asegúrate que este sea tu XML

        // Enlazar botones
        btnPatron1 = findViewById(R.id.Patron1);
        btnPatron2 = findViewById(R.id.Patron2);
        btnPatron3 = findViewById(R.id.Patron3);
        btnPatron4 = findViewById(R.id.Patron4);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        dbManager = new DbManager(this);

        // IMPORTANTE: Aquí deberíamos conectarnos.
        // Para simplificar, llamaremos a la lista de dispositivos al iniciar la pantalla
        // o puedes agregar un botón "Conectar" en tu XML arriba como hicimos antes.
        mostrarListaDispositivos();

        // Configurar los botones para enviar letras
        btnPatron1.setOnClickListener(v -> enviarComando("A")); // A = Patrón 1
        btnPatron2.setOnClickListener(v -> enviarComando("B")); // B = Patrón 2
        btnPatron3.setOnClickListener(v -> enviarComando("C")); // C = Patrón 3
        btnPatron4.setOnClickListener(v -> enviarComando("D")); // D = Patrón 4
    }

    // --- LÓGICA DE CONEXIÓN

    private void mostrarListaDispositivos() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            return;
        }

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        ArrayList<String> deviceNames = new ArrayList<>();
        final ArrayList<String> deviceAddresses = new ArrayList<>();

        for (BluetoothDevice device : pairedDevices) {
            deviceNames.add(device.getName());
            deviceAddresses.add(device.getAddress());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Conecta a los LEDs:");
        builder.setItems(deviceNames.toArray(new String[0]), (dialog, which) -> {
            conectarBluetooth(deviceAddresses.get(which));
        });
        builder.show();
    }

    private void conectarBluetooth(String address) {
        new Thread(() -> {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return;

                BluetoothDevice device = btAdapter.getRemoteDevice(address);
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                btSocket.connect();
                outputStream = btSocket.getOutputStream();

                runOnUiThread(() -> Toast.makeText(this, "¡Conectado a las luces!", Toast.LENGTH_SHORT).show());

            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al conectar", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void enviarComando(String comando) {
        if (outputStream != null) {
            try {
                // 1. Intentar enviar el comando
                outputStream.write(comando.getBytes());

                // 2. SI LLEGA AQUÍ, EL ENVÍO FUE "CORRECTO"
                //    Procedemos a guardar en la BD (si es un patrón)
                if (comando.equals("A") || comando.equals("B") || comando.equals("C") || comando.equals("D")) {
                    guardarPatronEnDB(comando);
                }

            } catch (IOException e) {
                Toast.makeText(this, "Error al enviar", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No estás conectado", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarPatronEnDB(String comando) {
        // Preparamos el objeto Patrones.
        // Solo la columna del comando enviado tendrá un valor, el resto "N/A".
        String a = "N/A", b = "N/A", c = "N/A", d = "N/A";

        switch (comando) {
            case "A": a = "Patrón 1 Activado"; break;
            case "B": b = "Patrón 2 Activado"; break;
            case "C": c = "Patrón 3 Activado"; break;
            case "D": d = "Patrón 4 Activado"; break;
        }

        Patrones nuevoPatron = new Patrones(a, b, c, d);

        // Usar un hilo separado para la base de datos (Buena práctica)
        new Thread(() -> {
            long id = dbManager.insertarPatron(nuevoPatron);
            if (id != -1) {
                // Éxito
                runOnUiThread(() -> Log.d("DB", "Patrón guardado en BD con ID: " + id));
            } else {
                // Falla
                runOnUiThread(() -> Log.e("DB", "Error al guardar patrón en BD"));
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try { if (btSocket != null) btSocket.close(); } catch (Exception e) {}
    }
}