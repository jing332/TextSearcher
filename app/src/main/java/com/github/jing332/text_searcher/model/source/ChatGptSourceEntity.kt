package com.github.jing332.text_searcher.model.source

import androidx.compose.runtime.Composable
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.ui.home.edit.ChatGptSourceEditScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@SerialName("ChatGPT")
data class ChatGptSourceEntity(
    val apiKey: String = "",
    val model: String = "",
    val systemPrompt: String = "",
    val messageTemplate: String = "",
    val testText: String = ""
) : SourceEntity() {
    override fun type(): String {
        return "ChatGPT"
    }

    @Composable
    override fun EditScreen(src: SearchSource, onChanged: (SearchSource) -> Unit) {
        ChatGptSourceEditScreen(src = src, onChanged = onChanged)
    }
}