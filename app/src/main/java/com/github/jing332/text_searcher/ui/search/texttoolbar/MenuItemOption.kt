package com.github.jing332.text_searcher.ui.search.texttoolbar

import com.github.jing332.text_searcher.R

internal enum class MenuItemOption(val id: Int) {
    Copy(0),
    Paste(1),
    Cut(2),
    SelectAll(3),
    TTS(4);

    val titleResource: Int
        get() = when (this) {
            Copy -> android.R.string.copy
            Paste -> android.R.string.paste
            Cut -> android.R.string.cut
            SelectAll -> android.R.string.selectAll
            TTS -> R.string.tts
        }

    /**
     * This item will be shown before all items that have order greater than this value.
     */
    val order = id
}