package com.github.jing332.text_searcher.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import java.io.File
import java.io.InputStream

object FileUtils {
    fun exists(file: File): Boolean {
        runCatching {
            if (file.isFile)
                return file.exists()
        }.onFailure {
            it.printStackTrace()
        }
        return false
    }

    fun exists(filePath: String): Boolean {
        return exists(File(filePath))
    }

    fun Uri.readAllText(context: Context): String {
        return when (scheme) {
            ContentResolver.SCHEME_CONTENT -> {
                val input = context.contentResolver.openInputStream(this)
                val str = input!!.readBytes().decodeToString()
                input.close()
                str
            }

            ContentResolver.SCHEME_FILE -> toFile().readText()
            else -> File(this.toString()).readText()
        }
    }

    /**
     * 按行读取txt
     */
    fun InputStream.readAllText(): String {
        val bufferedReader = this.bufferedReader()
        val buffer = StringBuffer("")
        var str: String?
        while (bufferedReader.readLine().also { str = it } != null) {
            buffer.append(str)
            buffer.append("\n")
        }
        return buffer.toString()
    }
}