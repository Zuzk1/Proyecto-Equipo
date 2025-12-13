package com.example.proyectoaula;

// Clases Necesarias
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyectoaula.databinding.ActivityAddReminderBinding;

// Se importan clases para fechas, alarmas y permisos
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

    // Se declara el binding para acceder a las vistas de forma segura
    private ActivityAddReminderBinding binding;
    // Se declaran variables para guardar la hora y fecha que elija el usuario
    private int selectedHour = -1;
    private int selectedMinute = -1;
    private String selectedDate;
    private int year, month, day;
    // Se prepara el launcher para poder pedir permisos
    private ActivityResultLauncher<Intent> requestPermissionLauncher;
    // Se declara el DAO para poder hablar con la base de datos de recordatorios
    private ReminderDao reminderDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Se infla la vista usando el binding que declaramos arriba
        binding = ActivityAddReminderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Se obtiene la base de datos para poder usarla
        AppDatabase db = AppDatabase.getDatabase(this);
        // Se obtiene el DAO específico de los recordatorios desde la base de datos
        reminderDao = db.reminderDao();

        // Se configuran todos los botones y listeners de la pantalla
        setupPermissionLauncher();
        setupDatePicker();
        setupTimePicker();
        setupSaveButton();

        // Se pone a escuchar el switch de notificaciones por si el usuario lo activa o desactiva
        binding.NotificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Se habilita o deshabilita el campo de la hora según el estado del switch
            binding.TimeReminder.setEnabled(isChecked);
            // Se elige el mensaje correcto para mostrar en el Toast
            int messageResId = isChecked ? R.string.LasNotificacionsEstanActivadasReminder : R.string.LasNotificacionesEstanDesactivadasReminder;
            Toast.makeText(AddReminderActivity.this, messageResId, Toast.LENGTH_SHORT).show();
        });
    }

    // Se prepara el sistema para recibir una respuesta cuando se piden permisos
    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // Se revisa de nuevo si el permiso ya fue concedido después de que el usuario vuelve de los ajustes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && canScheduleExactAlarms()) {
                // Si ya tenemos permiso, le avisamos al usuario que puede guardar de nuevo
                Toast.makeText(this, R.string.permiso_concedido_addRemAct, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Se revisa si la app tiene permiso para programar alarmas exactas
    private boolean canScheduleExactAlarms() {
        // Esto solo aplica para Android 12 (letra S) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Se obtiene el manejador de alarmas del sistema
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            // Se le pregunta directamente al sistema si la app tiene permiso o no
            return alarmManager.canScheduleExactAlarms();
        }
        // Si la versión de Android es más vieja, el permiso se tiene por defecto
        return true;
    }

    // Se le muestra al usuario un diálogo para que vaya a los ajustes a dar el permiso
    private void requestExactAlarmPermission() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permiso_necesario_AddRemAct)
                .setMessage(R.string.permiso_especial_AddRemAct)
                .setPositiveButton(R.string.ir_a_los_ajustes_AddRemAct, (dialog, which) -> {
                    // Se crea un intent para abrir la pantalla de ajustes de alarmas exactas
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    requestPermissionLauncher.launch(intent);
                })
                .setNegativeButton(R.string.cancelar_AddRemAct, null)
                .show();
    }

    // Se configura el selector de fecha
    private void setupDatePicker() {
        // Se intenta obtener una fecha que venga de la pantalla anterior
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");

        // Si no viene ninguna fecha, se usa la de hoy por defecto
        if (selectedDate == null || selectedDate.isEmpty()) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            selectedDate = day + "/" + (month + 1) + "/" + year;
        } else {
            // Si viene una fecha, se parte el texto para obtener los números
            String[] parts = selectedDate.split("/");
            if (parts.length == 3) {
                day = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]) - 1; // Se resta 1 al mes porque en Calendar van de 0 a 11
                year = Integer.parseInt(parts[2]);
            }
        }
        // Se muestra la fecha en el campo de texto correspondiente
        binding.fechaSave.setText(selectedDate);

        // Se pone un listener para que al tocar el campo de fecha, se abra el calendario
        binding.fechaSave.setOnClickListener(v -> {
            // Se crea y muestra el diálogo para seleccionar la fecha
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, monthOfYear, dayOfMonth) -> {
                // Se actualizan las variables con la nueva fecha que eligió el usuario
                year = selectedYear;
                month = monthOfYear;
                day = dayOfMonth;
                selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                // Se actualiza el texto para que el usuario vea la nueva fecha
                binding.fechaSave.setText(selectedDate);
            }, year, month, day);
            datePickerDialog.show();
        });
    }

    // Se configura el selector de hora
    private void setupTimePicker() {
        binding.TimeReminder.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            // Se crea y muestra el diálogo para seleccionar la hora
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                // Se guardan la hora y minuto que eligió el usuario
                selectedHour = hourOfDay;
                selectedMinute = minute;
                // Se le da formato a la hora para que se vea bien (ej. 09:05)
                binding.TimeReminder.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true); // true para formato de 24 horas
            timePickerDialog.show();
        });
    }

    // Se configura lo que pasa cuando el usuario le da al botón de guardar
    private void setupSaveButton() {
        binding.ButtonSave.setOnClickListener(v -> {
            // Se obtiene el texto del título y se le quitan los espacios de los lados
            String title = binding.ActNameEdit.getText().toString().trim();
            // Se revisa si el switch de notificaciones está activado
            boolean useNotification = binding.NotificationSwitch.isChecked();

            // Se valida que el título no esté vacío
            if (title.isEmpty()) {
                binding.ActNameLayout.setError(getString(R.string.TituloNoVacioReminder));
                return; // Se detiene el proceso si no hay título
            } else {
                binding.ActNameLayout.setError(null); // Se quita el error si ya hay título
            }

            // Se valida que se haya elegido una hora si las notificaciones están activadas
            if (useNotification && selectedHour == -1) {
                Toast.makeText(this, R.string.PVSeleccionaUnaHoraReminder, Toast.LENGTH_LONG).show();
                return; // Se detiene el proceso
            }

            // Se crea un objeto Calendar para convertir la fecha y hora a un número 'long' (timestamp)
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, (selectedHour != -1 ? selectedHour : 0), (selectedMinute != -1 ? selectedMinute : 0), 0);
            long triggerTime = calendar.getTimeInMillis();

            // Se crea un nuevo objeto Reminder listo para guardar
            Reminder newReminder = new Reminder();
            newReminder.titulo = title;
            newReminder.timestamp = triggerTime;

            // Se manda a la base de datos a que guarde el nuevo recordatorio en un hilo aparte para no trabar la app
            AppDatabase.databaseWriteExecutor.execute(() -> {
                reminderDao.insert(newReminder);
            });

            // Si se usan notificaciones, se programa la alarma
            if (useNotification) {
                // Primero se revisa si tenemos permiso para alarmas exactas
                if (canScheduleExactAlarms()) {
                    // Si tenemos permiso, se programa la notificación
                    scheduleNotification(title, getString(R.string.recordatorio_AddRemAct), triggerTime);
                } else {
                    // Si no tenemos permiso, se lo pedimos al usuario
                    requestExactAlarmPermission();
                    return; // Se detiene aquí para que el usuario dé el permiso antes de continuar
                }
            }

            // Se le informa a la actividad anterior que all estar good
            setResult(RESULT_OK);
            // Se muestra un mensaje de confirmación
            Toast.makeText(this, getString(R.string.TareaReminder) + " '" + title + "' " + getString(R.string.GuardadaReminder), Toast.LENGTH_SHORT).show();
            // Se cierra esta pantalla para volver a la anterior
            finish();
        });
    }

    // Se programa la notificación usando el AlarmManager
    private void scheduleNotification(String title, String note, long triggerTime) {
        // Se prepara un 'Intent' que se va a disparar cuando sea la hora
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        // Se le meten datos extra al intent para que la notificación sepa qué mostrar
        intent.putExtra("EXTRA_TASK_TITLE", title);
        intent.putExtra("EXTRA_TASK_NOTE", note);

        // Se envuelve el intent en un PendingIntent, que es como un permiso para que el sistema lo use después
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                (int) System.currentTimeMillis(), // Se usa un ID único para que no se sobreescriban las alarmas
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Se obtiene el servicio de alarmas del sistema
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Se programa la alarma para que se dispare exactamente a la hora, incluso si el celular está dormido
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

        // Se le avisa al usuario que el recordatorio ya quedó programado
        Toast.makeText(this, R.string.recordatorio_programado_con_exito_AddRemAct, Toast.LENGTH_LONG).show();
    }
}
