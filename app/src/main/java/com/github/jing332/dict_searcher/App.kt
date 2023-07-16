package com.github.jing332.dict_searcher

import android.app.Application

val app: App by lazy { App.instance }

class App : Application() {
    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

    }
}