package ru.llama.tool.domain.models

import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.StringResource

@Immutable
sealed interface UiText {

    @JvmInline
    value class StringValue(val value: String) : UiText

    class StringRes(val resId: StringResource, vararg val args: Any) : UiText

    data object Empty : UiText


    fun isEmpty(): Boolean {
        return when (this) {
            is Empty -> true
            else -> false
        }
    }


}