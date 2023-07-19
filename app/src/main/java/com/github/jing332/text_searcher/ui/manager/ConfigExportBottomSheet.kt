package com.github.jing332.text_searcher.ui.manager

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.drake.net.utils.fileName
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.utils.ASFUriUtils.getPath

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigExportBottomSheet(
    json: String,
    fileName: String = "config.json",
    onDismissRequest: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var longToastMsg by remember { mutableStateOf<String?>(null) }
    if (longToastMsg != null) {
        Toast.makeText(context, longToastMsg, Toast.LENGTH_LONG).show()
        longToastMsg = null
    }

    var savedFileData by remember { mutableStateOf<ByteArray?>(null) }
    val fileSaver =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) {
            it?.let { uri ->
                savedFileData?.let { data ->
                    context.contentResolver.openOutputStream(uri, "wt"/* 覆写 */)?.use { os ->
                        os.write(data)
                        longToastMsg = context.getString(
                            R.string.saved_to_file,
                            context.getPath(uri, false) ?: uri.fileName() ?: uri.toString()
                        )
                    }
                }
            }
        }


    val clipboardManager = LocalClipboardManager.current

    ModalBottomSheet(onDismissRequest = onDismissRequest, modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)) {
            Row(Modifier.align(Alignment.CenterHorizontally)) {
                TextButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(json))
                        longToastMsg = context.getString(R.string.copied_to_clipboard)
                    }) {
                    Text(stringResource(id = R.string.copy))
                }
                TextButton(
                    onClick = {
                        savedFileData = json.toByteArray()
                        fileSaver.launch(fileName)
                    }) {
                    Text(stringResource(id = R.string.save_as_file))
                }
            }
            SelectionContainer(
                Modifier
                    .horizontalScroll(rememberScrollState()),
            ) {
                Text(
                    text = json,
                    Modifier
                        .verticalScroll(rememberScrollState()),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}