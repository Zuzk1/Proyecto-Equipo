package com.example.proyectoaula;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TaskDao {

    // Inserta una nueva tarea en la base de datos.
    @Insert
    void insert(Task task);

    // Obtiene TODAS las tareas de la base de datos.
    @Query("SELECT * FROM tasks")
    List<Task> getAll();

    // Obtiene todas las tareas que están dentro de un rango de tiempo (timestamp).
    @Query("SELECT * FROM tasks WHERE timestamp >= :from AND timestamp < :to")
    List<Task> getTasksBetween(long from, long to);

    // Obtiene tareas para una fecha específica (formato de texto).
    @Query("SELECT * FROM tasks WHERE task_date = :date")
    List<Task> getTasksForDate(String date);
}
