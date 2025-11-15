package com.devst.proyecto_aplicacin;

import android.Manifest;

import android.app.AlertDialog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;



public class ControlActivity extends AppCompatActivity {

    // Agregamos btnConnect
    Button btnUp, btnDown, btnLeft, btnRight, btnConnect;

    BluetoothSocket btSocket;
    OutputStream outputStream;
    BluetoothAdapter btAdapter;

    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // YA NO USAMOS DIRECCIÓN FIJA. La obtendremos de la lista.
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_control);

        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnConnect = findViewById(R.id.btnConnect); // Enlazamos el nuevo botón

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBluetoothPermissions();

        // 1. Al hacer clic en "Buscar", mostramos la lista
        btnConnect.setOnClickListener(v -> showPairedDevicesList());

        // Listeners de movimiento (igual que antes)
        btnUp.setOnTouchListener(controlTouchListener("F"));
        btnDown.setOnTouchListener(controlTouchListener("B"));
        btnLeft.setOnTouchListener(controlTouchListener("L"));
        btnRight.setOnTouchListener(controlTouchListener("R"));
    }

    /**
     * Muestra un menú con los dispositivos vinculados (HC-05, etc)
     */
    private void showPairedDevicesList() {
        // Verificaciones de seguridad
        if (btAdapter == null) return;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSIONS);
            return;
        }

        // Obtener dispositivos vinculados
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            ArrayList<String> deviceNames = new ArrayList<>();
            final ArrayList<String> deviceAddresses = new ArrayList<>();

            for (BluetoothDevice device : pairedDevices) {
                deviceNames.add(device.getName() + "\n" + device.getAddress());
                deviceAddresses.add(device.getAddress());
            }

            final String[] namesArray = deviceNames.toArray(new String[0]);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Selecciona tu Araña:");
            builder.setItems(namesArray, (dialog, which) -> {
                // Cuando el usuario toca uno, obtenemos su dirección y conectamos
                String selectedAddress = deviceAddresses.get(which);
                connectBluetooth(selectedAddress);
            });
            builder.show();
        } else {
            Toast.makeText(this, "No hay dispositivos vinculados. Ve a Ajustes Bluetooth primero.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Verifica y solicita permisos Bluetooth si son necesarios.
     */
    private void checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN
                        },
                        REQUEST_BLUETOOTH_PERMISSIONS);
            }
        }
    }

    /**
     * Conecta al dispositivo seleccionado.
     * AHORA RECIBE LA DIRECCIÓN COMO ARGUMENTO.
     */
    private void connectBluetooth(String address) {
        new Thread(() -> {
            try {
                if (btSocket != null && btSocket.isConnected()) {
                    btSocket.close(); // Cerrar conexión anterior si existe
                }

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                BluetoothDevice device = btAdapter.getRemoteDevice(address);
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                btAdapter.cancelDiscovery();

                try {
                    btSocket.connect();
                    outputStream = btSocket.getOutputStream();

                    // Actualizar la UI para confirmar conexión
                    runOnUiThread(() -> {
                        Toast.makeText(this, "✅ Conectado a: " + device.getName(), Toast.LENGTH_SHORT).show();
                        btnConnect.setText("Conectado");
                        // Opcional: Cambiar color del botón para indicar éxito
                        btnConnect.setBackgroundColor(0xFF4CAF50); // Verde
                    });

                } catch (IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "❌ Error al conectar", Toast.LENGTH_SHORT).show());
                    try { btSocket.close(); } catch (IOException e2) {}
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private View.OnTouchListener controlTouchListener(String command) {
        return (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sendCommand(command);
                    break;
                case MotionEvent.ACTION_UP:
                    sendCommand("S");
                    break;
            }
            return true;
        };
    }

    private void sendCommand(String command) {
        try {
            if (outputStream != null) {
                outputStream.write(command.getBytes());
            }
        } catch (IOException e) {
            // Error silencioso o Toast si prefieres
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (btSocket != null && btSocket.isConnected()) {
                btSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Manejo básico de respuesta de permisos
    }
}