package com.github.jing332.text_searcher.model.source

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class BrowserInfo(val packageName: String = "", val className: String = "") : Parcelable