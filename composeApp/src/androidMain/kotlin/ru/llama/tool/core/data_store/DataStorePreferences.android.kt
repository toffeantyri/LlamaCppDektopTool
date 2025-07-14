package ru.llama.tool.core.data_store

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import ru.llama.tool.App
import java.io.File

actual fun dataStorePreferences(
    corruptionHandler: ReplaceFileCorruptionHandler<Preferences>?,
    coroutineScope: CoroutineScope,
    migrations: List<DataMigration<Preferences>>,
    context: Any?,
): DataStore<Preferences> = createDataStoreWithDefaults(
    corruptionHandler = corruptionHandler,
    coroutineScope = coroutineScope,
    migrations = migrations,
    path = { File(App.appContext.filesDir, "datastore/$SETTING_PREFERENCES").path }
)