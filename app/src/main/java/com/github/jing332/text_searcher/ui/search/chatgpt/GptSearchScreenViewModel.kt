package com.github.jing332.text_searcher.ui.search.chatgpt

import android.content.Context
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
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.RetryStrategy
import com.drake.net.utils.withIO
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.help.LocalTtsEngineHelper
import com.github.jing332.text_searcher.model.source.ChatGptTTS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.coroutines.coroutineContext

class GptSearchScreenViewModel : ViewModel() {
    var result by mutableStateOf("")
    private var isLoading by mutableStateOf(false)

    private var mTtsEngine: LocalTtsEngineHelper? = null
    fun load(context: Context) {
        mTtsEngine = mTtsEngine ?: LocalTtsEngineHelper(context)
    }

    suspend fun speak(text: String, ttsConfig: ChatGptTTS) {
        mTtsEngine?.apply {
            setEngine(ttsConfig.engine)
            val locale =
                if (ttsConfig.locale.isBlank()) null else Locale.forLanguageTag(ttsConfig.locale)
            val voice = mTtsEngine?.voices?.find { it.name == ttsConfig.voice }
            mTtsEngine?.speak(
                text = text,
                locale = locale,
                voice = voice,
                speechRate = ttsConfig.speechRate,
                pitch = ttsConfig.pitch
            )
        }
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
            retry = RetryStrategy(1),
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

    suspend fun requestGpt(
        context: Context,
        msg: String,
        token: String,
        systemPrompt: String,
        model: String
    ) {
        if (isLoading) return

        result = ""
        isLoading = true
        withIO {
            if (token.isBlank()) {
                result = context.getString(R.string.error_open_ai_api_key_empty)
                return@withIO
            }
            try {
                requestInternal(
                    msg,
                    token,
                    systemPrompt,
                    model
                )
                isLoading = false
            } catch (e: RateLimitException) {
                result = "RateLimit: 您已被OpenAI限制，请检查可用额度、更换代理或稍后重试。"
            } catch (e: Exception) {
                result = "错误: $e"
            }
        }
    }


}