package com.github.jing332.text_searcher.ui.manager

import android.os.Bundle
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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
                    .padding(vertical = 2.dp, horizontal = 16.dp),
                name = it.name,
                type = it.sourceEntity.type(),
                onClick = {
                    navController.navigateSingleTop(
                        AppNavRoutes.SourceEdit.route,
                        args = Bundle().apply {
                            putParcelable(AppNavRoutes.SourceEdit.KEY_SOURCE, it)
                        }
                    )
                },
                onDelete = {
                    appDb.searchSource.delete(it)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Item(
    modifier: Modifier = Modifier,
    name: String,
    type: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(modifier = modifier, onClick = onClick) {
        ConstraintLayout(
            modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            val (titleRef, typeRef, optionsRef) = createRefs()

            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(parent.top)
                    end.linkTo(optionsRef.start)
                    linkTo(start = titleRef.start, end = parent.start, bias = 0f)
                }
            )
            Row(
                Modifier
                    .wrapContentWidth()
                    .constrainAs(typeRef) {
                        top.linkTo(titleRef.bottom)
                        linkTo(start = typeRef.start, end = titleRef.start, bias = 0f)
                        end.linkTo(titleRef.end)
                    }) {
                Icon(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    imageVector = Icons.Filled.Link,
                    contentDescription = stringResource(R.string.type),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            var showOptions by remember { mutableStateOf(false) }
            IconButton(modifier = Modifier.constrainAs(optionsRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }, onClick = { showOptions = true }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.more_options)
                )

                CascadeDropdownMenu(
                    expanded = showOptions,
                    onDismissRequest = { showOptions = false }) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(R.string.delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        }, leadingIcon = {
                            Icon(
                                Icons.Default.DeleteOutline,
                                null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }, children = {
                            androidx.compose.material3.DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(R.string.confirm_delete),
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                onClick = {
                                    showOptions = false
                                    onDelete()
                                }
                            )
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(stringResource(R.string.cancel)) },
                                onClick = { showOptions = false }
                            )
                        })
                }
            }

        }
    }
}

@Preview
@Composable
fun SourceManagerScreenPreview() {

}