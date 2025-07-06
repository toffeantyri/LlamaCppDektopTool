package ru.llama.tool.core.data_store.preferances

import kotlinx.coroutines.flow.Flow

interface IAppPreferences {

    suspend fun getAppThemeIsDarkMode(): Flow<Boolean>
}