#include <jni.h>
#include <string>
#include <vector>
#include <cstring>
#include <android/log.h>
#include <cstdlib>
#include <cmath>
#include <algorithm>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "LLAMA", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "LLAMA", __VA_ARGS__)

// Подключаем ваш llama.h
extern "C" {
#include "include/llama.h"
}

// Глобальные параметры генерации с дефолтными значениями
static float g_temperature = 0.8f;
static float g_top_p = 0.9f;
static int g_top_k = 40;
static float g_repeat_penalty = 1.1f;
static int g_seed = -1;

static std::vector<llama_token>
tokenize_prompt(JNIEnv *env, jstring prompt, const struct llama_vocab *vocab);

static bool evaluate_prompt(const std::vector<llama_token> &tokens);

static std::vector<llama_token>
generate_tokens(int maxTokens, const struct llama_vocab *vocab, int n_past);

static std::string
decode_tokens(const std::vector<llama_token> &tokens, const struct llama_vocab *vocab);

// Глобальные указатели
static llama_model *g_model = nullptr;
static llama_context *g_ctx = nullptr;

extern "C" {

// Тестовая функция
JNIEXPORT jstring JNICALL
Java_ru_llama_tool_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    return env->NewStringUTF("LLAMA_LOG");
}

// Установка параметров генерации
JNIEXPORT void JNICALL
Java_ru_llama_tool_MainActivity_setTemperature(JNIEnv *env, jobject /* this */,
                                               jfloat temperature) {
    g_temperature = temperature > 0.0f ? temperature : 1.0f;
    LOGI("LLAMA_LOG Temperature set to: %.2f", g_temperature);
}

JNIEXPORT void JNICALL
Java_ru_llama_tool_MainActivity_setTopP(JNIEnv *env, jobject /* this */, jfloat top_p) {
    g_top_p = top_p;
    LOGI("LLAMA_LOG Top-P set to: %.2f", g_top_p);
}

JNIEXPORT void JNICALL
Java_ru_llama_tool_MainActivity_setTopK(JNIEnv *env, jobject /* this */, jint top_k) {
    g_top_k = top_k;
    LOGI("LLAMA_LOG Top-K set to: %d", g_top_k);
}

JNIEXPORT void JNICALL
Java_ru_llama_tool_MainActivity_setRepeatPenalty(JNIEnv *env, jobject /* this */, jfloat penalty) {
    g_repeat_penalty = penalty;
    LOGI("LLAMA_LOG Repeat penalty set to: %.2f", g_repeat_penalty);
}

JNIEXPORT void JNICALL
Java_ru_llama_tool_MainActivity_setSeed(JNIEnv *env, jobject /* this */, jint seed) {
    g_seed = seed;
    if (g_seed >= 0) {
        srand(g_seed);
    }
    LOGI("LLAMA_LOG Seed set to: %d", g_seed);
}

// Получение текущих параметров
JNIEXPORT jstring JNICALL
Java_ru_llama_tool_MainActivity_getGenerationParams(JNIEnv *env, jobject /* this */) {
    std::string params = "Temperature: " + std::to_string(g_temperature) +
                         "\nTop-P: " + std::to_string(g_top_p) +
                         "\nTop-K: " + std::to_string(g_top_k) +
                         "\nRepeat Penalty: " + std::to_string(g_repeat_penalty) +
                         "\nSeed: " + std::to_string(g_seed);
    return env->NewStringUTF(params.c_str());
}

// Загрузка модели
JNIEXPORT jboolean JNICALL
Java_ru_llama_tool_MainActivity_loadModel(JNIEnv *env, jobject /* this */, jstring modelPath) {
    if (g_model || g_ctx) {
        LOGE("LLAMA_LOG Model already loaded");
        return false;
    }

    const char *path = env->GetStringUTFChars(modelPath, nullptr);
    if (!path) {
        LOGE("LLAMA_LOG Failed to get model path string");
        return false;
    }
    LOGI("LLAMA_LOG Loading model from: %s", path);

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
        LOGE("LLAMA_LOG Failed to load model");
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
        LOGE("LLAMA_LOG Failed to create context");
        llama_model_free(g_model);
        g_model = nullptr;
        return false;
    }

    LOGI("LLAMA_LOG Model loaded successfully");
    return true;
}

