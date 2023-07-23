package com.github.jing332.text_searcher.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import com.github.jing332.text_searcher.help.LocalTtsEngineHelper
import com.github.jing332.text_searcher.model.source.ChatGptTTS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class TtsService : Service() {
    companion object {
        const val TAG = "TtsService"

        const val KEY_TTS_CONFIG = "tts_config"
        const val KEY_TTS_TEXT = "tts_text"

        private val timeout = 15.minutes.toLong(DurationUnit.MILLISECONDS)
    }

    private var scope = CoroutineScope(Dispatchers.IO + Job())
    private var mTtsEngine: LocalTtsEngineHelper? = null
    private val channel = Channel<Pair<String, ChatGptTTS>>(Channel.UNLIMITED)

    // 超时自动关闭服务
    private var mLastUpdateTime: Long = 0L
    private fun updateLastTime(){
        mLastUpdateTime = SystemClock.elapsedRealtime()
    }


    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        Log.d(TAG, "onCreate: ...")
        super.onCreate()

        scope.launch {
            while (coroutineContext.isActive) {
                delay(1.minutes)
                val currentTime = SystemClock.elapsedRealtime()
                val timeDiff = currentTime - mLastUpdateTime
                if (timeDiff > timeout){
                    stopSelf()
                }
            }
        }

        mTtsEngine = mTtsEngine ?: LocalTtsEngineHelper(this@TtsService)
        scope.launch {
            for (pair in channel) {
                updateLastTime()

                val config = pair.second
                val text = pair.first
                mTtsEngine?.apply {
                    setEngine(config.engine)
                    val locale =
                        if (config.locale.isBlank()) null else Locale.forLanguageTag(config.locale)
                    val voice = mTtsEngine?.voices?.find { it.name == config.voice }
                    mTtsEngine?.speak(
                        text = text,
                        locale = locale,
                        voice = voice,
                        speechRate = config.speechRate,
                        pitch = config.pitch
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ...")
        super.onDestroy()
        scope.cancel()
        mTtsEngine?.shutdown()
    }

    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val config: ChatGptTTS? = intent.getParcelableExtra(KEY_TTS_CONFIG)
        val text = intent.getStringExtra(KEY_TTS_TEXT) ?: ""
        Log.d(TAG, "onStartCommand: $config $text")
        scope.launch { channel.send(Pair(text, config!!)) }

        return super.onStartCommand(intent, flags, startId)
    }
}