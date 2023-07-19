package com.github.jing332.text_searcher.ui.search.chatgpt

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.RetryStrategy

class ModelSelectionViewModel : ViewModel() {
    var models by mutableStateOf(listOf<String>())
    val lazyListState by lazy { LazyListState() }

    suspend fun loadModels(token: String, currentModel: String) {
        val openAi = OpenAI(token, retry = RetryStrategy(1))
        models =
            openAi.models().filter { it.id.id.contains("gpt-") }.map { it.id.id }.sortedBy { it }
    }
}