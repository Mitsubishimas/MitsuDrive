package com.mitsudrive.core.storage

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID

class FileManager(private val context: Context) {
    
    companion object {
        private const val MEDIA_DIR = "media"
        private const val IMAGES_DIR = "images"
        private const val VIDEOS_DIR = "videos"
        private const val DOCUMENTS_DIR = "documents"
        private const val AVATARS_DIR = "avatars"
        private const val TEMP_DIR = "temp"
        private const val BACKUPS_DIR = "backups"
    }
    
    // Получение корневой директории
    private val rootDir: File
        get() = context.filesDir
    
    // Получение директории для медиа
    fun getMediaDir(): File {
        return File(rootDir, MEDIA_DIR).apply { mkdirs() }
    }
    
    // Получение директории для изображений
    fun getImagesDir(): File {
        return File(getMediaDir(), IMAGES_DIR).apply { mkdirs() }
    }
    
    // Получение директории для видео
    fun getVideosDir(): File {
        return File(getMediaDir(), VIDEOS_DIR).apply { mkdirs() }
    }
    
    // Получение директории для документов
    fun getDocumentsDir(): File {
        return File(getMediaDir(), DOCUMENTS_DIR).apply { mkdirs() }
    }
    
    // Получение директории для аватаров
    fun getAvatarsDir(): File {
        return File(getMediaDir(), AVATARS_DIR).apply { mkdirs() }
    }
    
    // Получение временной директории
    fun getTempDir(): File {
        return File(rootDir, TEMP_DIR).apply { mkdirs() }
    }
    
    // Получение директории для бэкапов
    fun getBackupsDir(): File {
        return File(rootDir, BACKUPS_DIR).apply { mkdirs() }
    }
    
    // Создание уникального имени файла
    fun generateFileName(extension: String): String {
        return "${UUID.randomUUID()}.$extension"
    }
    
    // Создание файла для медиа
    fun createMediaFile(
        mediaType: String,
        extension: String
    ): File {
        val dir = when (mediaType) {
            "image" -> getImagesDir()
            "video" -> getVideosDir()
            "document" -> getDocumentsDir()
            "avatar" -> getAvatarsDir()
            else -> getTempDir()
        }
        
        return File(dir, generateFileName(extension))
    }
    
    // Копирование файла
    fun copyFile(source: File, destination: File): Boolean {
        return try {
            FileInputStream(source).use { input ->
                FileOutputStream(destination).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // Перемещение файла
    fun moveFile(source: File, destination: File): Boolean {
        return try {
            source.renameTo(destination)
        } catch (e: Exception) {
            false
        }
    }
    
    // Удаление файла
    fun deleteFile(file: File): Boolean {
        return try {
            file.delete()
        } catch (e: Exception) {
            false
        }
    }
    
    // Получение размера файла
    fun getFileSize(file: File): Long {
        return file.length()
    }
    
    // Получение размера директории
    fun getDirectorySize(dir: File): Long {
        var size = 0L
        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) {
                getDirectorySize(file)
            } else {
                file.length()
            }
        }
        return size
    }
    
    // Очистка временной директории
    fun clearTempDir() {
        getTempDir().listFiles()?.forEach { file ->
            file.delete()
        }
    }
    
    // Очистка всех медиа
    fun clearAllMedia() {
        getMediaDir().listFiles()?.forEach { file ->
            if (file.isDirectory) {
                file.listFiles()?.forEach { it.delete() }
            }
            file.delete()
        }
    }
    
    // Проверка свободного места
    fun hasEnoughSpace(requiredBytes: Long): Boolean {
        val freeSpace = rootDir.usableSpace
        return freeSpace > requiredBytes * 2 // двойной запас
    }
}
