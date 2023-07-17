package com.github.jing332.text_searcher.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.funny.data_saver.core.LocalDataSaver
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.help.AppConfig
import com.github.jing332.text_searcher.ui.chatgpt.ChatGPTSettingsScreen
import com.github.jing332.text_searcher.ui.theme.TxtSearcher
import com.github.jing332.text_searcher.ui.widgets.TransparentSystemBars

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        AppConfig.fillDefaultValues(this)

        setContent {
            CompositionLocalProvider(
                LocalDataSaver provides AppConfig.dataSaverPref,
            ) {
                TxtSearcher {
                    TransparentSystemBars()
                    Scaffold(
                        modifier = Modifier.imePadding(),
                        topBar = {
                            TopAppBar(
                                title = { Text(stringResource(id = R.string.app_name)) },
                                actions = {
                                    IconButton({}) {
                                        Icon(
                                            Icons.Filled.MoreVert,
                                            contentDescription = stringResource(R.string.more_options),
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            )
                        }

                    ) {
                        Surface(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            ChatGPTSettingsScreen()
                        }
                    }
                }
            }
        }
    }
}


