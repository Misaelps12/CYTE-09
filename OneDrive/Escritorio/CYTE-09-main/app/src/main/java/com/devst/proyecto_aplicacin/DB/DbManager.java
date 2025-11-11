package com.devst.proyecto_aplicacin.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * Es la capa intermedia entre la aplicación y la base de datos.
 * Se encarga de insertar, buscar, validar y eliminar usuarios.
 */
public class DbManager {

    // -- Referencia al objeto DbConexion
    private DbConexion dbConexion;

    // -- Constructor
    public DbManager(Context context) {
        // -- Crea una instancia de DbConexion
        this.dbConexion = new DbConexion(context);
    }

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
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_NOMBRE));
                String apellido = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_APELLIDO));
                String telefono = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_TELEFONO));
                String correo = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_EMAIL));
                String passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_PASSWORD));

                usuario = new Usuario(nombre, apellido, telefono, correo, passwordHash);
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

        // Devuelve el número de filas eliminadas (1 si tuvo éxito, 0 si falló)
        int filasEliminadas = 0;
        SQLiteDatabase db = null;

        try {
            // Abre la base de datos en modo escritura para la operación DELETE
            db = dbConexion.getWritableDatabase();

            // Cláusula WHERE para asegurar que solo se elimine el usuario con las credenciales correctas
            String whereClause = DbConexion.COLUMN_EMAIL + " = ? AND " +
                    DbConexion.COLUMN_PASSWORD + " = ?";

            // Los argumentos de la cláusula WHERE
            String[] whereArgs = {email, contrasena};

            // Ejecuta la eliminación y obtiene el número de filas afectadas
            filasEliminadas = db.delete(
                    DbConexion.TABLE_USUARIOS,
                    whereClause,
                    whereArgs
            );

        } catch (Exception e) {
            Log.e("DbManager", "Error al eliminar usuario", e);
            filasEliminadas = 0; // Se asegura que el retorno sea 0 en caso de error
        } finally {
            // Cierra la base de datos
            if (db != null) {
                db.close();
            }
        }
        return filasEliminadas;
    }

    public boolean actualizarInformacion (Usuario usuario) {

        // -- Abrimos la base de datos en modo escritura
        SQLiteDatabase db = dbConexion.getWritableDatabase();

        int filasActualizadas = 0;

        try{
            // -- Preparamos los valores a actualizar
            ContentValues values = new ContentValues();
            values.put(DbConexion.COLUMN_NOMBRE, usuario.getNombre());
            values.put(DbConexion.COLUMN_APELLIDO, usuario.getApellido());
            values.put(DbConexion.COLUMN_TELEFONO, usuario.getTelefono());

            // -- Definimos la clausula where

            String whereClause = DbConexion.COLUMN_EMAIL + " = ?";
            String[] whereArgs = {usuario.getCorreo()};

            // -- Ejecutamos el update.
            filasActualizadas = db.update(
                    DbConexion.TABLE_USUARIOS, // La tabla
                    values,                    // Los nuevos valores
                    whereClause,               // La cláusula WHERE
                    whereArgs                  // Los argumentos para el WHERE
            );
        } catch (Exception e) {
            Log.e("DbManager", "Error al actualizar usuario", e);
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }
    // En DbManager.java
    public boolean actualizarPassword(String email, String hashedPassword) {
        SQLiteDatabase db = dbConexion.getWritableDatabase();
        int filasActualizadas = 0;

        try {
            ContentValues values = new ContentValues();
            // Solo actualizamos la columna de la contraseña con el nuevo hash
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
}