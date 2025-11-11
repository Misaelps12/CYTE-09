package com.devst.proyecto_aplicacin;

import android.Manifest;
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
import java.util.UUID;

public class ControlActivity extends AppCompatActivity {

    Button btnUp, btnDown, btnLeft, btnRight;
    BluetoothSocket btSocket;
    OutputStream outputStream;
    BluetoothAdapter btAdapter;

    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Cambia esto por la dirección MAC de tu módulo HC-05
    private static final String DEVICE_ADDRESS = "00:21:13:01:23:45";

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

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBluetoothPermissions();

        // Conectar automáticamente
        connectBluetooth();

        // Listeners
        btnUp.setOnTouchListener(controlTouchListener("F"));
        btnDown.setOnTouchListener(controlTouchListener("B"));
        btnLeft.setOnTouchListener(controlTouchListener("L"));
        btnRight.setOnTouchListener(controlTouchListener("R"));
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
     * Conecta con el módulo Bluetooth HC-05 de forma segura.
     */
    private void connectBluetooth() {
        new Thread(() -> {
            try {
                if (btAdapter == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Bluetooth no soportado", Toast.LENGTH_SHORT).show());
                    return;
                }

                if (!btAdapter.isEnabled()) {
                    runOnUiThread(() -> Toast.makeText(this, "Activa el Bluetooth", Toast.LENGTH_SHORT).show());
                    return;
                }

                // ✅ Verificación de permisos ANTES de acceder al dispositivo
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Permiso Bluetooth denegado", Toast.LENGTH_SHORT).show());
                    return;
                }

                BluetoothDevice device = btAdapter.getRemoteDevice(DEVICE_ADDRESS);
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                btAdapter.cancelDiscovery();

                try {
                    btSocket.connect();
                    outputStream = btSocket.getOutputStream();
                    runOnUiThread(() -> Toast.makeText(this, "✅ Conectado a HC-05", Toast.LENGTH_SHORT).show());
                } catch (SecurityException e) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "⚠️ Permiso Bluetooth requerido", Toast.LENGTH_SHORT).show());
                } catch (IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "❌ Error al conectar Bluetooth", Toast.LENGTH_SHORT).show());
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error inesperado en Bluetooth", Toast.LENGTH_SHORT).show());
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
        } catch (SecurityException e) {
            runOnUiThread(() ->
                    Toast.makeText(this, "Permiso Bluetooth requerido", Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            runOnUiThread(() ->
                    Toast.makeText(this, "Error enviando comando", Toast.LENGTH_SHORT).show());
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
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos Bluetooth concedidos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permisos Bluetooth denegados", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