// Выгрузка модели
JNIEXPORT void JNICALL
Java_ru_llama_tool_MainActivity_unloadModel(JNIEnv *env, jobject /* this */) {
    if (g_ctx) {
        llama_free(g_ctx);
        g_ctx = nullptr;
        LOGI("LLAMA_LOG Context freed");
    }
    if (g_model) {
        llama_model_free(g_model);
        g_model = nullptr;
        LOGI("LLAMA_LOG Model freed");
    }
}

JNIEXPORT jstring JNICALL
Java_ru_llama_tool_MainActivity_generateText(JNIEnv *env, jobject /* this */, jstring prompt,
                                             jint maxTokens) {
    LOGI("LLAMA_LOG Starting generateText");

    if (!g_ctx || !g_model) {
        LOGE("LLAMA_LOG Model or context not loaded");
        return env->NewStringUTF("Error: Model not loaded");
    }

    const struct llama_vocab *vocab = llama_model_get_vocab(g_model);
    LOGI("LLAMA_LOG Got vocab");

    // 1. Токенизация
    std::vector<llama_token> tokens = tokenize_prompt(env, prompt, vocab);
    if (tokens.empty()) {
        LOGE("LLAMA_LOG Tokenization failed or empty prompt");
        return env->NewStringUTF("Tokenization failed or empty prompt");
    }
    LOGI("LLAMA_LOG Tokenized %zu tokens", tokens.size());

    // 2. Оценка промпта
    LOGI("LLAMA_LOG Starting prompt evaluation");
    if (!evaluate_prompt(tokens)) {
        LOGE("LLAMA_LOG Prompt evaluation failed");
        return env->NewStringUTF("Prompt evaluation failed");
    }
    LOGI("LLAMA_LOG Prompt evaluation completed");

    // Сохраняем n_past для генерации
    int n_past = tokens.size();
    LOGI("LLAMA_LOG n_past set to: %d", n_past);

    // 3. Генерация токенов
    LOGI("LLAMA_LOG Starting token generation");
    std::vector<llama_token> generated = generate_tokens(maxTokens, vocab, n_past);
    LOGI("LLAMA_LOG Token generation completed, got %zu tokens", generated.size());

    if (generated.empty()) {
        LOGI("LLAMA_LOG No tokens generated");
    }

    // 4. Декодинг в строку
    LOGI("LLAMA_LOG Starting token decoding");
    std::string result = decode_tokens(generated, vocab);
    LOGI("LLAMA_LOG Decoding completed, result length: %zu", result.length());
    LOGI("LLAMA_LOG Final result: '%s'", result.c_str());

    LOGI("LLAMA_LOG Returning result");
    return env->NewStringUTF(result.c_str());
}

static std::vector<llama_token>
tokenize_prompt(JNIEnv *env, jstring prompt, const struct llama_vocab *vocab) {
    const char *promptStr = env->GetStringUTFChars(prompt, nullptr);
    if (!promptStr) return {};

    int text_len = strlen(promptStr);
    std::vector<llama_token> tokens(text_len + 32);

    int n_tokens = llama_tokenize(vocab, promptStr, text_len,
                                  tokens.data(), tokens.size(),
                                  true, false);

    env->ReleaseStringUTFChars(prompt, promptStr);

    if (n_tokens < 0) {
        LOGE("LLAMA_LOG Tokenization failed (buffer too small)");
        tokens.resize(-n_tokens);
        n_tokens = llama_tokenize(vocab, promptStr, text_len,
                                  tokens.data(), tokens.size(),
                                  true, false);
        if (n_tokens < 0) {
            LOGE("LLAMA_LOG Tokenization failed again");
            return {};
        }
    }

    tokens.resize(n_tokens);
    return tokens;
}

