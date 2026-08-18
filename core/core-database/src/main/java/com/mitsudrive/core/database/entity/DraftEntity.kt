package com.mitsudrive.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "drafts",
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DraftEntity(
    @PrimaryKey
    val chatId: String,
    val content: String? = null,
    val mediaPaths: String? = null, // JSON array
    val replyToMessageId: String? = null,
    val updatedAt: String
)
