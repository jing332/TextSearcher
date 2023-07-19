package com.github.jing332.text_searcher.ui

import androidx.annotation.StringRes
import com.github.jing332.text_searcher.R

sealed class AppNavRoutes(@StringRes val titleResId: Int, val route: String) {
    object SourceManager : AppNavRoutes(R.string.search_source_manager, "SourceManager")
    object SourceEdit : AppNavRoutes(R.string.edit_search_source, "SourceEdit") {
        const val KEY_SOURCE = "gpt_source"
    }

    //    object DictFileManager : AppNavRoutes(R.string.confirm, "DictFileManager")
    object About : AppNavRoutes(R.string.about, "About")
}