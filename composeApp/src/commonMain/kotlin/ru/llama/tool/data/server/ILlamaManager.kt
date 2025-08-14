package ru.llama.tool.data.server

// Общий интерфейс в commonMain
interface ILlamaManager {
    /**
     * Загружает модель - логика зависит от платформы
     * Android: копирует модель во внутреннее хранилище
     * Desktop: использует путь напрямую
     */
    suspend fun loadLangModel(sourcePath: String): Result<Unit>

    /**
     * Инициализирует модель для использования
     */
    suspend fun initialize(): Result<Unit>

    /**
     * Генерирует текст на основе промпта
     */
    suspend fun generate(
        prompt: String,
        options: GenerationOptions = GenerationOptions.Default
    ): Result<String>

    /**
     * Освобождает ресурсы модели
     */
    fun release(): Result<Unit>

    /**
     * Проверяет, загружена ли модель
     */
    fun isModelLoaded(): Boolean

    /**
     * Получает информацию о модели
     */
    fun getLangModelInfo(): ModelInfo?

    /**
     * Опции генерации текста
     */
    data class GenerationOptions(
        val maxTokens: Int = 128,
        val temperature: Float = 0.8f,
        val topP: Float = 0.9f,
        val stopTokens: List<String> = emptyList()
    ) {
        companion object {
            val Default = GenerationOptions()
        }
    }

    /**
     * Информация о модели
     */
    data class ModelInfo(
        val modelName: String,
        val modelPath: String,
        val fileSize: Long,
        val isLoaded: Boolean,
        val parameters: Map<String, String> = emptyMap()
    )
}