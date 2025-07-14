package ru.llama.tool.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ru.llama.tool.core.data_store.ApplicationComponent
import ru.llama.tool.core.data_store.preferances.IAppPreferences

val dataStoreModule: Module = module {


    single<IAppPreferences> {
        ApplicationComponent.coreComponent.preferences
    }


}
