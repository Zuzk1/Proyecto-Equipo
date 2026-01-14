package com.example.proyectoaula;

// Se importan las clases necesarias para manejar notificaciones y recibir eventos del sistema
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent; // ¡NUEVA IMPORTACIÓN! Para manejar el toque en la notificación.
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

        // --- INICIO DE LA MEJORA SUGERIDA ---
        // Se genera un ID único para la notificación y el PendingIntent.
        // Usar System.currentTimeMillis() es una forma sencilla de asegurar que cada ID sea único.
        // Esto previene que una nueva notificación sobreescriba el comportamiento de una antigua
        // si el usuario aún no la ha tocado.
        int uniqueId = (int) System.currentTimeMillis();
        // --- FIN DE LA MEJORA SUGERIDA ---

        // Se obtiene el manejador de notificaciones del sistema para poder mostrar algo
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // --- INICIO DEL CAMBIO IMPORTANTE ---

        // 1. Crear un Intent para abrir la actividad de actividades guardadas.
        //    ¡IMPORTANTE! Reemplaza 'SavedActivitiesActivity.class' con el nombre real de tu clase
        //    que muestra la lista de actividades.
        Intent notificationIntent = new Intent(context, com.example.proyectoaula.AddReminderViewActivity.class);

        // 2. Opcional: Flags para controlar cómo se abre la actividad.
        //    - FLAG_ACTIVITY_CLEAR_TOP: Si la actividad ya está abierta en la pila, limpia las que están encima y la trae al frente.
        //    - FLAG_ACTIVITY_SINGLE_TOP: Si la actividad ya está en la cima, reutiliza esa instancia en lugar de crear una nueva.
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // 3. Crear el PendingIntent. Esto le da permiso al sistema para abrir tu app.
        //    - El requestCode ahora es único (usando 'uniqueId') para cada notificación.
        //    - FLAG_UPDATE_CURRENT indica que si ya existe un PendingIntent con el mismo requestCode, se actualice con este nuevo Intent.
        //    - FLAG_IMMUTABLE es requerido para mayor seguridad en versiones recientes de Android.
        PendingIntent pendingIntent = PendingIntent.getActivity(context, uniqueId, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // --- FIN DEL CAMBIO IMPORTANTE ---

        // Se revisa si la versión de Android es 8 (Oreo) o más nueva
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.recordatorios_de_actividades_NotiReci),
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(context.getString(R.string.canal_actividades_pendientes_NotiReci));
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationManager.createNotificationChannel(channel);
        }

        // Se empieza a construir la notificación usando un 'Builder'
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(note)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                // --- ASOCIACIÓN DEL PENDINGINTENT ---
                // ¡Aquí se le dice a la notificación qué hacer cuando el usuario la toque!
                .setContentIntent(pendingIntent);

        // Se le dice al manejador de notificaciones que ya está lista y que la muestre
        // Se usa el mismo ID único para mostrar la notificación. Esto asegura que cada notificación
        // se maneje de forma independiente.
        notificationManager.notify(uniqueId, builder.build());
    }
}
