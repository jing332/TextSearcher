package com.github.jing332.text_searcher.model.source

import android.content.Context
import android.os.Parcelable
import androidx.compose.runtime.Composable
import com.github.jing332.text_searcher.app
import com.github.jing332.text_searcher.data.entites.SearchSource
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("#type")
@Serializable
@Parcelize
sealed class SourceEntity(
    @Transient
    @IgnoredOnParcel
    var context: Context = app
) : Parcelable {
    open fun type(): String = "SourceEntity"

    @Composable
    open fun EditScreen(src: SearchSource, onChanged: (SearchSource) -> Unit) {
    }
}