package com.github.jing332.text_searcher.ui.search.chatgpt

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.exception.OpenAIException
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.RetryStrategy
import com.github.jing332.text_searcher.R

class ModelSelectionViewModel : ViewModel() {
    var models by mutableStateOf(listOf<String>())
    val lazyListState by lazy { LazyListState() }

    suspend fun loadModels(context: Context, token: String, currentModel: String) {
        if (token.isEmpty()) {
            throw Exception(context.getString(R.string.error_open_ai_api_key_empty))
        }

        val openAi =
            OpenAI(token, retry = RetryStrategy(1), logging = LoggingConfig(logger = Logger.Empty))
        models =
            openAi.models().filter { it.id.id.contains("gpt-") }.map { it.id.id }.sortedBy { it }
    }
}