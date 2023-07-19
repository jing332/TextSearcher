package com.github.jing332.text_searcher.ui.search

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.help.AppConfig
import com.github.jing332.text_searcher.ui.widgets.ExpandableText
import com.github.jing332.text_searcher.ui.widgets.LabelSlider
import com.github.jing332.text_searcher.utils.StringUtils.uriEncode
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
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

    var gptAppearanceScreenVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Column(modifier.verticalScroll(scrollState)) {
        Row(
            modifier
                .wrapContentWidth()
                .align(Alignment.CenterHorizontally)
                .clickable(
                    enabled = true,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                    onClick = { gptAppearanceScreenVisible = !gptAppearanceScreenVisible },
                )
        ) {
            Text(text = stringResource(R.string.appearance_settings))
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.appearance_settings),
            )
        }
        if (gptAppearanceScreenVisible) ChatGPTAppearanceSettingsScreen(Modifier.animateContentSize())

        val titleFontSize by remember { AppConfig.gptTitleFontSize }
        val titleFontWeight by remember { AppConfig.gptTitleFontWeight }
        val titleLineHeight by remember { AppConfig.gptTitleLineHeight }
        ExpandableText(
            text = inputText, style = MaterialTheme.typography.titleMedium,
            fontSize = titleFontSize.sp,
            lineHeight = titleFontSize.sp * titleLineHeight,
            fontWeight = FontWeight(titleFontWeight)
        )

        Spacer(modifier = Modifier.height(2.dp))

        val fontSize by remember { AppConfig.gptFontSize }
        val fontWeight by remember { AppConfig.gptFontWeight }
        val lineHeight by remember { AppConfig.gptLineHeightScale }

        SelectionContainer {
            Text(
                text = vm.result,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = fontSize.sp,
                fontWeight = FontWeight(fontWeight),
                lineHeight = fontSize.sp * lineHeight,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatGPTAppearanceSettingsScreen(modifier: Modifier = Modifier) {
    OutlinedCard(modifier) {
        val scope = rememberCoroutineScope()
        val pages = remember { listOf("标题", "内容") }
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
                        scope.launch { pagerState.scrollToPage(index) }
                    },
                )
            }
        }
        HorizontalPager(pagerState, userScrollEnabled = false) {
            when (it) {
                0 -> {
                    TextStyleSettingsScreen(
                        fontSize = AppConfig.gptTitleFontSize,
                        lineHeight = AppConfig.gptTitleLineHeight,
                        fontWeight = AppConfig.gptTitleFontWeight,
                    )
                }

                1 -> {
                    TextStyleSettingsScreen(
                        fontSize = AppConfig.gptFontSize,
                        lineHeight = AppConfig.gptLineHeightScale,
                        fontWeight = AppConfig.gptFontWeight,
                    )
                }
            }
        }
    }
}

@Composable
private fun TextStyleSettingsScreen(
    fontSize: MutableState<Int>,
    fontWeight: MutableState<Int>,
    lineHeight: MutableState<Float>
) {
    val context = LocalContext.current
    var showFontSelectionDialog by remember { mutableStateOf(false) }
    if (showFontSelectionDialog)
        FontSelectionDialog { showFontSelectionDialog = false }

    Column {
        OutlinedTextField(
            value = "", onValueChange = {},
            readOnly = true,
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true)
                ) { showFontSelectionDialog = true },
        )


        Spacer(modifier = Modifier.height(8.dp))

        var vFontWeight by remember { fontWeight }
        var fontWeightLabel by remember {
            mutableStateOf(
                context.getString(R.string.font_weight, (vFontWeight / 100).toString())
            )
        }
        LabelSlider(
            modifier = Modifier.fillMaxWidth(),
            value = (vFontWeight / 100).toFloat(),
            onValueChange = {
                vFontWeight = (it.toInt() * 100)
                fontWeightLabel = when (vFontWeight) {
                    FontWeight.Normal.weight -> context.getString(R.string.font_weight_normal)
                    FontWeight.Bold.weight -> context.getString(R.string.font_weight_bold)
                    else -> context.getString(R.string.font_weight, it.toInt().toString())
                }
            },
            valueRange = 1f..9f,
            steps = 8,
        ) {
            Text(fontWeightLabel)
        }

        var vFontSize by remember { fontSize }
        var fontSizeLabel by remember {
            mutableStateOf(context.getString(R.string.font_size, vFontSize.toString()))
        }
        LabelSlider(
            modifier = Modifier.fillMaxWidth(),
            value = vFontSize.toFloat(),
            onValueChange = {
                vFontSize = it.toInt()
                fontSizeLabel = context.getString(R.string.font_size, it.toInt().toString())
            },
            valueRange = 10f..40f,
            steps = 30,
        ) {
            Text(fontSizeLabel)
        }

        var vLineHeight by remember { lineHeight }
        var lineHeightLabel by remember {
            mutableStateOf(
                context.getString(R.string.line_height, String.format("%.2f", vLineHeight))
            )
        }
        LabelSlider(
            modifier = Modifier.fillMaxWidth(),
            value = vLineHeight,
            onValueChange = {
                vLineHeight = it
                lineHeightLabel =
                    context.getString(R.string.line_height, String.format("%.2f", it))
            },
            valueRange = 0.8f..2.0f,
            steps = 120,
        ) {
            Text(lineHeightLabel)
        }
    }
}


@Preview
@Composable
fun PreviewChatGPTSettingsDialog() {
    var show by remember { mutableStateOf(true) }
//    if (show) {
//        ChatGPTAppearanceSettingsDialog(onDismissRequest = { show = false })
//    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(modifier: Modifier, url: String) {
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

@Preview
@Composable
private fun PreviewSearcherDialog() {
    var isShow by remember { mutableStateOf(true) }
    if (isShow) SearcherDialog(onDismissRequest = { isShow = false }, inputText = "帝国主义")
}