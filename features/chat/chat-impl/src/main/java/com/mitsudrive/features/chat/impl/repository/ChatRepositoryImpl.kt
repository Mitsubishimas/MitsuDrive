package com.mitsudrive.features.chat.impl.repository

import com.mitsudrive.features.chat.api.ChatRepository
import com.mitsudrive.features.chat.api.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ChatRepositoryImpl : ChatRepository {
    
    // Локальный кэш чатов
    private val _chats = MutableStateFlow<List<ChatRoom>>(emptyList())
    
    // Кэш сообщений по чатам
    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    
    private val mutex = Mutex()
    
    override fun observeChats(): Flow<List<ChatRoom>> {
        return _chats.asStateFlow()
    }
    
    override fun observeMessages(chatId: String): Flow<List<ChatMessage>> {
        return _messages
            .map { messagesMap -> messagesMap[chatId] ?: emptyList() }
            .distinctUntilChanged()
    }
    
    override suspend fun sendMessage(
        chatId: String,
        request: SendMessageRequest
    ): Result<ChatMessage> {
        return try {
            val message = ChatMessage(
                id = "msg_${System.currentTimeMillis()}",
                chatId = chatId,
                senderId = "current_user",
                senderName = "Вы",
                senderAvatarUrl = null,
                messageType = request.messageType,
                content = request.content,
                mediaId = request.mediaId,
                mediaCaption = null,
                lat = null,
                lng = null,
                createdAt = System.currentTimeMillis().toString(),
                updatedAt = null,
                isEdited = false,
                status = MessageStatus.SENDING,
                replyTo = null,
                isPending = true
            )
            
            // Добавляем в локальный кэш
            _messages.update { current ->
                val chatMessages = current[chatId] ?: emptyList()
                current + (chatId to (chatMessages + message))
            }
            
            // Обновляем чат
            updateChatLastMessage(chatId, message)
            
            // TODO: Отправка через WebSocket
            // Имитация успешной отправки
            kotlinx.coroutines.delay(500)
            updateMessageStatus(chatId, message.id, MessageStatus.SENT)
            
            Result.success(message.copy(status = MessageStatus.SENT))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun editMessage(
        chatId: String,
        messageId: String,
        request: EditMessageRequest
    ): Result<ChatMessage> {
        return try {
            var editedMessage: ChatMessage? = null
            
            _messages.update { current ->
                val chatMessages = current[chatId] ?: emptyList()
                current + (chatId to chatMessages.map { message ->
                    if (message.id == messageId) {
                        val newMessage = message.copy(
                            content = request.content,
                            updatedAt = System.currentTimeMillis().toString(),
                            isEdited = true
                        )
                        editedMessage = newMessage
                        newMessage
                    } else {
                        message
                    }
                })
            }
            
            if (editedMessage != null) {
                Result.success(editedMessage!!)
            } else {
                Result.failure(Exception("Message not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteMessage(
        chatId: String,
        messageId: String,
        forEveryone: Boolean
    ): Result<Unit> {
        return try {
            _messages.update { current ->
                val chatMessages = current[chatId] ?: emptyList()
                current + (chatId to chatMessages.filter { it.id != messageId })
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun loadHistory(chatId: String, before: String?) {
        mutex.withLock {
            // TODO: Загрузка с сервера
            // Имитация загрузки
            kotlinx.coroutines.delay(300)
            
            // Временные данные
            val mockMessages = createMockMessages(chatId)
            _messages.update { current ->
                val existing = current[chatId] ?: emptyList()
                current + (chatId to (mockMessages + existing).distinctBy { it.id })
            }
        }
    }
    
    override suspend fun markAsRead(chatId: String) {
        _chats.update { current ->
            current.map { chat ->
                if (chat.id == chatId) {
                    chat.copy(unreadCount = 0)
                } else {
                    chat
                }
            }
        }
        
        _messages.update { current ->
            val chatMessages = current[chatId] ?: emptyList()
            current + (chatId to chatMessages.map { message ->
                if (message.status == MessageStatus.DELIVERED) {
                    message.copy(status = MessageStatus.READ)
                } else {
                    message
                }
            })
        }
    }
    
    override suspend fun createChat(
        type: ChatType,
        participantIds: List<String>,
        title: String?
    ): Result<ChatRoom> {
        return try {
            val chat = ChatRoom(
                id = "chat_${System.currentTimeMillis()}",
                chatType = type,
                title = title,
                avatarUrl = null,
                lastMessage = null,
                unreadCount = 0,
                participantsCount = participantIds.size + 1,
                isMuted = false,
                isPinned = false,
                isArchived = false,
                updatedAt = System.currentTimeMillis().toString()
            )
            
            _chats.update { current -> listOf(chat) + current }
            Result.success(chat)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun searchChats(query: String): Flow<List<ChatRoom>> {
        return _chats.map { chats ->
            chats.filter { chat ->
                chat.title?.contains(query, ignoreCase = true) ?: false
            }
        }
    }
    
    // Вспомогательные методы
    
    private fun updateChatLastMessage(chatId: String, message: ChatMessage) {
        _chats.update { current ->
            current.map { chat ->
                if (chat.id == chatId) {
                    chat.copy(
                        lastMessage = message,
                        updatedAt = message.createdAt,
                        unreadCount = chat.unreadCount + 1
                    )
                } else {
                    chat
                }
            }
        }
    }
    
    private fun updateMessageStatus(
        chatId: String,
        messageId: String,
        status: MessageStatus
    ) {
        _messages.update { current ->
            val chatMessages = current[chatId] ?: emptyList()
            current + (chatId to chatMessages.map { message ->
                if (message.id == messageId) {
                    message.copy(status = status, isPending = false)
                } else {
                    message
                }
            })
        }
    }
    
    
    // Создание демо-чатов
    suspend fun createMockChatsForDemo() {
        if (_chats.value.isNotEmpty()) return
        
        val mockChats = listOf(
            com.mitsudrive.features.chat.api.model.ChatRoom(
                id = "chat_demo_1",
                chatType = com.mitsudrive.features.chat.api.model.ChatType.PRIVATE,
                title = "Дмитрий",
                avatarUrl = null,
                lastMessage = com.mitsudrive.features.chat.api.model.ChatMessage(
                    id = "msg_last_1",
                    chatId = "chat_demo_1",
                    senderId = "user_1",
                    senderName = "Дмитрий",
                    senderAvatarUrl = null,
                    messageType = com.mitsudrive.features.chat.api.model.MessageType.TEXT,
                    content = "Привет! Как дела с вариатором?",
                    mediaId = null,
                    mediaCaption = null,
                    lat = null,
                    lng = null,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = null,
                    isEdited = false,
                    status = com.mitsudrive.features.chat.api.model.MessageStatus.READ,
                    replyTo = null,
                    isPending = false
                ),
                unreadCount = 2,
                participantsCount = 2,
                isMuted = false,
                isPinned = true,
                isArchived = false,
                updatedAt = System.currentTimeMillis().toString()
            ),
            com.mitsudrive.features.chat.api.model.ChatRoom(
                id = "chat_demo_2",
                chatType = com.mitsudrive.features.chat.api.model.ChatType.GROUP,
                title = "Mitsubishi Club",
                avatarUrl = null,
                lastMessage = com.mitsudrive.features.chat.api.model.ChatMessage(
                    id = "msg_last_2",
                    chatId = "chat_demo_2",
                    senderId = "user_2",
                    senderName = "Алексей",
                    senderAvatarUrl = null,
                    messageType = com.mitsudrive.features.chat.api.model.MessageType.TEXT,
                    content = "Кто едет на встречу в субботу?",
                    mediaId = null,
                    mediaCaption = null,
                    lat = null,
                    lng = null,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = null,
                    isEdited = false,
                    status = com.mitsudrive.features.chat.api.model.MessageStatus.DELIVERED,
                    replyTo = null,
                    isPending = false
                ),
                unreadCount = 5,
                participantsCount = 12,
                isMuted = false,
                isPinned = false,
                isArchived = false,
                updatedAt = System.currentTimeMillis().toString()
            ),
            com.mitsudrive.features.chat.api.model.ChatRoom(
                id = "chat_demo_3",
                chatType = com.mitsudrive.features.chat.api.model.ChatType.CITY,
                title = "Москва",
                avatarUrl = null,
                lastMessage = com.mitsudrive.features.chat.api.model.ChatMessage(
                    id = "msg_last_3",
                    chatId = "chat_demo_3",
                    senderId = "user_3",
                    senderName = "Сергей",
                    senderAvatarUrl = null,
                    messageType = com.mitsudrive.features.chat.api.model.MessageType.TEXT,
                    content = "На МКАДе пробка 10 км",
                    mediaId = null,
                    mediaCaption = null,
                    lat = null,
                    lng = null,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = null,
                    isEdited = false,
                    status = com.mitsudrive.features.chat.api.model.MessageStatus.DELIVERED,
                    replyTo = null,
                    isPending = false
                ),
                unreadCount = 0,
                participantsCount = 156,
                isMuted = true,
                isPinned = false,
                isArchived = false,
                updatedAt = System.currentTimeMillis().toString()
            )
        )
        
        _chats.value = mockChats
        
        // Создаём сообщения для демо-чатов
        mockChats.forEach { chat ->
            val messages = createMockMessages(chat.id)
            _messages.update { current ->
                current + (chat.id to messages)
            }
        }
    }
    
    private fun createMockMessages(chatId: String): List<ChatMessage> {
        return listOf(
            ChatMessage(
                id = "msg_1_$chatId",
                chatId = chatId,
                senderId = "user_1",
                senderName = "Дмитрий",
                senderAvatarUrl = null,
                messageType = MessageType.TEXT,
                content = "Привет! Как дела с вариатором?",
                mediaId = null,
                mediaCaption = null,
                lat = null,
                lng = null,
                createdAt = (System.currentTimeMillis() - 3600000).toString(),
                updatedAt = null,
                isEdited = false,
                status = MessageStatus.READ,
                replyTo = null,
                isPending = false
            ),
            ChatMessage(
                id = "msg_2_$chatId",
                chatId = chatId,
                senderId = "current_user",
                senderName = "Вы",
                senderAvatarUrl = null,
                messageType = MessageType.TEXT,
                content = "Привет! Пока не решил проблему. Думаю менять гидроблок.",
                mediaId = null,
                mediaCaption = null,
                lat = null,
                lng = null,
                createdAt = (System.currentTimeMillis() - 3500000).toString(),
                updatedAt = null,
                isEdited = false,
                status = MessageStatus.READ,
                replyTo = null,
                isPending = false
            )
        )
    }
}