static std::vector<llama_token>
generate_tokens(int maxTokens, const struct llama_vocab *vocab, int n_past) {
    LOGI("LLAMA_LOG Starting generate_tokens with maxTokens=%d, n_past=%d", maxTokens, n_past);

    std::vector<llama_token> generated;
    std::vector<llama_token> previously_generated; // Для отслеживания повторов
    const int max_tokens_gen = (maxTokens > 0 && maxTokens <= 512) ? maxTokens : 128;

    llama_token eos_token = llama_vocab_eos(vocab);
    LOGI("LLAMA_LOG EOS token: %d", (int) eos_token);
    LOGI("LLAMA_LOG Generation params - Temp: %.2f, Top-P: %.2f, Top-K: %d, Repeat Penalty: %.2f",
         g_temperature, g_top_p, g_top_k, g_repeat_penalty);

    LOGI("LLAMA_LOG Starting generation loop, max_tokens_gen=%d", max_tokens_gen);

    // Инициализация seed если нужно
    if (g_seed >= 0) {
        srand(g_seed);
    }

    for (int i = 0; i < max_tokens_gen; ++i) {
        LOGI("LLAMA_LOG Generating token %d/%d", i + 1, max_tokens_gen);

        if (!g_ctx) {
            LOGE("LLAMA_LOG Context is null");
            break;
        }

        // Получаем логиты для последнего токена
        float *logits = llama_get_logits(g_ctx);
        if (!logits) {
            LOGE("LLAMA_LOG Cannot get logits for sampling");
            break;
        }

        int n_vocab = llama_vocab_n_tokens(vocab);

        // Применяем повторную пенальти
        if (g_repeat_penalty != 1.0f && !previously_generated.empty()) {
            for (const auto &token: previously_generated) {
                if (token >= 0 && token < n_vocab) {
                    if (logits[token] > 0) {
                        logits[token] /= g_repeat_penalty;
                    } else {
                        logits[token] *= g_repeat_penalty;
                    }
                }
            }
        }

        // Применяем температуру
        const float temperature = g_temperature > 0.0f ? g_temperature : 1.0f;
        LOGI("LLAMA_LOG Temperature: %.2f", temperature);

        // Создаем массив пар (логит, индекс) для сортировки
        std::vector<std::pair<float, int>> logits_with_idx;
        for (int j = 0; j < n_vocab; j++) {
            logits_with_idx.push_back({logits[j] / temperature, j});
        }

        // Сортируем по убыванию
        std::sort(logits_with_idx.begin(), logits_with_idx.end(),
                  [](const std::pair<float, int> &a, const std::pair<float, int> &b) {
                      return a.first > b.first;
                  });

        // Применяем top-k, если задан
        if (g_top_k > 0 && g_top_k < (int) logits_with_idx.size()) {
            logits_with_idx.resize(g_top_k);
            LOGI("LLAMA_LOG Applied Top-K: %d candidates", (int) logits_with_idx.size());
        }

        // Применяем top-p (nucleus) sampling, если задан
        std::vector<std::pair<float, int>> top_p_candidates;
        if (g_top_p > 0.0f && g_top_p < 1.0f && !logits_with_idx.empty()) {
            // Преобразуем логиты в вероятности для расчета кумулятивной суммы
            float max_logit = logits_with_idx[0].first;
            float sum_exp = 0.0f;
            std::vector<float> exp_logits(logits_with_idx.size());

            for (size_t j = 0; j < logits_with_idx.size(); j++) {
                exp_logits[j] = expf(logits_with_idx[j].first - max_logit);
                sum_exp += exp_logits[j];
            }

            // Нормализуем и находим top-p кандидатов
            float cum_sum = 0.0f;
            top_p_candidates.clear();

            for (size_t j = 0; j < logits_with_idx.size() && cum_sum < g_top_p; j++) {
                float prob = exp_logits[j] / sum_exp;
                cum_sum += prob;
                top_p_candidates.push_back(logits_with_idx[j]);
                LOGI("LLAMA_LOG Top-P candidate %zu: token=%d, prob=%.4f, cum_sum=%.4f",
                     j, logits_with_idx[j].second, prob, cum_sum);
            }

            LOGI("LLAMA_LOG Applied Top-P: %zu candidates", top_p_candidates.size());
        } else {
            top_p_candidates = logits_with_idx;
        }

        if (top_p_candidates.empty()) {
            top_p_candidates = logits_with_idx;
            LOGI("LLAMA_LOG Using fallback candidates: %zu", top_p_candidates.size());
        }

        // Преобразуем логиты в вероятности (softmax)
        float max_logit = top_p_candidates[0].first;
        float sum_exp = 0.0f;
        std::vector<float> exp_logits(top_p_candidates.size());

        for (size_t j = 0; j < top_p_candidates.size(); j++) {
            exp_logits[j] = expf(top_p_candidates[j].first - max_logit);
            sum_exp += exp_logits[j];
        }

        // Нормализуем
        std::vector<float> probs(top_p_candidates.size());
        for (size_t j = 0; j < top_p_candidates.size(); j++) {
            probs[j] = exp_logits[j] / sum_exp;
            LOGI("LLAMA_LOG Candidate %zu: token=%d, prob=%.4f",
                 j, top_p_candidates[j].second, probs[j]);
        }

        // Сэмплируем
        float rand_val = static_cast<float>(rand()) / RAND_MAX;
        LOGI("LLAMA_LOG Random value: %.4f", rand_val);

        float cum_sum = 0.0f;
        llama_token id = top_p_candidates[0].second; // fallback
        bool found = false;

        for (size_t j = 0; j < probs.size(); j++) {
            cum_sum += probs[j];
            LOGI("LLAMA_LOG Cumulative sum: %.4f", cum_sum);
            if (rand_val <= cum_sum) {
                id = top_p_candidates[j].second;
                found = true;
                LOGI("LLAMA_LOG Selected token: %d (prob=%.4f)", (int) id, probs[j]);
                break;
            }
        }

        // Если не нашли подходящий токен, используем первый
        if (!found && !top_p_candidates.empty()) {
            id = top_p_candidates[0].second;
            LOGI("LLAMA_LOG Fallback to best token: %d", (int) id);
        }

        LOGI("LLAMA_LOG Sampled token: %d", (int) id);

        // Проверяем EOS токен, но даем модели сгенерировать хотя бы несколько токенов
        if (id == eos_token && i > 2) {  // Позволяем сгенерировать минимум 3 токена
            LOGI("LLAMA_LOG EOS token reached, stopping generation. Generated %d tokens", i);
            break;
        }

        // Если EOS токен появляется сразу, пробуем другой токен
        if (id == eos_token && i <= 2 && top_p_candidates.size() > 1) {
            LOGI("LLAMA_LOG EOS token too early, trying alternative token");
            id = top_p_candidates[1].second;  // Берем второй по вероятности токен
            LOGI("LLAMA_LOG Using alternative token: %d", (int) id);
        }

        generated.push_back(id);
        previously_generated.push_back(id);
        LOGI("LLAMA_LOG Added token %d to generated list, size now: %zu", (int) id,
             generated.size());

        LOGI("LLAMA_LOG Creating batch for token %d, pos=%d", (int) id, n_past);
        // Используем более безопасный способ создания батча
        llama_batch batch = {0};
        llama_token token_data[1] = {id};
        llama_pos pos_data[1] = {static_cast<llama_pos>(n_past)};
        int32_t n_seq_id_data[1] = {1};
        int32_t seq_id_arr[1] = {0};
        int32_t *seq_id_ptr[1] = {seq_id_arr};
        int8_t logits_data[1] = {1}; // Всегда запрашиваем logits

        batch.n_tokens = 1;
        batch.token = token_data;
        batch.pos = pos_data;
        batch.n_seq_id = n_seq_id_data;
        batch.seq_id = seq_id_ptr;
        batch.logits = logits_data;

        LOGI("LLAMA_LOG Decoding generated token %d at pos %d", (int) id, n_past);
        int result = llama_decode(g_ctx, batch);

        if (result != 0) {
            LOGE("LLAMA_LOG Failed to decode generated token %d: error %d", (int) id, result);
            break;
        }
        n_past++;
        LOGI("LLAMA_LOG Successfully decoded token, n_past now: %d", n_past);
    }

    LOGI("LLAMA_LOG generate_tokens completed, generated %zu tokens", generated.size());
    return generated;
}

