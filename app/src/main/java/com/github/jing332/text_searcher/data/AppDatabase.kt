package com.github.jing332.text_searcher.data

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.app
import com.github.jing332.text_searcher.data.dao.SearchSourceDao
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.model.source.ChatGptSourceEntity

val appDb by lazy { AppDatabase.createDatabase(app) }

@Database(
    version = 2,
    entities = [SearchSource::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract val searchSource: SearchSourceDao

    companion object {
        private const val DATABASE_NAME = "text_searcher.db"

        fun createDatabase(context: Context) = Room
            .databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .allowMainThreadQueries()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)

//                    if (appDb.searchSource.count == 0) {
//                        appDb.searchSource.insert(
//                            SearchSource(
//                                name = getString(R.string.chatgpt_search_source_name),
//                                sourceEntity = ChatGptSourceEntity()
//                            )
//                        )
//                    }
                }
            })
            .build()
    }

}