package ru.llama.tool.presentation.chat_screen.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun extractErrorMessage503LoadingModel(errorLog: String): String? {
    // Регулярное выражение для извлечения JSON строки после "Text: \"...\""
    val textRegex = Regex("""Text:\s*"(\{.*\})"""", RegexOption.DOT_MATCHES_ALL)
    val jsonMatch = textRegex.find(errorLog) ?: return null

    val jsonString = jsonMatch.groupValues[1]
        .replace("\\\"", "\"")  // Убираем экранирование кавычек
        .replace(
            "\\\\",
            "\\"
        )  // Если были экранированные слеши, восстанавливаем (на всякий случай)

    return try {
        val json = Json { ignoreUnknownKeys = true }
        val parsed = json.parseToJsonElement(jsonString).jsonObject
        parsed["error"]
            ?.jsonObject
            ?.get("message")
            ?.jsonPrimitive
            ?.content
    } catch (e: Exception) {
        null // Если JSON битый или структура не та
    }
}