package com.github.jing332.text_searcher.ui.search

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.help.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun FontSelectionDialog(vm: FontSelectionViewModel = viewModel(), onDismissRequest: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var fontDir by remember { AppConfig.fontDir }
    val ffResolver = LocalFontFamilyResolver.current

    LaunchedEffect(vm.hashCode()) {
        launch(Dispatchers.IO) {
            vm.loadFonts(context, fontDir, ffResolver)
        }
    }

    val dirSelection = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) {
        it?.let { uri ->
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            fontDir = uri.toString()
            scope.launch(Dispatchers.IO) {
                vm.loadFonts(context, fontDir, ffResolver)
            }
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
        ) {
            Column {
                OutlinedTextField(
                    value = fontDir, onValueChange = { fontDir = it },
                    label = { Text(stringResource(id = R.string.font_directory)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    trailingIcon = {
                        IconButton(onClick = { dirSelection.launch(null) }) {
                            Icon(
                                Icons.Filled.FileOpen,
                                stringResource(R.string.select_font_directory)
                            )
                        }
                    }
                )
                Text(text = "选择字体", modifier = Modifier.padding(8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(vm.fontList.toList(), { it.uri }) {

                        Text(
                            text = it.name,
                            fontFamily = it.fontFamily,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                    onDismissRequest()
                                }
                                .padding(8.dp)
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun FontSelectionDialogPreview() {
    var showDialog by remember { mutableStateOf(true) }
    if (showDialog) {
        FontSelectionDialog(onDismissRequest = { showDialog = false })
    }
}