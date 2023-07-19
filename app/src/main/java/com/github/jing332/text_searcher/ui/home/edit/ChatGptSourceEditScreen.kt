package com.github.jing332.text_searcher.ui.home.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.model.source.ChatGptSourceEntity
import com.github.jing332.text_searcher.ui.chatgpt.ChatGPTSettingsScreen

@Composable
fun ChatGptSourceEditScreen(src: SearchSource, onChanged: (SearchSource) -> Unit) {
    val entity = remember { src.sourceEntity as ChatGptSourceEntity }
    var key by remember { mutableStateOf(entity.apiKey) }
    var model by remember { mutableStateOf(entity.model) }
    var systemPrompt by remember { mutableStateOf(entity.systemPrompt) }
    var messageTemplate by remember { mutableStateOf(entity.messageTemplate) }

    BaseSourceEditScreen(content = {
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

    }, src = src, onSave = {
        onChanged(
            it.copy(
                sourceEntity = entity.copy(
                    apiKey = key,
                    model = model,
                    systemPrompt = systemPrompt,
                    messageTemplate = messageTemplate
                )
            )
        )
    })
}