package com.devst.proyecto_aplicacin.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.devst.proyecto_aplicacin.DB.Modelo.Patrones; // <-- IMPORTAR MODELO PATRONES
import com.devst.proyecto_aplicacin.DB.Modelo.Usuario;

import java.util.ArrayList; // <-- IMPORTAR ARRAYLIST

/**
 * Es la capa intermedia entre la aplicación y la base de datos.
 * Se encarga de insertar, buscar, validar y eliminar usuarios Y patrones.
 */
public class DbManager {

    // -- Referencia al objeto DbConexion
    private DbConexion dbConexion;

    // -- Constructor
    public DbManager(Context context) {
        // -- Crea una instancia de DbConexion
        this.dbConexion = new DbConexion(context);
    }

    // ===========================================
    // --- MÉTODOS PARA LA TABLA "USUARIOS" ---
    // ===========================================

    // -- Metodo para insertar un usuario en la base de datos
    public long insertarUsuario(Usuario usuario) {
        long nuevoId = -1;
        SQLiteDatabase db = dbConexion.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(DbConexion.COLUMN_NOMBRE, usuario.getNombre());
            values.put(DbConexion.COLUMN_APELLIDO, usuario.getApellido());
            values.put(DbConexion.COLUMN_TELEFONO, usuario.getTelefono());
            values.put(DbConexion.COLUMN_EMAIL, usuario.getCorreo());
            values.put(DbConexion.COLUMN_PASSWORD, usuario.getPassword());

            nuevoId = db.insert(DbConexion.TABLE_USUARIOS, null, values);
        } catch (Exception e) {
            Log.e("DbManager", "Error al insertar usuario", e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return nuevoId;
    }

    // -- Metodo para validar un usuario en la base de datos
    public boolean validarUsuario(String correo, String contrasena) {
        boolean existe = false;
        SQLiteDatabase db = dbConexion.getReadableDatabase();
        String[] projection = {DbConexion.COLUMN_ID};
        String selection = DbConexion.COLUMN_EMAIL + " = ? AND " +
                DbConexion.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {correo, contrasena};

        Cursor cursor = null;
        try {
            cursor = db.query(
                    DbConexion.TABLE_USUARIOS,
                    projection,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            existe = cursor.getCount() > 0;

        } catch (Exception e) {
            Log.e("DbManager", "Error al validar usuario", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return existe;
    }


    // -- Metodo para obtener un usuario por su email
    public Usuario getUsuarioPorEmail(String email) {
        Usuario usuario = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbConexion.getReadableDatabase();

            String[] projection = {
                    DbConexion.COLUMN_ID, // <-- Faltaba el ID
                    DbConexion.COLUMN_NOMBRE,
                    DbConexion.COLUMN_APELLIDO,
                    DbConexion.COLUMN_TELEFONO,
                    DbConexion.COLUMN_EMAIL,
                    DbConexion.COLUMN_PASSWORD
            };

            String selection = DbConexion.COLUMN_EMAIL + " = ?";
            String[] selectionArgs = {email};

            cursor = db.query(
                    DbConexion.TABLE_USUARIOS,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_NOMBRE));
                String apellido = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_APELLIDO));
                String telefono = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_TELEFONO));
                String correo = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_EMAIL));
                String passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_PASSWORD));

                // Usamos el constructor que incluye el ID
                usuario = new Usuario(id, nombre, apellido, telefono, correo, passwordHash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return usuario;
    }

    // -- Metodo para eliminar un usuario verificando correo y contraseña
    public int eliminarUsuario(String email, String contrasena) {

        int filasEliminadas = 0;
        SQLiteDatabase db = null;

        try {
            db = dbConexion.getWritableDatabase();
            String whereClause = DbConexion.COLUMN_EMAIL + " = ? AND " +
                    DbConexion.COLUMN_PASSWORD + " = ?";
            String[] whereArgs = {email, contrasena};

            filasEliminadas = db.delete(
                    DbConexion.TABLE_USUARIOS,
                    whereClause,
                    whereArgs
            );

        } catch (Exception e) {
            Log.e("DbManager", "Error al eliminar usuario", e);
            filasEliminadas = 0;
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return filasEliminadas;
    }

    public boolean actualizarInformacion(Usuario usuario) {
        SQLiteDatabase db = dbConexion.getWritableDatabase();
        int filasActualizadas = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(DbConexion.COLUMN_NOMBRE, usuario.getNombre());
            values.put(DbConexion.COLUMN_APELLIDO, usuario.getApellido());
            values.put(DbConexion.COLUMN_TELEFONO, usuario.getTelefono());

            String whereClause = DbConexion.COLUMN_EMAIL + " = ?";
            String[] whereArgs = {usuario.getCorreo()};

            filasActualizadas = db.update(
                    DbConexion.TABLE_USUARIOS,
                    values,
                    whereClause,
                    whereArgs
            );
        } catch (Exception e) {
            Log.e("DbManager", "Error al actualizar usuario", e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
        // --- ¡CORREGIDO! ---
        return filasActualizadas > 0; // Devuelve true si se actualizó al menos 1 fila
    }

    public boolean actualizarPassword(String email, String hashedPassword) {
        SQLiteDatabase db = dbConexion.getWritableDatabase();
        int filasActualizadas = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(DbConexion.COLUMN_PASSWORD, hashedPassword);

            String whereClause = DbConexion.COLUMN_EMAIL + " = ?";
            String[] whereArgs = {email};

            filasActualizadas = db.update(
                    DbConexion.TABLE_USUARIOS,
                    values,
                    whereClause,
                    whereArgs
            );
        } catch (Exception e) {
            Log.e("DbManager", "Error al actualizar la contraseña", e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return filasActualizadas > 0;
    }


    // ===========================================
    // --- MÉTODOS NUEVOS PARA "PATRONES" ---
    // ===========================================

    /**
     * Inserta un nuevo patrón en la base de datos.
     * @param patron El objeto Patrones con la info (A, B, C, D).
     * @return El ID de la nueva fila, o -1 si hubo un error.
     */
    public long insertarPatron(Patrones patron) {
        long nuevoId = -1;
        SQLiteDatabase db = dbConexion.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(DbConexion.COLUMN_A, patron.getA());
            values.put(DbConexion.COLUMN_B, patron.getB());
            values.put(DbConexion.COLUMN_C, patron.getC());
            values.put(DbConexion.COLUMN_D, patron.getD());

            nuevoId = db.insert(DbConexion.TABLE_PATRONES, null, values);
        } catch (Exception e) {
            Log.e("DbManager", "Error al insertar patrón", e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return nuevoId;
    }

    /**
     * Obtiene todos los patrones guardados en la base de datos.
     * @return Una lista (ArrayList) de objetos Patrones.
     */
    public ArrayList<Patrones> getAllPatrones() {
        ArrayList<Patrones> listaPatrones = new ArrayList<>();
        SQLiteDatabase db = dbConexion.getReadableDatabase();
        Cursor cursor = null;

        try {
            // null en projection significa "traer todas las columnas (*)"
            cursor = db.query(
                    DbConexion.TABLE_PATRONES,
                    null, null, null, null, null, null
            );

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_ID_PATRONES));
                    String a = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_A));
                    String b = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_B));
                    String c = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_C));
                    String d = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_D));

                    Patrones patron = new Patrones(id, a, b, c, d);
                    listaPatrones.add(patron);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DbManager", "Error al leer todos los patrones", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return listaPatrones;
    }

    /**
     * Elimina un patrón usando su ID.
     * @param id El id del patrón a eliminar (id_patron).
     * @return El número de filas eliminadas (debería ser 1).
     */
    public int eliminarPatron(int id) {
        int filasEliminadas = 0;
        SQLiteDatabase db = dbConexion.getWritableDatabase();

        try {
            String whereClause = DbConexion.COLUMN_ID_PATRONES + " = ?";
            String[] whereArgs = {String.valueOf(id)};

            filasEliminadas = db.delete(
                    DbConexion.TABLE_PATRONES,
                    whereClause,
                    whereArgs
            );
        } catch (Exception e) {
            Log.e("DbManager", "Error al eliminar patrón", e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return filasEliminadas;
    }
}