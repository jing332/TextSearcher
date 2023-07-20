package com.github.jing332.text_searcher.ui.search.website

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.model.source.WebSiteSourceEntity
import com.github.jing332.text_searcher.ui.search.SearchSourceState
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@Composable
fun WebsiteSearchScreen(
    text: String,
    state: SearchSourceState,
    src: SearchSource,
    onSourceChange: (SearchSource) -> Unit,
) {
    val entity = src.sourceEntity as WebSiteSourceEntity
    WebViewScreen(modifier = Modifier.fillMaxWidth(), url = entity.url.replace("\${text}", text))
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewScreen(modifier: Modifier, url: String) {
    val state = rememberWebViewState(
        url = url
    )
    val client = remember {
        object : AccompanistWebViewClient() {}
    }
    val chromeClient = remember {
        object : AccompanistWebChromeClient() {}
    }
    WebView(
        modifier = modifier, state = state,
        onCreated = { it.settings.javaScriptEnabled = true },
        client = client,
        chromeClient = chromeClient,
    )
}