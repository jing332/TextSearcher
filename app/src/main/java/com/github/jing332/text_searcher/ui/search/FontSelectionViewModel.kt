package com.github.jing332.text_searcher.ui.search

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel

class FontSelectionViewModel : ViewModel() {
    val fontList = mutableStateListOf<FontItem>()

    fun loadFonts(context: Context, fontDir: String, ffResolver: FontFamily.Resolver) {
        val files = DocumentFile.fromTreeUri(context, fontDir.toUri())
        files?.let {
            it.listFiles().forEach { documentFile ->
                context.contentResolver.openFileDescriptor(documentFile.uri, "r")?.use { pfd ->
                    runCatching {
                        val ff = FontFamily(Font(pfd))
                        ffResolver.resolve(ff)

                        fontList.add(
                            FontItem(documentFile.uri.toString(), documentFile.name ?: "", ff)
                        )
                    }

                }
            }
        }
    }

    data class FontItem(val uri: String, val name: String, val fontFamily: FontFamily)
}