package ru.llama.tool.presentation.screen_b

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper

class SettingsComponentImpl(
    componentContext: ComponentContext,
    override val isDarkMode: Value<Boolean>,
    override val onToggleDarkMode: () -> Unit,
) : SettingsComponent, ComponentContext by componentContext, InstanceKeeper.Instance