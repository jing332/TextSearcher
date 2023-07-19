package com.github.jing332.text_searcher.data.entites

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.github.jing332.text_searcher.model.source.ChatGptSourceEntity
import com.github.jing332.text_searcher.model.source.SourceEntity
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Parcelize
@TypeConverters(SearchSource.Converters::class)
@Entity(tableName = "search_sources")
data class SearchSource(
    @PrimaryKey(autoGenerate = false)
    val id: Long = System.currentTimeMillis(),
    val name: String = "",
    val order: Int = 0,

    val sourceEntity: SourceEntity,
) : Parcelable {

    object Converters {
        @OptIn(ExperimentalSerializationApi::class)
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

        @TypeConverter
        fun fromSource(sourceEntity: SourceEntity): String {
            return try {
                return json.encodeToString(sourceEntity)
            } catch (e: Exception) {
                ""
            }
        }

        @TypeConverter
        fun toSource(str: String): SourceEntity {
            return try {
                return json.decodeFromString(str)
            } catch (e: Exception) {
                ChatGptSourceEntity()
            }
        }
    }
}