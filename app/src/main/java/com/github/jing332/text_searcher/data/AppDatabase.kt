package com.github.jing332.text_searcher.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.jing332.text_searcher.app
import com.github.jing332.text_searcher.data.dao.SearchSourceDao
import com.github.jing332.text_searcher.data.entites.SearchSource

val appDb by lazy { AppDatabase.createDatabase(app) }

@Database(
    version = 2,
    entities = [SearchSource::class],
)
abstract class AppDatabase : RoomDatabase() {
    abstract val searchSource: SearchSourceDao

    companion object {
        private const val DATABASE_NAME = "editor.db"

        fun createDatabase(context: Context) = Room
            .databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .allowMainThreadQueries()
            .build()
    }

}