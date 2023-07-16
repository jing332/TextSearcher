package com.github.jing332.text_searcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.exception.RateLimitException
import com.github.jing332.text_searcher.help.AppConfig
import kotlinx.coroutines.launch


@Composable
fun SearcherDialog(onDismissRequest: () -> Unit, inputText: String) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            tonalElevation = 4.dp,
            shape = MaterialTheme.shapes.small,
        ) {
            Column {
                SearcherScreen(
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    inputText
                )
                Box(modifier = Modifier.align(Alignment.End)) {
                    IconButton(onClick = { onDismissRequest() }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }
            }
        }
    }
}

@OptIn(BetaOpenAI::class)
@Composable
private fun SearcherScreen(
    modifier: Modifier,
    inputText: String,
    viewModel: SearchDialogViewModel = viewModel()
) {
    var result by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(key1 = viewModel, block = {
        scope.launch {
            try {
                AppConfig.fillDefaultValues(context)

                viewModel.chat(
                    msg = inputText,
                    token = AppConfig.openAiApiKey.value,
                    systemPrompt = AppConfig.systemPrompt.value
                ).collect { compChunk ->
                    compChunk.choices.forEach {
                        result += it.delta?.content ?: ""
                    }
                }
            } catch (e: RateLimitException) {
                result = "RateLimit: 您已被OpenAI限制，请检查可用额度、更换代理或稍后重试。"
            } catch (e: Exception) {
                result = "错误: $e"
            }
        }
    })

    val scrollState = rememberScrollState()
    Column(modifier.verticalScroll(scrollState)) {
        SelectionContainer {
            Text(text = inputText, style = MaterialTheme.typography.titleMedium)
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
        )
        SelectionContainer {
            Text(text = result, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview
@Composable
private fun PreviewSearcherDialog() {
    var isShow by remember { mutableStateOf(true) }
    if (isShow)
        SearcherDialog(onDismissRequest = { isShow = false }, inputText = "帝国主义")
}