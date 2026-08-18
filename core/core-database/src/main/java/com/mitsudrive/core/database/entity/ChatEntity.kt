package com.mitsudrive.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chats",
    indices = [
        Index(value = ["updatedAt"])
    ]
)
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val chatType: String, // 'private', 'group', 'brand', 'city'
    val title: String? = null,
    val avatarUrl: String? = null,
    val avatarLocalPath: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val lastMessageId: String? = null,
    val lastMessageText: String? = null,
    val lastMessageTime: String? = null,
    val unreadCount: Int = 0,
    val isMuted: Boolean = false,
    val isArchived: Boolean = false,
    val isPinned: Boolean = false,
    val participantsCount: Int = 1,
    val description: String? = null,
    val ownerId: String? = null,
    val syncStatus: String = "synced",
    val lastSyncAt: String? = null
)
