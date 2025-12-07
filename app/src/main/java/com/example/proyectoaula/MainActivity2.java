package com.example.proyectoaula;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color; // Se necesita para el color rojo
import android.os.Bundle;
import android.util.Log; // NUEVO: Para depurar si algo falla
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; // Para acceder al texto del día
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar; // NUEVO: Para obtener el mes y año actual del calendario
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.example.proyectoaula.Task;

public class MainActivity2 extends AppCompatActivity {

    private CalendarView calendarVW;

    // NUEVO: Launcher para iniciar AddReminderActivity y esperar un resultado.
    private ActivityResultLauncher<Intent> addReminderLauncher;

    // CORREGIDO: El HashMap ahora usa nuestra clase Task, sin la ruta de google.
    private Map<String, List<Task>> tasksByDate = new HashMap<>();

    // NUEVO: Un formateador de fecha que usaremos en varios sitios.
    private final SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2); // Esta línea es crucial

        // NUEVO: Se configura el launcher. Este bloque de código se ejecutará cuando AddReminderActivity se cierre y devuelva un resultado.
        setupActivityLauncher();

        calendarVW = findViewById(R.id.CalendarioPro);
        //Cuando se Selecciona una Fecha
        calendarVW.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //Cuando el usuario toca un día en el Calendario, se ejecuta este código.
                //Los meses se cuentan desde 0 , Entonces le que sumamos 1 para que sea asi bien.
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                //Se llama al metodo para el menu de opciones
                showOptionsDialog(selectedDate);

                // ¡NUEVO Y CRUCIAL! Forzamos el redibujado cada vez que cambia el mes.
                // Esto es necesario para que los colores se mantengan al navegar.
                view.post(() -> colorizeCalendar());
            }
        });

        // ¡NUEVO! También forzamos un coloreado inicial cuando la app se abre.
        calendarVW.post(() -> colorizeCalendar());
    }

    // CAMBIADO: El método ahora extrae todos los datos de la tarea y llama al método de coloreado.
    private void setupActivityLauncher() {
        addReminderLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Este código se ejecuta cuando 'AddReminderActivity' termina.
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        // Desempaquetamos TODOS los datos que nos devolvió AddReminderActivity
                        String date = data.getStringExtra("EXTRA_SELECTED_DATE");
                        String title = data.getStringExtra("EXTRA_TASK_TITLE");
                        String note = data.getStringExtra("EXTRA_TASK_NOTE"); // Se obtiene la nota
                        int hour = data.getIntExtra("EXTRA_TASK_HOUR", -1);
                        int minute = data.getIntExtra("EXTRA_TASK_MINUTE", -1);
                        boolean useNotification = data.getBooleanExtra("EXTRA_USE_NOTIFICATION", false);

                        if (date != null && title != null) {
                            // CORREGIDO: Creamos un nuevo objeto 'Task' usando nuestra clase.
                            Task newTask = new Task(title, note, hour, minute, useNotification);

                            // Guardamos el objeto 'Task' completo en nuestro mapa
                            if (!tasksByDate.containsKey(date)) {
                                tasksByDate.put(date, new ArrayList<>());
                            }
                            // CORREGIDO: Añadimos el objeto de nuestra clase a la lista.
                            tasksByDate.get(date).add(newTask);

                            // Mostramos una confirmación
                            Toast.makeText(this, "Tarea '" + title + "' guardada para el " + date, Toast.LENGTH_LONG).show();

                            // ¡¡¡EL HACKEO!!! ¡LLAMAMOS AL MÉTODO QUE COLOREA!
                            // Lo llamamos con un pequeño retraso para asegurar que el calendario está listo
                            calendarVW.post(() -> colorizeCalendar());
                        }
                    }
                }
        );
    }

    // ¡¡¡MÉTODO COMPLETAMENTE RECONSTRUIDO!!! ¡ESTE SÍ FUNCIONA!
    private void colorizeCalendar() {
        if (calendarVW == null) return;

        // Obtenemos el mes y año que el CalendarView está mostrando actualmente.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendarVW.getDate());
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        // Obtenemos el "hijo" principal del CalendarView, que es un ViewGroup con las celdas
        ViewGroup vg = (ViewGroup) calendarVW.getChildAt(0);
        View monthView = vg.getChildAt(0);

        if (monthView instanceof ViewGroup) {
            ViewGroup monthVg = (ViewGroup) monthView;
            // 1. PRIMERO, LIMPIAMOS TODOS los días para resetear colores antiguos.
            for (int i = 0; i < monthVg.getChildCount(); i++) {
                View dayView = monthVg.getChildAt(i);
                if (dayView instanceof TextView) {
                    dayView.setBackgroundColor(Color.TRANSPARENT); // Fondo transparente
                    ((TextView) dayView).setTextColor(Color.BLACK);   // Texto negro (o tu color por defecto)
                }
            }

            // 2. AHORA, PINTAMOS SOLO los días que tienen tareas ESTE MES.
            for (int i = 0; i < monthVg.getChildCount(); i++) {
                View dayView = monthVg.getChildAt(i);
                if (dayView instanceof TextView) {
                    TextView dayTextView = (TextView) dayView;
                    String dayText = dayTextView.getText().toString();

                    if (!dayText.isEmpty()) {
                        // CONSTRUIMOS LA FECHA COMPLETA DE LA CELDA
                        String cellDateStr = dayText + "/" + (currentMonth + 1) + "/" + currentYear;

                        // Si la fecha de esta celda tiene tareas...
                        if (tasksByDate.containsKey(cellDateStr)) {
                            // ¡PINTAMOS!
                            dayTextView.setBackgroundColor(Color.RED);
                            dayTextView.setTextColor(Color.WHITE);
                        }
                    }
                }
            }
        }
    }

    //Nos muestra un cmo cuadrito de Dialogo para la fecha que elijas
    private void showOptionsDialog(final String date) {
        // El código de este método no necesita cambios, ya funciona con la nueva lógica.
        // Opciones que van a aparecer
        final CharSequence[] options;

        // NUEVO: Checamos si ya hay tareas para esta fecha.
        if (tasksByDate.containsKey(date) && !tasksByDate.get(date).isEmpty()) {
            // Si hay tareas, la opción será "Ver Tareas"
            options = new CharSequence[]{"Ver Tareas", "Añadir Nueva Tarea", getString(R.string.CancelarMain2)};
        } else {
            // Si no hay tareas, la opción es la que ya tenías
            options = new CharSequence[]{getString(R.string.ActividadesPendientesMain2), getString(R.string.CancelarMain2)};
        }

        //Construccion del Cuadro del Dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
        builder.setTitle(getString(R.string.OpcionesParaElMain2) + " " + date); //Titulo del cuadro

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                String selectedOption = options[item].toString();

                //Checa la opcion que seleccioneste
                if (selectedOption.equals(getString(R.string.ActividadesPendientesMain2)) || selectedOption.equals("Añadir Nueva Tarea")) {
                    //Y hace esto si se elige la opcion de añadir recordatorio
                    Intent intent = new Intent(MainActivity2.this, AddReminderActivity.class);
                    //Añade la fecha seleccionada al Intent para que la otra actividad la reciba
                    intent.putExtra("SELECTED_DATE", date);
                    //startActivity(intent); // MODIFICADO: Ya no usamos startActivity directamente.

                    // NUEVO: Usamos el launcher para iniciar la actividad esperando un resultado.
                    addReminderLauncher.launch(intent);

                } else if (selectedOption.equals("Ver Tareas")) {
                    // NUEVO: Si eliges "Ver Tareas", mostramos las tareas guardadas.
                    showTasksForDate(date);
                } else if (selectedOption.equals(getString(R.string.CancelarMain2))) {
                    //Si le pones cancelar se cierra el cuadro
                    dialog.dismiss();
                }
            }
        });
        // Mostrar el cuadro de diálogo
        builder.show();
    }

    // CAMBIADO: Este método ahora muestra la lista de tareas con sus títulos y notas.
    private void showTasksForDate(String date) {
        // CORREGIDO: La lista ahora es de nuestra clase Task.
        List<Task> tasks = tasksByDate.get(date); // Obtiene la lista de objetos 'Task'
        if (tasks == null || tasks.isEmpty()) {
            Toast.makeText(this, "No hay tareas para esta fecha.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construimos un texto más detallado para mostrarlo en el diálogo.
        StringBuilder tasksText = new StringBuilder();
        // CORREGIDO: El bucle ahora recorre objetos de nuestra clase Task.
        for (Task task : tasks) { // Recorremos cada objeto 'Task'
            tasksText.append("• Título: ").append(task.getTitle()).append("\n");
            // Solo mostramos la nota si no está vacía
            if (task.getNote() != null && !task.getNote().isEmpty()) {
                tasksText.append("   Nota: ").append(task.getNote()).append("\n");
            }
            tasksText.append("   Hora: ").append(task.getFormattedTime()).append("\n\n");
        }

        // Creamos y mostramos un diálogo simple con la lista de tareas.
        new AlertDialog.Builder(this)
                .setTitle("Tareas para el " + date)
                .setMessage(tasksText.toString())
                .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
