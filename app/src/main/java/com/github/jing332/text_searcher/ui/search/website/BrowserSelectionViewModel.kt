package com.github.jing332.text_searcher.ui.search.website

import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import com.github.jing332.text_searcher.model.source.BrowserInfo
import com.github.jing332.text_searcher.utils.IntentUtils.getInstalledBrowsers

class BrowserSelectionViewModel : ViewModel() {
    val browsers = mutableStateListOf<Browser>()

    fun load(packageManager: PackageManager) {
        packageManager.getInstalledBrowsers().forEach {
            browsers.add(
                Browser(
                    name = it.loadLabel(packageManager).toString(),
                    info = BrowserInfo(
                        packageName = it.activityInfo.packageName,
                        className = it.activityInfo.name,
                    ),
                    icon = it.loadIcon(packageManager).toBitmap().asImageBitmap()
                )
            )
        }
    }

    data class Browser(
        val name: String,
        val info: BrowserInfo,
        val icon: ImageBitmap
    )
}