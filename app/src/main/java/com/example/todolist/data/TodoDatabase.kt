package com.example.todolist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TodoEntity::class],
    version = 2,
)
abstract class TodoDatabase : RoomDatabase() {
    abstract val todoDao: TodoDao
}


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE todos ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
    }
}

object TodoDatabaseProvider {
    @Volatile
    private var INSTANCE: TodoDatabase? = null

    fun provide(context: Context): TodoDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                TodoDatabase::class.java,
                "todo-app"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
            INSTANCE = instance
            instance
        }
    }
}