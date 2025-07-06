package ru.llama.tool.core.data_store


import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import okio.Path.Companion.toPath

expect fun dataStorePreferences(
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>?,
    coroutineScope: CoroutineScope,
    migrations: List<DataMigration<Preferences>>,
    context: Any? = null,
): DataStore<Preferences>


internal const val SETTING_PREFERENCES = "setting_preferences.preferences_pb"

internal fun createDataStoreWithDefaults(
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>?,
    coroutineScope: CoroutineScope,
    migrations: List<DataMigration<Preferences>>,
    path: () -> String
) = PreferenceDataStoreFactory.createWithPath(
    corruptionHandler = corruptionHandler,
    scope = coroutineScope,
    migrations = migrations,
    produceFile = { path().toPath() }
)
