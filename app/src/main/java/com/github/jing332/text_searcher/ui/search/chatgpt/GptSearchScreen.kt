package com.github.jing332.text_searcher.ui.search.chatgpt

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.help.AppConfig
import com.github.jing332.text_searcher.model.source.ChatGptAppearance
import com.github.jing332.text_searcher.model.source.ChatGptSourceEntity
import com.github.jing332.text_searcher.model.source.ChatGptTTS
import com.github.jing332.text_searcher.ui.search.SearchSourceState
import com.github.jing332.text_searcher.ui.search.chatgpt.tts.TtsSettingsDialog
import com.github.jing332.text_searcher.ui.widgets.ExpandableText
import kotlinx.coroutines.launch

@Composable
fun GptSearchScreen(
    src: SearchSource,
    text: String,
    state: SearchSourceState,
    onTtsChange: (SearchSource) -> Unit,
) {
    val entity = src.sourceEntity as ChatGptSourceEntity

    fun onEntityChange(en: ChatGptSourceEntity) {
        onTtsChange(src.copy(sourceEntity = en))
    }

    ChatGPTScreen(
        id = src.id,
        state = state,
        message = entity.messageTemplate.replace("\$text", text),
        token = entity.apiKey,
        model = entity.model,
        systemPrompt = entity.systemPrompt,

        titleAppearance = entity.titleAppearance,
        contentAppearance = entity.contentAppearance,
        onTitleAppearanceChange = {
            onEntityChange(entity.copy(titleAppearance = it))
        },
        onContentAppearanceChange = {
            onEntityChange(entity.copy(contentAppearance = it))
        },
        tts = entity.tts,
        onTtsChange = {
            onEntityChange(entity.copy(tts = it))
        },

        testText = src.testText,
        onTestTextChange = {
            onTtsChange(src.copy(testText = it))
        },


        )
}

@Composable
private fun ChatGPTScreen(
    id: Long,
    modifier: Modifier = Modifier,
    state: SearchSourceState,
    token: String,
    model: String,
    systemPrompt: String,
    message: String,
    titleAppearance: ChatGptAppearance,
    contentAppearance: ChatGptAppearance,
    onTitleAppearanceChange: (ChatGptAppearance) -> Unit,
    onContentAppearanceChange: (ChatGptAppearance) -> Unit,

    tts: ChatGptTTS,
    onTtsChange: (ChatGptTTS) -> Unit,

    testText: String,
    onTestTextChange: (String) -> Unit,

    vm: GptSearchScreenViewModel = viewModel(key = id.toString())
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var mTitleAppearance by remember { mutableStateOf(titleAppearance) }
    var mContentAppearance by remember { mutableStateOf(contentAppearance) }

    fun request() {
        scope.launch {
            AppConfig.fillDefaultValues(context)
            vm.requestChatGPT(
                context,
                msg = message,
                token = token,
                model = model,
                systemPrompt = systemPrompt,
            )
        }
    }

    if (state.requestLoad) {
        request()
        state.requestLoad = false
    }

    var gptAppearanceScreenVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Column(modifier.verticalScroll(scrollState)) {
        Row {
            Column(
                modifier
                    .weight(1f)
                    .clickable(
                        enabled = true,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = true),
                        onClick = { gptAppearanceScreenVisible = !gptAppearanceScreenVisible },
                    )
            ) {
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Text(text = stringResource(R.string.appearance_settings))
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Filled.Settings,
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
            /*Divider(
                Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .padding(horizontal = 2.dp)
            )*/
            Column(
                Modifier
                    .weight(1f)
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
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.tts_settings),
                    )
                }
            }
        }

        // GPT外观设置
        if (gptAppearanceScreenVisible) GptAppearanceSettingsScreen(
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
        ExpandableText(
            text = message, style = MaterialTheme.typography.titleMedium,
            fontFamily = fontFamily(mTitleAppearance.fontUri),
            fontSize = mTitleAppearance.fontSize.sp,
            lineHeight = mTitleAppearance.fontSize.sp * mTitleAppearance.lineWidthScale,
            fontWeight = FontWeight(mTitleAppearance.fontWeight)
        )

        Spacer(modifier = Modifier.height(2.dp))

        SelectionContainer {
            Text(
                text = vm.result,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = fontFamily(mContentAppearance.fontUri),
                fontSize = mContentAppearance.fontSize.sp,
                fontWeight = FontWeight(mContentAppearance.fontWeight),
                lineHeight = mContentAppearance.fontSize.sp * mContentAppearance.lineWidthScale,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

