package com.github.jing332.text_searcher.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SearchSourceState(initialLoad: Boolean = true) {
    var requestLoad by mutableStateOf(initialLoad)
}