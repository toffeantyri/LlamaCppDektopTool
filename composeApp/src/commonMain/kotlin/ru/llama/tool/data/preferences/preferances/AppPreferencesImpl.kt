package ru.llama.tool.data.preferences.preferances

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.llama.tool.core.io
import ru.llama.tool.data.api.setting_http_client_provider.ISettingHttpClientProvider
import ru.llama.tool.data.preferences.PreferencesConstants
import ru.llama.tool.data.preferences.data_store_handler.IPreferenceHandler

class AppPreferencesImpl(
    private val preferences: IPreferenceHandler,
    private val httpSettingProvider: ISettingHttpClientProvider
) : IAppPreferences {

    private fun <T> syncLoad(key: Preferences.Key<T>): T? {
        return preferences.syncLoad(key)
    }

    override fun getThemeIsDarkMode(): Boolean {
        return syncLoad(PreferencesConstants.PREF_KEY_IS_DARK_MODEL) ?: false
    }

    override suspend fun getAppThemeIsDarkMode(): Flow<Boolean> {
        return flow {
            preferences.load(PreferencesConstants.PREF_KEY_IS_DARK_MODEL, false)
                .collect { isDarkMode ->
                    emit(isDarkMode)
                }
        }.flowOn(Dispatchers.io())
    }

    override suspend fun setThemeIsDarkMode(isDarkMode: Boolean) {
        preferences.save(PreferencesConstants.PREF_KEY_IS_DARK_MODEL, isDarkMode)
    }

    override suspend fun getSystemPrompt(defaultValue: String): String {
        return preferences.load(PreferencesConstants.PREF_KEY_DEFAULT_SYSTEM_PROMPT, defaultValue)
            .first()
    }

    override suspend fun setSystemPrompt(prompt: String) {
        preferences.save(PreferencesConstants.PREF_KEY_DEFAULT_SYSTEM_PROMPT, prompt)
    }

    override suspend fun getBaseUrl(): String {
        return preferences.load(
            PreferencesConstants.PREF_KEY_BASE_URL,
            httpSettingProvider.getBaseUrl()
        ).first()
    }

    override suspend fun setBaseUrl(url: String) {
        preferences.save(PreferencesConstants.PREF_KEY_BASE_URL, url)
    }


}