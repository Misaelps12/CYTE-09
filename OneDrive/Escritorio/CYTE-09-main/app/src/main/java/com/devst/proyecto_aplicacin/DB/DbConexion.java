package com.devst.proyecto_aplicacin.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbConexion extends SQLiteOpenHelper {

    // --- Configuración de la Base de Datos ---

    // -- le ponemos el nombre a la base de datos --
    private static final String DATABASE_NAME = "mi_base_de_datos.db";

    // -- La versión de la base de datos -- (cada vez que se haga un modificación se le cambia el numero)
    private static final int DATABASE_VERSION = 1;


    // --- Constantes de la Tabla "usuarios" ---

    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "Nombre";
    public static final String COLUMN_APELLIDO = "Apellido";
    public static final String COLUMN_TELEFONO = "Telefono";
    public static final String COLUMN_EMAIL = "Correo";
    public static final String COLUMN_PASSWORD = "Password";

    // Constructor

    public DbConexion(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


// -- Metodo para que se ejecuta solo una vez para crear la base de datos --
    @Override
    public void onCreate(SQLiteDatabase db) {
        // -- Creamos la tabla de usuarios --
        String sql = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NOMBRE + " TEXT NOT NULL," +
                COLUMN_APELLIDO + " TEXT NOT NULL," +
                COLUMN_TELEFONO + " TEXT NOT NULL," +
                COLUMN_EMAIL + " TEXT NOT NULL UNIQUE," +
                COLUMN_PASSWORD + " TEXT NOT NULL)";

        db.execSQL(sql);
    }


 // -- solo se usa cuando se actualiza la base de datos --

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Esto elimina todos los datos
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }
}

