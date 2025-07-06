package ru.llama.tool.core.data_store.preferances

import kotlinx.coroutines.flow.Flow

interface IAppPreferences {

    fun getThemeIsDarkMode(): Boolean

    suspend fun getAppThemeIsDarkMode(): Flow<Boolean>
    suspend fun setThemeIsDarkMode(isDarkMode: Boolean)
}