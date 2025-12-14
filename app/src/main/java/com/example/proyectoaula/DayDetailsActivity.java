package com.example.proyectoaula;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DayDetailsActivity extends AppCompatActivity {

    // Esta constante no es visible para el usuario, está bien dejarla aquí.
    public static final String EXTRA_TIMESTAMP = "extra_timestamp";

    private TextView tvDateTitle;
    private TextView tvTasksList;
    private AppDatabase db;
    private ReminderDao reminderDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_details);

        tvDateTitle = findViewById(R.id.tvDateTitle);
        tvTasksList = findViewById(R.id.tvTasksList);

        db = AppDatabase.getDatabase(getApplicationContext());
        reminderDao = db.reminderDao();

        long startOfDayTimestamp = getIntent().getLongExtra(EXTRA_TIMESTAMP, -1);
        Log.d("DAY_DETAILS_DEBUG", "Timestamp recibido: " + startOfDayTimestamp);

        if (startOfDayTimestamp != -1) {
            Calendar startOfDay = Calendar.getInstance(TimeZone.getDefault());
            startOfDay.setTimeInMillis(startOfDayTimestamp);

            // =======================================================
            // === USA EL RECURSO DE STRING PARA EL TÍTULO
            // =======================================================
            String dialogTitle = getString(R.string.day_details_title,
                    startOfDay.get(Calendar.DAY_OF_MONTH),
                    startOfDay.get(Calendar.MONTH) + 1);
            tvDateTitle.setText(dialogTitle);

            AppDatabase.databaseWriteExecutor.execute(() -> {
                Calendar endOfDay = (Calendar) startOfDay.clone();
                endOfDay.add(Calendar.DAY_OF_MONTH, 1);
                long endOfDayTimestamp = endOfDay.getTimeInMillis();

                final List<Reminder> reminders = reminderDao.getRemindersBetween(startOfDayTimestamp, endOfDayTimestamp);

                Log.d("DAY_DETAILS_DEBUG", "Buscando entre: " + startOfDayTimestamp + " y " + endOfDayTimestamp);
                Log.d("DAY_DETAILS_DEBUG", "Reminders encontrados: " + reminders.size());

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (reminders.isEmpty()) {
                        // =======================================================
                        // === USA EL RECURSO DE STRING PARA "NO HAY ACTIVIDADES"
                        // =======================================================
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
            // =======================================================
            // === USA LOS RECURSOS DE STRING PARA LOS MENSAJES DE ERROR
            // =======================================================
            tvDateTitle.setText(R.string.day_details_error_title);
            tvTasksList.setText(R.string.day_details_error_message);
        }
    }
}
