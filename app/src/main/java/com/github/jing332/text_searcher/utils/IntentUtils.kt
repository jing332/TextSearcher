package com.github.jing332.text_searcher.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.core.net.toUri


object IntentUtils {
    @Suppress("DEPRECATION")
    fun PackageManager.getInstalledBrowsers(): MutableList<ResolveInfo> {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.data = Uri.parse("http://example.com")

        return queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    }

    fun Context.goExternalBrowser(packageName: String, activityClassName: String, url: String) {
        startActivity(Intent(android.content.Intent.ACTION_VIEW).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            addCategory(android.content.Intent.CATEGORY_BROWSABLE);
            data = "https://www.baidu.com".toUri()
            setClassName(packageName, activityClassName)
        })
    }
}