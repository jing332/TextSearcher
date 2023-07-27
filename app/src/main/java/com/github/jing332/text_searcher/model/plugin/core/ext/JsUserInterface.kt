package com.github.jing332.tts_server_android.model.rhino.core.ext

import com.github.jing332.text_searcher.app
import com.github.jing332.text_searcher.utils.ToastUtils.longToast
import com.github.jing332.text_searcher.utils.ToastUtils.toast


interface JsUserInterface {
    fun toast(msg: String) = app.toast(msg)
    fun longToast(msg: String) = app.longToast(msg)

//    fun setMargins(v: View, left: Int, top: Int, right: Int, bottom: Int) {
//        (v.layoutParams as ViewGroup.MarginLayoutParams).setMargins(
//            left.dp,
//            top.dp,
//            right.dp,
//            bottom.dp
//        )
//    }
//
}