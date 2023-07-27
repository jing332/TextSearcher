package com.github.jing332.text_searcher.model.plugin.core

import android.content.Context
import com.github.jing332.text_searcher.model.plugin.core.ext.JsExtensions

/**
 * ttsrv 对象类
 */
open class BaseScriptEngineContext(
    override val context: Context, override val engineId: String,

    /*val globalData: Map<Any, Any> = BaseScriptEngineContext.globalDataSet.run {
        if (!this.containsKey(engineId))
            this[engineId] = mutableMapOf()

        this[engineId]!!
    }*/
) :
    JsExtensions(context, engineId) {
    /*companion object {
        private val globalDataSet = mutableMapOf<String, Map<Any, Any>>()
    }*/
}