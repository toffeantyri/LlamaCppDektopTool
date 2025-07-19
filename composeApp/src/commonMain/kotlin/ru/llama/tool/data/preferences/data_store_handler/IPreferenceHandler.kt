package ru.llama.tool.data.preferences.data_store_handler


import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow


interface IPreferenceHandler {
    suspend fun <T> save(key: Preferences.Key<T>, value: T)
    suspend fun <T> load(key: Preferences.Key<T>): Flow<T?>
    suspend fun <T> load(key: Preferences.Key<T>, defaultValue: T): Flow<T>
    fun <T> syncLoad(key: Preferences.Key<T>): T?
}