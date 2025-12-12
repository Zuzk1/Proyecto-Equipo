// Indica el paquete de tu aplicación
package com.example.proyectoaula;

// Imports que ya tenías
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

// Imports para la barra lateral
import android.view.MenuItem;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

// Imports de tus clases y utilidades
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.proyectoaula.Task;

//Implementa la interfaz para que la clase pueda "escuchar" los clics del menú
public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CalendarView calendarVW;
    private ActivityResultLauncher<Intent> addReminderLauncher;
    private Map<String, List<Task>> tasksByDate = new HashMap<>();
    private TextView lastSelectedDayView = null;
    private int originalTextColor;

    //Variable para la barra esa
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Codigo para la barra esa de la esquina

        //Busca la Toolbar en tu layout y la establece como la barra de acción principal
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        //Busca el DrawerLayout (el contenedor raíz)
        drawerLayout = findViewById(R.id.drawer_layout);

        //Busca el NavigationView (el menú) y le dice que esta clase manejará los clics
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Crea el "botón de de las 3 o 4 rayitas" que conecta la Toolbar con el DrawerLayout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,  // Texto para accesibilidad (abrir)
                R.string.navigation_drawer_close // Texto para accesibilidad (cerrar)
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState(); // Sincroniza el ícono para que aparezca

        // Configura el launcher
        setupActivityLauncher();

        calendarVW = findViewById(R.id.CalendarioPro);

        //Cuando se Selecciona una Fecha
        calendarVW.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //Cuando el usuario toca un día en el Calendario, se ejecuta este código.
                //Los meses se cuentan desde 0 , Entonces le que sumamos 1 para que sea asi bien.
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                //Limpiamos la celda que pintamos antes
                if (lastSelectedDayView != null) {
                    lastSelectedDayView.setBackgroundColor(Color.TRANSPARENT);
                    lastSelectedDayView.setTextColor(originalTextColor);
                }

                //Buscamos la celda del día que acabamos de tocar.
                TextView dayView = findDayView(dayOfMonth);

                if (dayView != null) {
                    //Se guarda el color del texto
                    if (lastSelectedDayView == null) {
                        originalTextColor = dayView.getCurrentTextColor();
                    }

                    //Se checa si el dia tiene tareas
                    if (tasksByDate.containsKey(selectedDate) && !tasksByDate.get(selectedDate).isEmpty()) {
                        // Color Azul
                        dayView.setBackgroundColor(Color.BLUE);
                        dayView.setTextColor(Color.WHITE);
                    } else {
                        //Color Normal
                        dayView.setBackgroundColor(Color.TRANSPARENT);
                        dayView.setTextColor(originalTextColor);
                    }
                    //Guardamos la referencia a esta celda para poder limpiarla después.
                    lastSelectedDayView = dayView;
                }
                //Se llama al metodo para el menu de opciones
                showOptionsDialog(selectedDate);
            }
        });
    }

    // PASO 2: Gestiona el botón "Atrás" para cerrar primero el menú si está abierto
    @Override
    public void onBackPressed() {
        // Comprueba si el menú lateral está abierto
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Si está abierto, lo cierra
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Si está cerrado, ejecuta la acción normal (salir de la actividad)
            super.onBackPressed();
        }
    }

    // PASO 3: Añade el método que se ejecuta cuando se hace clic en una opción del menú
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_opcion1) {
            // Lógica para cuando se presiona la "Opción 1"
            Toast.makeText(this, "Has seleccionado la Opción 1", Toast.LENGTH_SHORT).show();

        } else if (itemId == R.id.nav_opcion2) {
            // Lógica para cuando se presiona la "Opción 2"
            Toast.makeText(this, "Has seleccionado la Opción 2", Toast.LENGTH_SHORT).show();

        } else if (itemId == R.id.nav_settings) {
            // Lógica para la configuración
            Toast.makeText(this, "Abriendo Configuración...", Toast.LENGTH_SHORT).show();
        }

        // Cierra el menú lateral después de seleccionar una opción
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //Encontrar la celda de cada dia
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
                            return dayTextView;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //Si esto falla la app no se va acerrar asi cmo las otras
            return null;
        }
        return null;
    }


    //Metodo Extrae los datos de la Tarea
    private void setupActivityLauncher() {
        addReminderLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    //Este código se ejecuta cuando 'AddReminderActivity' termina.
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        //Se sacan los datos
                        String date = data.getStringExtra("EXTRA_SELECTED_DATE");
                        String title = data.getStringExtra("EXTRA_TASK_TITLE");
                        String note = data.getStringExtra("EXTRA_TASK_NOTE");
                        int hour = data.getIntExtra("EXTRA_TASK_HOUR", -1);
                        int minute = data.getIntExtra("EXTRA_TASK_MINUTE", -1);
                        boolean useNotification = data.getBooleanExtra("EXTRA_USE_NOTIFICATION", false);

                        if (date != null && title != null) {
                            Task newTask = new Task(title, note, hour, minute, useNotification);
                            if (!tasksByDate.containsKey(date)) {
                                tasksByDate.put(date, new ArrayList<>());
                            }
                            tasksByDate.get(date).add(newTask);
                            //Se Muestra una Confirmacion
                            Toast.makeText(this, "Tarea '" + title + "' guardada para el " + date, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    //Nos muestra un cmo cuadrito de Dialogo para la fecha que elijas
    private void showOptionsDialog(final String date) {
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

    //Metodo que muestra las Tareas para una fecha
    private void showTasksForDate(String date) {
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
