package com.example.proyectoaula;

// Importaciones necesarias
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
import androidx.room.Room;

import com.google.android.material.navigation.NavigationView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

// Importaciones requeridas para la conversión de fechas con ThreeTenABP
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;

public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private MaterialCalendarView calendarView;
    private ActivityResultLauncher<Intent> addReminderLauncher;
    private AppDatabase db;

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

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "task-database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();

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

        // Formatea el título para que el mes empiece con mayúscula
        calendarView.setTitleFormatter(day -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            LocalDate localDate = day.getDate();
            Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Date utilDate = new Date(instant.toEpochMilli());
            String title = sdf.format(utilDate);
            return Character.toUpperCase(title.charAt(0)) + title.substring(1);
        });

        // Listener para cuando el usuario selecciona una fecha
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (selected) {
                List<Task> tasksForDay = getTasksForDate(date);
                if (tasksForDay.isEmpty()) {
                    Toast.makeText(this, "No hay actividades para este día.", Toast.LENGTH_SHORT).show();
                } else {
                    showTasksDialog(tasksForDay, date);
                }
            }
        });
    }

    private void loadAndDecorateEvents() {
        List<Task> allTasks = db.taskDao().getAll();
        HashSet<CalendarDay> eventDays = new HashSet<>();
        for (Task task : allTasks) {
            long timestamp = task.getTimestamp();
            if (timestamp > 0) {
                Instant instant = Instant.ofEpochMilli(timestamp);
                LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                eventDays.add(CalendarDay.from(localDate));
            }
        }
        calendarView.removeDecorators();
        if (!eventDays.isEmpty()) {
            int eventColor = ContextCompat.getColor(this, R.color.event_day_color);
            calendarView.addDecorator(new EventDecorator(eventColor, eventDays));
        }
        calendarView.invalidateDecorators();
    }

    // ===== INICIO DE LA CORRECCIÓN =====
    private void showTasksDialog(List<Task> tasks, CalendarDay date) {
        StringBuilder tasksText = new StringBuilder();
        for (Task task : tasks) {
            tasksText.append("• ").append(task.getTitle()).append("\n");
        }
        // Para mostrar al usuario, el mes de CalendarDay (que empieza en 1) es correcto.
        String dialogTitle = String.format(Locale.getDefault(), "Actividades del %d/%d", date.getDay(), date.getMonth());

        new AlertDialog.Builder(this)
                .setTitle(dialogTitle)
                .setMessage(tasksText.toString())
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private List<Task> getTasksForDate(CalendarDay date) {
        // Esta es la forma más simple y correcta
        Calendar startOfDay = Calendar.getInstance();
        // Usamos los datos de 'date' y RESTAMOS 1 AL MES para que java.util.Calendar lo entienda.
        startOfDay.set(date.getYear(), date.getMonth() - 1, date.getDay(), 0, 0, 0);
        startOfDay.set(Calendar.MILLISECOND, 0);

        Calendar endOfDay = (Calendar) startOfDay.clone();
        endOfDay.add(Calendar.DAY_OF_MONTH, 1);

        return db.taskDao().getTasksBetween(startOfDay.getTimeInMillis(), endOfDay.getTimeInMillis());
    }
    // ===== FIN DE LA CORRECCIÓN =====

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_opcion1) {
            Intent intent = new Intent(MainActivity2.this, AddReminderActivity.class);
            addReminderLauncher.launch(intent);
        } else if (itemId == R.id.nav_opcion2) {
            Intent intent = new Intent(MainActivity2.this, AddReminderViewActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_settings) {
            Toast.makeText(this, R.string.abriendo_ajustes_Main2, Toast.LENGTH_SHORT).show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

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
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void checkAndRequestAutoStartPermission() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
        if (isFirstRun && Build.BRAND.toLowerCase().contains("xiaomi")) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permiso_adicional_xiaomi_Main2)
                    .setMessage(R.string.inicio_auto_Main2)
                    .setPositiveButton(R.string.ir_a_ajustes_Main2, (dialog, which) -> {
                        try {
                            startActivity(getAutoStartPermissionIntent(MainActivity2.this));
                        } catch (Exception e) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                            e.printStackTrace();
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
