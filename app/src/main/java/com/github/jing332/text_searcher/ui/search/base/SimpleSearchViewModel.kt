package com.github.jing332.text_searcher.ui.search.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

open class SimpleSearchViewModel : ViewModel() {
    var result by mutableStateOf("")
    var errorMessage by mutableStateOf("")

    var isLoading by mutableStateOf(false)
}