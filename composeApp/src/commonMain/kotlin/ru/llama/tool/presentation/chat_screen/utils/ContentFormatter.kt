package ru.llama.tool.presentation.chat_screen.utils


fun formatAiResponse(rawContent: String): String {

    try {
        val (reasoning, answer) = extractLeadingNewlineBlock(rawContent)

        val formatted = if (reasoning.isNotBlank()) {
            "… " + reasoning + "… \n\n" + answer
        } else {
            answer
        }
        return formatted
    } catch (e: Exception) {
        println("formatAiResponse error $e")
        return rawContent
    }

}

private fun extractLeadingNewlineBlock(text: String): Pair<String, String> {
    // Паттерн:
    // ^           — начало строки
    // \n+         — один или несколько \n в начале
    // (.*?)       — "лениво" захватываем всё, что идёт после \n
    // \n+         — один или несколько \n после этого текста
    // (.*)        — весь оставшийся текст
    // $           — конец строки
    val pattern = Regex("""^\n+(.*?)\n+(.*)$""", RegexOption.DOT_MATCHES_ALL)
    val match = pattern.find(text)

    if (match != null) {
        val thoughts = match.groupValues[1].trim() // текст между \n и \n
        val mainContent = match.groupValues[2].trim() // то, что после второго \n
        return thoughts to mainContent
    }

    // Если паттерн не найден — весь текст считаем основным
    return "" to text
}