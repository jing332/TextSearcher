package com.github.jing332.text_searcher.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.jing332.text_searcher.data.entites.SearchSource
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchSourceDao {
    @get:Query("SELECT * FROM search_sources ORDER BY `order` ASC")
    val all: List<SearchSource>

    @get:Query("SELECT * FROM search_sources ORDER BY `order` ASC")
    val flowAll: Flow<List<SearchSource>>

    @get:Query("SELECT COUNT(*) FROM search_sources")
    val count:Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg args: SearchSource)

    @Update
    fun update(vararg args: SearchSource)

    @Delete
    fun delete(vararg args: SearchSource)
}