package com.github.jing332.text_searcher.ui.search.chatgpt

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.github.jing332.text_searcher.help.LocalTtsEngineHelper
import com.github.jing332.text_searcher.help.OpenAIHelper
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

class GptSearchScreenViewModel : ViewModel() {
    var result by mutableStateOf("")
    var errorMessage by mutableStateOf("")

    var isLoading by mutableStateOf(false)

    private var mTtsEngine: LocalTtsEngineHelper? = null
    fun load(context: Context) {
        mTtsEngine = mTtsEngine ?: LocalTtsEngineHelper(context)
    }

    @OptIn(BetaOpenAI::class)
    suspend fun requestInternal(
        msg: String,
        token: String,
        systemPrompt: String,
        model: String
    ) {
        val openAI = OpenAI(OpenAIHelper.openAiConfig(token))
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

    /**
     * @return true if execute
     */
    suspend fun requestGpt(
        msg: String,
        token: String,
        systemPrompt: String,
        model: String
    ) {
        if (isLoading) return

        result = ""
        isLoading = true
        if (token.isBlank()) {
            throw IllegalArgumentException("token is blank")
        } else {
            try {
                requestInternal(
                    msg,
                    token,
                    systemPrompt,
                    model
                )
            } catch (e: Exception) {
                isLoading = false
                throw e
            }
        }
        isLoading = false
    }


}