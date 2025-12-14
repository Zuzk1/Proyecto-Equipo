package com.example.proyectoaula;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class DayDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_TIMESTAMP = "extra_timestamp";

    private TextView tvDateTitle;
    private TextView tvTasksList;
    private AppDatabase db;
    private ReminderDao reminderDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ¡OJO! El título de la barra ("Erro Task") podría venir de aquí o del AndroidManifest.
        // Si quieres cambiarlo dinámicamente, usa:
        // getSupportActionBar().setTitle("Tu nuevo título");

        setContentView(R.layout.activity_day_details);

        tvDateTitle = findViewById(R.id.tvDateTitle);
        tvTasksList = findViewById(R.id.tvTasksList);

        db = AppDatabase.getDatabase(getApplicationContext());
        reminderDao = db.reminderDao();

        long startOfDayTimestamp = getIntent().getLongExtra(EXTRA_TIMESTAMP, -1);
        Log.d("DAY_DETAILS_DEBUG", "Timestamp recibido: " + startOfDayTimestamp);

        if (startOfDayTimestamp != -1) {
            // ... (el código cuando todo va bien no cambia)
            Calendar startOfDay = Calendar.getInstance(TimeZone.getDefault());
            startOfDay.setTimeInMillis(startOfDayTimestamp);

            String dialogTitle = getString(R.string.day_details_title,
                    startOfDay.get(Calendar.DAY_OF_MONTH),
                    startOfDay.get(Calendar.MONTH) + 1);
            tvDateTitle.setText(dialogTitle);

            AppDatabase.databaseWriteExecutor.execute(() -> {
                Calendar endOfDay = (Calendar) startOfDay.clone();
                endOfDay.add(Calendar.DAY_OF_MONTH, 1);
                long endOfDayTimestamp = endOfDay.getTimeInMillis();

                final List<Reminder> reminders = reminderDao.getRemindersBetween(startOfDayTimestamp, endOfDayTimestamp);

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (reminders.isEmpty()) {
                        tvTasksList.setText(R.string.day_details_no_activities);
                    } else {
                        StringBuilder tasksText = new StringBuilder();
                        for (Reminder reminder : reminders) {
                            tasksText.append("• ").append(reminder.titulo).append("\n");
                        }
                        tvTasksList.setText(tasksText.toString());
                    }
                });
            });

        } else {
            // --- ¡AQUÍ ESTÁ LA CORRECCIÓN PARA EL TEXTO DEL ERROR! ---
            // Asignamos los textos de error desde los recursos.
            tvDateTitle.setText(R.string.day_details_error_title);
            tvTasksList.setText(R.string.day_details_error_message);

            // Se aplican los colores de error correctos.
            // El título del error debe ser llamativo (rojo).
            tvDateTitle.setTextColor(ContextCompat.getColor(this, R.color.background_color));

            // El mensaje del error puede ser más sutil.
            tvTasksList.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        }
    }
}
