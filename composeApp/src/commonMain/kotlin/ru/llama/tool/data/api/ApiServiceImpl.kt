package ru.llama.tool.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.sse.sseSession
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ru.llama.tool.data.api.models.llama_models.LLamaMessageDto
import ru.llama.tool.data.api.models.llama_models.LlamaResponseDto
import ru.llama.tool.data.api.models.llama_props_dto.HealthAiDto
import ru.llama.tool.data.api.models.llama_props_dto.LlamaProperties
import ru.llama.tool.data.api.models.messages.MessageRequest
import ru.llama.tool.data.api.setting_http_client_provider.ISettingHttpClientProvider
import ru.llama.tool.domain.models.EnumSender
import ru.llama.tool.domain.models.Message
import java.util.concurrent.CancellationException
import kotlin.time.Duration.Companion.seconds

private val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    explicitNulls = true
}

private fun extractHostAndPort(url: String): String {
    return url
        .removePrefix("http://")
        .removePrefix("https://")
        .trimEnd('/')
}

class ApiServiceImpl(
    private val client: HttpClient,
    private val settingProvider: ISettingHttpClientProvider
) : ApiService {

    private val baseUrl = { settingProvider.getBaseUrl() }

    override suspend fun getModelProperties(): LlamaProperties {
        return client.get(baseUrl()) {
            url {
                appendPathSegments("props")
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun sseRequestAi(messages: List<MessageRequest>): Flow<Message> =
        callbackFlow {
            println("[SSE] Starting new SSE session")
            val path = "v1/chat/completions"
            val properties = settingProvider.getRequestSetting()


            var job: Job? = null

            job = launch(Dispatchers.IO) {

                val sseSession =
                    client.sseSession(
                        baseUrl(),
                        showCommentEvents = true,
                        showRetryEvents = true,
                        reconnectionTime = 10.seconds
                    ) {
                        method = HttpMethod.Post
                        url {
                            appendPathSegments(path)
                        }
                        contentType(ContentType.Application.Json)
                        header("Accept", "text/event-stream")
                        header("Cache-Control", "no-store")
                        setBody(
                            LLamaMessageDto(
                                messages = messages,
                                stream = true,
                                top_p = properties.topP,
                                temperature = properties.temperature,
                                max_tokens = properties.maxTokens,
                            )
                        )
                    }

                try {
                    sseSession.incoming.onCompletion {
                        println("API onCompletion $it")
                        this@callbackFlow.cancel(CancellationException(it?.message))
                    }.catch {
                        println("API catch $it")
                        this@callbackFlow.cancel(CancellationException(it.message))
                    }.collect { event ->

                        event.event?.let {
                            println("API event $it")
                        }

                        event.comments?.let {
                            println("API comments $it")
                        }

                        event.retry?.let {
                            println("API retry $it")
                        }

                        event.data?.let { line ->
                            if (line.startsWith("{")) {
                                val data = json.decodeFromString<LlamaResponseDto>(line)
                                    .choices.getOrNull(0)?.delta?.content ?: return@let

                                trySend(
                                    Message(
                                        content = data,
                                        sender = EnumSender.AI,
                                        id = messages.last().id
                                    )
                                )
                            } else if (line == "[DONE]") {
                                // Завершаем поток
                                sseSession.call.response.cancel()
//                                sseSession.cancel()
                                close()
                                job?.cancel()
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("[SSE] Error in SSE session: $e")
                    sseSession.call.response.cancel()
//                    sseSession.cancel()
                    close(e)
                    job?.cancel()
                } finally {
                    sseSession.call.response.cancel()
//                    sseSession.cancel()
                    close()
                    job?.cancel()
                    println("[SSE] Old SSE session closed")
                }
            }

            awaitClose {
                println("[SSE] Closing SSE session via awaitClose")
//                sseSession.call.response.cancel()
//                                sseSession.cancel()
                close()
                job.cancel()
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun simpleRequestAi(messages: List<MessageRequest>): Flow<Message> {
        val path = "v1/chat/completions"
        val properties = settingProvider.getRequestSetting()

        val response = client.post(baseUrl()) {
            url {
                appendPathSegments(path)
            }
            contentType(ContentType.Application.Json)
            setBody(
                LLamaMessageDto(
                    messages = messages,
                    stream = true,
                    top_p = properties.topP,
                    temperature = properties.temperature,
                    max_tokens = properties.maxTokens,
                )
            )
        }

        require(response.contentType()?.withoutParameters() == ContentType.Text.EventStream) {
            "Expected text/event-stream but got ${response.contentType()}"
        }

        return flow {
            val content = response.bodyAsChannel()
            val dataBuilder = StringBuilder()

            while (!content.isClosedForRead) {
                val line = content.readUTF8Line(1) ?: break

                if (line.startsWith("data:")) {
                    val jsonString = line.substringAfter(":").trim()
                    if (jsonString == "[DONE]") {
                        emit(
                            Message(
                                content = "",
                                sender = EnumSender.AI,
                                id = messages.last().id
                            )
                        )
                    } else {
                        val data =
                            json.decodeFromString<LlamaResponseDto>(jsonString).choices[0].delta.content
                                ?: ""
                        dataBuilder.clear()
                        dataBuilder.append(data)
                        emit(
                            Message(
                                content = data,
                                sender = EnumSender.AI,
                                id = messages.last().id
                            )
                        )
                    }
                }
                delay(10)
            }

            if (dataBuilder.isNotEmpty()) {
                emit(
                    Message(
                        content = dataBuilder.toString(),
                        sender = EnumSender.AI,
                        id = messages.last().id
                    )
                )
            }


        }

    }

    override suspend fun getHealthAi(): HealthAiDto {
        return client.get(baseUrl()) {
            url {
                appendPathSegments("health")
            }
        }.body()
    }
}