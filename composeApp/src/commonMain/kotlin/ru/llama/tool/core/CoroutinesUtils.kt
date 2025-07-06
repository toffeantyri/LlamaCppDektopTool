package ru.llama.tool.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

expect fun Dispatchers.io(): CoroutineDispatcher