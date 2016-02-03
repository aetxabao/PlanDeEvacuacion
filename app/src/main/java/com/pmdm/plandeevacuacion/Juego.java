package com.pmdm.plandeevacuacion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 * Define el modelo de juego. Utiliza una matriz para indicar si las celdas del tablero tienen fuego,
 * o cuantas celdas vecinas (arriba,izquierda,abajo,derecha) no tienen fuego. Las posiciones importantes
 * se definen mediante objetos para tal propósito y las posiciones visitadas en un conjunto.
 * @author Aitor Etxabarren (GPL v3)
 */
public class Juego {

    /** Puntos por descubrir una celda que no tiene fuego */
    public static final int PTOS_CELDA = 10;
    /** Puntos por descubrir un premio */
    public static final int PTOS_PREMIO = 50;
    /** Puntos por superar una nivel */
    public static final int PTOS_FASE = 100;

    /** Cte que define un fuego en una celda */
    public static final int FUEGO = 9;
    /** Cte que define una celda vacia */
    public static final int VACIO = 0;
    /** Cte que define un estado nulo para una celda */
    public static final int NULO = -1;
    /** Para evitar bucles infinitos */
    public static final int FACTOR_INTENTOS = 4;
    /** Tamaño vertical del tablero */
    private int filas;
    /** Tamaño horizontal del tablero */
    private int columnas;
    /** Número de celdas con fuego */
    private int fuegos;
    /** Matriz con los valores de juego */
    private Matriz mapa;
    /** Conjunto con las celdas visitadas */
    private Set<Posicion> visitadas;

    /**Dónde empieza*/
    private Posicion posicionInicial = new Posicion();
    /**Dónde termina*/
    private Posicion posicionFinal = new Posicion();
    /**Dónde hay una vida extra*/
    private Posicion posicionVida = new Posicion();
    /**Dónde hay un premio*/
    private Posicion posicionPremio = new Posicion();
    /**Última posición desvelada*/
    private Posicion posicionUltima = new Posicion();

    /** Fuegos que te puedes encontrar sin perder */
    private int vidas = 3;
    /** Marcador */
    private int puntos = 0;
    /** Número asociado a la fase en juego */
    private int nivel = 1;
    /** Fase en curso */
    private Fase fase;
    /** Array con las características de cada fase */
    private Fase[] fases = {new Fase(4,4,2,30,R.drawable.puntoencuentro),new Fase(4,4,3,30,R.drawable.puntoencuentro),new Fase(4,4,4,30,R.drawable.puntoencuentro),
            new Fase(4,6,5,60,R.drawable.pasillobiblio),new Fase(4,6,6,60,R.drawable.pasillobiblio),new Fase(4,6,7,60,R.drawable.pasillobiblio),
            new Fase(6,8,10,90,R.drawable.escalerascafe),new Fase(6,8,12,90,R.drawable.escalerascafe),new Fase(6,8,14,90,R.drawable.escalerascafe),
            new Fase(8,8,20,120,R.drawable.puertaemergencia),new Fase(8,8,24,120,R.drawable.puertaemergencia),new Fase(8,8,28,120,R.drawable.puertaemergencia)};

    private String[][] mensajes = { {"No rezagarse","a recoger","objetos personales"},
                                    {"Salir","ordenadamente y","sin correr"},
                                    {"No hablar","durante la","evacuación"},
                                    {"Mantener el orden,","no alarmarse","ni provocar","situaciones de pánico"},
                                    {"Permanecer en el","punto de encuentro","hasta ser informados"},
                                    {"Tras ser informados","ir al punto de","encuentro exterior"} };

    private int ini = new Random().nextInt(mensajes.length);

    public String[] getLineasMensaje(){
        return mensajes[(ini+nivel)%mensajes.length];
    }

    /** Si se ha encontrado un camino */
    private boolean finFase = false;
    /** Si se han agotado todas las vidas o ha vencido el timeout y el tiempo es 0 */
    private boolean finPartida = false;

    /** Tiempo pendiente para la fase */
    private int tiempo;


    /**
     * Define el tablero de juego según la fase
     */
    public Juego() {
        fase = fases[(nivel-1)%fases.length];
        reiniciarJuego(fase);
    }

    public void reiniciarJuego(Fase fase){
        this.filas = fase.getFilas();
        this.columnas = fase.getColumnas();
        this.tiempo = fase.getTiempo();
        mapa = new Matriz(filas,columnas);
        mapa.putValor(VACIO);
        ponerInicioFin();
        visitadas = new HashSet<>();
        visitadas.add(posicionInicial);
        visitadas.add(posicionFinal);
        this.fuegos = ponerFuegos(fase.getFuegos());
        ponerVidaPremio();
        calcularVecinosSonFuego();
    }

