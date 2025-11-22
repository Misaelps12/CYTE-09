package com.cyte_09.ctye_09.data.db;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ConexionFireBase {

    private final FirebaseFirestore db;

    public ConexionFireBase() {
        db = FirebaseFirestore.getInstance();
        probarConexion();
    }

    private void probarConexion() {
        Map<String, Object> testData = new HashMap<>();
        testData.put("status", "conectado");

        db.collection("pruebas_conexion")
                .document("test1")
                .set(testData)
                .addOnSuccessListener(aVoid ->
                        Log.d("FirebaseTest", "¡Dato escrito con éxito! La conexión funciona."))
                .addOnFailureListener(e ->
                        Log.e("FirebaseTest", "Error al escribir el dato.", e));
    }

    public void insertarUsuario(String nombre, String apellido, String telefono,
                                String correo, String password) {

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("Nombre", nombre);
        usuario.put("Apellido", apellido);
        usuario.put("Telefono", telefono);
        usuario.put("Correo", correo);
        usuario.put("Password", password);

        db.collection("usuarios")
                .add(usuario)
                .addOnSuccessListener(doc ->
                        Log.d("Firebase", "Usuario agregado ID: " + doc.getId()))
                .addOnFailureListener(e ->
                        Log.e("Firebase", "Error al agregar usuario", e));
    }

    public void obtenerUsuarios() {
        db.collection("usuarios")
                .get()
                .addOnSuccessListener(query -> {
                    for (var doc : query) {
                        Log.d("Firebase", doc.getId() + " => " + doc.getData());
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("Firebase", "Error al leer usuarios", e));
    }

    public void insertarPatron(String A, String B, String C, String D) {

        Map<String, Object> patron = new HashMap<>();
        patron.put("A", A);
        patron.put("B", B);
        patron.put("C", C);
        patron.put("D", D);

        db.collection("patrones")
                .add(patron)
                .addOnSuccessListener(doc ->
                        Log.d("Firebase", "Patrón agregado ID: " + doc.getId()))
                .addOnFailureListener(e ->
                        Log.e("Firebase", "Error al agregar patrón", e));
    }

    public void obtenerPatrones() {
        db.collection("patrones")
                .get()
                .addOnSuccessListener(query -> {
                    for (var doc : query) {
                        Log.d("Firebase", doc.getId() + " => " + doc.getData());
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("Firebase", "Error al leer patrones", e));
    }
}
