package com.example.proyectoaula;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    // Nuevo campo para guardar la fecha
    @ColumnInfo(name = "task_date")
    private String date;

    @ColumnInfo(name = "task_hour")
    private int hour;

    @ColumnInfo(name = "task_minute")
    private int minute;

    @ColumnInfo(name = "use_notification")
    private boolean useNotification;

    // Constructor modificado para incluir la fecha
    public Task(String title, String note, String date, int hour, int minute, boolean useNotification) {
        this.title = title;
        this.note = note;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.useNotification = useNotification;
    }

    // --- GETTERS Y SETTERS (Room los necesita) ---
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
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
}
