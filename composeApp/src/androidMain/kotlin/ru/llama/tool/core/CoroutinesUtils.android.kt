package ru.llama.tool.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual fun Dispatchers.io(): CoroutineDispatcher = Dispatchers.IO