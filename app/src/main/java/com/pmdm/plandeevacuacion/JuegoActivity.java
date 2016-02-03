package com.pmdm.plandeevacuacion;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Define el funcionamiento del juego. Crea las circunstancias de juego que consiste en alcanzar
 * una meta desde una posición inicial seleccionando las celdas horizontales o verticales adyacentes
 * que no sean fuego.
 * @author Aitor Etxabarren (GPL v3)
 */
public class JuegoActivity extends AppCompatActivity implements View.OnTouchListener {

    /** Elemento sobre el que dibujar */
    private Lienzo lienzo;
    /** Modelo del juego */
    private Juego juego;

    /** Para invocar la ejecución cada segundo */
    private Handler handler;
    /** Código que se ejecuta cada segundo */
    private Runnable runnable;

    /**
     * Cuando se crea la actividad. Definir el juego, poner el lienzo y el listener.
     * @param savedInstanceState Para obtener parámetros
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.LayoutJuego);

        juego = new Juego();

        lienzo = new Lienzo(this, juego);
        lienzo.setOnTouchListener(this);
        layout.addView(lienzo);

        iniciarTemporizador();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        juego.setFinPartida(true);
    }

    public void iniciarTemporizador(){
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("PdeE","ejecución temporal " + juego.getTiempo());
                    if ((juego.getTiempo()<=0) || juego.isFinPartida()){
                        juego.setFinPartida(true);
                        lienzo.invalidate();
                    }else if (!juego.isFinFase()) {
                        juego.restarTiempo();
                        lienzo.invalidate();
                    }
                    if (!juego.isFinPartida()) {
                        handler.postDelayed(this, 1000);
                        if (!juego.isFinFase()) {
                            lienzo.invalidate();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    /**
     * Cuando se toca el lienzo. Visita celda si es adyacente a las visitadas.
     * @param v El lienzo
     * @param event Objeto con los detalles del evento
     * @return true
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int fila = lienzo.getFila(event.getY());
            int columna = lienzo.getColumna(event.getX());

            if (juego.isFinPartida()){
                this.finish();
                return true;
            }

            if (juego.isFinFase()){
                //Sumar tiempo restante como puntos
                juego.setPuntos(juego.getPuntos()+juego.getTiempo()*2);

                juego.avanzaFase();
                lienzo.invalidate();
                return true;
            }

            if ( (fila>=0) && (fila<juego.getFilas()) && (columna>=0) && (columna<juego.getColumnas()) ) {
                juego.visitaPosicion(fila, columna);

                lienzo.invalidate();
            }
        }
        return true;
    }

}
