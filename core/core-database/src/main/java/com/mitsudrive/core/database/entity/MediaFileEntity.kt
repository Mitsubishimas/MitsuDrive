package com.mitsudrive.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "media_files",
    foreignKeys = [
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["messageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["messageId"]),
        Index(value = ["downloadStatus"])
    ]
)
data class MediaFileEntity(
    @PrimaryKey
    val id: String,
    val messageId: String,
    val chatId: String,
    val mediaType: String, // 'image', 'video', 'document'
    val fileName: String? = null,
    val localPath: String? = null,
    val remoteUrl: String? = null,
    val mimeType: String? = null,
    val fileSize: Long = 0,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Int? = null,
    val thumbnailPath: String? = null,
    val thumbnailUrl: String? = null,
    val documentType: String? = null,
    val downloadStatus: String = "not_downloaded", // 'not_downloaded', 'downloading', 'downloaded', 'failed'
    val downloadProgress: Float = 0f,
    val uploadedToServer: Boolean = false,
    val originalSize: Long = 0,
    val compressedSize: Long = 0,
    val compressionRatio: Float = 1f,
    val createdAt: String
)
