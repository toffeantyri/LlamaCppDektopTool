package ru.llama.tool.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.llama.tool.data.preferences.ApplicationComponent
import ru.llama.tool.data.preferences.preferances.IAppPreferences

val dataStoreModule: Module = module {


    single<IAppPreferences> {
        ApplicationComponent.coreComponent.preferences
    }


}
