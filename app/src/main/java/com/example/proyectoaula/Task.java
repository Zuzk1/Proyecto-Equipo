package com.example.proyectoaula;

public class Task {
    private String title;
    private String note;
    private int hour;
    private int minute;
    private boolean useNotification;

    // Constructor para crear un nuevo objeto Tarea
    public Task(String title, String note, int hour, int minute, boolean useNotification) {
        this.title = title;
        this.note = note;
        this.hour = hour;
        this.minute = minute;
        this.useNotification = useNotification;
    }

    // Métodos para poder leer los datos de la tarea desde otras clases
    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public String getFormattedTime() {
        if (hour == -1 || minute == -1) {
            return "Sin hora";
        }
        // Formatea la hora a un formato como "09:05"
        return String.format("%02d:%02d", hour, minute);
    }
}
