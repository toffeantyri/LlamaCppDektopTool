package ru.llama.tool.utils

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.statekeeper.StateKeeper

class DesktopLifecycleManager(stateKeeper: StateKeeper) {
    private val lifecycle = LifecycleRegistry()
    val componentContext = DefaultComponentContext(
        lifecycle = lifecycle,
        stateKeeper = stateKeeper
    )
    private var currentState: WindowState = WindowState.CREATED

    init {
        lifecycle.onCreate()
    }

    fun handleWindowState(isVisible: Boolean, isFocused: Boolean) {
        val newState = when {
            !isVisible -> WindowState.HIDDEN
            isVisible && !isFocused -> WindowState.VISIBLE
            isVisible && isFocused -> WindowState.FOCUSED
            else -> WindowState.VISIBLE
        }

        transitionTo(newState)
    }


    private fun transitionTo(newState: WindowState) {

        if (newState == currentState) return

        when {
            currentState == WindowState.CREATED && newState == WindowState.HIDDEN -> {
                currentState = newState
            }

            currentState == WindowState.CREATED && newState == WindowState.VISIBLE -> {
                lifecycle.onStart()
                currentState = newState
            }

            currentState == WindowState.CREATED && newState == WindowState.FOCUSED -> {
                lifecycle.onStart()
                lifecycle.onResume()
                currentState = newState
            }

            currentState == WindowState.HIDDEN && newState == WindowState.VISIBLE -> {
                lifecycle.onStart()
            }

            currentState == WindowState.HIDDEN && newState == WindowState.FOCUSED -> {
                lifecycle.onStart()
                lifecycle.onResume()
                currentState = newState
            }

            currentState == WindowState.VISIBLE && newState == WindowState.HIDDEN -> {
                lifecycle.onStop()
                currentState = newState
            }

            currentState == WindowState.VISIBLE && newState == WindowState.FOCUSED -> {
                lifecycle.onResume()
                currentState = newState
            }

            currentState == WindowState.FOCUSED && newState == WindowState.HIDDEN -> {
                lifecycle.onPause()
                lifecycle.onStop()
                currentState = newState
            }

            currentState == WindowState.FOCUSED && newState == WindowState.VISIBLE -> {
                lifecycle.onPause()
                currentState = newState
            }

        }
    }

    fun onDestroy() {

        when (currentState) {
            WindowState.CREATED -> lifecycle.onDestroy()
            WindowState.VISIBLE -> {
                lifecycle.onStop()
                lifecycle.onDestroy()
            }

            WindowState.FOCUSED -> {
                lifecycle.onPause()
                lifecycle.onStop()
                lifecycle.onDestroy()
            }

            WindowState.HIDDEN -> lifecycle.onDestroy()
            WindowState.DESTROYED -> {}
        }
        currentState = WindowState.DESTROYED

    }


    private enum class WindowState {
        CREATED, VISIBLE, FOCUSED, HIDDEN, DESTROYED
    }

}