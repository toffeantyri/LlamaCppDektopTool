#include <jni.h>
#include <string>
#include <vector>
#include <cstring>
#include <android/log.h>
#include <cstdlib>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "LLAMA", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "LLAMA", __VA_ARGS__)

// Подключаем ваш llama.h
extern "C" {
#include "include/llama.h"
}

// Глобальные указатели
static llama_model *g_model = nullptr;
static llama_context *g_ctx = nullptr;

extern "C" {

// Тестовая функция
JNIEXPORT jstring JNICALL
Java_ru_llama_tool_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    return env->NewStringUTF("Hello from llama.cpp!");
}

// Загрузка модели
JNIEXPORT jboolean JNICALL
Java_ru_llama_tool_MainActivity_loadModel(JNIEnv *env, jobject /* this */, jstring modelPath) {
    if (g_model || g_ctx) {
        LOGE("Model already loaded");
        return false;
    }

    const char *path = env->GetStringUTFChars(modelPath, nullptr);
    if (!path) {
        LOGE("Failed to get model path string");
        return false;
    }
    LOGI("Loading model from: %s", path);

    // Инициализация бэкенда (один раз)
    llama_backend_init();

    // === Параметры модели ===
    struct llama_model_params model_params = llama_model_default_params();
    model_params.n_gpu_layers = 0;          // CPU-only
    model_params.vocab_only = false;
    model_params.use_mmap = true;
    model_params.use_mlock = false;
    model_params.progress_callback = nullptr;
    model_params.progress_callback_user_data = nullptr;

    g_model = llama_model_load_from_file(path, model_params);
    env->ReleaseStringUTFChars(modelPath, path);

    if (!g_model) {
        LOGE("Failed to load model");
        return false;
    }

    // === Параметры контекста ===
    struct llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx = 2048;
    ctx_params.n_batch = 512;
    ctx_params.n_ubatch = 512;
    ctx_params.n_threads = 4;
    ctx_params.n_threads_batch = 4;
    ctx_params.pooling_type = LLAMA_POOLING_TYPE_NONE;
    ctx_params.offload_kqv = false;
    ctx_params.embeddings = false;  // не извлекаем эмбеддинги
    ctx_params.no_perf = true;      // отключаем замеры производительности

    g_ctx = llama_init_from_model(g_model, ctx_params);
    if (!g_ctx) {
        LOGE("Failed to create context");
        llama_model_free(g_model);
        g_model = nullptr;
        return false;
    }

    LOGI("Model loaded successfully");
    return true;
}

// Выгрузка модели
JNIEXPORT void JNICALL
Java_ru_llama_tool_MainActivity_unloadModel(JNIEnv *env, jobject /* this */) {
    if (g_ctx) {
        llama_free(g_ctx);
        g_ctx = nullptr;
        LOGI("Context freed");
    }
    if (g_model) {
        llama_model_free(g_model);
        g_model = nullptr;
        LOGI("Model freed");
    }
}

