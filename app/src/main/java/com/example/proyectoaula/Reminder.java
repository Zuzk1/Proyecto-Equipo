package com.example.proyectoaula;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders") // Define el nombre de la tabla en la base de datos
public class Reminder {

    @PrimaryKey(autoGenerate = true)
    public int id; // Columna para el ID único, se autogenera

    public String titulo; // Columna para guardar el título del recordatorio

    // ========= INICIO DE LA MODIFICACIÓN =========
    public String descripcion; // Columna para guardar la descripción del recordatorio
    // ========= FIN DE LA MODIFICACIÓN =========

    public long timestamp; // Columna para guardar la fecha y hora en formato long

    // Puedes dejar este constructor vacío o añadir uno si lo necesitas
    public Reminder() {
    }
}
