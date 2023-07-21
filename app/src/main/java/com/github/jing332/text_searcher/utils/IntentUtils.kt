package com.github.jing332.text_searcher.utils

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri


object IntentUtils {
    @Suppress("DEPRECATION")
    fun PackageManager.getInstalledBrowsers(): MutableList<ResolveInfo> {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.data = Uri.parse("http://example.com")

        return queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    }
}