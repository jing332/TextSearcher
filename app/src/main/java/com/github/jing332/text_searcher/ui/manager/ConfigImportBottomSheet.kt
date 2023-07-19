@file:Suppress("DEPRECATION")

package com.github.jing332.tts_dict_editor.ui.replace

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drake.net.Net
import com.drake.net.exception.NetException
import com.drake.net.okhttp.trustSSLCertificate
import com.drake.net.utils.withIO
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.app
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.ui.widgets.ErrorDialog
import com.github.jing332.text_searcher.ui.widgets.LoadingDialog
import com.github.jing332.text_searcher.utils.FileUtils.readAllText
import com.github.jing332.text_searcher.utils.ToastUtils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Response
import splitties.systemservices.clipboardManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigImportBottomSheet(
    onImportFromJson: (String) -> Unit,
    initialFilePath: String = "",
    state: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit = {},
) {
    var isVisibleLoadingDialog by remember { mutableStateOf(false) }
    if (isVisibleLoadingDialog)
        LoadingDialog(
            dismissOnBackPress = true,
            onDismissRequest = {
                isVisibleLoadingDialog = false
            },
        )

    val coroutine = rememberCoroutineScope()
    var filePath by remember { mutableStateOf(initialFilePath) }
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        it?.let {
            filePath = it.toString()
        }
    }
    ModalBottomSheet(
        onDismissRequest = {
            coroutine.launch {
                state.hide()
                onDismiss.invoke()
            }
        }, sheetState = state,
        modifier = Modifier.fillMaxSize()
    ) {
        var url by remember { mutableStateOf("") }
        Column(Modifier.padding(horizontal = 8.dp)) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                text = stringResource(id = R.string.config_import),
                style = MaterialTheme.typography.displaySmall
            )
            val buttons = listOf(
                stringResource(R.string.clipboard),
                stringResource(R.string.file),
                stringResource(R.string.url)
            )
            var selectedIndex by remember { mutableIntStateOf(0) }

            Row(
                Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                buttons.forEachIndexed { index, txt ->
                    Row(
                        Modifier
                            .selectable(
                                index == selectedIndex,
                                onClick = { selectedIndex = index },
                                role = Role.RadioButton
                            )
                            .padding(4.dp)
                    ) {
                        RadioButton(selected = index == selectedIndex, onClick = null)
                        Text(txt)
                    }
                }
            }
            Box(Modifier.fillMaxWidth()) {
                when (selectedIndex) {
                    1 -> {
                        OutlinedTextField(
                            label = { Text(stringResource(id = R.string.file_path)) },
                            trailingIcon = {
                                IconButton(onClick = {
                                    filePicker.launch(arrayOf("text/*", "application/json"))
                                }) {
                                    Icon(
                                        Icons.Filled.FileOpen,
                                        stringResource(id = R.string.select_file),
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            },
                            value = filePath,
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    2 -> {
                        OutlinedTextField(
                            label = { Text(stringResource(id = R.string.url)) },
                            value = url,
                            onValueChange = { url = it },
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }

            val context = LocalContext.current
            var errorDialog by remember { mutableStateOf<Throwable?>(null) }
            errorDialog?.let {
                ErrorDialog(it) {
                    errorDialog = null
                }
            }

            OutlinedButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp),
                onClick = {
                    coroutine.launch(Dispatchers.Main) {
                        isVisibleLoadingDialog = true
                        kotlin.runCatching {
                            onImportFromJson(getJson(selectedIndex, context, url, filePath))
                        }.onFailure {
                            errorDialog = it
                        }
                        isVisibleLoadingDialog = false
                    }
                }) {
                Text(stringResource(id = R.string.config_import))
            }
        }
    }
}

private suspend fun getJson(
    selectedIndex: Int,
    context: Context,
    url: String? = null,
    uri: String? = null
): String {
    return when (selectedIndex) {
        2 -> {
            withIO {
                val resp: Response =
                    Net.get(url!!) { setClient { trustSSLCertificate() } }.execute()
                if (resp.isSuccessful) {
                    resp.body?.string() ?: ""
                } else throw NetException(resp.request, "GET失败, 状态码: ${resp.code}")
            }
        }

        1 -> {
            withIO { Uri.parse(uri).readAllText(context) }
        }

        else -> {
            clipboardManager.text.toString() ?: ""
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun PreviewScreen() {
    ConfigImportBottomSheet(onImportFromJson = {
        app.toast(it)
    })
}

@Composable
fun ConfigImportSelectDialog(
    list: List<SearchSource>,
    onConfirm: (List<SearchSource>) -> Unit,
    onDismissRequest: () -> Unit
) {
    val selectedList =
        remember { mutableStateListOf(*list.toTypedArray()) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            LazyColumn {
                items(list, key = { it.id }) {
                    val index = selectedList.indexOf(it)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = index != -1,
                                onClick = {
                                    if (index == -1) selectedList.add(it)
                                    else selectedList.removeAt(index)
                                },
                                role = Role.Checkbox,
                                indication = rememberRipple(bounded = true),
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = index != -1,
                            onCheckedChange = null,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(it.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                it.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = {
                    onConfirm.invoke(selectedList)
                }
            ) {
                Text(stringResource(id = android.R.string.ok))
            }
        }
    )
}

/*
@Preview
@Composable
fun PreviewSelectDialog() {
    var isVisible by remember { mutableStateOf(true) }

    val json = ""

//    val items = Json.decodeFromString<List<GroupWithReplaceRule>>(json)

    if (isVisible)
        ConfigImportSelectDialog(
            groupWithRules = items,
            onConfirm = {
                app.toast(it.size.toString())
                isVisible = false
            }, onDismissRequest = {
                isVisible = false
            }
        )
}*/
