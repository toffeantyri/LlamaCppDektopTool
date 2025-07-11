package ru.llama.tool.data.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import ru.llama.tool.data.api.models.llama_models.LLamaMessageDto
import ru.llama.tool.data.api.models.llama_models.LlamaResponseDto
import ru.llama.tool.data.api.models.llama_models.MessageRequest
import ru.llama.tool.domain.models.EnumSender
import ru.llama.tool.domain.models.Message

private val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    explicitNulls = true
}

class ApiServiceImpl(
    private val client: HttpClient
) : ApiService {

    override suspend fun simpleRequestAi(message: MessageRequest): Flow<LlamaResponseDto> = flow {
        val url = "http://127.0.0.1:8080/v1/chat/completions"

        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(
                LLamaMessageDto(
                    messages = listOf(message),
                    stream = true
                )
            )
        }


        val input = response.bodyAsChannel()
        val packet = input.readRemaining(4096)
        var partial = ""

        var counter = 0

        while (!packet.exhausted()) {
            val bytes = packet.readByteArray()
            val text = bytes.decodeToString()
            val combined = partial + text

            val lines = combined.split("\n")

            partial = lines.last()

            for (line in lines.dropLast(1)) {
                if (line.startsWith(" ")) {
                    val jsonString = line.substringAfter(" ").trim()
                    if (jsonString == "[DONE]") continue

                    println("Count ${++counter}")
                    try {
                        val dto = json.decodeFromString<LlamaResponseDto>(line)
                        emit(dto)
                    } catch (e: Exception) {
                        println("Error 1 $e")
                    }

                }
            }
        }


        if (partial.isNotBlank()) {

            val jsonString = partial.trim()
            if (jsonString != "[DONE]") {

                try {
                    val dto = json.decodeFromString<LlamaResponseDto>(partial)
                    emit(dto)
//                println(dto.choices[0].delta.content)
                } catch (e: Exception) {
                    println("Error 2 $e")
                }
            }
        }
    }

    @OptIn(InternalAPI::class)
    override suspend fun simpleRequestAi2(message: MessageRequest): Flow<Message> = flow {
        val url = "http://127.0.0.1:8080/v1/chat/completions"

        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(
                LLamaMessageDto(
                    messages = listOf(message),
                    stream = true
                )
            )
        }

        require(response.contentType()?.withoutParameters() == ContentType.Text.EventStream) {
            "Expected text/event-stream but got ${response.contentType()}"
        }

        val content = response.bodyAsChannel()

        var dataBuilder = StringBuilder()

        while (!content.isClosedForRead) {
            val line = content.readUTF8Line() ?: break

            if (line.startsWith("data:")) {
                val jsonString = line.substringAfter(":").trim()
                val data =
                    json.decodeFromString<LlamaResponseDto>(jsonString).choices[0].delta.content
                        ?: ""
                dataBuilder.clear()
                dataBuilder.append(data)
            } else if (line.isBlank() && dataBuilder.isNotEmpty()) {
                emit(
                    Message(
                        content = dataBuilder.toString(),
                        sender = EnumSender.AI
                    )
                )
            }
        }

        if (dataBuilder.isNotEmpty()) {
            emit(
                Message(
                    content = dataBuilder.toString(),
                    sender = EnumSender.AI
                )
            )
        }


    }
}