// Генерация текста
JNIEXPORT jstring JNICALL
Java_ru_llama_tool_MainActivity_generateText(JNIEnv *env, jobject /* this */, jstring prompt,
                                             jint maxTokens) {
    if (!g_ctx || !g_model) {
        LOGE("Model or context not loaded");
        return env->NewStringUTF("Error: Model not loaded");
    }

    const char *promptStr = env->GetStringUTFChars(prompt, nullptr);
    if (!promptStr) {
        return env->NewStringUTF("");
    }
    LOGI("Generating text for prompt: %s", promptStr);

    const struct llama_vocab *vocab = llama_model_get_vocab(g_model);
    int32_t n_vocab = llama_vocab_n_tokens(vocab);

    // === 1. Токенизация ===
    int text_len = strlen(promptStr);
    std::vector<llama_token> tokens;
    int max_tokens_possible = text_len + 32;
    tokens.resize(max_tokens_possible);

    int n_tokens = llama_tokenize(vocab, promptStr, text_len,
                                  tokens.data(), tokens.size(),
                                  true,  // add_bos
                                  false  // parse_special
    );

    if (n_tokens < 0) {
        LOGE("Tokenization failed (buffer too small), trying larger buffer");
        tokens.resize(-n_tokens);
        n_tokens = llama_tokenize(vocab, promptStr, text_len,
                                  tokens.data(), tokens.size(),
                                  true, false);
        if (n_tokens < 0 || n_tokens > (int) tokens.size()) {
            LOGE("Tokenization failed again");
            env->ReleaseStringUTFChars(prompt, promptStr);
            return env->NewStringUTF("Tokenization failed");
        }
    }
    tokens.resize(n_tokens);

    env->ReleaseStringUTFChars(prompt, promptStr);

    // === 2. Оценка промпта через llama_batch ===
    llama_batch batch = llama_batch_init(n_tokens, 0, 1);

    for (int i = 0; i < n_tokens; i++) {
        batch.token[i] = tokens[i];
        batch.pos[i] = i;
        batch.n_seq_id[i] = 1;
        batch.seq_id[i][0] = 0;
        batch.logits[i] = (i == n_tokens - 1) ? 1 : 0;  // только последний токен выводит логиты
    }

    if (llama_decode(g_ctx, batch) != 0) {
        LOGE("Failed to decode prompt");
        llama_batch_free(batch);
        return env->NewStringUTF("Decode failed");
    }
    llama_batch_free(batch);

    int n_past = n_tokens;

    // === 3. Настройка сэмплера ===
    llama_sampler *sampler = llama_sampler_chain_init(llama_sampler_chain_default_params());
    llama_sampler_chain_add(sampler, llama_sampler_init_top_k(40));
    llama_sampler_chain_add(sampler, llama_sampler_init_top_p(0.95f, 1));
    llama_sampler_chain_add(sampler, llama_sampler_init_temp(1.0f));
    llama_sampler_chain_add(sampler, llama_sampler_init_dist(1234)); // seed

    // === 4. Генерация токенов ===
    std::vector<llama_token> generated;
    const int max_tokens_gen = (maxTokens > 0 && maxTokens <= 512) ? maxTokens : 128;
    llama_token eos_token = llama_vocab_eos(vocab);

    for (int i = 0; i < max_tokens_gen; i++) {
        llama_token id = llama_sampler_sample(sampler, g_ctx, -1);  // последний логит
        if (id == eos_token) break;

        generated.push_back(id);
        llama_sampler_accept(sampler, id);

        // Подаем сгенерированный токен обратно
        llama_batch single = llama_batch_get_one(&id, 1);
        single.pos[0] = n_past;
        if (llama_decode(g_ctx, single) != 0) {
            LOGE("Failed to decode generated token");
            llama_batch_free(single);
            break;
        }
        llama_batch_free(single);
        n_past++;
    }

    llama_sampler_free(sampler);

    // === 5. Декодинг в строку ===
    std::string result;
    for (llama_token token: generated) {
        char piece[64];
        int len = llama_token_to_piece(vocab, token, piece, sizeof(piece), 0, true);
        if (len > 0) {
            result.append(piece, len);
        }
    }

    // Очистка: убираем управляющие символы, кроме \n и \t
    std::string cleaned;
    for (char c: result) {
        if (c >= 32 || c == '\n' || c == '\t') {
            cleaned += c;
        }
    }

    return env->NewStringUTF(cleaned.c_str());
}

// Получение информации о модели
JNIEXPORT jstring JNICALL
Java_ru_llama_tool_MainActivity_getModelInfo(JNIEnv *env, jobject /* this */) {
    if (!g_model) {
        return env->NewStringUTF("Model not loaded");
    }

    const struct llama_vocab *vocab = llama_model_get_vocab(g_model);
    std::string info;
    info += "Model loaded\n";
    info += "Vocab: " + std::to_string(llama_vocab_n_tokens(vocab)) + "\n";
    info += "Embeddings: " + std::to_string(llama_model_n_embd(g_model)) + "\n";
    info += "Layers: " + std::to_string(llama_model_n_layer(g_model)) + "\n";
    info += "Context (train): " + std::to_string(llama_model_n_ctx_train(g_model)) + "\n";

    return env->NewStringUTF(info.c_str());
}

} // extern "C"