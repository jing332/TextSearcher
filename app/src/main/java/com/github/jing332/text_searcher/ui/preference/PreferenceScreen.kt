package com.github.jing332.text_searcher.ui.preference

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.help.AppConfig
import com.github.jing332.text_searcher.ui.LocalNavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreen(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(R.string.preference)) },
            navigationIcon = {
                IconButton(onClick = {
                    scope.launch { navController.popBackStack() }
                }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }
        )
    }) {
        ContentScreen(modifier = Modifier.padding(it))
    }
}

@Composable
private fun ContentScreen(modifier: Modifier) {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current

    Column(modifier = modifier) {
        var isDialogFullScreen by remember { AppConfig.isWindowFullScreen }
        PreferenceSwitch(
            title = { Text(stringResource(R.string.search_windows_full_screen)) },
            subTitle = { Text(stringResource(R.string.search_window_full_screen_msg)) },
            checked = isDialogFullScreen,
            onCheckedChange = {
                isDialogFullScreen = it
            }
        )
    }
}

@Composable
private fun PreferenceSwitch(
    modifier: Modifier = Modifier,
    title: @Composable ColumnScope.() -> Unit,
    subTitle: @Composable ColumnScope.() -> Unit,

    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = modifier
        .minimumInteractiveComponentSize()
        .clip(MaterialTheme.shapes.extraSmall)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple()
        ) {
            onCheckedChange(!checked)
        }
        .padding(8.dp)
    ) {
        Column(
            Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleMedium) {
                title()
            }

            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleSmall) {
                subTitle()
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Preview
@Composable
fun ContentPreview() {
    MaterialTheme {
        ContentScreen(modifier = Modifier.fillMaxSize())
    }
}