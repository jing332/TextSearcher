package com.github.jing332.text_searcher.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen(drawerState: DrawerState) {
    Scaffold {
        Column(Modifier.padding(it).padding(8.dp)) {
            Text(text = "ABOUT")
            SelectionContainer {
                Text("Github项目地址：https://github.com/jing332/TextSearcher")
            }
        }
    }
}