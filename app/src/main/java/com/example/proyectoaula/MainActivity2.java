package com.example.proyectoaula;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2); // Esta línea es crucial

        // 1. Enlazar la vista del calendario
        calendarView = findViewById(R.id.Cal_PA);

        // 2. Configurar el listener para cuando el usuario selecciona una fecha
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Cuando el usuario toca un día, se ejecuta este código.
                // Los meses se cuentan desde 0 (enero=0, febrero=1...), así que sumamos 1 para que sea normal.
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                // 3. Llama a un método para mostrar el menú de opciones
                showOptionsDialog(selectedDate);
            }
        });
    }

    /**
     * Muestra un cuadro de diálogo con opciones para la fecha seleccionada.
     * @param date La fecha que el usuario seleccionó (en formato "dd/MM/yyyy").
     */
    private void showOptionsDialog(final String date) {
        // Opciones que aparecerán en el menú
        final CharSequence[] options = {"Añadir recordatorio", "Añadir actividad", "Cancelar"};

        // Construir el cuadro de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
        builder.setTitle("Opciones para " + date); // Título del diálogo

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                // Comprueba qué opción se seleccionó
                if (options[item].equals("Añadir recordatorio")) {
                    // Si se elige "Añadir recordatorio"...
                    Intent intent = new Intent(MainActivity2.this, AddReminderActivity.class);
                    // Añade la fecha seleccionada al Intent para que la otra actividad la reciba
                    intent.putExtra("SELECTED_DATE", date);
                    startActivity(intent);

                } else if (options[item].equals("Añadir actividad")) {
                    // Aquí pondrías la lógica para "Añadir actividad"
                    // Podría ser otra actividad diferente si quieres.
                    // Toast.makeText(MainActivity2.this, "Función no implementada", Toast.LENGTH_SHORT).show();

                } else if (options[item].equals("Cancelar")) {
                    // Si se elige "Cancelar", simplemente se cierra el diálogo
                    dialog.dismiss();
                }
            }
        });

        // Mostrar el cuadro de diálogo
        builder.show();
    }
}
