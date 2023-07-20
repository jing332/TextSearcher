package com.github.jing332.text_searcher.help


import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.withTimeout
import kotlinx.coroutines.withTimeout
import java.util.Locale

class LocalTtsEngineHelper(val context: Context) {
    companion object {
        private const val INIT_STATUS_WAITING = -2

        fun getEngines(context: Context): List<TextToSpeech.EngineInfo> {
            val tts = TextToSpeech(context, null)
            val engines = tts.engines

            tts.shutdown()
            return engines
        }

        suspend fun speak(
            context: Context,
            engine: String,
            text: String,
            speechRate: Float,
            pitch: Float,
            timeout: Long = 5000,
        ) = coroutineScope {
            var waitJob: Job? = null

            launch(Dispatchers.Main) {
                var tts: TextToSpeech? = null
                tts = TextToSpeech(context, {
                    tts?.apply {
                        setSpeechRate(speechRate)
                        setPitch(pitch)
                        speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                    waitJob?.cancel()
                }, engine)
            }

            waitJob = launch {
                delay(timeout)
            }.job
            waitJob.join()
        }
    }

    private var tts: TextToSpeech? = null

    private var engineName: String = ""

    /**
     * return 是否 初始化成功
     */
    suspend fun setEngine(name: String): Boolean {
        if (engineName != name) {
            engineName = name
            shutdown()

            var status = INIT_STATUS_WAITING
            tts = TextToSpeech(context, { status = it }, name)

            for (i in 1..50) { // 5s
                if (status == TextToSpeech.SUCCESS) break
                else if (i == 50) return false
                delay(100)
            }

        }
        return true
    }

    suspend fun speak(
        text: String,
        locale: Locale? = null,
        voice: Voice? = null,
        speechRate: Float,
        pitch: Float,
        // 由 speak() 到 onStart() 的超时时间
        timeout: Long = 5000
    ) = coroutineScope {
        var waitJob: Job? = null
        var isStarted = false

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                waitJob?.cancel()
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
            }

            override fun onStart(utteranceId: String?) {
                isStarted = true
            }
        })

        tts?.apply {
            setSpeechRate(speechRate)
            setPitch(pitch)

            if (locale != null) language = locale
            setVoice(voice ?: defaultVoice)

            speak(text, TextToSpeech.QUEUE_FLUSH, null, null)

            launch {
                withTimeout(timeout) {
                    if (!isStarted) waitJob?.cancel()
                }
            }

            waitJob = launch {
                awaitCancellation()
            }.job
            waitJob?.join()
        }
    }

    fun shutdown() {
        tts?.shutdown()
    }

    val voices: List<Voice>
        get() = try {
            tts!!.voices?.toList()!!
        } catch (e: NullPointerException) {
            emptyList()
        }

    val locales: List<Locale>
        get() = try {
            tts!!.availableLanguages.toList().sortedBy { it.toString() }
        } catch (e: NullPointerException) {
            emptyList()
        }

}