package com.github.jing332.text_searcher.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.help.AppConfig
import com.github.jing332.text_searcher.ui.LocalNavController
import com.github.jing332.text_searcher.ui.LocalSnackbarHostState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseSourceEditScreen(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
    src: SearchSource,
    onSave: (SearchSource) -> Unit,
    onTest: (String) -> Unit,
) {
    val navController = LocalNavController.current
    var name by remember { mutableStateOf(src.name) }
    var testText by remember { AppConfig.testText }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onSave(src.copy(name = name))
                    }) {
                        Icon(Icons.Filled.Save, contentDescription = "Save")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(LocalSnackbarHostState.current) }
    ) { padValues ->
        Column(Modifier.padding(padValues)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                OutlinedTextField(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name)) }
                )

                var isVisibleTintText by remember { mutableStateOf(false) }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .onFocusChanged { isVisibleTintText = it.hasFocus },
                    value = testText,
                    onValueChange = { testText = it },
                    label = { Text(stringResource(id = R.string.test)) },
                    leadingIcon = { Icon(Icons.Filled.BugReport, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = {
                            onTest(testText)
                        }) {
                            Icon(
                                Icons.Filled.TextFields,
                                contentDescription = stringResource(R.string.test),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    supportingText = {
                        if (isVisibleTintText) Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.test_button_hint),
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                )
            }

            Divider(Modifier.padding(vertical = 4.dp))

            content()
        }
    }
}