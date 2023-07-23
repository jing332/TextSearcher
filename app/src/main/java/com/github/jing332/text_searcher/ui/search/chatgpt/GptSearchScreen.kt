package com.github.jing332.text_searcher.ui.search.chatgpt

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.getSelectedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.model.source.ChatGptAppearance
import com.github.jing332.text_searcher.model.source.ChatGptSourceEntity
import com.github.jing332.text_searcher.model.source.ChatGptTTS
import com.github.jing332.text_searcher.service.TtsService
import com.github.jing332.text_searcher.ui.search.SearchSourceState
import com.github.jing332.text_searcher.ui.search.chatgpt.tts.TtsSettingsDialog
import com.github.jing332.text_searcher.ui.search.texttoolbar.CustomTextToolbar
import com.github.jing332.text_searcher.ui.widgets.ExpandableText
import kotlinx.coroutines.launch

@Composable
fun GptSearchScreen(
    src: SearchSource,
    text: String,
    state: SearchSourceState,
    onSrcChange: (SearchSource) -> Unit,
) {
    val entity = src.sourceEntity as ChatGptSourceEntity

    fun onEntityChange(en: ChatGptSourceEntity) {
        onSrcChange(src.copy(sourceEntity = en))
    }

    ChatGPTScreen(
        id = src.id,
        state = state,
        message = entity.messageTemplate.replace("\$text", text),
        token = entity.apiKey,
        model = entity.model,
        systemPrompt = entity.systemPrompt,

        titleAppearance = entity.titleAppearance,
        contentAppearance = entity.contentAppearance,
        onTitleAppearanceChange = {
            onEntityChange(entity.copy(titleAppearance = it))
        },
        onContentAppearanceChange = {
            onEntityChange(entity.copy(contentAppearance = it))
        },

        tts = entity.tts,
        onTtsChange = {
            onEntityChange(entity.copy(tts = it))
        },

        testText = src.testText,
        onTestTextChange = {
            onSrcChange(src.copy(testText = it))
        },
    )
}

