package com.github.jing332.text_searcher.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.funny.data_saver.core.LocalDataSaver
import com.github.jing332.text_searcher.data.appDb
import com.github.jing332.text_searcher.data.entites.SearchSource
import com.github.jing332.text_searcher.help.AppConfig
import com.github.jing332.text_searcher.ui.theme.TxtSearcherTheme
import com.github.jing332.text_searcher.ui.widgets.TransparentSystemBars

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        AppConfig.fillDefaultValues(this)

        setContent {
            CompositionLocalProvider(
                LocalDataSaver provides AppConfig.dataSaverPref,
            ) {
                TxtSearcherTheme {
                    TransparentSystemBars()
                    AppNavigation()
                }
            }
        }
    }

    @Composable
    fun AppNavigation(
        navController: NavHostController = rememberNavController(),
        drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    ) {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        CompositionLocalProvider(
            LocalDataSaver provides AppConfig.dataSaverPref,
            LocalNavController provides navController,
            LocalSnackbarHostState provides snackbarHostState
        ) {
            NavHost(
                navController,
                startDestination = AppNavRoutes.SourceManager.route
            ) {
                composable(AppNavRoutes.SourceManager.route) {
                    MainScreen { finish() }
                }

                composable(AppNavRoutes.SourceEdit.route) {
                    @Suppress("DEPRECATION")
                    val src: SearchSource? =
                        it.arguments?.getParcelable(AppNavRoutes.SourceEdit.KEY_SOURCE)
                    if (src == null) {
                        navController.popBackStack()
                        return@composable
                    } else {
                        src.sourceEntity.EditScreen(src = src, onChanged = { changedSrc ->
                            appDb.searchSource.insert(changedSrc)
                            navController.popBackStack()
                        })
                    }
                }

                composable(AppNavRoutes.About.route) {
                    AboutScreen(drawerState)
                }
            }
        }
    }
}


