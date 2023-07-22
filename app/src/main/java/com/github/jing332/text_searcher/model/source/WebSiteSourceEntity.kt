package com.github.jing332.text_searcher.model.source

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.jing332.text_searcher.data.appDb
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
data class WebSiteSourceEntity(val url: String = "", val browserInfo: BrowserInfo = BrowserInfo()) :
    SourceEntity() {
    override fun type(): String {
        return "Website"
    }

    @Composable
    override fun SearchScreen(src: SearchSource, text: String, state: SearchSourceState) {
        var vSrc by remember { mutableStateOf(src) }
        WebsiteSearchScreen(text, state, vSrc) { vSrc = it }

        DisposableEffect(src.id) {
            onDispose {
                appDb.searchSource.update(vSrc)
            }
        }
    }

    @Composable
    override fun EditScreen(src: SearchSource, onChanged: (SearchSource) -> Unit) {
        WebsiteEditScreen(src = src, onChanged = onChanged)
    }
}