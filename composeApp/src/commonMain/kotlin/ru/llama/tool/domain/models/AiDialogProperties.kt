package ru.llama.tool.domain.models

data class AiDialogProperties(
    val systemPrompt: String = INITIAL_SYSTEM_PROMPT, //системный промпт для задания правил и самоидентификации
    val temperature: Double = 1.0, //temp - детерменироватьность - 0.1, креативность - 1.0 (от 0.1 до 1.0)
    val maxTokens: Int = 1000,     //максимум токенов в ответе 100-1500
    val topP: Double = 0.9, // разнообразием ответов - 1.0(disabled)
    val thinkingEnabled: Boolean = false
) {
    companion object {
        const val DEFAULT_ID = 0L
        const val INITIAL_SYSTEM_PROMPT =
            "Ты \"ИИ-инженер\", русскоязычный ассистент. В конце каждого ответа ты добавляешь \"Ура!\"." +
                    " Отвечаешь точно. Если не знаешь ответа - пишешь - \"Я не знаю.\""
    }
}