    /**
     * Tamaño horizontal del tablero
     * @return columnas del tablero
     */
    public int getColumnas() {
        return columnas;
    }

    /**
     * Tamaño vertical del tablero
     * @return filas del tablero
     */
    public int getFilas() {
        return filas;
    }

    /**
     * Celdas con fuego
     * @return número de celdas que son fuego
     */
    public int getFuegos() {
        return fuegos;
    }

    /**
     * Fija el número de celdas con fuego
     */
    public void setFuegos(int fuegos) {
        this.fuegos = fuegos;
    }

    /**
     * Cuantos tiempo quedan para terminar la fase
     * @return tiempo pendientes
     */
    public int getTiempo() {
        return tiempo;
    }

    /**
     * Cuantos tiempo se define para cumplir la fase
     * @return segundos correspondientes a la fase
     */
    public int getTiempoFase() {
        return fase.getTiempo();
    }

    /**
     * Disminuye el tiempo un segundo
     */
    public void restarTiempo() {
        tiempo--;
    }

    /**
     * Devuelve el número de vidas
     * @return número de vidas
     */
    public int getVidas() {
        return vidas;
    }

    /**
     * Fija el número de vidas
     */
    public void setVidas(int vidas) {
        this.vidas = vidas;
    }

    /**
     * Obtiene la puntuación
     * @return puntos
     */
    public int getPuntos() {
        return puntos;
    }

    /**
     * Fija la puntuación
     */
    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    /**
     * Incrementa la puntuación
     */
    public void incrementaPuntos(int puntos) {
        this.puntos += puntos;
    }

    /**
     * Devuelve la nivel de juego
     * @return nivel de juego
     */
    public int getNivel() {
        return nivel;
    }

    /**
     * Fija la nivel de juego
     */
    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    /**
     * Incrementa el nivel, pone el estado de fin de nivel a false, suma puntos y reinicia juego
     */
    public void avanzaFase() {
        this.nivel++;
        setFinFase(false);
        incrementaPuntos(PTOS_FASE);
        fase = fases[(nivel-1)%fases.length];
        reiniciarJuego(fase);
    }

    /** Devuelve la fase en curso */
    public Fase getFase(){
        return fase;
    }

    /**
     * Si se ha encontrado el camino
     * @return si la nivel está superada
     */
    public boolean isFinFase() {
        return finFase;
    }

    /**
     * Define si la nivel está superada o no
     * @param finFase cierto/false
     */
    public void setFinFase(boolean finFase) {
        this.finFase = finFase;
    }

    /**
     * Si se han agotado las vidas o el tiempo
     * @return si se ha terminado la partida
     */
    public boolean isFinPartida() {
        return finPartida;
    }

    /**
     * Fija la situación de fin de partida al acabarse las vidas o el tiempo
     * @param finPartida cierto/false
     */
    public void setFinPartida(boolean finPartida) {
        this.finPartida = finPartida;
    }

    /**
     * Define hasta n celdas aleatorias que tienen fuego.
     * No se puede poner fuego donde ya hay fuego, ni en el inicio, ni en el final.
     * Se tiene que garantizar que haya una solución.
     * Se intentado satisfacer limitado por FACTOR_INTENTOS por los fuego veces.
     * @param fuegos Número de celdas en las que intentar poner fuego
     * @return número de celdas en las que se ha puesto fuego
     */
    public int ponerFuegos(int fuegos){
        Posicion posicion = new Posicion();
        int fila, columna, valor;
        int intentos = fuegos*FACTOR_INTENTOS;
        int contador = 0;
        Random random;
        if (fuegos>=filas*columnas){
            mapa.putValor(FUEGO);
            return filas*columnas;
        }
        random = new Random();
        while((fuegos>0)&&(intentos>0)){
            intentos--;
            fila = random.nextInt(filas);
            columna = random.nextInt(columnas);
            posicion.setFilaColumna(fila,columna);
            valor = mapa.getValor(posicion);
            if ( (valor!=FUEGO) && (!posicion.equals(posicionInicial)) && (!posicion.equals(posicionFinal)) ){
                mapa.putValor(posicion,FUEGO);
                if(hayCaminoPosible()) {
                    fuegos--;
                    contador++;
                }else{
                    mapa.putValor(posicion,VACIO);
                }
            }
        }
        return contador;
    }

