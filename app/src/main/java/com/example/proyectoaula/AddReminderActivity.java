package com.example.proyectoaula;

// Imports que ya tenías
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyectoaula.databinding.ActivityAddReminderBinding;

// NUEVOS IMPORTS para el selector de fecha y las notificaciones
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.widget.DatePicker;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

import java.util.Calendar;
import java.util.Locale;

public class AddReminderActivity extends AppCompatActivity {

    //Se usa ViewBinding para q la vista sea segura
    private ActivityAddReminderBinding binding;

    //Variables para almacenar la hora seleccionada
    private int selectedHour = -1;
    private int selectedMinute = -1;

    //NUEVA VARIABLE para almacenar la fecha seleccionada
    private String selectedDate;

    //NUEVAS VARIABLES para guardar la fecha para la alarma
    private int year, month, day;

    //NUEVO: Un "launcher" para gestionar la pantalla de solicitud de permisos.
    private ActivityResultLauncher<Intent> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflar el layout usando ViewBinding
        binding = ActivityAddReminderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Se configura el launcher para la solicitud de permisos.
        setupPermissionLauncher();
        //Configuracion de Selector de Fecha (NUEVA LLAMADA)
        setupDatePicker();
        //Configuracion de Selector de Hora
        setupTimePicker();
        //Configuracion del Boton Guardar
        setupSaveButton();

        //Logica del Switch de notificaciones
        binding.NotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Lógica si las notificaciones están prendidas
                    binding.TimeReminder.setEnabled(true); // Habilita el selector de hora
                    Toast.makeText(AddReminderActivity.this, R.string.LasNotificacionsEstanActivadasReminder, Toast.LENGTH_SHORT).show();
                } else {
                    //Lógica si estan apagadas
                    binding.TimeReminder.setEnabled(false); // Deshabilita el selector de hora
                    Toast.makeText(AddReminderActivity.this, R.string.LasNotificacionesEstanDesactivadasReminder, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {});
    }

    private boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            return alarmManager.canScheduleExactAlarms();
        }
        return true;
    }

    private void requestExactAlarmPermission() {
        new AlertDialog.Builder(this)
                .setTitle("Permiso Necesario")
                .setMessage("Para programar recordatorios a una hora exacta, esta aplicación necesita permiso. Por favor, actívalo en la siguiente pantalla.")
                .setPositiveButton("Ir a Ajustes", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    requestPermissionLauncher.launch(intent);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    //MÉTODO MODIFICADO: Configuracion del Selector de Fecha
    private void setupDatePicker() {
        // Intenta obtener la fecha pasada desde la actividad anterior
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");

        // Si no se pasó ninguna fecha, usa la fecha actual como valor por defecto
        if (selectedDate == null || selectedDate.isEmpty()) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            // Formatea la fecha y la guarda
            selectedDate = day + "/" + (month + 1) + "/" + year;
        } else {
            // Si la fecha viene de otra actividad, necesitamos parsearla para obtener los valores numéricos
            String[] parts = selectedDate.split("/");
            if (parts.length == 3) {
                day = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]) - 1; // El mes en Calendar va de 0 a 11
                year = Integer.parseInt(parts[2]);
            }
        }

        // Muestra la fecha (ya sea la pasada o la actual) en el TextView
        binding.fechaSave.setText(selectedDate);

        // Establece el OnClickListener para mostrar el diálogo de calendario al tocar
        binding.fechaSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Usa las variables de clase para mostrar la fecha actual o la ya seleccionada
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddReminderActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int monthOfYear, int dayOfMonth) {
                                // Se guardan los componentes numéricos
                                year = selectedYear;
                                month = monthOfYear;
                                day = dayOfMonth;

                                // Se guarda la fecha seleccionada en la variable
                                selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                // Se actualiza el texto del TextView para mostrar la nueva fecha
                                binding.fechaSave.setText(selectedDate);
                            }
                        }, year, month, day);

                // Muestra el diálogo
                datePickerDialog.show();
            }
        });
    }

    //Configuracion del Selector de Hora (sin cambios)
    private void setupTimePicker() {
        binding.TimeReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddReminderActivity.this,
                        (view, hourOfDay, minute) -> {
                            selectedHour = hourOfDay;
                            selectedMinute = minute;
                            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                            binding.TimeReminder.setText(selectedTime);
                        },
                        currentHour,
                        currentMinute,
                        true
                );
                timePickerDialog.show();
            }
        });
    }

    //Configura el OnClickListener para el botón de guardar. (MODIFICADO)
    private void setupSaveButton() {
        binding.ButtonSave.setOnClickListener(v -> {
            String title = binding.ActNameEdit.getText().toString().trim();
            String note = binding.ActNoteEdit.getText().toString().trim();
            boolean useNotification = binding.NotificationSwitch.isChecked();

            if (title.isEmpty()) {
                binding.ActNameLayout.setError(getString(R.string.TituloNoVacioReminder));
                return;
            } else {
                binding.ActNameLayout.setError(null);
            }

            if (useNotification && (selectedHour == -1 || selectedMinute == -1)) {
                Toast.makeText(AddReminderActivity.this, R.string.PVSeleccionaUnaHoraReminder, Toast.LENGTH_LONG).show();
                return;
            }

            if (selectedDate == null || selectedDate.isEmpty()) {
                Toast.makeText(AddReminderActivity.this, "Por favor, selecciona una fecha", Toast.LENGTH_LONG).show();
                return;
            }

            //--- Lógica de la Base de Datos ---
            Task newTask = new Task(title, note, selectedDate, selectedHour, selectedMinute, useNotification);
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            AppDatabase.databaseWriteExecutor.execute(() -> {
                db.taskDao().insert(newTask);
            });
            //--- Fin Lógica DB ---

            if (useNotification) {
                if (canScheduleExactAlarms()) {
                    scheduleNotification(title, note);
                } else {
                    requestExactAlarmPermission();
                    return;
                }
            }

            setResult(RESULT_OK);
            Toast.makeText(AddReminderActivity.this, getString(R.string.TareaReminder) + title + getString(R.string.GuardadaReminder), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    //MÉTODO CORREGIDO: Se encarga de programar la alarma para la notificación.
    private void scheduleNotification(String title, String note) {
        // 1. Crea un Intent que apunta al NotificationReceiver.
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        intent.putExtra("EXTRA_TASK_TITLE", title);
        intent.putExtra("EXTRA_TASK_NOTE", note);

        // 2. Crea un PendingIntent que el sistema ejecutará en el futuro.
        int pendingIntentId = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                pendingIntentId,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // 3. Obtiene el servicio de alarmas del sistema.
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 4. Configura la fecha y hora exactas para la alarma.
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, selectedHour, selectedMinute, 0);
        long triggerTime = calendar.getTimeInMillis();

        // 5. --- ¡¡LA LÍNEA CORREGIDA!! ---
        // Usamos setExactAndAllowWhileIdle() para forzar que la alarma se dispare
        // incluso si el dispositivo está en modo de ahorro de energía (Doze).
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

        // Muestra un Toast de confirmación de la programación.
        Toast.makeText(this, "Recordatorio programado para " + selectedDate + " a las " + String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute), Toast.LENGTH_LONG).show();
    }
}
