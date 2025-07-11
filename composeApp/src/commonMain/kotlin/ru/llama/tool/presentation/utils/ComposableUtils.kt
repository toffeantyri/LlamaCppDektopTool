package ru.llama.tool.presentation.utils

import androidx.compose.foundation.focusable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type


fun Modifier.onKeyEscape(focusRequester: FocusRequester, onKeyEscapeAction: () -> Unit) =
    this.onKeyEvent {
        if (it.key == Key.Escape && it.type == KeyEventType.KeyUp) {
            onKeyEscapeAction()
            true
        } else false
    }.focusRequester(focusRequester).focusable()


fun Modifier.onKeyEnter(focusRequester: FocusRequester, onKeyEscapeAction: () -> Unit) =
    this.onKeyEvent {
        if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
            onKeyEscapeAction()
            true
        } else false
    }.focusRequester(focusRequester).focusable()

