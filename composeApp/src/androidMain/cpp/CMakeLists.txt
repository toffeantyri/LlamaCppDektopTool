cmake_minimum_required(VERSION 3.22.1)

# Название библиотеки, которую мы создаём
project("native-lib")

# Создаём shared library: libnative-lib.so
# Будет содержать код из native-lib.cpp и llama.h
add_library(
        native-lib
        SHARED
        native-lib.cpp
        llama.h
)

# Подключаем системные библиотеки Android (обязательно)
find_library(
        log-lib
        log
)

# Указываем, что native-lib зависит от log (для Android logging)
target_link_libraries(
        native-lib
        ${log-lib}
)