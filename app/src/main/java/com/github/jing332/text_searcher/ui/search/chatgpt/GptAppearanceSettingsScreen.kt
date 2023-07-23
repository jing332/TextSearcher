package com.github.jing332.text_searcher.ui.search.chatgpt

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.model.source.ChatGptAppearance
import com.github.jing332.text_searcher.ui.search.FontSelectionDialog
import com.github.jing332.text_searcher.ui.search.TabIndicator
import com.github.jing332.text_searcher.ui.widgets.LabelSlider
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GptAppearanceSettingsScreen(
    modifier: Modifier = Modifier,
    titleAppearance: ChatGptAppearance,
    onTitleAppearanceChange: (ChatGptAppearance) -> Unit,

    contentAppearance: ChatGptAppearance,
    onContentAppearanceChange: (ChatGptAppearance) -> Unit,
) {
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
                        scope.launch { pagerState.animateScrollToPage(index) }
                    },
                )
            }
        }
        HorizontalPager(pagerState, userScrollEnabled = false) { pageIndex ->
            when (pageIndex) {
                0 -> {
                    TextStyleSettingsScreen(
                        fontUri = titleAppearance.fontUri,
                        onFontUriChange = { onTitleAppearanceChange(titleAppearance.copy(fontUri = it)) },

                        fontSize = titleAppearance.fontSize,
                        onFontSizeChange = { onTitleAppearanceChange(titleAppearance.copy(fontSize = it)) },

                        fontWeight = titleAppearance.fontWeight,
                        onFontWeightChange = {
                            onTitleAppearanceChange(titleAppearance.copy(fontWeight = it))
                        },

                        lineHeight = titleAppearance.lineWidthScale,
                        onLineHeightChange = {
                            onTitleAppearanceChange(titleAppearance.copy(lineWidthScale = it))
                        },

                        horizontalMargin = titleAppearance.horizontalMargin,
                        onHorizontalMarginChange = {
                            onTitleAppearanceChange(titleAppearance.copy(horizontalMargin = it))
                        },

                        verticalMargin = titleAppearance.verticalMargin,
                        onVerticalMarginChange = {
                            onTitleAppearanceChange(titleAppearance.copy(verticalMargin = it))
                        },
                    )
                }

                1 -> {
                    TextStyleSettingsScreen(
                        fontUri = contentAppearance.fontUri,
                        onFontUriChange = { onContentAppearanceChange(contentAppearance.copy(fontUri = it)) },

                        fontSize = contentAppearance.fontSize,
                        onFontSizeChange = {
                            onContentAppearanceChange(contentAppearance.copy(fontSize = it))
                        },

                        fontWeight = contentAppearance.fontWeight,
                        onFontWeightChange = {
                            onContentAppearanceChange(contentAppearance.copy(fontWeight = it))
                        },

                        lineHeight = contentAppearance.lineWidthScale,
                        onLineHeightChange = {
                            onContentAppearanceChange(contentAppearance.copy(lineWidthScale = it))
                        },

                        horizontalMargin = contentAppearance.horizontalMargin,
                        onHorizontalMarginChange = {
                            onContentAppearanceChange(contentAppearance.copy(horizontalMargin = it))
                        },

                        verticalMargin = contentAppearance.verticalMargin,
                        onVerticalMarginChange = {
                            onContentAppearanceChange(contentAppearance.copy(verticalMargin = it))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomTextField(
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    placeholderText: String = "Placeholder",
    fontSize: TextUnit = MaterialTheme.typography.bodySmall.fontSize,

    value: String,
    onValueChange: (String) -> Unit,

    readOnly: Boolean = false,
    enabled: Boolean = false,
    colors: TextFieldColors = TextFieldDefaults.colors(),
) {
    BasicTextField(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.shapes.small,
            )
            .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = fontSize
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) Text(
                        placeholderText,
                        style = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            fontSize = fontSize
                        )
                    )
                    innerTextField()
                }
                if (trailingIcon != null) trailingIcon()
            }
        },
        readOnly = readOnly,
        enabled = enabled,
    )
}

@Composable
private fun TextStyleSettingsScreen(
    fontUri: Uri,
    onFontUriChange: (Uri) -> Unit,

    fontSize: Int,
    onFontSizeChange: (Int) -> Unit,

    fontWeight: Int,
    onFontWeightChange: (Int) -> Unit,

    lineHeight: Float,
    onLineHeightChange: (Float) -> Unit,

    horizontalMargin: Float,
    onHorizontalMarginChange: (Float) -> Unit,

    verticalMargin: Float,
    onVerticalMarginChange: (Float) -> Unit,
) {
    val context = LocalContext.current
    var showFontSelectionDialog by remember { mutableStateOf(false) }
    if (showFontSelectionDialog)
        FontSelectionDialog(onDismissRequest = { showFontSelectionDialog = false }, onSelectFont = {
            onFontUriChange(it)
            showFontSelectionDialog = false
        })

    Column {
        OutlinedTextField(
            value = DocumentFile.fromSingleUri(context, fontUri)?.name ?: fontUri.toString(),
            onValueChange = {},
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
                .padding(vertical = 2.dp, horizontal = 4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true)
                ) { showFontSelectionDialog = true },
            label = {
                Text(stringResource(R.string.font))
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LabelSlider(
            modifier = Modifier.fillMaxWidth(),
            value = (fontWeight / 100).toFloat(),
            onValueChange = {
                onFontWeightChange(it.toInt() * 100)
            },
            valueRange = 1f..9f,
            steps = 8,
        ) {
            Text(
                stringResource(
                    R.string.font_weight, when (fontWeight) {
                        FontWeight.Normal.weight -> context.getString(R.string.font_weight_normal)
                        FontWeight.Bold.weight -> context.getString(R.string.font_weight_bold)
                        else -> fontWeight.toString()
                    }
                )
            )
        }

        LabelSlider(
            modifier = Modifier.fillMaxWidth(),
            value = fontSize.toFloat(),
            onValueChange = {
                onFontSizeChange(it.toInt())
            },
            valueRange = 10f..40f,
            steps = 30,
        ) {
            Text(stringResource(R.string.font_size, fontSize.toString()))
        }

        LabelSlider(
            modifier = Modifier.fillMaxWidth(),
            value = lineHeight,
            onValueChange = {
                onLineHeightChange(it)
            },
            valueRange = 0.8f..2.0f,
            steps = 120,
        ) {
            Text(stringResource(R.string.line_height, String.format("%.2f", lineHeight)))
        }

        Divider(Modifier.fillMaxWidth())

        LabelSlider(
            valueRange = 0.0f..48.0f,
            value = horizontalMargin, onValueChange = onHorizontalMarginChange
        ) {
            Text(
                stringResource(
                    R.string.label_horizontal_margin,
                    String.format("%.1f", horizontalMargin)
                )
            )
        }

        LabelSlider(
            valueRange = 0.0f..48.0f,
            value = verticalMargin, onValueChange = onVerticalMarginChange
        ) {
            Text(
                stringResource(
                    R.string.labe_vertical_margin,
                    String.format("%.1f", verticalMargin)
                )
            )
        }
    }
}
