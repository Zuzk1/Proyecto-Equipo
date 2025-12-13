package com.example.proyectoaula;

// Se importan las clases necesarias de Android
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Se importan clases para manejar listas
import java.util.ArrayList;
import java.util.List;

// Se implementa la interfaz para que la Activity pueda escuchar los clics de borrado del adaptador
public class AddReminderViewActivity extends AppCompatActivity implements ReminderAdapter.OnItemDeleteListener {

    // Se declaran las variables para los elementos de la vista
    private RecyclerView recyclerView; // La lista que mostrará los recordatorios
    private ReminderAdapter adapter; // El adaptador que conecta los datos con la lista
    private TextView textViewEmptyState; // El texto que se muestra si no hay nada
    private ReminderDao reminderDao; // El objeto para hablar con la base de datos de recordatorios

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Se conecta esta clase con su archivo de diseño XML
        setContentView(R.layout.activity_add_reminder_view);

        // Se buscan los elementos de la vista por su ID para poder usarlos
        recyclerView = findViewById(R.id.recyclerViewReminders);
        textViewEmptyState = findViewById(R.id.textViewEmptyState);

        // Se le dice al RecyclerView cómo debe mostrar los elementos, en este caso, como una lista vertical
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Se crea el adaptador que manejará los datos, iniciando con una lista vacía
        adapter = new ReminderAdapter(new ArrayList<>(), this);
        // Se le asigna el adaptador a nuestro RecyclerView
        recyclerView.setAdapter(adapter);

        // Se obtiene la instancia de la base de datos usando el método que ya creamos
        AppDatabase db = AppDatabase.getDatabase(this);
        // Se obtiene el DAO específico para los recordatorios desde la base de datos
        reminderDao = db.reminderDao();
    }

    // Este método se llama cada vez que la pantalla se vuelve visible para el usuario
    @Override
    protected void onResume() {
        super.onResume();
        // Se llama a este método para asegurarse de que la lista siempre muestre los datos más recientes
        loadReminders();
    }

    // Este método se encarga de cargar los recordatorios desde la base de datos
    private void loadReminders() {
        // Se ejecuta la consulta a la base de datos en un hilo secundario para no congelar la app
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Se le pide al DAO que traiga todos los recordatorios de la tabla
            final List<Reminder> reminders = reminderDao.getAll();

            // Una vez que tenemos los datos, volvemos al hilo principal para actualizar la vista
            runOnUiThread(() -> {
                // Se revisa si la lista de recordatorios vino vacía o nula
                if (reminders == null || reminders.isEmpty()) {
                    // Si está vacía, se esconde la lista y se muestra el mensaje de "No hay nada"
                    recyclerView.setVisibility(View.GONE);
                    textViewEmptyState.setVisibility(View.VISIBLE);
                } else {
                    // Si hay datos, se muestra la lista y se esconde el mensaje de "No hay nada"
                    recyclerView.setVisibility(View.VISIBLE);
                    textViewEmptyState.setVisibility(View.GONE);
                    // Se le pasan los nuevos datos al adaptador para que actualice lo que se ve en pantalla
                    adapter.updateData(reminders);
                }
            });
        });
    }

    // Este método se ejecuta cuando el usuario presiona el botón de borrar en un elemento de la lista
    @Override
    public void onDeleteClick(Reminder reminder) {
        // Se manda a borrar el recordatorio en un hilo secundario
        AppDatabase.databaseWriteExecutor.execute(() -> {
            reminderDao.delete(reminder);
            // Después de borrar, se le pide al hilo principal que vuelva a cargar la lista
            // para que el elemento borrado desaparezca de la pantalla
            runOnUiThread(this::loadReminders);
        });
    }
}
