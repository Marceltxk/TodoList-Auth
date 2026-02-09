package com.example.todolist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TodoEntity)

    @Delete
    suspend fun delete(entity: TodoEntity)

    // ← MUDE ESTA LINHA (adicione parâmetro userId)
    @Query("SELECT * FROM todos WHERE userId = :userId")
    fun getAll(userId: String): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getBy(id: Long): TodoEntity?
}