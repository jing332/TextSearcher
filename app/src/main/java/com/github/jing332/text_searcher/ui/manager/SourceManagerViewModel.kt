package com.github.jing332.text_searcher.ui.manager

import androidx.lifecycle.ViewModel
import com.github.jing332.text_searcher.data.appDb
import com.github.jing332.text_searcher.data.entites.SearchSource
import kotlin.math.max
import kotlin.math.min

class SourceManagerViewModel() : ViewModel() {
    val models
        get() = appDb.searchSource.all

    fun upMove(src: SearchSource) {
        reOrderList(src, -1)
    }

    fun downMove(src: SearchSource) {
        reOrderList(src, +1)
    }

    private fun reOrderList(src: SearchSource, offset: Int) {
        val list = models.toMutableList()
        val i = list.indexOfFirst { src.id == it.id }
        list.removeAt(i)

        val toIndex = min(list.size, max(0, i + offset))

        list.add(toIndex, src)

        list.toList().forEachIndexed { index, searchSource ->
            list[index] = searchSource.copy(order = index)
        }

        appDb.searchSource.update(*list.toTypedArray())
    }

}