static bool evaluate_prompt(const std::vector<llama_token> &tokens) {
    llama_memory_t mem = llama_get_memory(g_ctx);
    if (mem != nullptr) {
        llama_memory_clear(mem, true);
    } else {
        LOGE("LLAMA_LOG Failed to get memory from context");
        return false;
    }

    // Статический массив для seq_id
    static int32_t seq_id_arr[1] = {0};

    for (size_t i = 0; i < tokens.size(); ++i) {
        const int n_logits = (i == tokens.size() - 1) ? 1 : 0;

        // Создаем батч вручную
        struct llama_batch batch = {0};

        // Инициализируем поля вручную
        llama_token token_data[1];
        llama_pos pos_data[1];
        int32_t n_seq_id_data[1];
        int32_t *seq_id_ptr[1];
        int8_t logits_data[1];

        token_data[0] = tokens[i];
        pos_data[0] = static_cast<llama_pos>(i);
        n_seq_id_data[0] = 1;
        seq_id_ptr[0] = seq_id_arr;
        logits_data[0] = static_cast<int8_t>(n_logits);

        batch.n_tokens = 1;
        batch.token = token_data;
        batch.pos = pos_data;
        batch.n_seq_id = n_seq_id_data;
        batch.seq_id = seq_id_ptr;
        batch.logits = logits_data;

        LOGI("LLAMA_LOG Processing token %zu/%zu: token_id=%d, pos=%d, logits=%d",
             i + 1, tokens.size(), (int) batch.token[0], (int) batch.pos[0], (int) batch.logits[0]);

        int result = llama_decode(g_ctx, batch);

        if (result != 0) {
            LOGE("LLAMA_LOG Failed to decode token %zu: error %d", i, result);
            return false;
        }
    }

    LOGI("LLAMA_LOG Successfully evaluated %zu tokens", tokens.size());
    return true;
}

