package com.github.jing332.text_searcher.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AboutScreen(drawerState: DrawerState) {
    Scaffold {
        Text(modifier = Modifier.padding(it), text = "ABOUT")
    }
}