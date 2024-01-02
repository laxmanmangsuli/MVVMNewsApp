package com.androiddevs.mvvmnewsapp

import android.app.Application
import android.content.Context

class NewsApplication : Application() {

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
    }
}
