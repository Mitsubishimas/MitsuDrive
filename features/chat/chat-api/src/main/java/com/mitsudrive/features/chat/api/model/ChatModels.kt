package com.mitsudrive.features.chat.api.model

data class ChatRoom(
    val id: String,
    val chatType: ChatType,
    val title: String?,
    val avatarUrl: String?,
    val lastMessage: ChatMessage?,
    val unreadCount: Int,
    val participantsCount: Int,
    val isMuted: Boolean,
    val isPinned: Boolean,
    val isArchived: Boolean,
    val updatedAt: String
)

enum class ChatType {
    PRIVATE,
    GROUP,
    BRAND,
    CITY
}

data class ChatMessage(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatarUrl: String?,
    val messageType: MessageType,
    val content: String?,
    val mediaId: String?,
    val mediaCaption: String?,
    val lat: Double?,
    val lng: Double?,
    val createdAt: String,
    val updatedAt: String?,
    val isEdited: Boolean,
    val status: MessageStatus,
    val replyTo: ChatMessage?,
    val isPending: Boolean
)

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    DOCUMENT,
    LOCATION,
    SYSTEM
}

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}

data class SendMessageRequest(
    val messageType: MessageType,
    val content: String?,
    val mediaId: String?,
    val replyToMessageId: String?
)

data class EditMessageRequest(
    val content: String
)
