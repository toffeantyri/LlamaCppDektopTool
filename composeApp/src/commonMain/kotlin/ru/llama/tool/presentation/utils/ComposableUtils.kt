package ru.llama.tool.presentation.utils

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import org.jetbrains.compose.resources.stringResource
import ru.llama.tool.core.EMPTY
import ru.llama.tool.domain.models.UiText
import ru.llama.tool.domain.models.UiText.Empty
import ru.llama.tool.domain.models.UiText.StringRes
import ru.llama.tool.domain.models.UiText.StringValue


private fun KeyEvent.onKeyEventWithCondition(
    conditions: Boolean,
    onKeyAction: () -> Unit
): Boolean {
    return if (conditions) {
        onKeyAction()
        true
    } else false
}

fun Modifier.onKeyEscape(focusRequester: FocusRequester, onKeyAction: () -> Unit) =
    this.onKeyEvent {
        it.onKeyEventWithCondition(
            conditions = (it.key == Key.Escape && it.type == KeyEventType.KeyUp),
            onKeyAction = onKeyAction
        )
    }.focusRequester(focusRequester).focusable()


fun Modifier.onKeyEnter(focusRequester: FocusRequester, onKeyAction: () -> Unit) =
    this.onKeyEvent {
        it.onKeyEventWithCondition(
            conditions = (it.key == Key.Enter && it.type == KeyEventType.KeyUp && !it.isAltPressed && !it.isCtrlPressed),
            onKeyAction = onKeyAction
        )
    }.focusRequester(focusRequester).focusable()

fun Modifier.onKeyEnterWithAlt(focusRequester: FocusRequester, onKeyAction: () -> Unit) =
    this.onKeyEvent {
        it.onKeyEventWithCondition(
            conditions = (it.key == Key.Enter && it.type == KeyEventType.KeyUp && it.isAltPressed && !it.isCtrlPressed),
            onKeyAction = onKeyAction
        )
    }.focusRequester(focusRequester).focusable()

@Composable
fun UiText.asString(): String {
    return when (this) {
        is StringValue -> value
        is StringRes -> stringResource(resId, *args)
        is Empty -> EMPTY
    }
}