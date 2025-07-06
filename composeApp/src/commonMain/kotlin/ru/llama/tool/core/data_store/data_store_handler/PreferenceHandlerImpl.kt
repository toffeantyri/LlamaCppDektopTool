package ru.llama.tool.core.data_store.data_store_handler

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class PreferenceHandlerImpl(private val dataStore: DataStore<Preferences>) : IPreferenceHandler {

    override suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        dataStore.edit { pref ->
            pref[key] = value
        }
    }

    override suspend fun <T> load(key: Preferences.Key<T>): Flow<T?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { pref ->
            pref[key]
        }

    override suspend fun <T> load(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { pref ->
                pref[key] ?: defaultValue
            }

    override fun <T> syncLoad(key: Preferences.Key<T>): T? {
        return runBlocking { load(key).flowOn(Dispatchers.IO).firstOrNull() }
    }
}