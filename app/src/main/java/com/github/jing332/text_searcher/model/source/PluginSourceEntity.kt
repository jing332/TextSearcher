package com.github.jing332.text_searcher.model.source

import androidx.compose.runtime.Composable
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.ui.search.SearchSourceState
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Plugin")
@Parcelize
data class PluginSourceEntity(val code: String) : SourceEntity() {
    @Composable
    override fun EditScreen(src: SearchSource, onChanged: (SearchSource) -> Unit) {
        super.EditScreen(src, onChanged)
    }

    @Composable
    override fun SearchScreen(src: SearchSource, text: String, state: SearchSourceState) {
        super.SearchScreen(src, text, state)
    }
}