package com.github.jing332.text_searcher.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object ToastUtils {
    fun Context.toast(@StringRes message: Int) {
        runOnUI {
            kotlin.runCatching {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun Context.toast(message: CharSequence?) {
        runOnUI {
            kotlin.runCatching {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun Context.longToast(@StringRes message: Int) {
        runOnUI {
            kotlin.runCatching {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun Context.longToast(message: CharSequence?) {
        runOnUI {
            kotlin.runCatching {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}