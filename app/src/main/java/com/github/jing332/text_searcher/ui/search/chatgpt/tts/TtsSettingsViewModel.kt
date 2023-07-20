package com.github.jing332.text_searcher.ui.search.chatgpt.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.jing332.text_searcher.help.LocalTtsEngineHelper

class TtsSettingsViewModel : ViewModel() {
    var engines by mutableStateOf<List<TextToSpeech.EngineInfo>>(emptyList())

    fun load(context: Context) {
        engines = LocalTtsEngineHelper.getEngines(context)
    }

    suspend fun speak(
        context: Context,
        engine: String,
        text: String,
        speechRate: Float,
        pitch: Float,
        timeout: Long = 5000,
    ) = LocalTtsEngineHelper.speak(context, engine, text, speechRate, pitch, timeout)
}