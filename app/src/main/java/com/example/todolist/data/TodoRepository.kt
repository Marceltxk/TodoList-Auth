package com.example.todolist.data

import com.example.todolist.domain.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    // ← ADICIONE o parâmetro userId
    suspend fun insert(title: String, description: String?, userId: String, id: Long? = null)

    suspend fun updateCompleted(id: Long, isCompleted: Boolean)

    suspend fun delete(id: Long)

    // ← ADICIONE o parâmetro userId
    fun getAll(userId: String): Flow<List<Todo>>

    suspend fun getBy(id: Long): Todo?
}