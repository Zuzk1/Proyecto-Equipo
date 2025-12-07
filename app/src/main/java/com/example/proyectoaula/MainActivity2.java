package com.example.proyectoaula;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.proyectoaula.Task;

public class MainActivity2 extends AppCompatActivity {

    private CalendarView calendarVW;

    // Launcher para iniciar AddReminderActivity y esperar un resultado.
    private ActivityResultLauncher<Intent> addReminderLauncher;

    // El HashMap ahora usa nuestra clase Task.
    private Map<String, List<Task>> tasksByDate = new HashMap<>();

    // ¡NUEVO! Para recordar la última celda que pintamos.
    private TextView lastSelectedDayView = null;
    private int originalTextColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2); // Esta línea es crucial

        // Se configura el launcher.
        setupActivityLauncher();

        calendarVW = findViewById(R.id.CalendarioPro);

        //Cuando se Selecciona una Fecha
        calendarVW.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //Cuando el usuario toca un día en el Calendario, se ejecuta este código.
                //Los meses se cuentan desde 0 , Entonces le que sumamos 1 para que sea asi bien.
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                // ¡¡¡EL HACKEO FINAL!!!
                // 1. Limpiamos la celda que pintamos antes (si es que hay una).
                if (lastSelectedDayView != null) {
                    lastSelectedDayView.setBackgroundColor(Color.TRANSPARENT);
                    lastSelectedDayView.setTextColor(originalTextColor);
                }

                // 2. Buscamos la celda del día que acabamos de tocar.
                TextView dayView = findDayView(dayOfMonth);

                if (dayView != null) {
                    // Guardamos el color de texto original antes de hacer cambios.
                    if (lastSelectedDayView == null) {
                        originalTextColor = dayView.getCurrentTextColor();
                    }

                    // 3. Checamos si el NUEVO día seleccionado tiene tareas.
                    if (tasksByDate.containsKey(selectedDate) && !tasksByDate.get(selectedDate).isEmpty()) {
                        // ¡SÍ TIENE! Pintamos el fondo de AZUL y el texto de BLANCO.
                        dayView.setBackgroundColor(Color.BLUE);
                        dayView.setTextColor(Color.WHITE);
                    } else {
                        // NO TIENE. Lo dejamos con su color normal.
                        dayView.setBackgroundColor(Color.TRANSPARENT);
                        dayView.setTextColor(originalTextColor);
                    }
                    // Guardamos la referencia a esta celda para poder limpiarla después.
                    lastSelectedDayView = dayView;
                }

                //Se llama al metodo para el menu de opciones
                showOptionsDialog(selectedDate);
            }
        });
    }

    // ¡NUEVO MÉTODO! Para encontrar la vista de la celda de un día.
    private TextView findDayView(int dayOfMonth) {
        try {
            ViewGroup vg = (ViewGroup) calendarVW.getChildAt(0);
            View monthView = vg.getChildAt(0);
            if (monthView instanceof ViewGroup) {
                ViewGroup monthVg = (ViewGroup) monthView;
                for (int i = 0; i < monthVg.getChildCount(); i++) {
                    View dayView = monthVg.getChildAt(i);
                    if (dayView instanceof TextView) {
                        TextView dayTextView = (TextView) dayView;
                        if (dayTextView.getText().toString().equals(String.valueOf(dayOfMonth))) {
                            // ¡Lo encontramos!
                            return dayTextView;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // El hackeo puede fallar en algunos dispositivos, pero la app no se romperá.
            return null;
        }
        return null;
    }


    // El método ahora extrae todos los datos de la tarea.
    private void setupActivityLauncher() {
        addReminderLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Este código se ejecuta cuando 'AddReminderActivity' termina.
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        // Desempaquetamos TODOS los datos
                        String date = data.getStringExtra("EXTRA_SELECTED_DATE");
                        String title = data.getStringExtra("EXTRA_TASK_TITLE");
                        String note = data.getStringExtra("EXTRA_TASK_NOTE");
                        int hour = data.getIntExtra("EXTRA_TASK_HOUR", -1);
                        int minute = data.getIntExtra("EXTRA_TASK_MINUTE", -1);
                        boolean useNotification = data.getBooleanExtra("EXTRA_USE_NOTIFICATION", false);

                        if (date != null && title != null) {
                            // Creamos un nuevo objeto 'Task'
                            Task newTask = new Task(title, note, hour, minute, useNotification);

                            // Guardamos el objeto 'Task' completo en nuestro mapa
                            if (!tasksByDate.containsKey(date)) {
                                tasksByDate.put(date, new ArrayList<>());
                            }
                            tasksByDate.get(date).add(newTask);

                            // Mostramos una confirmación
                            Toast.makeText(this, "Tarea '" + title + "' guardada para el " + date, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    //Nos muestra un cmo cuadrito de Dialogo para la fecha que elijas
    private void showOptionsDialog(final String date) {
        // ... (Este método se queda igual, no necesita cambios)
        final CharSequence[] options;
        if (tasksByDate.containsKey(date) && !tasksByDate.get(date).isEmpty()) {
            options = new CharSequence[]{"Ver Tareas", "Añadir Nueva Tarea", getString(R.string.CancelarMain2)};
        } else {
            options = new CharSequence[]{getString(R.string.ActividadesPendientesMain2), getString(R.string.CancelarMain2)};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
        builder.setTitle(getString(R.string.OpcionesParaElMain2) + " " + date);

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                String selectedOption = options[item].toString();

                if (selectedOption.equals(getString(R.string.ActividadesPendientesMain2)) || selectedOption.equals("Añadir Nueva Tarea")) {
                    Intent intent = new Intent(MainActivity2.this, AddReminderActivity.class);
                    intent.putExtra("SELECTED_DATE", date);
                    addReminderLauncher.launch(intent);

                } else if (selectedOption.equals("Ver Tareas")) {
                    showTasksForDate(date);
                } else if (selectedOption.equals(getString(R.string.CancelarMain2))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    // Este método ahora muestra la lista de tareas con sus títulos y notas.
    private void showTasksForDate(String date) {
        // ... (Este método se queda igual, no necesita cambios)
        List<Task> tasks = tasksByDate.get(date);
        if (tasks == null || tasks.isEmpty()) {
            Toast.makeText(this, "No hay tareas para esta fecha.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder tasksText = new StringBuilder();
        for (Task task : tasks) {
            tasksText.append("• Título: ").append(task.getTitle()).append("\n");
            if (task.getNote() != null && !task.getNote().isEmpty()) {
                tasksText.append("   Nota: ").append(task.getNote()).append("\n");
            }
            tasksText.append("   Hora: ").append(task.getFormattedTime()).append("\n\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Tareas para el " + date)
                .setMessage(tasksText.toString())
                .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
