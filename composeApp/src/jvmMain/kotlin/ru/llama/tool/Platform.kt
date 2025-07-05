package ru.llama.tool


actual fun getPlatform() = "Java ${System.getProperty("java.version")}"