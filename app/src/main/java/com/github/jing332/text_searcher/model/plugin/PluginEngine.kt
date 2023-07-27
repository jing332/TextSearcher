package com.github.jing332.text_searcher.model.plugin

import android.content.Context
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.model.plugin.core.BaseScriptEngine
import com.github.jing332.text_searcher.model.plugin.core.BaseScriptEngineContext
import org.mozilla.javascript.ScriptableObject

class PluginEngine(
    val context: Context,
    engineId: String,
    override var code: String
) : BaseScriptEngine(txtsrObject = BaseScriptEngineContext(context, engineId)) {

    val searchJS: ScriptableObject
        get() = rhino["searchJS"] as ScriptableObject

    fun getPluginInfo(): PluginInfo {
        rhino.eval(code)
        searchJS.apply {
            val name = get("name").toString()
            val id = get("id").toString()
            val author = get("author").toString()

            var version = 0
            runCatching {
                version = (get("version") as Double).toInt()
            }.onFailure {
                throw NumberFormatException(context.getString(R.string.plugin_bad_format))
            }

            return PluginInfo(name, id, author, version)
        }
    }

    fun getSearchResult(text: String, ret: SearchResult): String {
        return rhino.invokeMethod(searchJS, "getSearchResult", text, ret).toString()
    }
}