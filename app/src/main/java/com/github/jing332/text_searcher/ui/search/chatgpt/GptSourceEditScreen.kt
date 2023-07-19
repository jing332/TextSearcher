package com.github.jing332.text_searcher.ui.search.chatgpt

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.model.source.ChatGptSourceEntity
import com.github.jing332.text_searcher.ui.LocalSnackbarHostState
import com.github.jing332.text_searcher.ui.search.BaseSearchDialog
import com.github.jing332.text_searcher.ui.search.BaseSourceEditScreen
import com.github.jing332.text_searcher.ui.search.SearchSourceState
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun GptSourceEditScreen(src: SearchSource, onChanged: (SearchSource) -> Unit) {
    val entity = remember { src.sourceEntity as ChatGptSourceEntity }
    var key by remember { mutableStateOf(entity.apiKey) }
    var model by remember { mutableStateOf(entity.model) }
    var systemPrompt by remember { mutableStateOf(entity.systemPrompt) }
    var messageTemplate by remember { mutableStateOf(entity.messageTemplate) }

    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun newSrc(src: SearchSource): SearchSource {
        return src.copy(
            sourceEntity = entity.copy(
                apiKey = key,
                model = model,
                systemPrompt = systemPrompt,
                messageTemplate = messageTemplate
            )
        )
    }

    var showTestDialog by remember { mutableStateOf<Pair<SearchSource, String>?>(null) }
    if (showTestDialog != null)
        BaseSearchDialog(onDismissRequest = { showTestDialog = null }) {
            val state = remember { SearchSourceState() }
            GptSearchScreen(
                src = showTestDialog!!.first,
                text = showTestDialog!!.second,
                state = state,
                onEntityChange = {
                    onChanged(src.copy(sourceEntity = it))
                }
            )
        }

    BaseSourceEditScreen(
        title = "ChatGPT",
        content = {
            ChatGPTSettingsScreen(
                modifier = Modifier,
                key = key,
                onKeyChange = { key = it },
                model = model,
                onModelChange = { model = it },
                systemPrompt = systemPrompt,
                onSystemPromptChange = { systemPrompt = it },
                messageTemplate = messageTemplate,
                onMessageTemplateChange = { messageTemplate = it }
            )
        },
        src = src,
        onSave = {
            if (key.isBlank()) {
                scope.launch {
                    withTimeout(2.seconds) {
                        snackbarHostState.showSnackbar(context.getString(R.string.error_open_ai_api_key_empty))
                    }
                }
            } else
                onChanged(newSrc(it))
        },
        onTest = {
            showTestDialog = newSrc(src) to it
        }
    )
}