    /**
     * Define las celdas aleatorias que tienen una vida extra y premio.
     * No se puede poner fuego donde ya hay fuego, ni en el inicio, ni en el final.
     */
    public void ponerVidaPremio(){
        posicionVida = getPosicionLibre();
        posicionPremio = getPosicionLibre();
    }

    /**
     * Obtiene una posición libre que no es fuego, ni inicio, ni fin
     * @return null o posición libre
     */
    private Posicion getPosicionLibre(){
        Posicion posicion = new Posicion();
        int fila, columna, valor;
        int intentos = FACTOR_INTENTOS;
        Random random = new Random();
        while(intentos>0){
            intentos--;
            fila = random.nextInt(filas);
            columna = random.nextInt(columnas);
            posicion.setFilaColumna(fila,columna);
            valor = mapa.getValor(posicion);
            if ( (valor!=FUEGO) && (!posicion.equals(posicionInicial)) && (!posicion.equals(posicionFinal)) ){
                return posicion;
            }
        }
        return null;
    }

    /**
     * Ubica la posición de inicio y fin de forma aleatoria,
     * pero en cuadrantes opuestos.
     */
    public void ponerInicioFin(){
        int cuadranteInicio, cuadranteFin;//opuestos
        cuadranteInicio = getCuadranteAleatorio();
        cuadranteFin = getCuadranteOpuesto(cuadranteInicio);
        posicionInicial = getPosicionAleatoria(cuadranteInicio);
        posicionFinal = getPosicionAleatoria(cuadranteFin);
    }

    /**
     * Obtiene un cuadrante aleatorio.
     * @return un identificador del cuadrante [0,1,2,3]
     * 0:arriba-izquierda, 1:arriba-derecha, 2:abajo-izquierda, 3: abajo-derecha
     */
    protected int getCuadranteAleatorio(){
        Random random = new Random();
        return random.nextInt(4);
    }

    /**
     * Obtiene el cuadrante opuesto al indicado
     * @param cuadrante 0:arriba-izquierda, 1:arriba-derecha, 2:abajo-izquierda, 3: abajo-derecha
     * @return un identificador del cuadrante opuesto al indicado [0,1,2,3]
     */
    protected int getCuadranteOpuesto(int cuadrante){
        switch (cuadrante){
            case 0:
                return 3;
            case 1:
                return 2;
            case 2:
                return 1;
            default:
                return 0;
        }
    }

    /**
     * Obtiene una posición aleatoria dentro del cuadrante indicado.
     * @param cuadrante 0:arriba-izquierda, 1:arriba-derecha, 2:abajo-izquierda, 3: abajo-derecha
     * @return posición valida dentro del cuadrante
     */
    protected Posicion getPosicionAleatoria(int cuadrante){
        int fila=0, columna=0;
        Posicion posicion = null;
        Random random = new Random();
        switch (cuadrante){
            case 0://izquierda-arriba
                fila = random.nextInt((int) Math.floor(filas / 2));
                columna = random.nextInt((int) Math.floor(columnas / 2));
                break;
            case 1://derecha-arriba
                fila = random.nextInt((int) Math.floor(filas / 2));
                columna =  (int) Math.ceil(columnas/2) + random.nextInt((int) Math.floor(columnas / 2));
                break;
            case 2://izquierda-abajo
                fila = (int) Math.ceil(filas/2) + random.nextInt((int) Math.floor(filas / 2));
                columna = random.nextInt((int) Math.floor(columnas / 2));
                break;
            case 3://derecha-abajo
                fila = (int) Math.ceil(filas/2) + random.nextInt((int) Math.floor(filas / 2));
                columna =  (int) Math.ceil(columnas/2) + random.nextInt((int) Math.floor(columnas / 2));
                break;
        }
        return posicion = new Posicion(fila, columna);
    }

    /**
     * Pone en cada celda del mapa que no sea fuego cuantas celdas vecinas tienen fuego.
     * Las celdas vecinas son la de arriba, izquierda, abajo y derecha.
     */
    public void calcularVecinosSonFuego() {
        int valor, n;
        Posicion posicion = new Posicion();
        for(int i=0;i<filas;i++) {
            for (int j = 0; j < columnas; j++) {
                n = 0;
                posicion.setFilaColumna(i,j);
                valor = mapa.getValor(posicion);
                if (valor!= FUEGO){
                    for(Posicion aux : posicion.getVecinos(filas,columnas)){
                        if (mapa.getValor(aux)==FUEGO){
                            n++;
                        }
                    }
                    mapa.putValor(posicion,n);
                }
            }
        }
    }

