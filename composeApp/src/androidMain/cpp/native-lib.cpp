#include <jni.h>
#include <string>
#include "llama.h"

extern "C" {

// Пример простой функции
JNIEXPORT jstring JNICALL
Java_ru_llama_tool_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from llama.cpp!";
    return env->NewStringUTF(hello.c_str());
}

// Пример: загрузка модели (заглушка)
JNIEXPORT jboolean JNICALL
Java_ru_llama_tool_MainActivity_loadModel(JNIEnv *env, jobject /* this */, jstring modelPath) {
    const char *path = env->GetStringUTFChars(modelPath, nullptr);
    // Здесь будет вызов llama_load_model_from_file(...)
    // Пока просто заглушка
    env->ReleaseStringUTFChars(modelPath, path);
    return true; // Успешно
}
}