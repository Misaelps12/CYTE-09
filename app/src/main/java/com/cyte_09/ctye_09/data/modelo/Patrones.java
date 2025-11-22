package com.cyte_09.ctye_09.data.modelo;

public class Patrones {

    private int id;
    private String a;
    private String b;
    private String c;
    private String d;

    public Patrones() {
    }

    public Patrones(String a, String b, String c, String d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public Patrones(int id, String a, String b, String c, String d) {
        this.id = id;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

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

    public void setD(String d) {
        this.d = d;
    }
}
