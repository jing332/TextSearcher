package com.github.jing332.text_searcher.ui.search.chatgpt

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aallam.openai.client.OpenAI
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.help.OpenAIHelper

class ModelSelectionViewModel : ViewModel() {
    var models by mutableStateOf(listOf<String>())
    val lazyListState by lazy { LazyListState() }

    suspend fun loadModels(context: Context, token: String, currentModel: String) {
        if (token.isEmpty()) {
            throw Exception(context.getString(R.string.error_open_ai_api_key_empty))
        }

        val openAI = OpenAI(OpenAIHelper.openAiConfig(token))
        models =
            openAI.models().filter { it.id.id.contains("gpt-") }.map { it.id.id }.sortedBy { it }
    }
}