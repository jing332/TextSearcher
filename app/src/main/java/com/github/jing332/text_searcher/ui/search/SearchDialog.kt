package com.github.jing332.text_searcher.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.data.appDb
import com.github.jing332.text_searcher.help.AppConfig
import kotlinx.coroutines.launch


@Composable
fun TabIndicator(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(5.dp)
            .fillMaxSize()
            .border(BorderStroke(2.dp, color), RoundedCornerShape(5.dp))
    )
}

@Composable
fun BaseSearchDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    val isFullScreen by remember { AppConfig.isWindowFullScreen }
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = !isFullScreen,
            decorFitsSystemWindows = !isFullScreen,
            dismissOnClickOutside = true,
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 4.dp,
            shape = MaterialTheme.shapes.small,
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearcherDialog(onDismissRequest: () -> Unit, inputText: String) {
    BaseSearchDialog(onDismissRequest = onDismissRequest) {
        val sourceList = rememberSaveable { appDb.searchSource.all }
        val pages = rememberSaveable { sourceList.map { it.name } }
        val scope = rememberCoroutineScope()
        val initialPage =
            remember {
                sourceList.indexOfFirst { AppConfig.lastSourceId.value == it.id }
                    .run { if (this > -1) this else 0 }
            }

        if (pages.isEmpty())
            Text(
                stringResource(R.string.please_add_search_source),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        else
            Column(Modifier.fillMaxWidth()) {
                var lastId by remember { AppConfig.lastSourceId }
                val pagerState = rememberPagerState(initialPage) { pages.size }

                DisposableEffect(key1 = pagerState.hashCode()) {
                    onDispose {
                        sourceList.getOrNull(pagerState.currentPage)?.let { lastId = it.id }
                    }
                }
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        TabIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        )
                    },
                    divider = {
                        Divider(Modifier.fillMaxWidth())
                    }
                ) {
                    pages.forEachIndexed { index, title ->
                        val selected = index == pagerState.currentPage
                        Tab(
                            text = {
                                Text(
                                    title,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = selected,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                        )
                    }
                }
                HorizontalPager(pagerState, userScrollEnabled = false) {
                    val src = remember { sourceList[it] }
                    val state = rememberSaveable { SearchSourceState() }

                    src.sourceEntity.SearchScreen(src = src, text = inputText, state = state)
                }
            }
    }
}

@Preview
@Composable
private fun PreviewSearcherDialog() {
    MaterialTheme {
        var isShow by remember { mutableStateOf(true) }
        if (isShow) SearcherDialog(
            onDismissRequest = { isShow = false },
            inputText = "帝国主义\n军国主义\n111"
        )
    }
}
