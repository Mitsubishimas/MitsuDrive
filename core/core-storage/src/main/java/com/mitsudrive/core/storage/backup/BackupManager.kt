package com.mitsudrive.core.storage.backup

import android.content.Context
import com.mitsudrive.core.storage.FileManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class BackupManager(
    private val context: Context,
    private val fileManager: FileManager
) {
    
    companion object {
        private const val MAX_BACKUPS = 7
        private val DATE_FORMAT = SimpleDateFormat("yyyy_MM_dd_HH_mm", Locale.getDefault())
    }
    
    // Создание бэкапа
    fun createBackup(): File? {
        return try {
            val timestamp = DATE_FORMAT.format(Date())
            val backupFile = File(
                fileManager.getBackupsDir(),
                "backup_$timestamp.db"
            )
            
            // Копируем БД
            val dbFile = context.getDatabasePath("drivenet.db")
            if (dbFile.exists()) {
                fileManager.copyFile(dbFile, backupFile)
                
                // Удаляем старые бэкапы
                cleanupOldBackups()
                
                backupFile
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    // Восстановление из бэкапа
    fun restoreBackup(backupFile: File): Boolean {
        return try {
            val dbFile = context.getDatabasePath("drivenet.db")
            fileManager.copyFile(backupFile, dbFile)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // Получение списка бэкапов
    fun getBackups(): List<File> {
        return fileManager.getBackupsDir()
            .listFiles()
            ?.filter { it.isFile && it.extension == "db" }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }
    
    // Удаление бэкапа
    fun deleteBackup(backupFile: File): Boolean {
        return fileManager.deleteFile(backupFile)
    }
    
    // Очистка старых бэкапов
    private fun cleanupOldBackups() {
        val backups = getBackups()
        if (backups.size > MAX_BACKUPS) {
            backups.drop(MAX_BACKUPS).forEach { backup ->
                backup.delete()
            }
        }
    }
}
