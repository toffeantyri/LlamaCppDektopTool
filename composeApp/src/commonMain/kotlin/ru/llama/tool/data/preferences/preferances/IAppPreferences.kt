package ru.llama.tool.data.preferences.preferances

import kotlinx.coroutines.flow.Flow

interface IAppPreferences {

    fun getThemeIsDarkMode(): Boolean

    suspend fun getAppThemeIsDarkMode(): Flow<Boolean>

    suspend fun setThemeIsDarkMode(isDarkMode: Boolean)

}