@file:Suppress("DEPRECATION")

package com.github.jing332.text_searcher.ui.search.chatgpt

import android.annotation.SuppressLint
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
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.model.source.Appearance
import com.github.jing332.text_searcher.model.source.ChatGptSourceEntity
import com.github.jing332.text_searcher.model.source.TextToSpeechInfo
import com.github.jing332.text_searcher.service.TtsService
import com.github.jing332.text_searcher.ui.search.SearchSourceState
import com.github.jing332.text_searcher.ui.search.base.SimpleSearchScreen
import com.github.jing332.text_searcher.ui.search.chatgpt.tts.TtsSettingsDialog
import com.github.jing332.text_searcher.ui.search.texttoolbar.CustomTextToolbar
import com.github.jing332.text_searcher.ui.widgets.ExpandableText
import com.github.jing332.text_searcher.ui.widgets.SelectableText
import kotlinx.coroutines.launch

@Composable
fun GptSearchScreen(
    src: SearchSource,
    text: String,
    state: SearchSourceState,
    onSrcChange: (SearchSource) -> Unit,
) {
    val entity = src.sourceEntity as ChatGptSourceEntity

    fun onEntityChange(en: ChatGptSourceEntity) {
        onSrcChange(src.copy(sourceEntity = en))
    }

    val context = LocalContext.current
    val vm = viewModel(modelClass = GptSearchScreenViewModel::class.java)

    fun load() {
        if (entity.apiKey.isBlank()) {
            vm.errorMessage = context.getString(R.string.error_open_ai_api_key_empty)
            return
        }

        vm.viewModelScope.launch {
            try {
                vm.requestGpt(
                    msg = entity.messageTemplate.replace("\$text", text),
                    token = entity.apiKey,
                    model = entity.model,
                    systemPrompt = entity.systemPrompt,
                )
            } catch (e: IllegalArgumentException) {
                if (e.message?.contains("token") == true) {
                    vm.errorMessage = context.getString(R.string.error_open_ai_api_key_empty)
                } else
                    vm.errorMessage = e.message ?: e.toString()
            } catch (e: Exception) {
                vm.errorMessage = e.message ?: e.toString()
            }
        }
    }

    if (state.requestLoad) {
        load()
        state.requestLoad = false
    }

    SimpleSearchScreen(
        text = entity.messageTemplate.replace("\$text", text),
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
            onSrcChange(src.copy(testText = it))
        },
        onLoad = {
            load()
        },
        vm = vm
    )
}