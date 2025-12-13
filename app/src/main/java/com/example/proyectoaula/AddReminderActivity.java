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

// NUEVOS IMPORTS para el selector de fecha, notificaciones y la solicitud de permisos
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

        //"Inflar" el layout usando ViewBinding
        binding = ActivityAddReminderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Se configura el launcher para la solicitud de permisos.
        setupPermissionLauncher();

        //Configuracion de Selector de Fecha (NUEVA LLAMADA)
        setupDatePicker();
        //Configuracion de Selector de Hora
        setupTimePicker();
        //Configuracion del Boton Guardar
        setupSaveButton();

        //Logica del Switch de notificaciones (sin cambios)
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

    //Metodo para abrir los ajustes de la app
    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Este código se ejecuta cuando el usuario vuelve de la pantalla de solicitud de permisos.
                });
    }
    //Comprueba que la App tenga los permisos
    private boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Para Android 12 y superior, se usa el método oficial del AlarmManager.
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            return alarmManager.canScheduleExactAlarms();
        }
        // Para versiones anteriores a Android 12, el permiso no es necesario, así que se considera concedido.
        return true;
    }
    //Dialogo para los ajustes de permisos
    private void requestExactAlarmPermission() {
        new AlertDialog.Builder(this)
                .setTitle("Permiso Necesario")
                .setMessage("Para programar recordatorios, Erro Task necesita permiso. Por favor, actívalo en la siguiente pantalla.")
                .setPositiveButton("Ir a Ajustes", (dialog, which) -> {
                    // Crea un Intent que abre directamente la pantalla de permisos de alarmas.
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    requestPermissionLauncher.launch(intent);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    //Configuracion del selector de Fecha
    private void setupDatePicker() {
        //Intenta obtener la fecha pasada desde la actividad anterior
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");

        // Si no se pasó ninguna fecha, usa la fecha actual como valor por defecto
        if (selectedDate == null || selectedDate.isEmpty()) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            //Formatea la fecha y la guarda
            selectedDate = day + "/" + (month + 1) + "/" + year;
        } else {
            //Si la fecha viene de otra actividad, necesitamos parsearla para obtener los valores numéricos
            //Esto es una simplificación. Si el formato cambia, esto podría fallar.
            String[] parts = selectedDate.split("/");
            if (parts.length == 3) {
                day = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]) - 1; // El mes en Calendar va de 0 a 11
                year = Integer.parseInt(parts[2]);
            }
        }

        //Muestra la fecha (ya sea la pasada o la actual) en el TextView
        binding.fechaSave.setText(selectedDate);

        //Establece el OnClickListener para mostrar el diálogo de calendario al tocar
        binding.fechaSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Usa las variables de clase para mostrar la fecha actual o la ya seleccionada
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddReminderActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int monthOfYear, int dayOfMonth) {
                                // Este código se ejecuta cuando el usuario selecciona una fecha y pulsa "OK"
                                // Se guardan los componentes numéricos
                                year = selectedYear;
                                month = monthOfYear;
                                day = dayOfMonth;

                                // Se guarda la fecha seleccionada en la variable (el mes se cuenta desde 0, por eso se suma 1)
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
        // ... tu código existente está perfecto aquí ...
        binding.TimeReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddReminderActivity.this,
                        (view, hourOfDay, minute) -> {
                            //Se guarda la hora y minuto seleccionados
                            selectedHour = hourOfDay;
                            selectedMinute = minute;

                            //Damos formato a la hora para mostrarla en el TextView
                            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                            binding.TimeReminder.setText(selectedTime);
                        },
                        currentHour,
                        currentMinute,
                        true //True para formato de 24 horas, False para AM/PM
                );
                timePickerDialog.show();
            }
        });
    }

    //Configura el OnClickListener para el botón de guardar. (LÓGICA DE PERMISOS AÑADIDA)
    private void setupSaveButton() {
        binding.ButtonSave.setOnClickListener(v -> {
            String title = binding.ActNameEdit.getText().toString().trim();
            String note = binding.ActNoteEdit.getText().toString().trim();
            boolean useNotification = binding.NotificationSwitch.isChecked();

            //(Validaciones existentes sin cambios)
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

            // --- NUEVA LÓGICA DE COMPROBACIÓN DE PERMISOS ---
            if (useNotification) {
                // Primero, comprueba si tiene el permiso.
                if (canScheduleExactAlarms()) {
                    // Si tiene permiso, procede a programar la notificación.
                    scheduleNotification(title, note);
                } else {
                    // Si no tiene permiso, lo solicita y detiene el proceso de guardado.
                    requestExactAlarmPermission();
                    return; // IMPORTANTE: Detiene la ejecución para no llamar a finish()
                }
            }
            // --- FIN DE LA NUEVA LÓGICA ---

            //(Código para devolver el resultado no cambia)
            Intent resultIntent = new Intent();
            resultIntent.putExtra("EXTRA_TASK_TITLE", title);
            resultIntent.putExtra("EXTRA_TASK_NOTE", note);
            resultIntent.putExtra("EXTRA_TASK_HOUR", selectedHour);
            resultIntent.putExtra("EXTRA_TASK_MINUTE", selectedMinute);
            resultIntent.putExtra("EXTRA_USE_NOTIFICATION", useNotification);
            resultIntent.putExtra("EXTRA_SELECTED_DATE", selectedDate);

            setResult(RESULT_OK, resultIntent);

            Toast.makeText(AddReminderActivity.this, getString(R.string.TareaReminder) + title + getString(R.string.GuardadaReminder), Toast.LENGTH_SHORT).show();

            // Esta línea solo se ejecutará si no se necesita el permiso o si ya está concedido.
            finish();
        });
    }

    //NUEVO MÉTODO: Se encarga de programar la alarma para la notificación (sin cambios en la lógica interna).
    private void scheduleNotification(String title, String note) {
        // ... tu código existente está perfecto aquí ...
        // 1. Crea un Intent que apunta al NotificationReceiver.
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        intent.putExtra("EXTRA_TASK_TITLE", title);
        intent.putExtra("EXTRA_TASK_NOTE", note);

        // 2. Crea un PendingIntent que el sistema ejecutará en el futuro.
        // Se usa un ID único para evitar que las alarmas se sobreescriban.
        int pendingIntentId = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                pendingIntentId,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // 3. Obtiene el servicio de alarmas del sistema.
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // 4. Configura la fecha y hora exactas para la alarma usando los valores numéricos.
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, selectedHour, selectedMinute, 0);
        long triggerTime = calendar.getTimeInMillis();

        // 5. Programa la alarma para que se dispare exactamente a la hora y despierte el dispositivo.
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

        // Muestra un Toast de confirmación de la programación.
        Toast.makeText(this, "Recordatorio programado para " + selectedDate + " a las " + String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute), Toast.LENGTH_LONG).show();
    }
}
