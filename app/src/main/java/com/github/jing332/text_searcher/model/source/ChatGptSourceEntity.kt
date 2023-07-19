package com.github.jing332.text_searcher.model.source

import androidx.compose.runtime.Composable
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.app
import com.github.jing332.text_searcher.data.appDb
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.ui.search.SearchSourceState
import com.github.jing332.text_searcher.ui.search.chatgpt.GptSearchScreen
import com.github.jing332.text_searcher.ui.search.chatgpt.GptSourceEditScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@SerialName("ChatGPT")
data class ChatGptSourceEntity(
    val apiKey: String = "",
    val model: String = VALUE_MODEL,
    val systemPrompt: String = VALUE_SYSTEM_PROMPT,
    val messageTemplate: String = VALUE_MESSAGE_TEMPLATE,
    val testText: String = "",

    val titleAppearance: ChatGptAppearance = ChatGptAppearance(
        fontSize = VALUE_GPT_TITLE_FONT_SIZE,
        lineWidthScale = VALUE_GPT_TITLE_LINE_HEIGHT_SCALE
    ),

    val contentAppearance: ChatGptAppearance = ChatGptAppearance(
        fontSize = VALUE_GPT_FONT_SIZE,
        lineWidthScale = VALUE_GPT_LINE_HEIGHT_SCALE
    ),
) : SourceEntity() {
    companion object {
        const val VALUE_MODEL = "gpt-3.5-turbo"
        const val VALUE_MESSAGE_TEMPLATE = "\$text"
        val VALUE_SYSTEM_PROMPT: String
            get() = app.getString(R.string.gpt_system_prompt)

        const val VALUE_GPT_TITLE_FONT_SIZE = 18
        const val VALUE_GPT_TITLE_LINE_HEIGHT_SCALE = 1f

        const val VALUE_GPT_FONT_SIZE = 16
        const val VALUE_GPT_LINE_HEIGHT_SCALE = 1.2f
    }

    override fun type(): String {
        return "ChatGPT"
    }

    @Composable
    override fun EditScreen(src: SearchSource, onChanged: (SearchSource) -> Unit) {
        GptSourceEditScreen(src = src, onChanged = onChanged)
    }

    @Composable
    override fun SearchScreen(src: SearchSource, text: String, state: SearchSourceState) {
        GptSearchScreen(src = src, text = text, state = state, onEntityChange = {
            appDb.searchSource.update(src.copy(sourceEntity = it))
        })
    }
}