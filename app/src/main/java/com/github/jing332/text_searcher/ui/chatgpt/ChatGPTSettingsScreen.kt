package com.github.jing332.text_searcher.ui.chatgpt

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.help.AppConfig
import com.github.jing332.text_searcher.ui.SharedReceiverActivity

private fun doTest(context: Context, text: String) {
    context.startActivity(Intent(context, SharedReceiverActivity::class.java).apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    })
}


@Composable
fun ChatGPTSettingsScreen() {
    var showModelSelectionDialog by remember { mutableStateOf(false) }
    if (showModelSelectionDialog)
        ModelSelectionDialog(
            currentModel = AppConfig.openAiModel.value,
            onDismissRequest = { showModelSelectionDialog = false },
            onSelectChange = {
                AppConfig.openAiModel.value = it
                showModelSelectionDialog = false
            },
        )

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        var openAiApiKey by remember { AppConfig.openAiApiKey }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            value = openAiApiKey,
            onValueChange = { openAiApiKey = it },
            label = { Text(stringResource(R.string.openai_api_key)) },
            leadingIcon = { Icon(Icons.Filled.Api, contentDescription = "") }
        )

        var openAiModel by remember { AppConfig.openAiModel }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true)
                ) { showModelSelectionDialog = true },
            value = openAiModel,
            onValueChange = { openAiModel = it },
            label = { Text(stringResource(R.string.openai_model)) },
            readOnly = true,
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurface,
            ),
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        var systemPrompt by remember { AppConfig.systemPrompt }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            value = systemPrompt,
            onValueChange = { systemPrompt = it },
            label = { Text(stringResource(R.string.system_prompt_label)) },
            leadingIcon = { Icon(Icons.Filled.Info, contentDescription = "") }
        )

        var msgTemplate by remember { AppConfig.msgTemplate }
        var msgTemplateError by remember { mutableStateOf(false) }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            value = msgTemplate,
            onValueChange = {
                if (it.contains("\$text")) {
                    msgTemplateError = false
                    msgTemplate = it
                } else
                    msgTemplateError = true
            },
            label = { Text(stringResource(R.string.message_template_label)) },
            leadingIcon = { Icon(Icons.Filled.Message, contentDescription = "") },
            isError = msgTemplateError,
            supportingText = { if (msgTemplateError) Text(stringResource(R.string.message_template_error_msg)) }
        )

        Divider(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        var testText by remember { AppConfig.testText }
        var isVisibleTintText by remember { mutableStateOf(false) }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .onFocusChanged { isVisibleTintText = it.hasFocus },
            value = testText,
            onValueChange = { testText = it },
            label = { Text(stringResource(id = R.string.test)) },
            leadingIcon = { Icon(Icons.Filled.BugReport, contentDescription = "") },
            trailingIcon = {
                IconButton(onClick = { doTest(context, testText) }
                ) {
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
}

@Preview
@Composable
fun PreviewChatGptSettingsScreen() {
    ChatGPTSettingsScreen()
}
