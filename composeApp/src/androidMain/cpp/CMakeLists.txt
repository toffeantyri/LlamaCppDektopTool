cmake_minimum_required(VERSION 3.22)
project("llamaapp")

# Включаем C++17
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED TRUE)

# Опционально: отключаем RTTI и exceptions (если не используются)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-rtti -fno-exceptions")

# Указываем путь к заголовкам llama.h
# ВАЖНО: путь должен вести к папке, содержащей llama.h
# Пример: если llama.h лежит в src/main/cpp/include/llama.h → указываем эту папку
include_directories(src/androidMain/cpp/include)  # или src/main/cpp

# Импортируем libllama.so как shared library
add_library(llama SHARED IMPORTED)

# Устанавливаем путь к libllama.so для текущей архитектуры (ARM64, x86_64 и т.д.)
set_target_properties(llama PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libllama.so)

# Создаём свою библиотеку (native-lib.so)
add_library(native-lib SHARED native-lib.cpp)

# Подключаем системную библиотеку log (__android_log_print)
find_library(log-lib log)

# Линкуем: native-lib → libllama.so + log
target_link_libraries(
        native-lib
        llama
        ${log-lib}
)