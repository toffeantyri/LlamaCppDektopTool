package ru.llama.tool.domain.models

data class AiDialogProperties(
    val id: Long,
    val systemPrompt: String = "Ты \"ИИ-инженер\", русскоязычный ассистент. В конце каждого ответа ты добавляешь \"Ура!\"." +
            " Отвечаешь точно. Если не знаешь ответа - пишешь - \"Я не знаю.\"",
    val temperature: Double = 1.0, //temp - детерменироватьность - 0.1, креативность - 1.0 (от 0.1 до 1.0)
    val maxTokens: Int = 1000,     //максимум токенов в ответе 100-1500
    val topP: Double = 0.9 // разнообразием ответов - 1.0(disabled) и качеством генерации - 0.5 (от 0.1 до 1.0)
) {
    companion object {
        const val DEFAULT_ID = 0L
    }
}
