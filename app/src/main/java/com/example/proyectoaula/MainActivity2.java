package com.example.proyectoaula;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    private CalendarView calendarVW;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2); // Esta línea es crucial

        calendarVW = findViewById(R.id.CalendarioPro);
        //Cuando se Selecciona una Fecha
        calendarVW.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //Cuando el usuario toca un día en el Calendario, se ejecuta este código.
                //Los meses se cuentan desde 0 , Entonces le que sumamos 1 para que sea asi bien.
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                //Se llama al metodo para el menu de opciones
                showOptionsDialog(selectedDate);
            }
        });
    }
    //Nos muestra un cmo cuadrito de Dialogo para la fecha que elijas
    private void showOptionsDialog(final String date) {
        //Opciones que van a aparecer
        final CharSequence[] options = {getString(R.string.ActividadesPendientesMain2), getString(R.string.CancelarMain2)};

        //Construccion del Cuadro del Dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
        builder.setTitle(getString(R.string.OpcionesParaElMain2) + date);//Titulo del cuadro

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //Checa la opcion que seleccioneste
                if (options[item].equals(getString(R.string.ActividadesPendientesMain2))) {
                    //Y hace esto si se elige la opcion de añadir recordatorio
                    Intent intent = new Intent(MainActivity2.this, AddReminderActivity.class);
                    //Añade la fecha seleccionada al Intent para que la otra actividad la reciba
                    intent.putExtra("SELECTED_DATE", date);
                    startActivity(intent);

                } else if (options[item].equals(getString(R.string.CancelarMain2))) {
                    //Si le pones cancelar se cierra el cuadro
                    dialog.dismiss();
                }
            }
        });
        // Mostrar el cuadro de diálogo
        builder.show();


    }

}
