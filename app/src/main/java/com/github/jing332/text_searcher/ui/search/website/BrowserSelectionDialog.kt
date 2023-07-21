package com.github.jing332.text_searcher.ui.search.website

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.jing332.text_searcher.model.source.BrowserInfo
import com.github.jing332.text_searcher.ui.search.BaseSearchDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun BrowserSelectionDialog(
    onDismissRequest: () -> Unit,
    onBrowserChange: (BrowserInfo) -> Unit,
    vm: BrowserSelectionViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(vm.hashCode()) {
        launch(Dispatchers.IO) {
            vm.load(context.packageManager)
        }
    }

    BaseSearchDialog(onDismissRequest = onDismissRequest) {
        Column {
            Text(
                "选择浏览器",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text("设置默认浏览器后，您可长按 外部浏览器 按钮进行重新选择。")

            LazyColumn {
                items(vm.browsers, key = { it.info }) {
                    Column {
                        Item(it.name, it.icon) {
                            onBrowserChange(it.info)
                        }
                        Divider(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

        }
    }
}

@Composable
private fun Item(name: String, icon: ImageBitmap, onClick: () -> Unit) {
    Row(
        Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick,
            )
    ) {
        Image(
            bitmap = icon,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Text(
            name, modifier = Modifier
                .align(CenterVertically)
                .weight(1f)
                .padding(start = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview
@Composable
fun BrowserSelectionPreview() {
    val context = LocalContext.current
    var show by remember { mutableStateOf(true) }
    if (show)
        BrowserSelectionDialog(onDismissRequest = { show = false }, onBrowserChange = {
            context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addCategory(Intent.CATEGORY_BROWSABLE);
                data = "https://www.baidu.com".toUri()
                setClassName(it.packageName, it.className)
//                setClassName("mark.via.gp", "mark.via.Shell")
            })
        }
        )
}
