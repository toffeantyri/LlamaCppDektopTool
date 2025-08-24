package ru.llama.tool.data.io

import android.content.Context
import java.io.File

class GGUFFileManagerImpl(private val context: Context) : GGUFFileManager {


    private fun getGGUFFiles(): List<File> {
        val filesDir = context.filesDir
        return findGGUFFiles(filesDir)
    }

    private fun findGGUFFiles(directory: File): List<File> {
        val ggufFiles = mutableListOf<File>()
        println("LLAMA_LOG findGGUFFiles in $directory")
        if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()
            println("LLAMA_LOG files in $directory ${files.size}")
            files?.forEach { file ->
                when {
                    file.isFile && file.name.lowercase().endsWith(".gguf") -> {
                        ggufFiles.add(file)
                    }

                    file.isDirectory -> {
                        ggufFiles.addAll(findGGUFFiles(file))
                    }
                }
            }
        }

        return ggufFiles
    }

    override fun getExistFiles(): List<GGUFFileInfo> {
        return getGGUFFiles().map { file ->
            GGUFFileInfo(
                name = file.name,
                path = file.absolutePath,
                size = file.length()
            )
        }
    }

    override fun openFilePicker() {
        //todo
    }


}