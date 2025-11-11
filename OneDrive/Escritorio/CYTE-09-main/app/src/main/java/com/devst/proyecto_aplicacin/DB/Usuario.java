package com.devst.proyecto_aplicacin.DB;

public class Usuario {

    private int id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private String password;

    // Constructor Vacío
    public Usuario() {
    }

   //Constructor
    public Usuario(String nombre, String apellido, String telefono, String correo, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        this.password = password;
    }

    // Constructor para LEER un usuario desde la BD (con ID)
    public Usuario(int id, String nombre, String apellido, String telefono, String correo, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        this.password = password;
    }

    // --- Métodos GETTER

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }
    public String getPassword() {
        return password;
    }

    // --- Métodos SETTER

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
