package com.example.proyectoaula;

import android.app.TimePickerDialog;
import android.content.Intent; // NUEVO: Import necesario para devolver datos
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.proyectoaula.databinding.ActivityAddReminderBinding;

import java.util.Calendar;
import java.util.Locale;

public class AddReminderActivity extends AppCompatActivity {

    //Se usa ViewBinding para q la vista sea segura
    private ActivityAddReminderBinding binding;

    //Variables para almacenar la hora seleccionada
    private int selectedHour = -1;
    private int selectedMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflar el layout usando ViewBinding
        binding = ActivityAddReminderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
                    Toast.makeText(AddReminderActivity.this, "Las notificaciones están activadas", Toast.LENGTH_SHORT).show();
                } else {
                    //Lógica si estan apagadas
                    binding.TimeReminder.setEnabled(false); // Deshabilita el selector de hora
                    Toast.makeText(AddReminderActivity.this, "Las notificaciones están desactivadas", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Configuracion del Selector de Hora
    private void setupTimePicker() {
        binding.TimeReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddReminderActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                                //Se guarda la hora y minuto seleccionados
                                selectedHour = hourOfDay;
                                selectedMinute = minute;

                                //Damos formato a la hora para mostrarla en el TextView
                                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                                binding.TimeReminder.setText(selectedTime);
                            }
                        },
                        currentHour,
                        currentMinute,
                        true //True para formato de 24 horas, False para AM/PM
                );
                timePickerDialog.show();
            }
        });
    }

    //Configura el OnClickListener para el botón de guardar.
    private void setupSaveButton() {
        binding.ButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = binding.ActNameEdit.getText().toString().trim();
                String note = binding.ActNoteEdit.getText().toString().trim();
                boolean useNotification = binding.NotificationSwitch.isChecked();

                if (title.isEmpty()) {
                    binding.ActNameLayout.setError("El título no puede estar vacío");
                    return; //Detiene la ejecuccion del codigo
                } else {
                    binding.ActNameLayout.setError(null); //Limpiar errores y asi si hay
                }

                //Checa que se seleccionó una hora si las notificaciones están activadas
                if (useNotification && (selectedHour == -1 || selectedMinute == -1)) {
                    Toast.makeText(AddReminderActivity.this, "Por favor, selecciona una hora para la notificación", Toast.LENGTH_LONG).show();
                    return; // Detiene la ejecución
                }

                //Intent para el resultado
                Intent resultIntent = new Intent();

                //Guarda los datos antes de regresar
                resultIntent.putExtra("EXTRA_TASK_TITLE", title);
                resultIntent.putExtra("EXTRA_TASK_NOTE", note);
                resultIntent.putExtra("EXTRA_TASK_HOUR", selectedHour);
                resultIntent.putExtra("EXTRA_TASK_MINUTE", selectedMinute);
                resultIntent.putExtra("EXTRA_USE_NOTIFICATION", useNotification);

                //También debes devolver la fecha para la que se creó la tarea.
                //Asume que la actividad del calendario te pasó la fecha al iniciar.
                //Si la recibiste en el `onCreate`, la devuelves aquí.
                String dateFromCalendar = getIntent().getStringExtra("SELECTED_DATE");
                resultIntent.putExtra("EXTRA_SELECTED_DATE", dateFromCalendar);


                //Establece el resultado como OK y adjunta el Intent con los datos.
                setResult(RESULT_OK, resultIntent);

                //Muestra el Toast de confirmación (esto es opcional, pero útil)
                Toast.makeText(AddReminderActivity.this, "Tarea '" + title + "' guardada.", Toast.LENGTH_SHORT).show();

                //Finaliza la actividad.
                finish();
            }
        });
    }
}
