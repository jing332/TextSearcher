package com.github.jing332.text_searcher.ui

import androidx.lifecycle.ViewModel
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.RetryStrategy
import kotlinx.coroutines.flow.Flow

class SearchDialogViewModel : ViewModel() {
    @OptIn(BetaOpenAI::class)
    fun chat(
        msg: String,
        token: String,
        systemPrompt: String,
        model: String = "gpt-3.5-turbo"
    ): Flow<ChatCompletionChunk> {
        val openAI = OpenAI(
            token = token,
            retry = RetryStrategy(1),
            //timeout = Timeout(request = 8.seconds, connect = 8.seconds, socket = 8.seconds),
//            proxy = ProxyConfig.Http("http://127.0.0.1:10801")
        )
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = systemPrompt
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = msg
                )
            )
        )

        return openAI.chatCompletions(chatCompletionRequest)
    }
}