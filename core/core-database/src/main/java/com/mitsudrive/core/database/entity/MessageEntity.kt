package com.mitsudrive.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["chatId", "createdAt"]),
        Index(value = ["status"]),
        Index(value = ["messageType"])
    ]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val chatId: String,
    val userId: String,
    val messageType: String, // 'text', 'image', 'video', 'document', 'location', 'system'
    val content: String? = null,
    val mediaId: String? = null,
    val mediaCaption: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val locationName: String? = null,
    val createdAt: String,
    val updatedAt: String? = null,
    val isEdited: Boolean = false,
    val editHistory: String? = null, // JSON
    val status: String = "sent", // 'sending', 'sent', 'delivered', 'read', 'failed'
    val deliveredAt: String? = null,
    val readAt: String? = null,
    val replyToMessageId: String? = null,
    val forwardedFrom: String? = null,
    val isDeleted: Boolean = false,
    val deletedForEveryone: Boolean = false,
    val deletedAt: String? = null,
    val syncStatus: String = "synced"
)
