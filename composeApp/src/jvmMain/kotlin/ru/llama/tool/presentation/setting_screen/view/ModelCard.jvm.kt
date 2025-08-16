package ru.llama.tool.presentation.setting_screen.view

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.filechooser.FileNameExtensionFilter

actual fun openFilePicker(onFileSelected: (String?) -> Unit) {
    // Запускаем в EDT потоке для корректной работы UI
    SwingUtilities.invokeLater {
        // Принудительно устанавливаем системный Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        showFileChooserByJFileChooser(onFileSelected)
    }
}

private fun showFileChooserByJFileChooser(onFileSelected: (String?) -> Unit) {
    val fileChooser = JFileChooser().apply {
        dialogTitle = "Выберите GGUF модель"
        fileSelectionMode = JFileChooser.FILES_ONLY

        // Создаем строгий фильтр
        fileFilter = FileNameExtensionFilter("GGUF Model Files (*.gguf)", "gguf")


        // Отключаем все другие фильтры
        isAcceptAllFileFilterUsed = false

        // Добавляем только нужный фильтр
        resetChoosableFileFilters()
        addChoosableFileFilter(FileNameExtensionFilter("GGUF Files", "gguf"))

        // Устанавливаем стартовую папку
        currentDirectory = File(System.getProperty("user.home")) // Домашняя директория

        // Или можно установить конкретную папку:
        // currentDirectory = File("/path/to/your/folder")


        // Устанавливаем фильтр по умолчанию
        fileFilter = choosableFileFilters[0]
    }

    val result = fileChooser.showOpenDialog(null)

    if (result == JFileChooser.APPROVE_OPTION) {
        val selectedFile = fileChooser.selectedFile
        if (selectedFile.name.lowercase().endsWith(".gguf")) {
            onFileSelected(selectedFile.absolutePath)
        } else {
            onFileSelected(null)
        }
    } else {
        onFileSelected(null)
    }
}


private fun openFilePickerNative(onFileSelected: (String?) -> Unit) {
    var selectedPath: String? = null

    val fileDialog = FileDialog(null as Frame?, "Выберите GGUF модель", FileDialog.LOAD).apply {
        filenameFilter = FilenameFilter { _, name ->
            name.lowercase().endsWith(".gguf")
        }

        // Устанавливаем начальное значение для фильтрации
        file = "*.gguf"
    }

    fileDialog.isVisible = true

    val selectedFile = fileDialog.file
    val directory = fileDialog.directory

    if (selectedFile != null && directory != null) {
        // Проверяем, что файл действительно заканчивается на .gguf
        if (selectedFile.lowercase().endsWith(".gguf")) {
            val fullPath = buildFilePath(directory, selectedFile)
            selectedPath = fullPath
        }
        // Если файл не .gguf, возвращаем null (пользователь может игнорировать фильтр)
    }

    fileDialog.dispose()
    onFileSelected(selectedPath)
}

private fun buildFilePath(directory: String, file: String): String {
    return when {
        directory.endsWith("/") -> "$directory$file"
        directory.endsWith("\\") -> "$directory$file"
        else -> "$directory${java.io.File.separator}$file"
    }
}