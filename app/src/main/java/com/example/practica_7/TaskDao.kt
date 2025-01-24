package com.example.practica_7

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("UPDATE tasks SET estado = :estado where id = :id")
    suspend fun updateEstado(estado: Boolean, id: Int)

    @Query("SELECT * FROM tasks where id = :id LIMIT 1")
    suspend fun getTaskById(id: Int): Task

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<Task>
}