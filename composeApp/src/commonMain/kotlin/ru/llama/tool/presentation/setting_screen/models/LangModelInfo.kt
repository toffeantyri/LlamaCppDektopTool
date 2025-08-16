package ru.llama.tool.presentation.setting_screen.models

// Модель данных для информации о GGUF файле
data class LangModelInfo(
    val name: String,
    val path: String,
    val size: String,
    val architecture: String,
    val parameters: String = "",
    val quantization: String = ""
)

