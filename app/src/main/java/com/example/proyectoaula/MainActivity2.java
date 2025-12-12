// Indica el paquete de tu aplicación
package com.example.proyectoaula;

// Imports necesarios
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

//Implementa la interfaz para que la clase pueda "escuchar" los clics del menú
public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Variable para la barra lateral
    private DrawerLayout drawerLayout;

    // Launcher para recibir el resultado de AddReminderActivity
    private ActivityResultLauncher<Intent> addReminderLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // --- Configuración de la Barra Lateral (Navigation Drawer) ---

        // 1. Busca la Toolbar y la establece como la barra de acción principal
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        // 2. Busca el DrawerLayout (el contenedor raíz)
        drawerLayout = findViewById(R.id.drawer_layout);

        // 3. Busca el NavigationView (el menú) y le dice que esta clase manejará los clics
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 4. Crea el "botón de hamburguesa" que conecta la Toolbar con el DrawerLayout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,  // Texto para accesibilidad (abrir)
                R.string.navigation_drawer_close // Texto para accesibilidad (cerrar)
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState(); // Sincroniza el ícono para que aparezca

        // --- Configuración del Launcher ---
        // Se configura el launcher para recibir la tarea desde AddReminderActivity
        setupActivityLauncher();

        // El CalendarView sigue en el layout, pero ya no tiene lógica asociada en esta clase.
        //CalendarView calendarVW = findViewById(R.id.CalendarioPro);
    }

    // Gestiona el botón "Atrás" para cerrar primero el menú si está abierto
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Este método se ejecuta cuando se hace clic en una opción del menú lateral.
     * Aquí es donde conectamos la nueva lógica.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        // Comprueba si el usuario ha pulsado "Añadir Nueva Tarea"
        if (itemId == R.id.nav_opcion1) {
            // Lanza la actividad para añadir un recordatorio.
            // Como no tenemos una fecha seleccionada, la otra actividad deberá manejarlo
            // (por ejemplo, mostrando un selector de fecha o usando la fecha actual).
            Intent intent = new Intent(MainActivity2.this, AddReminderActivity.class);
            addReminderLauncher.launch(intent);

        } else if (itemId == R.id.nav_opcion2) {
            // Lógica para la "Opción 2"
            Toast.makeText(this, "Has seleccionado la Opción 2", Toast.LENGTH_SHORT).show();

        } else if (itemId == R.id.nav_settings) {
            // Lógica para la "Configuración"
            Toast.makeText(this, "Abriendo Configuración...", Toast.LENGTH_SHORT).show();
        }

        // Cierra el menú lateral después de seleccionar una opción
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * Configura el ActivityResultLauncher para manejar la respuesta de AddReminderActivity.
     */
    private void setupActivityLauncher() {
        addReminderLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Este código se ejecuta cuando 'AddReminderActivity' termina y devuelve un resultado.
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String date = data.getStringExtra("EXTRA_SELECTED_DATE");
                        String title = data.getStringExtra("EXTRA_TASK_TITLE");

                        // Aquí puedes volver a añadir la lógica para guardar la tarea si lo necesitas,
                        // o simplemente mostrar una confirmación.
                        if (title != null) {
                            Toast.makeText(this, "Tarea '" + title + "' guardada para el " + date, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }
}
