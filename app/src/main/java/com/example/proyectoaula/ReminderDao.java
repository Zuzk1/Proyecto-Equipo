package com.example.proyectoaula;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao // Está bien
public interface ReminderDao {

    // Está bien
    @Query("SELECT * FROM reminders ORDER BY timestamp ASC")
    List<Reminder> getAll();

    // Está bien
    @Insert
    void insert(Reminder reminder);

    // Está bien
    @Delete
    void delete(Reminder reminder);
}
