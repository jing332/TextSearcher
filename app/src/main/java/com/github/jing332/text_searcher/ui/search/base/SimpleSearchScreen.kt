package com.github.jing332.text_searcher.ui.search.base

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getSelectedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.model.source.Appearance
import com.github.jing332.text_searcher.model.source.TextToSpeechInfo
import com.github.jing332.text_searcher.service.TtsService
import com.github.jing332.text_searcher.ui.search.chatgpt.tts.TtsSettingsDialog
import com.github.jing332.text_searcher.ui.search.texttoolbar.CustomTextToolbar
import com.github.jing332.text_searcher.ui.widgets.ExpandableText
import com.github.jing332.text_searcher.ui.widgets.SelectableText

@Composable
fun SimpleSearchScreen(
    modifier: Modifier = Modifier,
    text: String,

    titleAppearance: Appearance,
    contentAppearance: Appearance,
    onTitleAppearanceChange: (Appearance) -> Unit,
    onContentAppearanceChange: (Appearance) -> Unit,

    tts: TextToSpeechInfo,
    onTtsChange: (TextToSpeechInfo) -> Unit,

    testText: String,
    onTestTextChange: (String) -> Unit,

    onLoad: (vm: SimpleSearchViewModel) -> Unit,
    vm: SimpleSearchViewModel,
) {
    val context = LocalContext.current

    var mTitleAppearance by remember { mutableStateOf(titleAppearance) }
    var mContentAppearance by remember { mutableStateOf(contentAppearance) }

    fun tts() {
        context.startService(Intent(context, TtsService::class.java).apply {
            putExtra(TtsService.KEY_TTS_TEXT, text)
            putExtra(TtsService.KEY_TTS_CONFIG, tts)
        })
    }

    var gptAppearanceScreenVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val refreshState = rememberPullRefreshState(refreshing = vm.isLoading, onRefresh = {
        onLoad(vm)
    })
    Box(
        Modifier
            .pullRefresh(refreshState)
    ) {
        Column(modifier.verticalScroll(scrollState)) {
            Row {
                Column(
                    modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .clickable(
                            enabled = true,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true),
                            onClick = { gptAppearanceScreenVisible = !gptAppearanceScreenVisible },
                        )
                ) {
                    Row(Modifier.align(Alignment.CenterHorizontally)) {
                        Text(
                            text = stringResource(R.string.appearance_settings),
                            fontWeight = if (gptAppearanceScreenVisible) FontWeight.Bold else FontWeight.Normal
                        )
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Filled.ColorLens,
                            contentDescription = stringResource(R.string.appearance_settings),
                        )
                    }
                }

                var showTtsSettings by remember { mutableStateOf(false) }
                if (showTtsSettings) {
                    var vTts by remember { mutableStateOf(tts) }
                    var vTestText by remember { mutableStateOf(testText) }

                    TtsSettingsDialog(
                        onDismissRequest = { showTtsSettings = false },
                        tts = vTts,
                        message = text,
                        testText = vTestText,
                        onTestTextChange = {
                            vTestText = it
                            onTestTextChange(it)
                        },
                        onTtsChange = {
                            vTts = it
                            onTtsChange(it)
                        }
                    )
                }

                Column(
                    Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .clickable(
                            enabled = true,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(bounded = true),
                            onClick = { showTtsSettings = true },
                        )
                ) {
                    Row(
                        Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = stringResource(R.string.tts_settings))
                        Icon(
                            modifier = Modifier.size(24.dp),
                            contentDescription = stringResource(R.string.tts_settings),
                            painter = painterResource(id = R.drawable.ic_tts)
                        )
                    }
                }
            }

            // GPT外观设置
            if (gptAppearanceScreenVisible) com.github.jing332.text_searcher.ui.search.chatgpt.GptAppearanceSettingsScreen(
                Modifier
                    .animateContentSize()
                    .padding(horizontal = 2.dp),
                titleAppearance = mTitleAppearance,
                onTitleAppearanceChange = {
                    onTitleAppearanceChange(it)
                    mTitleAppearance = it
                },
                contentAppearance = mContentAppearance,
                onContentAppearanceChange = {
                    onContentAppearanceChange(it)
                    mContentAppearance = it
                },
            )
            val ffResolver = LocalFontFamilyResolver.current
            fun fontFamily(uri: Uri): FontFamily {
                try {
                    context.contentResolver.openFileDescriptor(uri, "r")?.use {
                        return FontFamily(Font(it)).apply {
                            ffResolver.resolve(this)
                        }
                    }
                } catch (_: Exception) {
                }

                return FontFamily.Default
            }

            val v = LocalView.current
            val vibrator =
                remember { context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator }
            ExpandableText(
                modifier = Modifier.padding(
                    horizontal = mTitleAppearance.horizontalMargin.dp,
                    vertical = mTitleAppearance.verticalMargin.dp
                ),
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = fontFamily(mTitleAppearance.fontUri),
                fontSize = mTitleAppearance.fontSize.sp,
                lineHeight = mTitleAppearance.fontSize.sp * mTitleAppearance.lineWidthScale,
                fontWeight = FontWeight(mTitleAppearance.fontWeight),
                onLongClickLabel = "TTS",
                onLongClick = {
                    v.isHapticFeedbackEnabled = true
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    tts()
                }
            )

            Box {
                var resultText by remember { mutableStateOf(TextFieldValue(vm.errorMessage.ifEmpty { vm.result })) }
                val localView = LocalView.current
                val textToolbar = remember {
                    CustomTextToolbar(localView, onTtsRequested = {
                        val selectedText = resultText.getSelectedText().text
                        if (selectedText.isNotEmpty()) {
                            context.startService(Intent(context, TtsService::class.java).apply {
                                putExtra(TtsService.KEY_TTS_TEXT, selectedText)
                                putExtra(TtsService.KEY_TTS_CONFIG, tts)
                            })
                        }
                    })
                }

                CompositionLocalProvider(LocalTextToolbar provides textToolbar) {
                    LaunchedEffect(key1 = vm.errorMessage, key2 = vm.result) {
                        resultText = resultText.copy(text = vm.errorMessage.ifEmpty { vm.result })
                    }

                    SelectableText(
                        modifier = Modifier.padding(
                            horizontal = mContentAppearance.horizontalMargin.dp,
                            vertical = mContentAppearance.verticalMargin.dp
                        ),
                        value = resultText,
                        onValueChange = { resultText = it },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = fontFamily(mContentAppearance.fontUri),
                            fontSize = mContentAppearance.fontSize.sp,
                            fontWeight = FontWeight(mContentAppearance.fontWeight),
                            lineHeight = mContentAppearance.fontSize.sp * mContentAppearance.lineWidthScale,
                            color = if (vm.errorMessage.isEmpty()) Color.Unspecified else MaterialTheme.colorScheme.error
                        ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Column(Modifier.fillMaxWidth()) {
            PullRefreshIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                refreshing = refreshState.refreshing,
                state = refreshState
            )
        }
    }
}