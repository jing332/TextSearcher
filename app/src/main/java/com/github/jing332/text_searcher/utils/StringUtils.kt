package com.github.jing332.text_searcher.utils

object StringUtils {
    fun String.uriEncode(enc: String = "UTF-8"): String {
        return java.net.URLEncoder.encode(this, enc)
    }
}