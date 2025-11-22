package com.cyte_09.ctye_09.data.db;

import android.util.Log;

import com.cyte_09.ctye_09.data.modelo.Usuario;
import com.cyte_09.ctye_09.data.modelo.Patrones;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DbManagerFireBase {

    private final FirebaseFirestore db;

    public DbManagerFireBase() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void insertarUsuario(Usuario usuario) {

        Map<String, Object> data = new HashMap<>();
        data.put("Nombre", usuario.getNombre());
        data.put("Apellido", usuario.getApellido());
        data.put("Telefono", usuario.getTelefono());
        data.put("Correo", usuario.getCorreo());
        data.put("Password", usuario.getPassword());

        db.collection("usuarios")
                .document(usuario.getCorreo())
                .set(data)
                .addOnSuccessListener(a -> Log.d("FireDB", "Usuario insertado."))
                .addOnFailureListener(e -> Log.e("FireDB", "Error insertando usuario", e));
    }

    public void validarUsuario(String correo, String password, FireCallback<Boolean> callback) {

        db.collection("usuarios")
                .document(correo)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onResult(false);
                        return;
                    }
                    String storedPass = doc.getString("Password");
                    callback.onResult(storedPass != null && storedPass.equals(password));
                })
                .addOnFailureListener(e -> {
                    Log.e("FireDB", "Error validando usuario", e);
                    callback.onResult(false);
                });
    }

    public void getUsuarioPorEmail(String correo, FireCallback<Usuario> callback) {

        db.collection("usuarios")
                .document(correo)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onResult(null);
                        return;
                    }
                    Usuario u = new Usuario(
                            0,
                            doc.getString("Nombre"),
                            doc.getString("Apellido"),
                            doc.getString("Telefono"),
                            doc.getString("Correo"),
                            doc.getString("Password")
                    );
                    callback.onResult(u);
                })
                .addOnFailureListener(e -> {
                    Log.e("FireDB", "Error obteniendo usuario", e);
                    callback.onResult(null);
                });
    }

    public void eliminarUsuario(String correo, String password, FireCallback<Boolean> callback) {

        validarUsuario(correo, password, valido -> {
            if (!valido) {
                callback.onResult(false);
                return;
            }

            db.collection("usuarios")
                    .document(correo)
                    .delete()
                    .addOnSuccessListener(a -> callback.onResult(true))
                    .addOnFailureListener(e -> {
                        Log.e("FireDB", "Error eliminando usuario", e);
                        callback.onResult(false);
                    });
        });
    }

    public void actualizarInformacion(Usuario usuario, FireCallback<Boolean> callback) {

        Map<String, Object> data = new HashMap<>();
        data.put("Nombre", usuario.getNombre());
        data.put("Apellido", usuario.getApellido());
        data.put("Telefono", usuario.getTelefono());

        db.collection("usuarios")
                .document(usuario.getCorreo())
                .update(data)
                .addOnSuccessListener(a -> callback.onResult(true))
                .addOnFailureListener(e -> {
                    Log.e("FireDB", "Error actualizando usuario", e);
                    callback.onResult(false);
                });
    }

    public void actualizarPassword(String correo, String nuevaPass, FireCallback<Boolean> callback) {

        Map<String, Object> data = new HashMap<>();
        data.put("Password", nuevaPass);

        db.collection("usuarios")
                .document(correo)
                .update(data)
                .addOnSuccessListener(a -> callback.onResult(true))
                .addOnFailureListener(e -> {
                    Log.e("FireDB", "Error actualizando password", e);
                    callback.onResult(false);
                });
    }

    public void insertarPatron(Patrones patron) {

        Map<String, Object> data = new HashMap<>();
        data.put("A", patron.getA());
        data.put("B", patron.getB());
        data.put("C", patron.getC());
        data.put("D", patron.getD());

        db.collection("patrones")
                .add(data)
                .addOnSuccessListener(doc -> Log.d("FireDB", "Patrón agregado ID " + doc.getId()))
                .addOnFailureListener(e -> Log.e("FireDB", "Error agregando patrón", e));
    }

    public void getAllPatrones(FireCallback<ArrayList<Patrones>> callback) {

        db.collection("patrones")
                .get()
                .addOnSuccessListener(query -> {

                    ArrayList<Patrones> lista = new ArrayList<>();

                    for (DocumentSnapshot doc : query) {

                        Patrones p = new Patrones(
                                0,
                                doc.getString("A"),
                                doc.getString("B"),
                                doc.getString("C"),
                                doc.getString("D")
                        );

                        lista.add(p);
                    }

                    callback.onResult(lista);
                })
                .addOnFailureListener(e -> {
                    Log.e("FireDB", "Error obteniendo patrones", e);
                    callback.onResult(null);
                });
    }

    public void eliminarPatron(String idPatron, FireCallback<Boolean> callback) {

        db.collection("patrones")
                .document(idPatron)
                .delete()
                .addOnSuccessListener(a -> callback.onResult(true))
                .addOnFailureListener(e -> {
                    Log.e("FireDB", "Error eliminando patrón", e);
                    callback.onResult(false);
                });
    }

    public interface FireCallback<T> {
        void onResult(T result);
    }
}
