package com.mitsudrive.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "outgoing_queue",
    foreignKeys = [
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["messageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["nextAttemptAt"])
    ]
)
data class OutgoingQueueEntity(
    @PrimaryKey
    val id: String,
    val messageId: String,
    val chatId: String,
    val payload: String, // JSON
    val attempts: Int = 0,
    val maxAttempts: Int = 10,
    val nextAttemptAt: String? = null,
    val createdAt: String
)
