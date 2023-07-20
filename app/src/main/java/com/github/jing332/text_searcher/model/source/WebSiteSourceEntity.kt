package com.github.jing332.text_searcher.model.source

import androidx.compose.runtime.Composable
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.ui.search.SearchSourceState
import com.github.jing332.text_searcher.ui.search.website.WebsiteEditScreen
import com.github.jing332.text_searcher.ui.search.website.WebsiteSearchScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@SerialName("Website")
data class WebSiteSourceEntity(val url: String = "") : SourceEntity() {
    override fun type(): String {
        return "Website"
    }

    @Composable
    override fun SearchScreen(src: SearchSource, text: String, state: SearchSourceState) {
        WebsiteSearchScreen(text, state, src) {}
    }

    @Composable
    override fun EditScreen(src: SearchSource, onChanged: (SearchSource) -> Unit) {
        WebsiteEditScreen(src = src, onChanged = onChanged)
    }
}