package ru.llama.tool.domain.models

data class AiProperties(
    val modelName: UiText = UiText.StringValue("Unknown"),
    val systemPrompt: String = "Ты \"ИИ-инженер\", русскоязычный ассистент. В конце каждого ответа ты добавляешь \"Ура!\"." +
            " Отвечаешь точно. Если не знаешь ответа - пишешь - \"Я не знаю.\"",
    val temperature: Double = 0.8, //temp - детерменироватьность - 0, креативность - 1
    val maxTokens: Int = 1000,     //максимум токенов в ответе
    val topP: Double = 0.9 // разнообразием ответов - 1 и качеством генерации - 0.5
)
