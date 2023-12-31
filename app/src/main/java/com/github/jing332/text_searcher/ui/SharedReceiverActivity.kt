package com.github.jing332.text_searcher.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.github.jing332.text_searcher.ui.search.SearcherDialog
import com.github.jing332.text_searcher.ui.theme.TxtSearcherTheme
import com.github.jing332.text_searcher.ui.widgets.TransparentSystemBars

class SharedReceiverActivity : ComponentActivity() {
    private val receivingType = "text/plain"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var inputText by mutableStateOf("")
        when {
            intent.action == Intent.ACTION_SEND && intent.type == receivingType -> {
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                    inputText = it
                }
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && intent.action == Intent.ACTION_PROCESS_TEXT
                    && intent.type == receivingType -> {
                intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)?.let {
                    inputText = it
                }
            }

            intent.getStringExtra("action") == "readAloud" -> {
                TODO("intent.getStringExtra(\"action\") == \"readAloud\" ->")
            }
        }
        setContent {
            TxtSearcherTheme {
                TransparentSystemBars()
                var isVisible by remember { mutableStateOf(true) }

                if (isVisible)
                    SearcherDialog({ isVisible = false }, inputText)
                else
                    finish()
            }
        }
    }


}
