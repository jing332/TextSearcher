package com.github.jing332.text_searcher.ui.chatgpt

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.RetryStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModelSelectionViewModel : ViewModel() {
    var models by mutableStateOf(listOf<String>())
    val lazyListState by lazy { LazyListState() }

    suspend fun loadModels(token: String, currentModel: String) {
        val openAi = OpenAI(token, retry = RetryStrategy(1))
        models = openAi.models().map { it.id.id }.sortedBy { it }
    }
}