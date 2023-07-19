package com.github.jing332.text_searcher.ui.search.chatgpt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.help.AppConfig

@Composable
fun ChatGPTSettingsScreen(
    modifier: Modifier,
    key: String, onKeyChange: (String) -> Unit,
    model: String, onModelChange: (String) -> Unit,
    systemPrompt: String, onSystemPromptChange: (String) -> Unit,
    messageTemplate: String, onMessageTemplateChange: (String) -> Unit,
) {
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
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .verticalScroll(scrollState)
    ) {
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp).run {
                    if (key.isBlank()) this
                    else this.focusRequester(remember { androidx.compose.ui.focus.FocusRequester() })
                },
            value = key,
            onValueChange = onKeyChange,
            label = { Text(stringResource(R.string.openai_api_key)) },
            leadingIcon = { Icon(Icons.Filled.Api, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) stringResource(R.string.password_visible)
                        else stringResource(R.string.password_hide)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = key.isBlank(),
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true)
                ) { showModelSelectionDialog = true },
            value = model,
            onValueChange = onModelChange,
            label = { Text(stringResource(R.string.openai_model)) },
            leadingIcon = { Icon(Icons.Filled.ViewAgenda, contentDescription = null) },
            readOnly = true,
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            value = systemPrompt,
            onValueChange = onSystemPromptChange,
            label = { Text(stringResource(R.string.system_prompt_label)) },
            leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null) }
        )

        var msgTemplateError by remember { mutableStateOf(false) }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            value = messageTemplate,
            onValueChange = {
                msgTemplateError = !it.contains("\$text")
                onMessageTemplateChange(it)
            },
            label = { Text(stringResource(R.string.message_template_label)) },
            leadingIcon = { Icon(Icons.Filled.Message, contentDescription = null) },
            isError = msgTemplateError,
            supportingText = { if (msgTemplateError) Text(stringResource(R.string.message_template_error_msg)) }
        )
    }
}