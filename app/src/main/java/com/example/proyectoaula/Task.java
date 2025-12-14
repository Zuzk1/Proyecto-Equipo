package com.example.proyectoaula;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Calendar;
import java.util.GregorianCalendar;

// @Entity le dice a Room que esta clase es una tabla en la base de datos.
@Entity(tableName = "tasks")
public class Task {

    // @PrimaryKey hace que 'id' sea la clave única para cada tarea.
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "task_title")
    private String title;

    @ColumnInfo(name = "task_note")
    private String note;

    @ColumnInfo(name = "task_date")
    private String date;

    // --- NUEVO CAMPO ---
    // Guarda la fecha y hora como un número largo (timestamp) para consultas eficientes.
    @ColumnInfo(name = "timestamp")
    private long timestamp;

    @ColumnInfo(name = "task_hour")
    private int hour;

    @ColumnInfo(name = "task_minute")
    private int minute;

    @ColumnInfo(name = "use_notification")
    private boolean useNotification;

    // Constructor modificado para calcular y guardar el timestamp.
    public Task(String title, String note, String date, int hour, int minute, boolean useNotification) {
        this.title = title;
        this.note = note;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.useNotification = useNotification;
        this.timestamp = calculateTimestamp(date, hour, minute); // Calcula el timestamp al crear la tarea.
    }

    // --- GETTERS Y SETTERS (Room los necesita) ---
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public long getTimestamp() { return timestamp; } // Getter para el nuevo campo.
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; } // Setter para el nuevo campo.
    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }
    public int getMinute() { return minute; }
    public void setMinute(int minute) { this.minute = minute; }
    public boolean isUseNotification() { return useNotification; }
    public void setUseNotification(boolean useNotification) { this.useNotification = useNotification; }

    public String getFormattedTime() {
        if (hour == -1 || minute == -1) { return "Sin hora"; }
        return String.format("%02d:%02d", hour, minute);
    }

    // Método privado para convertir la fecha y hora a un timestamp.
    private long calculateTimestamp(String dateStr, int hour, int minute) {
        try {
            String[] parts = dateStr.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1; // El mes en Calendar empieza en 0.
            int year = Integer.parseInt(parts[2]);
            Calendar calendar = new GregorianCalendar(year, month, day, hour, minute);
            return calendar.getTimeInMillis();
        } catch (Exception e) {
            return 0; // Devuelve 0 si la fecha no es válida.
        }
    }
}
