package com.github.jing332.text_searcher.ui

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.github.jing332.text_searcher.BuildConfig
import com.github.jing332.text_searcher.R
import com.github.jing332.text_searcher.ui.manager.SourceManagerScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Locale


val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController has not been initialized! ")
}
val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("SnackbarHostState has not been initialized! ")
}

@Composable
fun MainScreen(
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    onFinishedActivity: () -> Unit,
) {
    var toastMsg by remember { mutableStateOf("") }
    if (toastMsg.isNotEmpty()) {
        toastMsg = ""
    }

    var warnMsg by remember { mutableStateOf("") }
    if (warnMsg.isNotEmpty()) {
//        SweetToastUtil.SweetWarning(
//            message = warnMsg,
//            Toast.LENGTH_LONG,
//            PaddingValues(bottom = 32.dp)
//        )
        warnMsg = ""
    }

    val isVisibleThemeDialog = remember { mutableStateOf(false) }
//    if (isVisibleThemeDialog.value)
//        ThemeSettingsDialog(
//            onDismissRequest = { isVisibleThemeDialog.value = false },
//            currentTheme = getAppTheme(),
//            onChangeTheme = {
//                if (it == AppTheme.DYNAMIC_COLOR && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {// SDK < A12
//                    warnMsg = context.getString(R.string.device_not_support_dynamic_theme)
//                } else setAppTheme(it)
//            }
//        )

    val snackbarHostState = LocalSnackbarHostState.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }
    var lastBackDownTime by remember { mutableLongStateOf(0L) }
    BackHandler(enabled = drawerState.isClosed) {
        val duration = 2000
        SystemClock.elapsedRealtime().let {
            if (it - lastBackDownTime <= duration) {
                onFinishedActivity.invoke()
            } else {
                lastBackDownTime = it
                scope.launch {
                    withTimeout(duration.toLong()) {
                        snackbarHostState.showSnackbar("再按一次退出")
                    }
                }
            }
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                Modifier
                    .fillMaxHeight()
                    .width(300.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(12.dp),
                navController = navController,
                isVisibleThemeDialog,
            )
        }
    ) {
        SourceManagerScreen(drawerState = drawerState)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DrawerContent(
    modifier: Modifier,
    navController: NavHostController,
    isVisibleThemeDialog: MutableState<Boolean>
) {
    val drawerItemIcon = @Composable { img: ImageVector, contentDescription: String ->
        Icon(
            img,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }

    @Composable
    fun drawerItem(
        img: ImageVector,
        targetScreen: AppNavRoutes,
        onClick: () -> Unit = { navController.navigateSingleTop(targetScreen.route) }
    ) {
        NavigationDrawerItem(
            icon = { drawerItemIcon(img, stringResource(id = targetScreen.titleResId)) },
            label = { Text(text = stringResource(id = targetScreen.titleResId)) },
            selected = false,
            onClick = onClick,
        )
    }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(24.dp))
        val context = LocalContext.current
        val clipboardManager = LocalClipboardManager.current
        val snackBarState = LocalSnackbarHostState.current
        val scope = rememberCoroutineScope()

        var isBuildTimeExpanded by remember { mutableStateOf(false) }
        val versionNameText =
            remember { "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})" }
        Column(modifier = Modifier
            .padding(end = 4.dp)
            .clip(MaterialTheme.shapes.small)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = {
                    isBuildTimeExpanded = !isBuildTimeExpanded
                },
                onLongClick = {
                    clipboardManager.setText(AnnotatedString(versionNameText))
                    scope.launch {
                        withTimeout(2000) {
                            snackBarState.showSnackbar(context.getString(R.string.copied_to_clipboard))
                        }
                    }
                }
            )) {
            Row {
                Image(
                    painterResource(id = R.drawable.ic_app_launcher_foreground),
                    stringResource(id = R.string.app_name),
                    modifier = Modifier.size(64.dp)
                )
                Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = versionNameText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            AnimatedVisibility(visible = isBuildTimeExpanded) {
                Text(
                    text = "构建于 " + SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                    ).format(BuildConfig.BUILD_TIME * 1000),
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 4.dp)
        )

//        NavigationDrawerItem(
//            icon = { drawerItemIcon(Icons.Filled.Style, stringResource(id = R.string.theme)) },
//            label = { Text(text = stringResource(id = R.string.theme)) },
//            selected = false,
//            onClick = {
//                isVisibleThemeDialog.value = true
//            },
//        )

        drawerItem(Icons.Default.Settings, AppNavRoutes.Preferences)
        drawerItem(Icons.Filled.Info, AppNavRoutes.About)
    }
}

/*
* 可传递 Bundle 到 Navigation
* */
fun NavController.navigate(
    route: String,
    args: Bundle,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    val routeLink = NavDeepLinkRequest
        .Builder
        .fromUri(NavDestination.createRoute(route).toUri())
        .build()

    val deepLinkMatch = graph.matchDeepLink(routeLink)
    if (deepLinkMatch != null) {
        val destination = deepLinkMatch.destination
        val id = destination.id
        navigate(id, args, navOptions, navigatorExtras)
    } else {
        navigate(route, navOptions, navigatorExtras)
    }
}

/**
 * 单例并清空其他栈
 */
fun NavHostController.navigateSingleTop(
    route: String,
    args: Bundle? = null,
    popUpToMain: Boolean = false
) {
    val navController = this
    val navOptions = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .apply {
            if (popUpToMain) setPopUpTo(
                navController.graph.startDestinationId,
                inclusive = false,
                saveState = true
            )
        }
        .setRestoreState(true)
        .build()
    if (args == null)
        navController.navigate(route, navOptions)
    else
        navController.navigate(route, args, navOptions)
}