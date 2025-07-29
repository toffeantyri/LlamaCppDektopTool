package ru.llama.tool.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(enableNetworkLogs: Boolean = true, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(rootModules(enableNetworkLogs))
        createEagerInstances()
    }


private fun rootModules(enableNetworkLogs: Boolean) = module {

    includes(
        dataStoreModule,
        networkModule(enableNetworkLogs),
        dataSourceModule,
        repositoryModule,
        useCasesModule,
        databaseModule
    )

}