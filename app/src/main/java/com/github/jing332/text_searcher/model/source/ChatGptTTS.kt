package com.github.jing332.text_searcher.model.source

import android.os.Parcelable
import androidx.annotation.FloatRange
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ChatGptTTS(
    val engine: String = "",

    @FloatRange(from = 0.1, to = 5.0)
    val speechRate: Float = 1.0f,

    @FloatRange(from = 0.0, to = 2.0)
    val pitch: Float = 1.0f
) : Parcelable