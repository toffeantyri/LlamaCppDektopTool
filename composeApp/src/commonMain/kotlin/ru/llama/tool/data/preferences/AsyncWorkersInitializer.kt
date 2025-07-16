package ru.llama.tool.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import ru.llama.tool.core.io
import ru.llama.tool.data.preferences.data_store_handler.PreferenceHandlerImpl
import ru.llama.tool.data.preferences.preferances.AppPreferencesImpl
import ru.llama.tool.data.preferences.preferances.IAppPreferences

interface CoroutinesComponent {
    val mainImmediateDispatcher: CoroutineDispatcher
    val appScope: CoroutineScope
}

internal class CoroutinesComponentImpl
private constructor() : CoroutinesComponent {
    override val mainImmediateDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main.immediate
    override val appScope: CoroutineScope
        get() = CoroutineScope(
            SupervisorJob() + mainImmediateDispatcher
        )

    companion object {
        fun create(): CoroutinesComponent = CoroutinesComponentImpl()
    }
}

interface CoreComponent : CoroutinesComponent {
    val preferences: IAppPreferences
}

internal class CoreComponentImpl
internal constructor(context: Any? = null) :
    CoreComponent, CoroutinesComponent by CoroutinesComponentImpl.create() {

    private val datastore: DataStore<Preferences> = dataStorePreferences(
        corruptionHandler = null,
        coroutineScope = appScope.plus(Dispatchers.io()),
        migrations = emptyList(),
    )
    override val preferences: IAppPreferences
        get() = AppPreferencesImpl(PreferenceHandlerImpl(datastore))

}

object ApplicationComponent {
    private var _coreComponent: CoreComponent? = null
    val coreComponent
        get() = _coreComponent
            ?: throw IllegalStateException("Make sure to call ApplicationComponent.init()")

    fun init(context: Any? = null) {
        _coreComponent = CoreComponentImpl(context)
    }
}

fun initializeDataStore() {
    ApplicationComponent.init()
}

