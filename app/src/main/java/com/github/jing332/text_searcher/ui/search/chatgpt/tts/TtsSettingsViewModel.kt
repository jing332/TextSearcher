package com.github.jing332.text_searcher.ui.search.chatgpt.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.jing332.text_searcher.help.LocalTtsEngineHelper
import java.util.Locale

class TtsSettingsViewModel : ViewModel() {
    var engines by mutableStateOf<List<TextToSpeech.EngineInfo>>(emptyList())
    var locales by mutableStateOf<List<Locale>>(emptyList())
    var voices by mutableStateOf<List<Voice>>(emptyList())

    private lateinit var engineHelper: LocalTtsEngineHelper

    fun load(context: Context) {
        engines = LocalTtsEngineHelper.getEngines(context)
        engineHelper = LocalTtsEngineHelper(context)
    }

    suspend fun updateEngine(engine: String) {
        engineHelper.setEngine(engine)
    }

    fun updateLocale() {
        locales = engineHelper.locales
    }

    fun updateVoice(locale: Locale) {
        voices = engineHelper.voices.filter { it.locale == locale }
    }


    suspend fun speak(
        text: String,
        locale: Locale? = null,
        voice: Voice? = null,
        speechRate: Float,
        pitch: Float,
        // 由 speak() 到 onStart() 的超时时间
        timeout: Long = 5000
    ) {
        engineHelper.speak(text, locale, voice, speechRate, pitch, timeout)
    }

    override fun onCleared() {
        super.onCleared()
        engineHelper.shutdown()
    }
}