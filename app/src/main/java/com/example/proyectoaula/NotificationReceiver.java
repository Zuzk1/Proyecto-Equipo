package com.example.proyectoaula;

// Se importan las clases necesarias para manejar notificaciones y recibir eventos del sistema
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

// Se crea una clase que puede "escuchar" eventos del sistema, como una alarma que se dispara
public class NotificationReceiver extends BroadcastReceiver {

    // Se define un nombre único para el canal donde se mostrarán nuestras notificaciones
    private static final String CHANNEL_ID = "task_reminder_channel";

    // Este es el corazón del archivo, se ejecuta justo cuando la alarma programada llega a su hora
    @Override
    public void onReceive(Context context, Intent intent) {
        // Se sacan los datos que le metimos al 'Intent' cuando programamos la alarma
        String title = intent.getStringExtra("EXTRA_TASK_TITLE");
        String note = intent.getStringExtra("EXTRA_TASK_NOTE");

        // Se obtiene el manejador de notificaciones del sistema para poder mostrar algo
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Se revisa si la versión de Android es 8 (Oreo) o más nueva
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Si es una versión nueva, es obligatorio crear un "canal" para las notificaciones
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.recordatorios_de_actividades_NotiReci), // Este es el nombre que el usuario ve en los ajustes de la app
                    NotificationManager.IMPORTANCE_HIGH // Se le pone importancia alta para que la notificación aparezca en grande
            );
            // Se le pone una descripción al canal que se ve en los ajustes
            channel.setDescription(context.getString(R.string.canal_actividades_pendientes_NotiReci));
            // Se le dice al canal que debe permitir la vibración
            channel.enableVibration(true);
            // Se le da un patrón de vibración para que se sienta chido
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            // Se registra el canal con el sistema
            notificationManager.createNotificationChannel(channel);
        }

        // Se empieza a construir la notificación usando un 'Builder'
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                // Se le pone un ícono pequeño y simple, que es lo que pide Android para la barra de estado
                .setSmallIcon(R.drawable.notification_icon)
                // Se le pone el título que sacamos del intent
                .setContentTitle(title)
                // Se le pone el texto del cuerpo de la notificación
                .setContentText(note)
                // Se le da prioridad alta para que aparezca encima de otras cosas
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Se le dice que se cierre sola cuando el usuario la toque
                .setAutoCancel(true)
                // Se le pone un patrón de vibración para celulares con versiones viejas de Android
                .setVibrate(new long[]{0, 1000, 500, 1000});

        // Se le dice al manejador de notificaciones que ya está lista y que la muestre
        // Se usa un ID único basado en la hora para no reemplazar notificaciones anteriores
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }
}
