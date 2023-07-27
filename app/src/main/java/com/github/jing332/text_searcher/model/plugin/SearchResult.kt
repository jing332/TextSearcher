package com.github.jing332.text_searcher.model.plugin

class SearchResult {
    private var mResultChangedListener: ResultChangedListener? = null

    private var mResult: String = ""
    val result: String
        get() = mResult

    private fun setResult(ret: String) {
        mResult = ret
        mResultChangedListener?.onResultChanged(mResult)
    }

    fun setChangeListener(listener: ResultChangedListener?) {
        mResultChangedListener = listener
    }

    fun interface ResultChangedListener {
        fun onResultChanged(result: String)
    }
}