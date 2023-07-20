package com.github.jing332.text_searcher.ui.search.chatgpt.tts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.github.jing332.text_searcher.ui.widgets.ExposedDropTextField
import com.github.jing332.text_searcher.ui.widgets.LabelSlider
import com.github.jing332.text_searcher.ui.widgets.LoadingDialog
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
fun TtsSettingsDialog(
    onDismissRequest: () -> Unit,
    tts: ChatGptTTS,
    // testText 为空时使用 message
    message: String,
    testText: String,
    onTestTextChange: (String) -> Unit,
    onTtsChange: (ChatGptTTS) -> Unit,
) {
    BaseSearchDialog(onDismissRequest = onDismissRequest) {
        Column(Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.tts_settings),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .minimumInteractiveComponentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple()
                    ) {
                        onTtsChange(tts.copy(isEnabled = !tts.isEnabled))
                    }
            ) {
                Text(
                    "TTS开关",
                    modifier = Modifier.align(Alignment.CenterVertically),

                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = tts.isEnabled, onCheckedChange = {
                        onTtsChange(tts.copy(isEnabled = it))
                    }
                )
            }

            if (tts.isEnabled)
                TtsSettingsContent(
                    tts = tts,
                    message = message,
                    testText = testText,
                    onTestTextChange = onTestTextChange,
                    onTtsChange = onTtsChange,
                )

        }
    }
}

@Composable
private fun TtsSettingsContent(
    tts: ChatGptTTS,
    // testText 为空时使用 message
    message: String,
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

    DisposableEffect(key1 = vm.hashCode()) {
        onDispose {

        }
    }

    var showLoading by remember { mutableStateOf(false) }
    if (showLoading)
        LoadingDialog(onDismissRequest = { showLoading = false })

    Column {
        Column(modifier = Modifier.padding(4.dp)) {
            ExposedDropTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                label = { Text(stringResource(R.string.tts_engine)) },
                key = tts.engine,
                keys = vm.engines.map { it.name },
                values = vm.engines.map { it.label },
                onKeyChange = {
                    scope.launch {
                        showLoading = true
                        vm.updateEngine(it.toString())
                        vm.updateLocale()
                        showLoading = false
                    }

                    onTtsChange(tts.copy(engine = it.toString()))
                }
            )

            ExposedDropTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                label = { Text(stringResource(R.string.locale)) },
                key = Locale.forLanguageTag(tts.locale.ifEmpty {
                    Locale.getDefault().toLanguageTag()
                }),
                keys = vm.locales,
                values = vm.locales.map { it.displayName },
                onKeyChange = {
                    if (it is Locale) {
                        vm.updateVoice(it)
                        onTtsChange(tts.copy(locale = it.toLanguageTag()))
                    }
                }
            )

            ExposedDropTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                label = { Text(stringResource(R.string.voice)) },
                key = tts.voice,
                keys = vm.voices.map { it.name },
                values = vm.voices.map { it.name },
                onKeyChange = {
                    if (it is String)
                        onTtsChange(tts.copy(voice = it.toString()))
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
        }

        fun speak() {
            scope.launch {
                showLoading = true
                vm.speak(
                    text = testText.ifBlank { message },
                    locale = vm.locales.find { it.toLanguageTag() == tts.locale },
                    voice = vm.voices.find { it.name == tts.voice },
                    speechRate = tts.speechRate,
                    pitch = tts.pitch
                )
                showLoading = false
            }
        }

        Divider(Modifier.fillMaxWidth())

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
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
            placeholder = { Text(message) }
        )
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
            message = "",
            testText = testText,
            onTestTextChange = { testText = it }
        )
}
