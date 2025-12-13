package com.example.proyectoaula;

// Se importan las clases necesarias de Android y Room
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Se usa @Database para decirle a Room que esta clase es la base de datos principal
// Se le indica qué tablas (entities) va a manejar y la versión actual de la base de datos
@Database(entities = {Task.class, Reminder.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Se declara un método abstracto para que Room sepa cómo darnos el DAO de Task
    public abstract TaskDao taskDao();

    // Se declara otro método para que Room sepa cómo darnos el DAO de Reminder
    public abstract ReminderDao reminderDao();

    // Se crea una variable 'INSTANCE' para guardar una única instancia de la base de datos y no crearla a cada rato
    private static volatile AppDatabase INSTANCE;
    // Se define un número de hilos para trabajar con la base de datos en segundo plano
    private static final int NUMBER_OF_THREADS = 4;
    // Se crea un pool de hilos para ejecutar las operaciones de la base de datos sin congelar la app
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Se crea un método 'getDatabase' para obtener la instancia de la base de datos de forma segura
    static AppDatabase getDatabase(final Context context) {
        // Se revisa si ya existe una instancia para no crearla de nuevo
        if (INSTANCE == null) {
            // Se usa 'synchronized' para evitar que dos hilos creen la base de datos al mismo tiempo
            synchronized (AppDatabase.class) {
                // Se vuelve a revisar por si otro hilo la creó mientras este esperaba
                if (INSTANCE == null) {
                    // Se usa el 'Room.databaseBuilder' para construir la base de datos
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "task_database") // Se le da un nombre al archivo de la base de datos
                            // Se usa esto para que, si cambiamos la versión, Room borre la BD vieja y cree una nueva
                            .fallbackToDestructiveMigration()
                            .build(); // Se construye la instancia final de la base de datos
                }
            }
        }
        // Se regresa la instancia única de la base de datos, ya sea la recién creada o la que ya existía
        return INSTANCE;
    }
}
