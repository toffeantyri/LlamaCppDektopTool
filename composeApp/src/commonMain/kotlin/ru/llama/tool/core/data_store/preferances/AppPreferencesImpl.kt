package ru.llama.tool.core.data_store.preferances

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.llama.tool.core.data_store.PreferencesConstants
import ru.llama.tool.core.data_store.data_store_handler.IPreferenceHandler
import ru.llama.tool.core.io

class AppPreferencesImpl(
    private val preferences: IPreferenceHandler
) : IAppPreferences {

    private fun <T> syncLoad(key: Preferences.Key<T>): T? {
        return preferences.syncLoad(key)
    }

    override suspend fun getAppThemeIsDarkMode(): Flow<Boolean> {
        return flow {
            preferences.load(PreferencesConstants.PREF_KEY_IS_DARK_MODEL, false)
                .collect { isDarkMode ->
                    emit(isDarkMode)
                }
        }.flowOn(Dispatchers.io())
    }

}