package ru.llama.tool

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import ru.llama.tool.core.data_store.ApplicationComponent
import ru.llama.tool.di.initKoin

class App : Application() {

    companion object {
        lateinit var appContext: Context
    }


    override fun onCreate() {
        appContext = this.applicationContext
        super.onCreate()
        initKoin(enableNetworkLogs = true) {
            androidContext(applicationContext)
        }
        ApplicationComponent.init()
    }


}