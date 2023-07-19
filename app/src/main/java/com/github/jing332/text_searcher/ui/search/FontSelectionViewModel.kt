package com.github.jing332.text_searcher.ui.search

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import com.github.jing332.text_searcher.R

class FontSelectionViewModel : ViewModel() {
    val fontList = mutableStateListOf<FontItem>()

    fun loadFonts(context: Context, fontDir: String, ffResolver: FontFamily.Resolver) {
        fontList.clear()

        fontList.add(
            FontItem(
                Uri.EMPTY,
                "",
                context.getString(R.string.system_default_font),
                FontFamily.Default
            )
        )
        val files = DocumentFile.fromTreeUri(context, fontDir.toUri())
        files?.let {
            it.listFiles().forEachIndexed { index, documentFile ->
                context.contentResolver.openFileDescriptor(documentFile.uri, "r")?.use { pfd ->
                    runCatching {
                        val ff = FontFamily(Font(pfd))
                        ffResolver.resolve(ff)

                        fontList.add(
                            FontItem(
                                uri = documentFile.uri,
                                key = documentFile.uri.toString() + index,
                                name = documentFile.name ?: "",
                                fontFamily = ff
                            )
                        )
                    }

                }
            }
        }
    }

    data class FontItem(
        val uri: Uri,
        val key: String,
        val name: String,
        val fontFamily: FontFamily
    )
}