package com.github.jing332.text_searcher.ui.search.website

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.model.source.WebSiteSourceEntity
import com.github.jing332.text_searcher.ui.search.BaseSearchDialog
import com.github.jing332.text_searcher.ui.search.BaseSourceEditScreen
import com.github.jing332.text_searcher.ui.search.SearchSourceState

@Composable
fun WebsiteEditScreen(src: SearchSource, onChanged: (SearchSource) -> Unit) {
    val entity = src.sourceEntity as WebSiteSourceEntity

    var url by remember { mutableStateOf(entity.url) }

    fun newSrc(src: SearchSource): SearchSource {
        return src.copy(
            sourceEntity = entity.copy(
                url = url
            )
        )
    }

    var showTestDialog by remember { mutableStateOf<Pair<SearchSource, String>?>(null) }
    if (showTestDialog != null)
        BaseSearchDialog(onDismissRequest = { showTestDialog = null }) {
            val state = remember { SearchSourceState() }
            WebsiteSearchScreen(
                src = showTestDialog!!.first,
                text = showTestDialog!!.second,
                state = state,
                onSourceChange = { onChanged(it) }
            )
        }
    BaseSourceEditScreen(title = stringResource(R.string.website), src = src, onSave = {
        onChanged(it.copy(sourceEntity = entity.copy(url = url)))
    }, onTest = {
        showTestDialog = newSrc(src) to it
    }, content = {
        Column(Modifier.padding(2.dp)) {
            Text("使用 \${text} 进行插入文本")
            SelectionContainer {
                Text(
                    "例如 必应Url为： https://www.bing.com/search?q=\${text}",
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = url, onValueChange = { url = it },
                label = { Text("Url") }
            )
        }
    })

}