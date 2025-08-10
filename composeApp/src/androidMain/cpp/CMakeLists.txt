cmake_minimum_required(VERSION 3.22.1)
project(llama_jni LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++17 -fexceptions -frtti")
# Для C
set(CMAKE_C_STANDARD 11)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c11")


# --- Источники ---
# JNI обёртка
set(JNI_SOURCES
        llama-android.cpp
)

# Common
set(COMMON_SOURCES
        common/common.cpp
        common/log.cpp # <-- Добавлено
)

# GGML
set(GGML_SOURCES
        ggml/src/ggml.c      # <-- Добавлено
        ggml/src/ggml-opt.cpp# <-- Добавлено
        ggml/src/gguf.cpp     # <-- Добавлено
        ggml/src/ggml-impl.h     # <-- Добавлено
        # Добавьте другие ggml-*.c файлы, если они используются common/common.cpp
        # ggml/src/ggml-alloc.c
        # ggml/src/ggml-backend.c
        # ggml/src/ggml-quants.c
        # ...
)

# Llama (основной код модели)
set(LLAMA_SOURCES
        # llama.cpp # <-- Обычно НЕ компилируется в JNI, если используется libllama.so.
        # Но если мы отказываемся от .so, нужно добавить.
        # Проверьте, требует ли common/common.cpp символы из llama.cpp.
        # Если да, добавьте сюда llama.cpp.
        # Это может значительно увеличить размер .so.
)

# Объединяем все источники для одной библиотеки
add_library(llama_jni SHARED
        ${JNI_SOURCES}
        ${COMMON_SOURCES}
        ${GGML_SOURCES}
        # ${LLAMA_SOURCES} # Добавьте, если необходимо
)

# --- Заголовки ---
target_include_directories(llama_jni PRIVATE
        .                     # Для файлов в корне cpp/
        ./include             # Если llama.h и ggml.h лежат здесь
        ./common              # Для common.h, log.h
        ./ggml/include        # Для ggml.h, gguf.h
        ./ggml/src            # Иногда нужно, если .c файлы инклудят друг друга по относительным путям
)

# --- Линковка ---
find_library(log-lib log)
# Линкуем ТОЛЬКО системные библиотеки. Все остальные .c/.cpp скомпилированы внутри.
target_link_libraries(llama_jni PRIVATE ${log-lib}) # -latomic и -lm могут понадобиться

# --- (Опционально) Флаги компилятора ---
# Добавьте флаги, если они требуются для ggml или других библиотек
# target_compile_definitions(llama_jni PRIVATE GGML_USE_ACCELERATE) # Например, для iOS
# target_compile_options(llama_jni PRIVATE -mfma -mf16c) # Например, для x86_64, если нужно
# Для Android arm64 эти флаги обычно устанавливаются тулчейном.