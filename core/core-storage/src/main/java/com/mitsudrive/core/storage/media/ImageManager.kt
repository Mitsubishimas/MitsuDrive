package com.mitsudrive.core.storage.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mitsudrive.core.storage.FileManager
import java.io.File
import java.io.FileOutputStream

class ImageManager(
    private val context: Context,
    private val fileManager: FileManager
) {
    
    companion object {
        private const val MAX_IMAGE_SIZE = 1920 // px
        private const val THUMBNAIL_SIZE = 320 // px
        private const val COMPRESSION_QUALITY = 80 // %
    }
    
    // Сохранение изображения
    fun saveImage(
        bitmap: Bitmap,
        mediaType: String = "image"
    ): File? {
        return try {
            val file = fileManager.createMediaFile(mediaType, "webp")
            
            // Сжатие изображения
            val compressedBitmap = compressImage(bitmap, MAX_IMAGE_SIZE)
            
            FileOutputStream(file).use { output ->
                compressedBitmap.compress(
                    Bitmap.CompressFormat.WEBP,
                    COMPRESSION_QUALITY,
                    output
                )
            }
            
            file
        } catch (e: Exception) {
            null
        }
    }
    
    // Создание миниатюры
    fun createThumbnail(
        imageFile: File,
        mediaType: String = "image"
    ): File? {
        return try {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            val thumbnailBitmap = compressImage(bitmap, THUMBNAIL_SIZE)
            
            val thumbnailFile = fileManager.createMediaFile(
                mediaType,
                "webp"
            )
            
            FileOutputStream(thumbnailFile).use { output ->
                thumbnailBitmap.compress(
                    Bitmap.CompressFormat.WEBP,
                    60,
                    output
                )
            }
            
            thumbnailFile
        } catch (e: Exception) {
            null
        }
    }
    
    // Загрузка изображения из файла
    fun loadImage(file: File): Bitmap? {
        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            null
        }
    }
    
    // Сжатие изображения
    private fun compressImage(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }
        
        val ratio = minOf(
            maxSize.toFloat() / width,
            maxSize.toFloat() / height
        )
        
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    // Удаление изображения
    fun deleteImage(file: File): Boolean {
        return fileManager.deleteFile(file)
    }
}
