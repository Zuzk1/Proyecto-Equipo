package com.example.proyectoaula;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Context;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ProgressBar ProBar;
    private TextView LoadTV, ProgTxt;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int dotCount = 0;
    private boolean isReady = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Instala la Splash Screen antes que nada
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        // Llama a super.onCreate() una sola vez
        super.onCreate(savedInstanceState);

        // Llama a setContentView() una sola vez
        setContentView(R.layout.activity_main);

        // Mantiene la Splash Screen visible hasta que la app esté lista
        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        //iNICIA SI LA APP ESTA LISTA OKENFJRNRJIGJIGRJIBN
                        if (isReady) {
                            content.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });


        ProBar = findViewById(R.id.BarraProgreso);
        LoadTV = findViewById(R.id.LoadingTextView);
        ProgTxt = findViewById(R.id.ProgresoTxt);

        animateProgressBar();
        animateLoadingText();

        // Avisa que la app ya puede dibujarse
        isReady = true;
    }

    //Metodo Animmar Barra
    private void animateProgressBar() {
        //Crear Animacion para la esa
        ObjectAnimator animation = ObjectAnimator.ofInt(ProBar, "progress", 0, 100);
        //Duracion de la Carga
        animation.setDuration(3500);
        //Hace que no se vea agresiva la carga
        animation.setInterpolator(new DecelerateInterpolator());

        //Animar el Texto
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                // Actualiza el TextView con el valor del progreso
                ProgTxt.setText(String.format(Locale.US, "%d%%", progress));
            }
        });

        // Listener para cmabiar de Actividad
        animation.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                super.onAnimationEnd(animation);
                // Cuando la barra llega a 100, se ejecuta este código
                Intent NewWindow = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(NewWindow);

                //Vibra el telefono cuando llega al 100%
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                //Comprobar la versión de Android y vibrar
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {

                    v.vibrate(100);
                }
                // Finaliza esta actividad
                finish();
            }
        });

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
