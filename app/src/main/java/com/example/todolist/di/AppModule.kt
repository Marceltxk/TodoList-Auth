package com.example.todolist.di

import android.content.Context
import androidx.room.Room
import com.example.todolist.data.TodoDao
import com.example.todolist.data.TodoDatabase
import com.example.todolist.data.TodoRepository
import com.example.todolist.data.TodoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTodoDatabase(@ApplicationContext context: Context): TodoDatabase {
        return Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTodoDao(database: TodoDatabase): TodoDao {
        return database.todoDao
    }

    @Provides
    @Singleton
    fun provideTodoRepository(dao: TodoDao): TodoRepository {
        return TodoRepositoryImpl(dao)
    }
}