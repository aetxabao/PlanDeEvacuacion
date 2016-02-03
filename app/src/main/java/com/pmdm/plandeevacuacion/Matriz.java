package com.pmdm.plandeevacuacion;

/**
 * Define los métodos para trabajar con una matriz de valores enteros.
 * @author Aitor Etxabarren
 */
public class Matriz {
    /** Tamaño vertical */
    private int filas;
    /** Tamaño horizontal */
    private int columnas;
    /** Cuidado: El primer índice es para las filas, el segundo para las columnas. */
    private int[][] matriz;

    /**
     * Define una matriz
     * @param filas tamaño vertical
     * @param columnas tamaño horizontal
     */
    public Matriz(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        matriz = new int[filas][columnas];
    }

    /**
     * Pone el valor indicado en la posición definida.
     * @param fila [0,filas-1]
     * @param columna [0,columnas-1]
     * @param valor Cte
     */
    public void putValor(int fila, int columna, int valor){
        matriz[fila][columna] = valor;
    }

    /**
     * Devuelve el valor indicado en la posición definida.
     * @param fila [0,filas-1]
     * @param columna [0,columnas-1]
     * @return Cte
     */
    public int getValor(int fila, int columna){
        return matriz[fila][columna];
    }

    /**
     * Pone el valor indicado en la posición definida.
     * @param posicion Debe estar dentro de límites ([0,filas-1],[0,columnas-1])
     * @param valor Cte
     */
    public void putValor(Posicion posicion, int valor){
        matriz[posicion.getFila()][posicion.getColumna()] = valor;
    }

    /**
     * Devuelve el valor indicado en la posición definida.
     * @param posicion Debe estar dentro de límites ([0,filas-1],[0,columnas-1])
     * @return Cte
     */
    public int getValor(Posicion posicion){
        return matriz[posicion.getFila()][posicion.getColumna()];
    }

    /**
     * Pone el valor indicado en toda la matriz.
     * @param valor Cte
     */
    public void putValor(int valor){
        for(int fila=0;fila<matriz.length;fila++) {
            for (int columna = 0; columna < matriz[0].length; columna++) {
                matriz[fila][columna] = valor;
            }
        }
    }

    /**
     * Tamaño vertical
     * @return Número de filas
     */
    public int getFilas() {
        return filas;
    }

    /**
     * Tamaño horizontal
     * @return Número de columnas
     */
    public int getColumnas() {
        return columnas;
    }
}
