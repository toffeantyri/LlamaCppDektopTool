package ru.llama.tool.data.io

interface GGUFFileManager {

    fun getExistFiles(): List<GGUFFileInfo>

    fun openFilePicker()

}