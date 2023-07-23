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
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.RetryStrategy
import com.github.jing332.text_searcher.help.LocalTtsEngineHelper
import com.github.jing332.text_searcher.model.source.ChatGptTTS
import kotlinx.coroutines.isActive
import java.util.Locale
import kotlin.coroutines.coroutineContext

class GptSearchScreenViewModel : ViewModel() {
    var result by mutableStateOf("")
    var errorMessage by mutableStateOf("")

    private var isLoading by mutableStateOf(false)

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
        val openAI = OpenAI(
            token = token,
            retry = RetryStrategy(0),
            logging = LoggingConfig(logger = Logger.Empty, logLevel = LogLevel.None)
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

    /**
     * @return true if execute
     */
    suspend fun requestGpt(
        context: Context,
        msg: String,
        token: String,
        systemPrompt: String,
        model: String
    ): Boolean {
        if (isLoading) return false

        result = ""
        isLoading = true
        if (token.isBlank()) {
            return false
        }
        requestInternal(
            msg,
            token,
            systemPrompt,
            model
        )
        isLoading = false

        return true
    }


}