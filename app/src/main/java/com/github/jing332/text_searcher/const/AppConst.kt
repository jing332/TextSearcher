package com.github.jing332.text_searcher.const

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import com.github.jing332.text_searcher.app
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@Suppress("DEPRECATION")
object AppConst {
    const val PACKET_NAME = "com.github.jing332.text_searcher"

    @OptIn(ExperimentalSerializationApi::class)
    val jsonBuilder by lazy {
        Json {
            allowStructuredMapKeys = true
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
            explicitNulls = false //忽略为null的字段
            allowStructuredMapKeys = true
        }
    }


    val timeFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("HH:mm")
    }

    val dateFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("yyyy/MM/dd HH:mm")
    }

    val fileNameFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("yy-MM-dd-HH-mm-ss")
    }

    val appInfo: AppInfo by lazy {
        val appInfo = AppInfo()
        app.packageManager.getPackageInfo(
            app.packageName,
            PackageManager.GET_ACTIVITIES
        )
            ?.let {
                appInfo.versionName = it.versionName
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    appInfo.versionCode = it.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    appInfo.versionCode = it.versionCode.toLong()
                }
            }
        appInfo
    }

    data class AppInfo(
        var versionCode: Long = 0L,
        var versionName: String = ""
    )
}