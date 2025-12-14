package com.example.proyectoaula;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ReminderDao {

    @Query("SELECT * FROM reminders ORDER BY timestamp ASC")
    List<Reminder> getAll();

    @Insert
    void insert(Reminder reminder);

    @Delete
    void delete(Reminder reminder);


    /**
     * Obtiene todos los recordatorios que están dentro de un rango de tiempo (un día completo).
     * @param from El timestamp de inicio del día.
     * @param to   El timestamp de fin del día.
     * @return Una lista de recordatorios para ese día.
     */
    @Query("SELECT * FROM reminders WHERE timestamp >= :from AND timestamp < :to")
    List<Reminder> getRemindersBetween(long from, long to);
}
