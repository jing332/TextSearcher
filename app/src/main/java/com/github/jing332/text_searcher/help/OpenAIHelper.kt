package com.github.jing332.text_searcher.help

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.RetryStrategy
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object OpenAIHelper {
    fun openAiConfig(token: String): OpenAIConfig {
        return OpenAIConfig(
            token = token,
            timeout = Timeout(socket = AppConfig.gptSocketTimeout.value.seconds),
            retry = RetryStrategy(0),
            logging = LoggingConfig(logger = Logger.Empty, logLevel = LogLevel.None),
        )
    }
}