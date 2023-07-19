package com.github.jing332.text_searcher.ui.search.chatgpt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drake.net.utils.withMain
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.help.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException


@Composable
private fun LoadingScreen(modifier: Modifier = Modifier.padding(4.dp), text: String) {
    Column(modifier) {
        CircularProgressIndicator()
        Text(text = text, modifier = Modifier.padding(top = 4.dp))
    }
}

@Preview
@Composable
fun PreviewLoadingScreen() {
    LoadingScreen(Modifier.padding(8.dp), "加载中")
}

@Composable
private fun ModelSelectionScreen(
    currentModel: String,
    onModelChange: (String) -> Unit,
    vm: ModelSelectionViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(key1 = vm.hashCode(), block = {
        scope.launch(Dispatchers.IO) {
            try {
                vm.loadModels(context, AppConfig.openAiApiKey.value, AppConfig.openAiModel.value)
                withMain {
                    val i = vm.models.indexOf(currentModel)
                    if (i >= 0) vm.lazyListState.scrollToItem(i)
                }
            } catch (_: CancellationException) {
            } catch (e: Exception) {
                errorMessage = e.toString()
            }
        }
    })

    if (errorMessage.isNotEmpty())
        Text(
            text = errorMessage,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.error
        )

    LazyColumn(state = vm.lazyListState) {
        items(vm.models, { it }) {
            Row(
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(true),
                        onClick = { onModelChange(it) }
                    )
            ) {
                RadioButton(
                    selected = it == currentModel,
                    onClick = { onModelChange(it) },
                )
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (it == currentModel) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun ModelSelectionDialog(
    currentModel: String, onSelectChange: (String) -> Unit, onDismissRequest: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = { onDismissRequest() }) {
                Text(text = stringResource(id = R.string.close))
            }
        }, title = {
            Text(
                text = stringResource(id = R.string.openai_model),
                style = MaterialTheme.typography.titleMedium
            )
        }, text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                var isLoading by remember { mutableStateOf(false) }
                if (isLoading)
                    LoadingScreen(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(id = R.string.loading)
                    )
                else {
                    ModelSelectionScreen(
                        currentModel = currentModel,
                        onModelChange = onSelectChange
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun PreviewModelSelectionDialog() {
    var showDialog by remember { mutableStateOf(true) }
    if (showDialog) {
        ModelSelectionDialog(
            "gpt-3.5-turbo",
            onDismissRequest = { showDialog = false },
            onSelectChange = {}
        )
    }
}