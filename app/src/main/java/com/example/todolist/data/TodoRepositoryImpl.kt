package com.example.todolist.data

import com.example.todolist.domain.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val dao: TodoDao
) : TodoRepository {

    override suspend fun insert(title: String, description: String?, userId: String, id: Long?) {
        val entity = TodoEntity(
            id = id ?: 0,
            title = title,
            description = description,
            isCompleted = false,
            userId = userId
        )
        dao.insert(entity)
    }

    override suspend fun updateCompleted(id: Long, isCompleted: Boolean) {
        val todo = dao.getBy(id)
        todo?.let {
            dao.insert(it.copy(isCompleted = isCompleted))
        }
    }

    override suspend fun delete(id: Long) {
        val todo = dao.getBy(id)
        todo?.let {
            dao.delete(it)
        }
    }

    override fun getAll(userId: String): Flow<List<Todo>> {
        return dao.getAll(userId).map { list ->
            list.map { entity ->
                Todo(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    isCompleted = entity.isCompleted
                )
            }
        }
    }

    override suspend fun getBy(id: Long): Todo? {
        val entity = dao.getBy(id)
        return entity?.let {
            Todo(
                id = it.id,
                title = it.title,
                description = it.description,
                isCompleted = it.isCompleted
            )
        }
    }
}
