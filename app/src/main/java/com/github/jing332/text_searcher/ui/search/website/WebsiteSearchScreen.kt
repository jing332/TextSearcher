package com.github.jing332.text_searcher.ui.search.website

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.model.source.WebSiteSourceEntity
import com.github.jing332.text_searcher.ui.search.SearchSourceState
import com.github.jing332.text_searcher.utils.IntentUtils.goExternalBrowser
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WebsiteSearchScreen(
    text: String,
    state: SearchSourceState,
    src: SearchSource,
    onSourceChange: (SearchSource) -> Unit,
) {
    val entity = src.sourceEntity as WebSiteSourceEntity

    val context = LocalContext.current
    var showBrowserSelectionDialog by remember { mutableStateOf(false) }
    if (showBrowserSelectionDialog)
        BrowserSelectionDialog(
            onDismissRequest = { showBrowserSelectionDialog = false },
            onBrowserChange = {
                showBrowserSelectionDialog = false
                try {
                    context.goExternalBrowser(it.packageName, it.className, entity.url)
                    onSourceChange(src.copy(sourceEntity = entity.copy(browserInfo = it)))
                } catch (e: ActivityNotFoundException) {
                    showBrowserSelectionDialog = true
                }
            }
        )

    Column {
        Column(
            Modifier
                .fillMaxWidth()
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                    onClick = {
                        try {
                            Log.d(
                                "WebsiteSearchScreen",
                                "goExternalBrowser(): ${entity.browserInfo}"
                            )
                            context.goExternalBrowser(
                                entity.browserInfo.packageName,
                                entity.browserInfo.className,
                                entity.url,
                            )
                        } catch (e: ActivityNotFoundException) {
                            showBrowserSelectionDialog = true
                        }
                    },
                    onLongClick = {
                        showBrowserSelectionDialog = true
                    },
                    onLongClickLabel = stringResource(R.string.select_browser)
                )
        ) {
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = stringResource(R.string.external_browser))
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = "",
                )
            }
        }
        WebViewScreen(
            modifier = Modifier.fillMaxHeight(),
            url = entity.url.replace("\${text}", text)
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewScreen(modifier: Modifier, url: String) {
    val state = rememberWebViewState(url = url)
    val navigator = rememberWebViewNavigator()

    val client = remember {
        object : AccompanistWebViewClient() {}
    }
    val chromeClient = remember {
        object : AccompanistWebChromeClient() {
        }
    }

    Column(modifier = modifier) {
        val process =
            if (state.loadingState is LoadingState.Loading) (state.loadingState as LoadingState.Loading).progress else 0f

        if (process > 0)
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = process
            )

        var lastTitle by remember { mutableStateOf("") }
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = state.pageTitle?.apply { lastTitle = this } ?: lastTitle,
            maxLines = 1,
            style = MaterialTheme.typography.titleMedium,
        )

        val refreshState = rememberPullRefreshState(refreshing = state.isLoading, onRefresh = {
            navigator.reload()
        })
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(refreshState)
        ) {
            val scrollState = rememberScrollState()
            WebView(
                modifier = Modifier.verticalScroll(scrollState),
                state = state,
                navigator = navigator,
                onCreated = {
                    it.settings.javaScriptEnabled = true
                },
                client = client,
                chromeClient = chromeClient,
            )

            Column(Modifier.fillMaxWidth()) {
                PullRefreshIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    refreshing = refreshState.refreshing,
                    state = refreshState
                )
            }
        }
    }
}
