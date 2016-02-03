package com.pmdm.plandeevacuacion;

import java.util.ArrayList;
import java.util.List;

/**
 * Define los métodos para trabajar con una posición de una matriz.
 * @author Aitor Etxabarren
 */
public class Posicion {
    /** Posisción vertical */
    private int fila;
    /** Posisción horizontal */
    private int columna;
    /** Hash = fila*HASH_PARAM+columna */
    private static final int HASH_PARAM = 1000;

    /**
     * Define la celda inicial (0,0) en un tablero
     */
    public Posicion() {
        fila = 0;
        columna = 0;
    }

    /**
     * Define una celda en un tablero
     * @param fila
     * @param columna
     */
    public Posicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    /**
     * Fija una celda en un tablero
     * @param fila
     * @param columna
     */
    public void setFilaColumna(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    /**
     * Devuelve la fila asociada
     * @return fila
     */
    public int getFila() {
        return fila;
    }

    /**
     * Fija la fila
     */
    public void setFila(int fila) {
        this.fila = fila;
    }

    /**
     * Devuelve la columna asociada
     * @return fila
     */
    public int getColumna() {
        return columna;
    }

    /**
     * Fija la columna
     */
    public void setColumna(int columna) {
        this.columna = columna;
    }

    /**
     * Obtiene las celdas superior, derecha, inferior e izquierda si están en los límites.
     * @param filas Cuantas filas tiene el lienzo
     * @param columnas Cuantas columnas tiene el lienzo
     * @return lista de vecinos dentro de los límites
     */
    public List<Posicion> getVecinos(int filas, int columnas){
        int f, c;
        Posicion pos;
        List<Posicion> lista = new ArrayList<Posicion>();
        //arriba
        f = fila-1;
        c = columna;
        putPosicionEnLista(f, c, filas, columnas, lista);
        //derecha
        f = fila;
        c = columna+1;
        putPosicionEnLista(f, c, filas, columnas, lista);
        //abajo
        f = fila+1;
        c = columna;
        putPosicionEnLista(f, c, filas, columnas, lista);
        //izquierda
        f = fila;
        c = columna-1;
        putPosicionEnLista(f, c, filas, columnas, lista);
        return lista;
    }

    /**
     * Introduce un objeto Posicion en la lista si es valido
     * @param f fila
     * @param c columna
     * @param filas Cuantas filas tiene el lienzo
     * @param columnas Cuantas columnas tiene el lienzo
     * @param lista Lista de posiciones en la que introducir
     */
    private void putPosicionEnLista(int f, int c, int filas, int columnas, List<Posicion> lista){
        if ((f>=0)&&(f<filas)&&(c>=0)&&(c<columnas)){
            Posicion pos = new Posicion(f,c);
            lista.add(pos);
        }
    }

    /**
     * Si dos posiciones son iguales
     * @param object Posición con la que comparar
     * @return Si son iguales (false si posición nula)
     */
    @Override
    public boolean equals(Object object) {
        if (object==null) return false;
        if ( (fila==((Posicion)object).getFila()) && (columna==((Posicion)object).getColumna()) ){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Código para diferenciar los objetos
     * @return Hash = fila*HASH_PARAM+columna
     */
    @Override
    public int hashCode() {
        return HASH_PARAM*fila+columna;
    }

    /**
     * Devuelve la representación JSON
     * @return ej. {fila:0,columna:0}
     */
    public String toJSON(){
        StringBuilder sb = new StringBuilder();
        sb.append("{fila:");
        sb.append(fila);
        sb.append(",columna:");
        sb.append(columna);
        sb.append("}");
        return sb.toString();
    }
}
