package com.github.jing332.text_searcher.ui.search.texttoolbar

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.ActionMode
import android.view.View
import androidx.annotation.DoNotInline
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus

class CustomTextToolbar(private val view: View, onTtsRequested: (() -> Unit)? = null) :
    TextToolbar {
    companion object {
        const val TAG = "CustomTextToolbar"
    }

    private var actionMode: ActionMode? = null
    private val textActionModeCallback: TextActionModeCallback =
        TextActionModeCallback(
            onActionModeDestroy = { actionMode = null },
            onTtsRequested = { onTtsRequested?.invoke() }
        )
    override var status: TextToolbarStatus = TextToolbarStatus.Hidden
        private set

    @SuppressLint("ObsoleteSdkInt")
    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?
    ) {
        Log.d(TAG, "showMenu: ")

        textActionModeCallback.rect = rect
        textActionModeCallback.onCopyRequested = onCopyRequested
        textActionModeCallback.onCutRequested = onCutRequested
        textActionModeCallback.onPasteRequested = onPasteRequested
        textActionModeCallback.onSelectAllRequested = onSelectAllRequested
        if (actionMode == null) {
            status = TextToolbarStatus.Shown
            actionMode = if (Build.VERSION.SDK_INT >= 23) {
                TextToolbarHelperMethods.startActionMode(
                    view,
                    FloatingTextActionModeCallback(textActionModeCallback),
                    ActionMode.TYPE_FLOATING
                )
            } else {
                view.startActionMode(
                    PrimaryTextActionModeCallback(textActionModeCallback)
                )
            }
        } else {
            actionMode?.invalidate()
        }
    }

    override fun hide() {
        Log.d(TAG, "hide: ")
        
        status = TextToolbarStatus.Hidden
        actionMode?.finish()
        actionMode = null
    }
}

/**
 * This class is here to ensure that the classes that use this API will get verified and can be
 * AOT compiled. It is expected that this class will soft-fail verification, but the classes
 * which use this method will pass.
 */
@SuppressLint("ObsoleteSdkInt")
@RequiresApi(23)
internal object TextToolbarHelperMethods {
    @RequiresApi(23)
    @DoNotInline
    fun startActionMode(
        view: View,
        actionModeCallback: ActionMode.Callback,
        type: Int
    ): ActionMode? {
        return view.startActionMode(
            actionModeCallback,
            type
        )
    }

    @RequiresApi(23)
    @DoNotInline
    fun invalidateContentRect(actionMode: ActionMode) {
        actionMode.invalidateContentRect()
    }
}

