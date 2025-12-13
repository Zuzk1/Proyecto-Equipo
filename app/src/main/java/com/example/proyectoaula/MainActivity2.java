// Indica el paquete de tu aplicación
package com.example.proyectoaula;

// Imports necesarios
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager; // NUEVO: Para comprobar permisos
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat; // NUEVO: Para comprobar permisos
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

//Implementa la interfaz para que la clase pueda "escuchar" los clics del menú
public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Variable para la barra lateral
    private DrawerLayout drawerLayout;

    // Launcher para recibir el resultado de AddReminderActivity
    private ActivityResultLauncher<Intent> addReminderLauncher;

    // --- INICIO DE NUEVA VARIABLE ---
    // NUEVO: Launcher para la solicitud del permiso de notificaciones (necesario en Android 13+).
    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // El permiso fue concedido.
                    Toast.makeText(this, "¡Permiso de notificaciones concedido!", Toast.LENGTH_SHORT).show();
                } else {
                    // El permiso fue denegado. Informamos al usuario.
                    Toast.makeText(this, "No podremos mostrarte recordatorios si no permites las notificaciones.", Toast.LENGTH_LONG).show();
                }
            });
    // --- FIN DE NUEVA VARIABLE ---


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

        // --- INICIO DE LA NUEVA LÓGICA PARA PERMISOS ---
        askNotificationPermission(); // Pide permiso para notificaciones en Android 13+
        checkAndRequestAutoStartPermission(); // Pide permiso especial de "Inicio Automático" en Xiaomi
        // --- FIN DE LA NUEVA LÓGICA ---
    }

    // --- INICIO DE NUEVOS MÉTODOS PARA PERMISOS ---

    /**
     * NUEVO: Pide permiso para mostrar notificaciones. Obligatorio para Android 13 (API 33) y superior.
     */
    private void askNotificationPermission() {
        // Esta lógica solo se aplica a partir de Android 13 (TIRAMISU).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Comprueba si el permiso ya está concedido.
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Ya tenemos permiso, no hacemos nada.
            } else {
                // Si no lo tenemos, lanzamos la solicitud.
                requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }


    /**
     * Comprueba si es la primera vez que se ejecuta la app en un dispositivo Xiaomi
     * y, de ser así, pide al usuario que active el permiso de "Inicio Automático".
     */
    private void checkAndRequestAutoStartPermission() {
        // Usamos SharedPreferences para mostrar este diálogo solo una vez y no ser molestos.
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

        // Comprobamos si es la primera ejecución Y si la marca del teléfono es "xiaomi".
        if (isFirstRun && Build.BRAND.toLowerCase().contains("xiaomi")) {
            new AlertDialog.Builder(this)
                    .setTitle("Permiso Adicional (Xiaomi)")
                    .setMessage("Para asegurar que los recordatorios funcionen siempre, por favor, activa el 'Inicio Automático' para Erro Task en la siguiente pantalla.")
                    .setPositiveButton("Ir a Ajustes", (dialog, which) -> {
                        try {
                            // Intenta abrir la pantalla específica de "Inicio Automático" de MIUI.
                            startActivity(getAutoStartPermissionIntent(MainActivity2.this));
                        } catch (Exception e) {
                            // Si falla (por una versión de MIUI diferente), abre los ajustes generales como plan B.
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                            e.printStackTrace();
                        }
                    })
                    .setNegativeButton("Ahora no", null)
                    .show();

            // Marcamos 'isFirstRun' como falso para no volver a mostrar este diálogo.
            prefs.edit().putBoolean("isFirstRun", false).apply();
        }
    }

    /**
     * Crea un Intent específico para abrir la pantalla de "Inicio automático" en dispositivos Xiaomi (MIUI).
     * @param context El contexto de la aplicación.
     * @return El Intent para abrir los ajustes correspondientes.
     */
    private Intent getAutoStartPermissionIntent(final Context context) {
        String build_info = Build.BRAND.toLowerCase();
        if (build_info.contains("xiaomi")) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            return intent;
        }
        // Para otras marcas, no hay un intent estándar, así que se podría abrir los ajustes generales de la app.
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        return intent;
    }

    // --- FIN DE NUEVOS MÉTODOS ---


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
                    if (result.getResultCode() == RESULT_OK) {
                        // Ahora que usamos una base de datos, el intent de datos podría no traer nada,
                        // y está bien. Simplemente mostramos una confirmación genérica.
                        // Más adelante, podríamos actualizar el calendario aquí para mostrar la nueva tarea.
                        Toast.makeText(this, "Tarea guardada correctamente.", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
