package com.cyte_09.ctye_09.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cyte_09.ctye_09.data.modelo.Patrones;
import com.cyte_09.ctye_09.data.modelo.Usuario;

import java.util.ArrayList;

public class DbManager {

    private DbConexion dbConexion;

    public DbManager(Context context) {
        this.dbConexion = new DbConexion(context);
    }

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
            if (db != null) db.close();
        }
        return nuevoId;
    }

    public boolean validarUsuario(String correo, String contrasena) {
        boolean existe = false;
        SQLiteDatabase db = dbConexion.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] projection = {DbConexion.COLUMN_ID};
            String selection = DbConexion.COLUMN_EMAIL + " = ? AND " +
                    DbConexion.COLUMN_PASSWORD + " = ?";
            String[] selectionArgs = {correo, contrasena};

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
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return existe;
    }

    public Usuario getUsuarioPorEmail(String email) {
        Usuario usuario = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbConexion.getReadableDatabase();

            String[] projection = {
                    DbConexion.COLUMN_ID,
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
                    null, null, null
            );

            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_NOMBRE));
                String apellido = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_APELLIDO));
                String telefono = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_TELEFONO));
                String correo = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_EMAIL));
                String passwordHash = cursor.getString(cursor.getColumnIndexOrThrow(DbConexion.COLUMN_PASSWORD));

                usuario = new Usuario(id, nombre, apellido, telefono, correo, passwordHash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return usuario;
    }

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
        } finally {
            if (db != null) db.close();
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
            if (db != null) db.close();
        }

        return filasActualizadas > 0;
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
            Log.e("DbManager", "Error al actualizar contraseña", e);
        } finally {
            if (db != null) db.close();
        }

        return filasActualizadas > 0;
    }

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
            if (db != null) db.close();
        }

        return nuevoId;
    }

    public ArrayList<Patrones> getAllPatrones() {
        ArrayList<Patrones> listaPatrones = new ArrayList<>();
        SQLiteDatabase db = dbConexion.getReadableDatabase();
        Cursor cursor = null;

        try {
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

                    listaPatrones.add(new Patrones(id, a, b, c, d));

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("DbManager", "Error al obtener patrones", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return listaPatrones;
    }

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
            if (db != null) db.close();
        }

        return filasEliminadas;
    }
}
