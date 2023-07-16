package com.github.jing332.text_searcher.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.exception.RateLimitException
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.RetryStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class SearchDialogViewModel : ViewModel() {
    var result by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    @OptIn(BetaOpenAI::class)
    suspend fun requestInternal(
        msg: String,
        token: String,
        systemPrompt: String,
        model: String = "gpt-3.5-turbo"
    ) {
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

        openAI.chatCompletions(chatCompletionRequest).collect { compChunk ->
            if (!coroutineContext.isActive) return@collect

            compChunk.choices.forEach {
                result += it.delta?.content ?: ""
            }
        }
    }

    @OptIn(BetaOpenAI::class)
    fun requestChatGPT(
        msg: String,
        token: String,
        systemPrompt: String,
        model: String = "gpt-3.5-turbo"
    ) {
        if (isLoading) return

        result = ""
        isLoading = true
        try {
            viewModelScope.launch(Dispatchers.IO) {
                requestInternal(
                    msg,
                    token,
                    systemPrompt,
                    model
                )
                isLoading = false
            }
        } catch (e: RateLimitException) {
            result = "RateLimit: 您已被OpenAI限制，请检查可用额度、更换代理或稍后重试。"
        } catch (e: Exception) {
            result = "错误: $e"
        }

    }
}