static std::string
decode_tokens(const std::vector<llama_token> &tokens, const struct llama_vocab *vocab) {
    LOGI("LLAMA_LOG Decoding %zu tokens", tokens.size());

    std::string result;
    for (size_t i = 0; i < tokens.size(); i++) {
        llama_token token = tokens[i];
        char piece[64];
        int len = llama_token_to_piece(vocab, token, piece, sizeof(piece), 0, true);
        if (len > 0) {
            std::string token_str(piece, len);

            // Проверяем и заменяем специальные последовательности
            if (token_str == "<0x0A>") {
                result += "\n";
                LOGI("LLAMA_LOG Decoded token %zu (%d): '<0x0A>' -> '\\n'", i, (int) token);
            } else {
                result.append(piece, len);
                LOGI("LLAMA_LOG Decoded token %zu (%d): '%s'", i, (int) token, token_str.c_str());
            }
        } else {
            LOGI("LLAMA_LOG Failed to decode token %zu (%d), len=%d", i, (int) token, len);
        }
    }

    LOGI("LLAMA_LOG Raw decoded result: '%s'", result.c_str());

    // Очистка управляющих символов
    std::string cleaned;
    for (char c: result) {
        if (c >= 32 || c == '\n' || c == '\t' || c == ' ') {
            cleaned += c;
        }
    }

    LOGI("LLAMA_LOG Cleaned result: '%s'", cleaned.c_str());
    return cleaned;
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

}