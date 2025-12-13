package com.example.proyectoaula;

// Se importan las clases de Android que vamos a necesitar
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Se declaran las variables para los elementos de la pantalla
    private ProgressBar ProBar;
    private TextView LoadTV, ProgTxt;
    // Se declara un 'Handler' para manejar tareas repetitivas, como la animación del texto
    private final Handler handler = new Handler(Looper.getMainLooper());
    // Se declara un contador para los puntos del texto "Cargando..."
    private int dotCount = 0;
    // Se declara una bandera para saber cuándo la app está lista para dibujarse
    private boolean isReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Se instala la pantalla de bienvenida (splash screen) antes que cualquier otra cosa
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        // Se conecta esta clase con su archivo de diseño XML
        setContentView(R.layout.activity_main);

        // Se usa esto para mantener la pantalla de bienvenida visible hasta que estemos listos
        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        // Se pregunta si la bandera 'isReady' ya es verdadera
                        if (isReady) {
                            // Si estamos listos, se quita el listener para no preguntar más y se dibuja la pantalla
                            content.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        } else {
                            // Si no estamos listos, se le dice al sistema que espere y no dibuje nada todavía
                            return false;
                        }
                    }
                });

        // Se buscan los elementos de la vista por su ID para poder usarlos
        ProBar = findViewById(R.id.BarraProgreso);
        LoadTV = findViewById(R.id.LoadingTextView);
        ProgTxt = findViewById(R.id.ProgresoTxt);

        // Se inician las animaciones de la barra y del texto
        animateProgressBar();
        animateLoadingText();

        // Se levanta la bandera para avisarle al sistema que ya puede quitar la pantalla de bienvenida y dibujar esta
        isReady = true;
    }

    // Aquí se configura la animación de la barra de progreso
    private void animateProgressBar() {
        // Se crea un animador que moverá el progreso de la barra de 0 a 100
        ObjectAnimator animation = ObjectAnimator.ofInt(ProBar, "progress", 0, 100);
        // Se le dice a la animación que dure 3.5 segundos en completarse
        animation.setDuration(3500);
        // Se usa un interpolador para que la animación empiece rápido y termine lento, se ve más suave
        animation.setInterpolator(new DecelerateInterpolator());

        // Se pone un listener para que en cada pasito de la animación, se actualice el texto del porcentaje
        animation.addUpdateListener(animationUpdated -> {
            int progress = (int) animationUpdated.getAnimatedValue();
            // Se actualiza el texto del porcentaje para que coincida con la barra
            ProgTxt.setText(String.format(Locale.US, "%d%%", progress));
        });

        // Se pone un listener para saber cuándo la animación ha terminado
        animation.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                super.onAnimationEnd(animation);
                // Cuando la barra llega al 100%, se prepara el brinco a la siguiente pantalla
                Intent newWindow = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(newWindow);

                // Se hace que el celular vibre un poquito para avisar que ya cargó
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Se revisa la versión de Android para usar el tipo de vibración correcta
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    // Para versiones viejas de Android, se usa el método antiguo
                    v.vibrate(100);
                }
                // Se finaliza esta pantalla de carga para que el usuario no pueda volver a ella
                finish();
            }
        });
        // Se inicia la animación de la barra
        animation.start();
    }

    // Aquí se configura la animación del texto "Cargando..."
    private void animateLoadingText() {
        // Se crea una tarea que se puede repetir
        Runnable textRunnable = new Runnable() {
            @Override
            public void run() {
                // Se obtiene el texto base "Cargando" desde los recursos
                String baseText = getString(R.string.BarraProgreso);
                // Se aumenta el contador de puntos y se reinicia si llega a 4
                dotCount = (dotCount + 1) % 4;

                // Se crea un constructor de texto para los puntos
                StringBuilder dots = new StringBuilder();
                for (int i = 0; i < dotCount; i++) {
                    dots.append(".");
                }
                // Se junta el texto base con los puntos y se muestra
                LoadTV.setText(baseText + dots.toString());
                // Se le dice al 'Handler' que vuelva a ejecutar esta misma tarea en medio segundo
                handler.postDelayed(this, 500);
            }
        };
        // Se le dice al 'Handler' que ejecute la tarea por primera vez
        handler.post(textRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cuando esta pantalla se destruye, se limpian las tareas pendientes del 'Handler' para no gastar batería
        handler.removeCallbacksAndMessages(null);
    }
}
