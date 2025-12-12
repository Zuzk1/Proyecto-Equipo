package com.example.proyectoaula;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

// 1. La clase debe heredar de BroadcastReceiver
public class NotificationReceiver extends BroadcastReceiver {

    // 2. Un nombre único para el canal de notificaciones
    private static final String CHANNEL_ID = "task_reminder_channel";

    // 3. El método onReceive() es el que se ejecuta cuando la alarma se dispara
    @Override
    public void onReceive(Context context, Intent intent) {
        // 4. Se recuperan los datos (título y nota) que enviamos desde AddReminderActivity
        String title = intent.getStringExtra("EXTRA_TASK_TITLE");
        String note = intent.getStringExtra("EXTRA_TASK_NOTE");

        // 5. Se obtiene el gestor de notificaciones del sistema
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 6. (SOLO PARA ANDROID 8.0 Y SUPERIOR) Es OBLIGATORIO crear un "Canal de Notificación".
        // Sin esto, las notificaciones no aparecen en versiones modernas de Android.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Recordatorios de Tareas", // Nombre visible para el usuario en los ajustes
                    NotificationManager.IMPORTANCE_HIGH // Importancia alta para que la notificación aparezca en la pantalla
            );
            channel.setDescription("Canal para notificaciones de tareas pendientes");
            channel.enableVibration(true); // Se activa la vibración en el canal
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000}); // Patrón: espera, vibra 1s, espera 0.5s, vibra 1s
            notificationManager.createNotificationChannel(channel);
        }

        // 7. Se construye la notificación paso a paso
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.icono_foreground) // Ícono pequeño que aparece en la barra de estado
                .setContentTitle(title) // El título de la notificación
                .setContentText(note)   // El texto del cuerpo de la notificación
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridad para que intente mostrarse sobre otras apps
                .setAutoCancel(true) // La notificación se cierra automáticamente cuando el usuario la toca
                .setVibrate(new long[]{0, 1000, 500, 1000}); // Se define la vibración también aquí por compatibilidad con versiones antiguas

        // 8. Se muestra la notificación
        // Se usa un ID único (la hora actual) para que si hay varias notificaciones, no se sobreescriban.
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }
}
