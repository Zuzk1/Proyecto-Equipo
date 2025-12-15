package com.example.proyectoaula;

// Importaciones (Añadí las que faltaban para el diálogo)
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Se eliminó la línea "final Dialog dialog = new Dialog(this);" de aquí

    private DrawerLayout drawerLayout;
    private MaterialCalendarView calendarView;
    private ActivityResultLauncher<Intent> addReminderLauncher;
    private AppDatabase db;
    private ReminderDao reminderDao;

    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, R.string.gracias_por_permitir_las_notificaciones_Main2, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.no_podremos_mostrarte_recordatorios_si_no_permites_las_notificaciones_Main2, Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        db = AppDatabase.getDatabase(this);
        reminderDao = db.reminderDao();

        setupToolbarAndNavigation();
        calendarView = findViewById(R.id.calendarView);
        setupCalendar();
        setupActivityLauncher();
        askNotificationPermission();
        checkAndRequestAutoStartPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndDecorateEvents();
    }

    private void setupToolbarAndNavigation() {
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupCalendar() {
        calendarView.setSelectedDate(CalendarDay.today());
        calendarView.setWeekDayTextAppearance(R.style.CustomWeekDayText);

        calendarView.setTitleFormatter(day -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            LocalDate localDate = day.getDate();
            Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Date utilDate = new Date(instant.toEpochMilli());
            String title = sdf.format(utilDate);
            return Character.toUpperCase(title.charAt(0)) + title.substring(1);
        });

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (selected) {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    LocalDate selectedDate = date.getDate();
                    long startMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                    long endMillis = selectedDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

                    final List<Reminder> reminders = reminderDao.getRemindersBetween(startMillis, endMillis);

                    runOnUiThread(() -> {
                        if (reminders.isEmpty()) {
                            Toast.makeText(MainActivity2.this, R.string.calendar_no_activities, Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(MainActivity2.this, DayDetailsActivity.class);
                            intent.putExtra(DayDetailsActivity.EXTRA_TIMESTAMP, startMillis);
                            startActivity(intent);
                        }
                    });
                });
            }
        });
    }

    private void loadAndDecorateEvents() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            final List<Reminder> allReminders = reminderDao.getAll();
            final HashSet<CalendarDay> eventDays = new HashSet<>();
            if (allReminders != null) {
                for (Reminder reminder : allReminders) {
                    long timestamp = reminder.timestamp;
                    if (timestamp > 0) {
                        Instant instant = Instant.ofEpochMilli(timestamp);
                        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                        eventDays.add(CalendarDay.from(localDate));
                    }
                }
            }
            runOnUiThread(() -> {
                calendarView.removeDecorators();
                if (!eventDays.isEmpty()) {
                    int eventColor = ContextCompat.getColor(MainActivity2.this, R.color.event_day_color);
                    calendarView.addDecorator(new EventDecorator(eventColor, eventDays));
                }
                calendarView.invalidateDecorators();
            });
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_opcion1) {
            // Opción 1: Lanza la actividad para añadir un nuevo recordatorio.
            Intent intent = new Intent(this, AddReminderActivity.class);
            addReminderLauncher.launch(intent);

        } else if (itemId == R.id.nav_opcion2) {
            // Opción 2: Lanza la actividad para añadir recordatorios
            Intent intent = new Intent(this, AddReminderViewActivity.class);
            addReminderLauncher.launch(intent);

        } else if (itemId == R.id.nav_settings) {
            // Inicia la actividad que muestra la imagen del calendario.
            Intent intent = new Intent(this, calendar_academic_images.class);
            startActivity(intent);

        } else if (itemId == R.id.nav_about) {
            // En lugar de un Intent, llamamos a la función que muestra el diálogo.
            showAcercaDeDialog();
        }

        // Cierra el menú lateral después de seleccionar una opción.
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    // ----- FUNCIÓN AÑADIDA -----
    private void showAcercaDeDialog() {
        // 1. Crea un diálogo
        final Dialog dialog = new Dialog(this);

        // 2. Opcional: Ponle un título
        dialog.setTitle("Acerca de Erro Task");

        // 3. "Infla" (carga) tu layout XML del "Acerca de"
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_acerca_de, null);

        // 4. Establece la vista como el contenido del diálogo
        dialog.setContentView(dialogView);

        // 5. Muestra el diálogo
        dialog.show();
    }
    // ------------------------------------

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupActivityLauncher() {
        addReminderLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(this, R.string.tarea_guardada_con_exito_Main2, Toast.LENGTH_LONG).show();
                        loadAndDecorateEvents();
                    }
                }
        );
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void checkAndRequestAutoStartPermission() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
        if (isFirstRun && Build.BRAND.toLowerCase().contains("xiaomi")) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(R.string.permiso_adicional_xiaomi_Main2)
                    .setMessage(R.string.inicio_auto_Main2)
                    .setPositiveButton(R.string.ir_a_ajustes_Main2, (dialog, which) -> {
                        try {
                            startActivity(getAutoStartPermissionIntent(MainActivity2.this));
                        } catch (Exception e) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.ahora_no_notis_Main2, null)
                    .show();
            prefs.edit().putBoolean("isFirstRun", false).apply();
        }
    }

    private Intent getAutoStartPermissionIntent(final Context context) {
        String build_info = Build.BRAND.toLowerCase();
        if (build_info.contains("xiaomi")) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            return intent;
        }
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        return intent;
    }
}

class EventDecorator implements DayViewDecorator {
    private final int color;
    private final HashSet<CalendarDay> dates;

    public EventDecorator(int color, Collection<CalendarDay> dates) {
        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(7, color));
    }
}
