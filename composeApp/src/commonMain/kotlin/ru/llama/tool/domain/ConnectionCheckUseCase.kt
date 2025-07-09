package ru.llama.tool.domain

import kotlinx.coroutines.flow.Flow

interface ConnectionCheckUseCase {
    operator fun invoke(): Flow<Boolean>
} 