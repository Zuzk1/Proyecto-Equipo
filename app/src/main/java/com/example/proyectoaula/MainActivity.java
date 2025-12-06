package com.example.proyectoaula;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ProgressBar ProBar;
    private TextView LoadTV;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int dotCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProBar = findViewById(R.id.BarraProgreso);
        LoadTV = findViewById(R.id.LoadingTextView);

        animateProgressBar();
        animateLoadingText();
    }
    //Metodo Animmar Barra
    private void animateProgressBar() {
        //Crear Animacion para la esa
        ObjectAnimator animation = ObjectAnimator.ofInt(ProBar, "progress", 0, 100);
        //Duracion de la Carga
        animation.setDuration(3500);
        //Hace que no se vea agresiva la carga
        animation.setInterpolator(new DecelerateInterpolator());
        //Inicia la Animacion
        animation.start();
    }
    //Metodo Animmar Texto de Carga
    private void animateLoadingText() {
        //Hace un ciclo repetitivo
        Runnable textRunnable = new Runnable() {
            @Override
            public void run() {
                String baseText = getString(R.string.BarraProgreso);
                dotCount = (dotCount + 1) % 4;

                StringBuilder dots = new StringBuilder();
                for (int i = 0; i < dotCount; i++) {
                    dots.append(".");
                }
                LoadTV.setText(baseText + dots.toString());
                //Hace que se Ejecute de Nuevo
                handler.postDelayed(this, 500);
            }
        };
        //Repite la tarea por Tercera vez
        handler.post(textRunnable);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Detiene la Animacion del Texto
        handler.removeCallbacksAndMessages(null);
    }
}
