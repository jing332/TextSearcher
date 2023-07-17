package com.github.jing332.text_searcher.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.jing332.text_searcher.help.AppConfig
import com.github.jing332.text_searcher.utils.StringUtils.uriEncode
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import kotlinx.coroutines.launch
import java.net.URLEncoder


@Composable
fun TabIndicator(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(5.dp)
            .fillMaxSize()
            .border(BorderStroke(2.dp, color), RoundedCornerShape(5.dp))
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearcherDialog(onDismissRequest: () -> Unit, inputText: String) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            tonalElevation = 4.dp,
            shape = MaterialTheme.shapes.small,
        ) {
            val pages = remember { listOf("ChatGPT", "Bing", "Baidu") }
            val scope = rememberCoroutineScope()
            val isRequestState = remember { mutableStateOf(true) }
            Column {
                val pagerState = rememberPagerState() { pages.size }
                TabRow(selectedTabIndex = pagerState.currentPage, indicator = { tabPositions ->
                    TabIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                    )
                }) {
                    pages.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = index == pagerState.currentPage,
                            onClick = {
                                scope.launch {
                                    pagerState.scrollToPage(index)
                                }
                            },
                        )
                    }
                }
                HorizontalPager(pagerState, userScrollEnabled = false) {
                    if (it == 0) {
                        ChatGPTScreen(
                            it.toString(),
                            Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            isRequestState,
                            inputText,
                        )
                    } else if (it == 1) {
                        WebViewScreen(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            "https://cn.bing.com/search?q=${inputText.uriEncode()}"
                        )
                    } else if (it == 2) {
                        WebViewScreen(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            "https://www.baidu.com/s?wd=${inputText.uriEncode()}"
                        )
                    }
                }

                /* Box(modifier = Modifier.align(Alignment.End)) {
                     Row {
                         IconButton(onClick = {
                             isRequestState.value = true
                         }) {
                             Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                         }
                         Spacer(modifier = Modifier.width(12.dp))
                         IconButton(onClick = { onDismissRequest() }) {
                             Icon(Icons.Filled.Close, contentDescription = "Close")
                         }
                     }
                 }*/
            }
        }
    }
}

@Composable
private fun ChatGPTScreen(
    key: String,
    modifier: Modifier,
    isRequestState: MutableState<Boolean>,
    inputText: String,
    vm: SearchDialogViewModel = viewModel(key = key)
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun request() {
        scope.launch {
            AppConfig.fillDefaultValues(context)
            vm.requestChatGPT(
                msg = inputText,
                token = AppConfig.openAiApiKey.value,
                model = AppConfig.openAiModel.value,
                systemPrompt = AppConfig.systemPrompt.value,
            )
        }
    }

    if (isRequestState.value) {
        request()
        isRequestState.value = false
    }

    val scrollState = rememberScrollState()
    Column {
        Column(modifier.verticalScroll(scrollState)) {
            SelectionContainer {
                Text(text = inputText, style = MaterialTheme.typography.titleMedium, maxLines = 1)
            }

            SelectionContainer {
                Text(text = vm.result, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(modifier: Modifier, url: String) {
    val state = rememberWebViewState(
        url = url
    )
    val client = remember {
        object : AccompanistWebViewClient() {
        }
    }
    val chromeClient = remember {
        object : AccompanistWebChromeClient() {
        }
    }
    WebView(
        modifier = modifier, state = state,
        onCreated = { it.settings.javaScriptEnabled = true },
        client = client,
        chromeClient = chromeClient,
    )
}

@Preview
@Composable
private fun PreviewSearcherDialog() {
    var isShow by remember { mutableStateOf(true) }
    if (isShow)
        SearcherDialog(onDismissRequest = { isShow = false }, inputText = "帝国主义")
}