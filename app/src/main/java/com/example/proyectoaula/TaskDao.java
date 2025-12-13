package com.example.proyectoaula;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TaskDao {

    // Inserta una nueva tarea en la base de datos
    @Insert
    void insert(Task task);

    // Obtiene todas las tareas para una fecha específica
    @Query("SELECT * FROM tasks WHERE task_date = :date")
    List<Task> getTasksForDate(String date);
}
