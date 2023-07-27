package com.github.jing332.tts_server_android.utils

import android.util.Base64

/**
 * 编码工具 escape base64
 */
@Suppress("unused")
object EncoderUtils {

    fun escape(src: String): String {
        val tmp = StringBuilder()
        for (char in src) {
            val charCode = char.code
            if (charCode in 48..57 || charCode in 65..90 || charCode in 97..122) {
                tmp.append(char)
                continue
            }

            val prefix = when {
                charCode < 16 -> "%0"
                charCode < 256 -> "%"
                else -> "%u"
            }
            tmp.append(prefix).append(charCode.toString(16))
        }
        return tmp.toString()
    }

    @JvmOverloads
    fun base64Decode(str: String, flags: Int = Base64.DEFAULT): String {
        val bytes = Base64.decode(str, flags)
        return String(bytes)
    }

    @JvmOverloads
    fun base64Encode(str: String, flags: Int = Base64.NO_WRAP): String? {
        return base64Encode(str.toByteArray(), flags)
    }

    fun base64Encode(src: ByteArray, flags: Int = Base64.NO_WRAP): String? {
        return Base64.encodeToString(src, flags)
    }

    @JvmOverloads
    fun base64DecodeToByteArray(str: String, flags: Int = Base64.DEFAULT): ByteArray {
        return Base64.decode(str, flags)
    }

}
