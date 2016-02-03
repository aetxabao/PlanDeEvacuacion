package com.pmdm.plandeevacuacion;

/**
 * Define las características de la fase de juego.
 * @author Aitor Etxabarren (GPL v3)
 */
public class Fase {
    private int filas;
    private int columnas;
    private int fuegos;
    private int tiempo;
    private int fondo;

    public Fase(int filas, int columnas, int fuegos, int tiempo, int fondo) {
        this.filas = filas;
        this.columnas = columnas;
        this.fuegos = fuegos;
        this.tiempo = tiempo;
        this.fondo = fondo;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public int getFuegos() {
        return fuegos;
    }

    public int getTiempo() { return tiempo; }

    public int getFondo() {
        return fondo;
    }

}
