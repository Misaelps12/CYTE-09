package com.devst.proyecto_aplicacin.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbConexion extends SQLiteOpenHelper {

    // --- Configuración de la Base de Datos ---
    private static final String DATABASE_NAME = "mi_base_de_datos.db";
    private static final int DATABASE_VERSION = 1; // Versión 1

    // --- Constantes de la Tabla "usuarios" ---
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "Nombre";
    public static final String COLUMN_APELLIDO = "Apellido";
    public static final String COLUMN_TELEFONO = "Telefono";
    public static final String COLUMN_EMAIL = "Correo";
    public static final String COLUMN_PASSWORD = "Password";

    // --- Constantes para la tabla "patrones" ---
    public static final String TABLE_PATRONES = "patrones";
    public static final String COLUMN_ID_PATRONES = "id_patron"; // Cambiado para evitar confusión con COLUMN_ID
    public static final String COLUMN_A = "A";
    public static final String COLUMN_B = "B";
    public static final String COLUMN_C = "C";
    public static final String COLUMN_D = "D";

    public DbConexion(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // -- Metodo para que se ejecuta solo una vez para crear la base de datos --
    // AHORA ESTÁN AMBAS TABLAS JUNTAS
    @Override
    public void onCreate(SQLiteDatabase db) {

        // 1. Creamos la tabla de usuarios
        String sqlUsuarios = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NOMBRE + " TEXT NOT NULL," +
                COLUMN_APELLIDO + " TEXT NOT NULL," +
                COLUMN_TELEFONO + " TEXT NOT NULL," +
                COLUMN_EMAIL + " TEXT NOT NULL UNIQUE," +
                COLUMN_PASSWORD + " TEXT NOT NULL)";

        db.execSQL(sqlUsuarios); // Ejecuta la primera creación

        // 2. Creamos la tabla Patrones
        String sqlPatrones = "CREATE TABLE " + TABLE_PATRONES + " (" +
                COLUMN_ID_PATRONES + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_A + " TEXT NOT NULL," +
                COLUMN_B + " TEXT NOT NULL," +
                COLUMN_C + " TEXT NOT NULL," +
                COLUMN_D + " TEXT NOT NULL)";

        db.execSQL(sqlPatrones); // Ejecuta la segunda creación
    }

    // -- solo se usa cuando se actualiza la base de datos --
    // AHORA BORRA AMBAS TABLAS
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Esto elimina todos los datos y tablas
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATRONES);

        // Vuelve a crear todo desde cero
        onCreate(db);
    }
}