    /**
     * Devuelve valor para una celda de la matriz si se ha visto
     * @param posicion de la matriz
     * @return FUEGO (9) o numero de vecinos (0-8) si se ha visto, o NULO (-1) si no se ha visto
     */
    public int getValorMapaSiVisto(Posicion posicion){
        if (visitadas.contains(posicion)){
            return mapa.getValor(posicion);
        }else {
            return NULO;
        }
    }

    /**
     * Devuelve valor para una celda de la matriz
     * @param posicion de la matriz
     * @return FUEGO (9) o numero de vecinos (0-8) si se ha visto, o NULO (-1) si no se ha visto
     */
    public int getValorMapa(Posicion posicion){
        return mapa.getValor(posicion);
    }

    /**
     * Obtiene la posición de inicio del juego
     * @return posición inicial
     */
    public Posicion getPosicionInicial(){
        return posicionInicial;
    }

    /**
     * Obtiene la posición de final del juego
     * @return posición final
     */
    public Posicion getPosicionFinal(){
        return posicionFinal;
    }

    /**
     * Obtiene la posición que tiene una vida extra
     * @return posición con vida extra
     */
    public Posicion getPosicionVida() {
        return posicionVida;
    }

    /**
     * Obtiene la posición que tiene premio
     * @return posición con premio
     */
    public Posicion getPosicionPremio() {
        return posicionPremio;
    }

    /**
     * Las celdas adyacentes verticales y horizontales de las celdas visitadas que no son fuego.
     * @return Conjunto de las celdas visitables
     */
    public Set<Posicion> getVisitables(){
        Set<Posicion> visitables = new HashSet<>();
        for(Posicion posicion:visitadas){
            if (getValorMapa(posicion)!=FUEGO) {
                for (Posicion vecino : posicion.getVecinos(filas, columnas)) {
                    if (!visitadas.contains(vecino)) {
                        visitables.add(vecino);
                    }
                }
            }
        }
        return visitables;
    }

    /**
     * Si la posición es visitable se añade a las visitadas.
     * Si es fuego se pierde una vida y si es el caso se fija el fin de juego.
     * Sino, se obtienen puntos o puede superarse la fase si hay camino visitado.
     * @param fila de la posición que se quiere visitar
     * @param columna de la posición que se quiere visitar
     * @return si se ha añadido a la lista de visitados porque era visitable
     */
    public boolean visitaPosicion(int fila, int columna){
        posicionUltima.setFilaColumna(fila,columna);
        Posicion posicion = new Posicion(fila, columna);
        if (getVisitables().contains(posicion)){
            visitadas.add(posicion);
            if (getValorMapa(posicion)==FUEGO){
                vidas--;
                if (vidas==0){
                    setFinPartida(true);
                }
            }else{
                puntos += PTOS_CELDA;
                if (posicion.equals(posicionPremio)){
                    puntos += PTOS_PREMIO;
                }
                if (posicion.equals(posicionVida)){
                    vidas ++;
                }
                if (hayCaminoVisitado()){
                    setFinFase(true);
                }
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * Búsqueda en anchura si existe un camino desde el inicio al final
     * @return si hay camino libre sin fuego por medio
     */
    public boolean hayCaminoPosible(){
        Posicion posicion;
        List<Posicion> recorridos = new ArrayList<>();
        Queue<Posicion> cola = new LinkedList<>();
        cola.add(posicionInicial);
        recorridos.add(posicionInicial);
        while(!cola.isEmpty()){
            posicion = cola.remove();
            for(Posicion vecino : posicion.getVecinos(filas,columnas)){
                if (vecino.equals(posicionFinal)){
                    return true;
                }
                if ( (getValorMapa(vecino)!=FUEGO) && (!recorridos.contains(vecino)) ){
                    recorridos.add(vecino);
                    cola.add(vecino);
                }
            }
        }
        return false;
    }

    /**
     * Búsqueda en anchura si existe un camino visitado desde el inicio al final
     * @return si hay camino libre sin fuego por medio
     */
    public boolean hayCaminoVisitado(){
        Posicion posicion;
        List<Posicion> recorridos = new ArrayList<>();
        Queue<Posicion> cola = new LinkedList<>();
        cola.add(posicionInicial);
        recorridos.add(posicionInicial);
        while(!cola.isEmpty()){
            posicion = cola.remove();
            for(Posicion vecino : posicion.getVecinos(filas,columnas)){
                if (vecino.equals(posicionFinal)){
                    return true;
                }
                if ( (getValorMapa(vecino)!=FUEGO) && (!recorridos.contains(vecino)) && (visitadas.contains(vecino)) ){
                    recorridos.add(vecino);
                    cola.add(vecino);
                }
            }
        }
        return false;
    }

}
