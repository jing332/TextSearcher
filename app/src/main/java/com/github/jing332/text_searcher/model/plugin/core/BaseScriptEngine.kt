package com.github.jing332.text_searcher.model.plugin.core

import com.github.jing332.tts_server_android.model.rhino.core.Logger
import com.script.rhino.RhinoScriptEngine
import org.mozilla.javascript.NativeObject

open class BaseScriptEngine(
    open val rhino: RhinoScriptEngine = RhinoScriptEngine(),
    open val txtsrObject: BaseScriptEngineContext,
    open var code: String = "",
    open val logger: Logger = Logger.global,
) {
    companion object {
        const val OBJ_TTSRV = "txtsr"
        const val OBJ_LOGGER = "logger"
    }

    open fun findObject(name: String): NativeObject {
        return rhino[name].run {
            if (this == null) throw Exception("Not found object: $name")
            else this as NativeObject
        }
    }

    fun putDefaultObjects() {
        rhino.put(OBJ_TTSRV, txtsrObject)
        rhino.put(OBJ_LOGGER, logger)
    }

    @Synchronized
    open fun eval(
        prefixCode: String = ""
    ): Any? {
        putDefaultObjects()

        return rhino.eval("${prefixCode.removePrefix(";").removeSuffix(";")};$code")
    }

}