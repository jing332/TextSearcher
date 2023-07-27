package com.github.jing332.text_searcher.utils

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

object CharsetUtils {
    /**
     * Return the charset of file simply.
     *
     * @param filePath The path of file.
     * @return the charset of file simply
     */
    fun getFileCharsetSimple(filePath: String): String? {
        return getFileCharsetSimple(File(filePath))
    }

    /**
     * Return the charset of file simply.
     *
     * @param file The file.
     * @return the charset of file simply
     */
    fun getFileCharsetSimple(file: File?): String {
        if (file == null) return ""
        if (isUtf8(file)) return "UTF-8"
        var p = 0
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(file))
            p = (`is`.read() shl 8) + `is`.read()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return when (p) {
            0xfffe -> "Unicode"
            0xfeff -> "UTF-16BE"
            else -> "GBK"
        }
    }

    /**
     * Return whether the charset of file is utf8.
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isUtf8(filePath: String): Boolean {
        return isUtf8(File(filePath))
    }

    /**
     * Return whether the charset of file is utf8.
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isUtf8(file: File?): Boolean {
        if (file == null) return false
        var `is`: InputStream? = null
        try {
            val bytes = ByteArray(24)
            `is` = BufferedInputStream(FileInputStream(file))
            val read = `is`.read(bytes)
            return if (read != -1) {
                val readArr = ByteArray(read)
                System.arraycopy(bytes, 0, readArr, 0, read)
                isUtf8(readArr) == 100
            } else {
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * UTF-8编码方式
     * ----------------------------------------------
     * 0xxxxxxx
     * 110xxxxx 10xxxxxx
     * 1110xxxx 10xxxxxx 10xxxxxx
     * 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
     */
    private fun isUtf8(raw: ByteArray): Int {
        var utf8 = 0
        var ascii = 0
        if (raw.size > 3) {
            if (raw[0].toInt() == 0xEF && raw[1].toInt() == 0xBB && raw[2].toInt() == 0xBF) {
                return 100
            }
        }
        val len: Int = raw.size
        var child = 0
        var i: Int = 0
        while (i < len) {

            // UTF-8 byte shouldn't be FF and FE
            if (raw[i].toInt() and 0xFF.toByte().toInt() == 0xFF.toByte()
                    .toInt() || raw[i].toInt() and 0xFE.toByte().toInt() == 0xFE.toByte().toInt()
            ) {
                return 0
            }
            if (child == 0) {
                // ASCII format is 0x0*******
                if (raw[i].toInt() and 0x7F.toByte()
                        .toInt() == raw[i].toInt() && raw[i].toInt() != 0
                ) {
                    ascii++
                } else if (raw[i].toInt() and 0xC0.toByte().toInt() == 0xC0.toByte().toInt()) {
                    // 0x11****** maybe is UTF-8
                    for (bit in 0..7) {
                        child = if ((0x80 shr bit).toByte()
                                .toInt() and raw[i].toInt() == (0x80 shr bit).toByte()
                                .toInt()
                        ) {
                            bit
                        } else {
                            break
                        }
                    }
                    utf8++
                }
                i++
            } else {
                child = if (raw.size - i > child) child else raw.size - i
                var currentNotUtf8 = false
                for (children in 0 until child) {
                    // format must is 0x10******
                    if (raw[i + children].toInt() and 0x80.toByte().toInt() != 0x80.toByte()
                            .toInt()
                    ) {
                        if (raw[i + children].toInt() and 0x7F.toByte()
                                .toInt() == raw[i + children].toInt() && raw[i].toInt() != 0
                        ) {
                            // ASCII format is 0x0*******
                            ascii++
                        }
                        currentNotUtf8 = true
                    }
                }
                if (currentNotUtf8) {
                    utf8--
                    i++
                } else {
                    utf8 += child
                    i += child
                }
                child = 0
            }
        }
        // UTF-8 contains ASCII
        return if (ascii == len) {
            100
        } else (100 * ((utf8 + ascii).toFloat() / len.toFloat())).toInt()
    }
}