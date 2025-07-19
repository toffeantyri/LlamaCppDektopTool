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
import io.ktor.sse.ServerSentEvent
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
import ru.llama.tool.core.EMPTY
import ru.llama.tool.data.api.models.llama_models.LLamaMessageDto
import ru.llama.tool.data.api.models.llama_models.LlamaResponseDto
import ru.llama.tool.data.api.models.llama_props_dto.HealthAiDto
import ru.llama.tool.data.api.models.llama_props_dto.LlamaProperties
import ru.llama.tool.data.api.models.messages.MessageRequest
import ru.llama.tool.data.api.setting_http_client_provider.ISettingHttpClientProvider
import ru.llama.tool.domain.models.AiDialogProperties
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

    override suspend fun sseRequestAi(
        messages: List<MessageRequest>,
        aiProps: AiDialogProperties
    ): Flow<Message> =
        callbackFlow {
            println("[SSE] Starting new SSE session")
            val path = "v1/chat/completions"
            var job: Job? = null

            job = launch(Dispatchers.IO) {
                val sseSession = client.sseSession(
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
                            top_p = aiProps.topP,
                            temperature = aiProps.temperature,
                            max_tokens = aiProps.maxTokens,
                        )
                    )
                }

                fun releaseResources(t: Throwable? = null) {
                    sseSession.call.response.cancel()
                    job?.cancel()
                    close(t)
                }

                fun ServerSentEvent.logEvent() {
                    if (this.event != null || this.retry != null || this.comments != null) {
                        println("ServerSentEvent : $this")
                    }
                }



                try {
                    var emitted: Boolean = false

                    fun sendNotEmittedError(t: Throwable? = null) {
                        if (emitted.not()) {
                            trySend(
                                Message(
                                    content = EMPTY,
                                    sender = EnumSender.Error(
                                        t ?: Throwable("Server is busy. Try again later.")
                                    ),
                                    id = messages.last().id,
                                )
                            )
                        }
                    }

                    sseSession.incoming.onCompletion {
                        println("API onCompletion $it")
                        sendNotEmittedError(it)
                        this@callbackFlow.cancel(CancellationException(it?.message))
                    }.catch {
                        println("API catch $it")
                        sendNotEmittedError(it)
                        this@callbackFlow.cancel(CancellationException(it.message))
                    }.collect { event ->
                        event.logEvent()

                        event.data?.let { data ->
                            if (data.startsWith("{")) {
                                val content = json.decodeFromString<LlamaResponseDto>(data)
                                    .choices.getOrNull(0)?.delta?.content ?: return@let
                                emitted = true
                                trySend(
                                    Message(
                                        content = content,
                                        sender = EnumSender.AI,
                                        id = messages.last().id
                                    )
                                )
                            } else if (data == "[DONE]") {
                                releaseResources()
                            }
                        }
                    }
                } catch (e: Exception) {
                    releaseResources(e)
                    println("[SSE] Error in SSE session: $e")
                } finally {
                    releaseResources()
                    println("[SSE] Old SSE session finally closed")
                }
            }

            awaitClose {
                println("[SSE] Closing SSE session by awaitClose")
                close()
                job.cancel()
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun simpleRequestAi(
        messages: List<MessageRequest>,
        aiProps: AiDialogProperties
    ): Flow<Message> {
        val path = "v1/chat/completions"

        val response = client.post(baseUrl()) {
            url {
                appendPathSegments(path)
            }
            contentType(ContentType.Application.Json)
            setBody(
                LLamaMessageDto(
                    messages = messages,
                    stream = true,
                    top_p = aiProps.topP,
                    temperature = aiProps.temperature,
                    max_tokens = aiProps.maxTokens,
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