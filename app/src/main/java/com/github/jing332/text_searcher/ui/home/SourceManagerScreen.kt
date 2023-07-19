package com.github.jing332.text_searcher.ui.home

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.data.appDb
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.model.source.ChatGptSourceEntity
import com.github.jing332.text_searcher.ui.AppNavRoutes
import com.github.jing332.text_searcher.ui.LocalNavController
import com.github.jing332.text_searcher.ui.LocalSnackbarHostState
import com.github.jing332.text_searcher.ui.navigateSingleTop
import kotlinx.coroutines.launch
import me.saket.cascade.CascadeDropdownMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceManagerScreen(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Filled.Menu, contentDescription = "抽屉菜单")
                    }
                },
                actions = {
                    var showAddMenu by remember { mutableStateOf(false) }
                    if (showAddMenu)
                        CascadeDropdownMenu(
                            expanded = showAddMenu,
                            onDismissRequest = { showAddMenu = false }) {
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text("ChatGPT") },
                                onClick = {
                                    navController.navigateSingleTop(
                                        AppNavRoutes.SourceEdit.route,
                                        args = Bundle().apply {
                                            putParcelable(
                                                AppNavRoutes.SourceEdit.KEY_SOURCE,
                                                SearchSource(sourceEntity = ChatGptSourceEntity())
                                            )
                                        })
                                }
                            )

                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text("搜索引擎") },
                                onClick = {}
                            )
                        }

                    IconButton(onClick = {
                        showAddMenu = true
                    }) {
                        Icon(
                            Icons.Filled.PlaylistAdd,
                            contentDescription = stringResource(R.string.add_search_source)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(LocalSnackbarHostState.current) },
    ) {
        ListScreen(Modifier.padding(it))
    }
}

@Composable
private fun ListScreen(modifier: Modifier, vm: SourceManagerViewModel = viewModel()) {
    val navController = LocalNavController.current
    val sources by appDb.searchSource.flowAll.collectAsState(initial = listOf())
    LaunchedEffect(vm.hashCode()) {

    }
    LazyColumn(modifier) {
        items(sources, { it.id }) {
            Item(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp, horizontal = 4.dp),
                onClick = {
                    navController.navigateSingleTop(
                        AppNavRoutes.SourceEdit.route,
                        args = Bundle().apply {
                            putParcelable(AppNavRoutes.SourceEdit.KEY_SOURCE, it)
                        }
                    )
                },
                name = it.name,
                type = it.sourceEntity.type()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Item(modifier: Modifier = Modifier, onClick: () -> Unit, name: String, type: String) {
    ElevatedCard(modifier = modifier, onClick = onClick) {
        Column(modifier.padding(2.dp)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Text(text = type, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview
@Composable
fun SourceManagerScreenPreview() {

}