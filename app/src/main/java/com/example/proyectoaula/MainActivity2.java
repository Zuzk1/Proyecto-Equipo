package com.example.proyectoaula;

// Se importan las clases de Android que se van a usar
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

// Se implementa la interfaz para que esta clase pueda reaccionar a los clics del menú
public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Se declara la variable para el layout de la barra lateral
    private DrawerLayout drawerLayout;

    // Se declara un 'launcher' para iniciar otra pantalla y esperar un resultado de ella
    private ActivityResultLauncher<Intent> addReminderLauncher;

    // Se prepara otro 'launcher' para pedir permiso de notificaciones en versiones nuevas de Android
    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // Se revisa si el usuario concedió el permiso
                if (isGranted) {
                    Toast.makeText(this, R.string.gracias_por_permitir_las_notificaciones_Main2, Toast.LENGTH_SHORT).show();
                } else {
                    // Se le avisa al usuario que sin permiso no habrá notificaciones
                    Toast.makeText(this, R.string.no_podremos_mostrarte_recordatorios_si_no_permites_las_notificaciones_Main2, Toast.LENGTH_LONG).show();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Se conecta esta clase con su archivo de diseño XML
        setContentView(R.layout.activity_main2);

        // Se busca la barra de herramientas y se establece como la barra de acción principal
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        // Se busca el layout principal de la barra lateral
        drawerLayout = findViewById(R.id.drawer_layout);

        // Se busca la vista del menú de navegación
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Se le dice a la vista del menú que esta clase manejará los clics
        navigationView.setNavigationItemSelectedListener(this);

        // Se crea el botón de "hamburguesa" que abre y cierra el menú
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        // Se pone a escuchar el estado del menú para animar el ícono
        drawerLayout.addDrawerListener(toggle);
        // Se sincroniza el ícono para que aparezca correctamente
        toggle.syncState();

        // Se llama a la configuración del 'launcher' que espera resultados
        setupActivityLauncher();

        // Se llaman a las revisiones de permisos al iniciar la pantalla
        askNotificationPermission();
        checkAndRequestAutoStartPermission();
    }

    // Aquí se define qué hacer cuando el usuario presiona una opción del menú lateral
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Se obtiene el ID único del elemento que se presionó
        int itemId = item.getItemId();

        // Se revisa si el usuario presionó la opción de "Añadir Tarea"
        if (itemId == R.id.nav_opcion1) {
            // Se prepara un intent para abrir la pantalla de añadir recordatorio
            Intent intent = new Intent(MainActivity2.this, AddReminderActivity.class);
            // Se inicia la pantalla esperando un resultado
            addReminderLauncher.launch(intent);

            // Se revisa si el usuario presionó la opción de "Ver Actividades"
        } else if (itemId == R.id.nav_opcion2) {
            // Se prepara un intent para abrir la pantalla que muestra la lista
            Intent intent = new Intent(MainActivity2.this, AddReminderViewActivity.class);
            // Se inicia la nueva pantalla
            startActivity(intent);

            // Se revisa si el usuario presionó la opción de "Configuración"
        } else if (itemId == R.id.nav_settings) {
            // Se muestra un mensaje temporal
            Toast.makeText(this, R.string.abriendo_ajustes_Main2, Toast.LENGTH_SHORT).show();
        }

        // Se cierra el menú lateral después de que se presiona una opción
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    // Aquí se define el comportamiento del botón "atrás" del celular
    @Override
    public void onBackPressed() {
        // Se revisa si el menú lateral está abierto
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Si está abierto, se cierra el menú
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Si el menú está cerrado, se ejecuta la acción normal de "atrás"
            super.onBackPressed();
        }
    }

    // Se configura el 'launcher' que espera el resultado de la pantalla de añadir tarea
    private void setupActivityLauncher() {
        addReminderLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Se revisa si la pantalla anterior terminó con un resultado 'OK'
                    if (result.getResultCode() == RESULT_OK) {
                        // Se muestra un mensaje de confirmación
                        Toast.makeText(this, R.string.tarea_guardada_con_exito_Main2, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    // Se encarga de pedir permiso para notificaciones en Android 13 o superior
    private void askNotificationPermission() {
        // Se revisa si la versión de Android es TIRAMISU (API 33) o más nueva
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Se revisa si la app ya tiene permiso para enviar notificaciones
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                // Si no tiene permiso, se lanza la petición al usuario
                requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    // Se encarga de pedir un permiso especial que necesitan los celulares Xiaomi
    private void checkAndRequestAutoStartPermission() {
        // Se usa SharedPreferences para guardar si ya se mostró este diálogo antes
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

        // Se revisa si es la primera vez que corre la app Y si la marca del celular es "xiaomi"
        if (isFirstRun && Build.BRAND.toLowerCase().contains("xiaomi")) {
            // Se muestra un diálogo explicándole al usuario por qué necesita este permiso
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permiso_adicional_xiaomi_Main2)
                    .setMessage(R.string.inicio_auto_Main2)
                    .setPositiveButton(R.string.ir_a_ajustes_Main2, (dialog, which) -> {
                        try {
                            // Se intenta abrir la pantalla específica de "Inicio Automático" de MIUI
                            startActivity(getAutoStartPermissionIntent(MainActivity2.this));
                        } catch (Exception e) {
                            // Si falla, se abre la pantalla de ajustes generales como plan B
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                            e.printStackTrace();
                        }
                    })
                    .setNegativeButton(R.string.ahora_no_notis_Main2, null)
                    .show();
            // Se guarda que ya se mostró este diálogo para no volver a molestarlo
            prefs.edit().putBoolean("isFirstRun", false).apply();
        }
    }

    // Construye el Intent especial para la pantalla de "Inicio Automático" de Xiaomi
    private Intent getAutoStartPermissionIntent(final Context context) {
        String build_info = Build.BRAND.toLowerCase();
        // Se confirma que la marca es xiaomi
        if (build_info.contains("xiaomi")) {
            Intent intent = new Intent();
            // Se le indica el componente exacto de la pantalla de MIUI que queremos abrir
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            return intent;
        }
        // Si no es un Xiaomi, se regresa un Intent para abrir los ajustes generales de la app
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        return intent;
    }
}
