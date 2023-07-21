package com.github.jing332.text_searcher.ui.search.website

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.model.source.WebSiteSourceEntity
import com.github.jing332.text_searcher.ui.search.BaseSearchDialog
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
    Column {
        Column(
            Modifier
                .weight(1f)
                .clickable(
                    enabled = true,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                    onClick = { },
                )
        ) {
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = "外部浏览器")
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = "",
                )
            }
        }

        WebViewScreen(
            modifier = Modifier.fillMaxWidth(),
            url = entity.url.replace("\${text}", text)
        )
    }
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