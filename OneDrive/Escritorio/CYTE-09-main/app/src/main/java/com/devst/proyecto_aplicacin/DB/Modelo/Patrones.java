package com.devst.proyecto_aplicacin.DB.Modelo;
public class Patrones {

    private int id;
    private String a;
    private String b;
    private String c;
    private String d;


    // -- Constructor Vacío
    public Patrones() {
    }

    // -- Constructor para CREAR un Patrón (sin ID)
    public Patrones(String a, String b, String c, String d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    // -- Constructor para LEER un Patrón desde la BD (con ID)
    public Patrones(int id, String a, String b, String c, String d) {
        this.id = id;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    // --- Métodos GETTER

    public int getId() {
        return id;
    }

    public String getA() {
        return a;
    }


    public String getB() {
        return b;
    }


    public String getC() {
        return c;
    }


    public String getD() {
        return d;
    }

    // --- Métodos SETTER

    public void setId(int id) {
        this.id = id;
    }

    public void setA(String a) {
        this.a = a;
    }

    public void setB(String b) {
        this.b = b;
    }

    public void setC(String c) {
        this.c = c;
    }

    public void setD(String d){
        this.d = d;
    }
}