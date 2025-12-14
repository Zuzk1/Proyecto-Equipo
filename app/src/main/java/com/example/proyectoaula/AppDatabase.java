package com.example.proyectoaula;

// Se importan las clases necesarias de Android y Room
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Se usa la anotación @Database para decirle a Room que esta clase es la base de datos principal
// Se le indica qué entidades (tablas) va a manejar y la versión actual del esquema de la base de datos
@Database(entities = {Task.class, Reminder.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Se declara un método abstracto para que Room provea una instancia del DAO para las tareas (TaskDao)
    public abstract TaskDao taskDao();

    // Se declara un método abstracto para que Room provea una instancia del DAO para los recordatorios (ReminderDao)
    public abstract ReminderDao reminderDao();

    // Se crea una variable estática 'INSTANCE' para implementar el patrón Singleton
    // 'volatile' asegura que los cambios en esta variable sean visibles para todos los hilos
    private static volatile AppDatabase INSTANCE;
    // Se define un número fijo de hilos para el pool que manejará las operaciones de la base de datos
    private static final int NUMBER_OF_THREADS = 4;
    // Se crea un pool de hilos para ejecutar las operaciones de la base de datos en segundo plano
    // Esto evita que operaciones largas (escrituras, borrados) congelen la interfaz de usuario
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Se crea un método estático 'getDatabase' para obtener la instancia única de la base de datos de forma segura
    static AppDatabase getDatabase(final Context context) {
        // Se revisa si la instancia ya fue creada para no repetir el proceso
        if (INSTANCE == null) {
            // Se usa 'synchronized' para asegurar que solo un hilo a la vez pueda crear la instancia
            // Esto previene condiciones de carrera si varios hilos llaman a getDatabase() al mismo tiempo
            synchronized (AppDatabase.class) {
                // Se vuelve a revisar dentro del bloque por si otro hilo creó la instancia mientras este esperaba
                if (INSTANCE == null) {
                    // Se usa el constructor de Room ('Room.databaseBuilder') para crear la base de datos
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "task_database") // Se le asigna un nombre al archivo físico de la base de datos
                            // Define una estrategia de migración destructiva
                            // Si se aumenta la versión de la BD, Room borrará los datos y la creará de nuevo
                            .fallbackToDestructiveMigration()
                            .build(); // Se construye y finaliza la instancia de la base de datos
                }
            }
        }
        // Se devuelve la instancia única de la base de datos, ya sea la recién creada o la que ya existía
        return INSTANCE;
    }
}
