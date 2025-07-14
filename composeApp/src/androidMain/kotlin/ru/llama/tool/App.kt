package ru.llama.tool

import android.app.Application
import android.content.Context

class App : Application() {

    companion object {
        lateinit var appContext: Context
    }


    override fun onCreate() {
        appContext = this.applicationContext
        super.onCreate()

    }


}