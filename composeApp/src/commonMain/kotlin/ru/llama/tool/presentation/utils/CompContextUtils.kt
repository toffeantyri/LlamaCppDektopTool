package ru.llama.tool.presentation.utils

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * создание coroutine-scope (внутри decompose компонента)
 * аналогичного по поведению viewModelScope
 */

fun ComponentContext.componentCoroutineScope(addCoroutineContext: CoroutineContext = SupervisorJob()): CoroutineScope {
    val scope = CoroutineScope(Dispatchers.Main.immediate + addCoroutineContext)
    if (lifecycle.state != Lifecycle.State.DESTROYED) {
        lifecycle.doOnDestroy {
            scope.cancel()
        }
    } else {
        scope.cancel()
    }

    return scope
}