@Composable
private fun ChatGPTScreen(
    id: Long,
    modifier: Modifier = Modifier,
    state: SearchSourceState,
    token: String,
    model: String,
    systemPrompt: String,
    message: String,
    titleAppearance: ChatGptAppearance,
    contentAppearance: ChatGptAppearance,
    onTitleAppearanceChange: (ChatGptAppearance) -> Unit,
    onContentAppearanceChange: (ChatGptAppearance) -> Unit,

    tts: ChatGptTTS,
    onTtsChange: (ChatGptTTS) -> Unit,

    testText: String,
    onTestTextChange: (String) -> Unit,

    vm: GptSearchScreenViewModel = viewModel(key = id.toString())
) {
    val context = LocalContext.current

    var mTitleAppearance by remember { mutableStateOf(titleAppearance) }
    var mContentAppearance by remember { mutableStateOf(contentAppearance) }

    fun load() {
        vm.load(context)
        if (tts.isEnabled) {
            context.startService(Intent(context, TtsService::class.java).apply {
                putExtra(TtsService.KEY_TTS_TEXT, message)
                putExtra(TtsService.KEY_TTS_CONFIG, tts)
            })
        }

        if (token.isBlank()) {
            vm.errorMessage = context.getString(R.string.error_open_ai_api_key_empty)
            return
        }

        vm.viewModelScope.launch {
            try {
                vm.requestGpt(
                    context,
                    msg = message,
                    token = token,
                    model = model,
                    systemPrompt = systemPrompt,
                )
            } catch (e: Exception) {
                vm.errorMessage = e.message ?: e.toString()
            }
        }
    }

    if (state.requestLoad) {
        load()
        state.requestLoad = false
    }

    var gptAppearanceScreenVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Column(modifier.verticalScroll(scrollState)) {
        Row {
            Column(
                modifier
                    .weight(1f)
                    .clickable(
                        enabled = true,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = true),
                        onClick = { gptAppearanceScreenVisible = !gptAppearanceScreenVisible },
                    )
            ) {
                Row(Modifier.align(Alignment.CenterHorizontally)) {
                    Text(text = stringResource(R.string.appearance_settings))
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.appearance_settings),
                    )
                }
            }

            var showTtsSettings by remember { mutableStateOf(false) }
            if (showTtsSettings) {
                var vTts by remember { mutableStateOf(tts) }
                var vTestText by remember { mutableStateOf(testText) }

                TtsSettingsDialog(
                    onDismissRequest = { showTtsSettings = false },
                    tts = vTts,
                    message = message,
                    testText = vTestText,
                    onTestTextChange = {
                        vTestText = it
                        onTestTextChange(it)
                    },
                    onTtsChange = {
                        vTts = it
                        onTtsChange(it)
                    }
                )
            }
            /*Divider(
                Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .padding(horizontal = 2.dp)
            )*/
            Column(
                Modifier
                    .weight(1f)
                    .clickable(
                        enabled = true,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = true),
                        onClick = { showTtsSettings = true },
                    )
            ) {
                Row(
                    Modifier.align(Alignment.CenterHorizontally)

                ) {
                    Text(text = stringResource(R.string.tts_settings))
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(R.string.tts_settings),
                    )
                }
            }
        }

        // GPT外观设置
        if (gptAppearanceScreenVisible) GptAppearanceSettingsScreen(
            Modifier
                .animateContentSize()
                .padding(horizontal = 2.dp),
            titleAppearance = mTitleAppearance,
            onTitleAppearanceChange = {
                onTitleAppearanceChange(it)
                mTitleAppearance = it
            },
            contentAppearance = mContentAppearance,
            onContentAppearanceChange = {
                onContentAppearanceChange(it)
                mContentAppearance = it
            },
        )
        val ffResolver = LocalFontFamilyResolver.current
        fun fontFamily(uri: Uri): FontFamily {
            try {
                context.contentResolver.openFileDescriptor(uri, "r")?.use {
                    return FontFamily(Font(it)).apply {
                        ffResolver.resolve(this)
                    }
                }
            } catch (_: Exception) {
            }

            return FontFamily.Default
        }
        ExpandableText(
            text = message, style = MaterialTheme.typography.titleMedium,
            fontFamily = fontFamily(mTitleAppearance.fontUri),
            fontSize = mTitleAppearance.fontSize.sp,
            lineHeight = mTitleAppearance.fontSize.sp * mTitleAppearance.lineWidthScale,
            fontWeight = FontWeight(mTitleAppearance.fontWeight)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Box {
            var text by remember { mutableStateOf(TextFieldValue(vm.errorMessage.ifEmpty { vm.result })) }
            val localView = LocalView.current
            val textToolbar = remember {
                CustomTextToolbar(localView, onTtsRequested = {
                    val selectedText = text.getSelectedText().text
                    if (selectedText.isNotEmpty()) {
                        context.startService(Intent(context, TtsService::class.java).apply {
                            putExtra(TtsService.KEY_TTS_TEXT, selectedText)
                            putExtra(TtsService.KEY_TTS_CONFIG, tts)
                        })
                    }
                })
            }

            CompositionLocalProvider(LocalTextToolbar provides textToolbar) {
                LaunchedEffect(key1 = vm.errorMessage, key2 = vm.result) {
                    text = text.copy(text = vm.errorMessage.ifEmpty { vm.result })
                }

                TextWithSelectedText(
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = fontFamily(mContentAppearance.fontUri),
                        fontSize = mContentAppearance.fontSize.sp,
                        fontWeight = FontWeight(mContentAppearance.fontWeight),
                        lineHeight = mContentAppearance.fontSize.sp * mContentAppearance.lineWidthScale,
                        color = if (vm.errorMessage.isEmpty()) Color.Unspecified else MaterialTheme.colorScheme.error
                    ),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextWithSelectedText(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors(
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedTextColor = LocalTextStyle.current.color,
        unfocusedTextColor = LocalTextStyle.current.color
    ),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    selectionColors: TextSelectionColors = TextSelectionColors(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.primaryContainer
    ),
) {
    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse { MaterialTheme.colorScheme.onBackground }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    CompositionLocalProvider(
        LocalTextSelectionColors provides selectionColors,
        LocalTextInputService provides null,
    ) {
        BasicTextField(
            value = value,
            modifier = modifier
                .defaultMinSize(
                    minWidth = TextFieldDefaults.MinWidth,
                    minHeight = TextFieldDefaults.MinHeight
                ),
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(Color.Blue),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            decorationBox = @Composable { innerTextField ->
                // places leading icon, text field with label and placeholder, trailing icon
                TextFieldDefaults.DecorationBox(
                    value = value.text,
                    visualTransformation = visualTransformation,
                    innerTextField = innerTextField,
                    placeholder = placeholder,
                    label = label,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    supportingText = supportingText,
                    shape = shape,
                    singleLine = singleLine,
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    contentPadding = contentPadding,
                )
            }
        )
    }
}

@Preview
@Composable
fun TextWithSelectedTextPreview() {
    val selectedText = remember { mutableStateOf("") }
    val localView = LocalView.current
    val textToolbar = remember {
        CustomTextToolbar(localView, onTtsRequested = {
            println("onTtsRequested: ${selectedText.value}")
        })
    }

    CompositionLocalProvider(LocalTextToolbar provides textToolbar) {
        var text by remember { mutableStateOf(TextFieldValue("Hello world!")) }

        TextWithSelectedText(
            value = text,
            onValueChange = {
                text = it
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            enabled = true,
        )
    }
}
