package com.github.jing332.text_searcher.ui.search.chatgpt.tts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.model.source.ChatGptTTS
import com.github.jing332.text_searcher.ui.search.BaseSearchDialog
import com.github.jing332.text_searcher.ui.widgets.DropMenuTextField
import com.github.jing332.text_searcher.ui.widgets.LabelSlider
import com.github.jing332.text_searcher.ui.widgets.LoadingDialog
import kotlinx.coroutines.launch


@Composable
fun TtsSettingsDialog(
    onDismissRequest: () -> Unit,
    tts: ChatGptTTS,
    testText: String,
    onTestTextChange: (String) -> Unit,
    onTtsChange: (ChatGptTTS) -> Unit,
    vm: TtsSettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(vm.hashCode()) {
        vm.load(context)
    }

    BaseSearchDialog(onDismissRequest = onDismissRequest) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                stringResource(R.string.tts_settings),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally)
            )

            DropMenuTextField(
                label = { Text(stringResource(R.string.tts_engine)) },
                key = tts.engine,
                keys = vm.engines.map { it.name },
                values = vm.engines.map { it.label },
                onKeyChange = {
                    onTtsChange(tts.copy(engine = it.toString()))
                }
            )

            LabelSlider(value = tts.speechRate, onValueChange = {
                onTtsChange(tts.copy(speechRate = String.format("%.2f", it).toFloat()))
            }, valueRange = 0.1f..3f, text = {
                Text(stringResource(R.string.tts_speech_rate, tts.speechRate))
            })

            LabelSlider(value = tts.pitch, onValueChange = {
                onTtsChange(tts.copy(pitch = String.format("%.2f", it).toFloat()))
            }, valueRange = 0.1f..3f, text = {
                Text(stringResource(R.string.tts_pitch, tts.pitch))
            })

            var showLoading by remember { mutableStateOf(false) }
            if (showLoading)
                LoadingDialog(onDismissRequest = { showLoading = false }, false)


            fun speak() {
                scope.launch {
                    showLoading = true
                    vm.speak(context, tts.engine, testText, tts.speechRate, tts.pitch)
                    showLoading = false
                }
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = testText,
                onValueChange = { onTestTextChange(it) },
                trailingIcon = {
                    IconButton(onClick = {
                        speak()
                    }) {
                        Icon(Icons.Default.BugReport, "test")
                    }
                },
                label = { Text(stringResource(R.string.test)) },
            )
        }
    }
}

@Preview
@Composable
private fun TtsSettingsPreview() {
    var show by remember { mutableStateOf(true) }
    var tts by remember { mutableStateOf(ChatGptTTS()) }
    var testText by remember { mutableStateOf("test") }
    if (show)
        TtsSettingsDialog(
            onDismissRequest = { show = false },
            tts = tts,
            onTtsChange = { tts = it },
            testText = testText,
            onTestTextChange = { testText = it }
        )
}