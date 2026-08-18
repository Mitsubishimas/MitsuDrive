package com.mitsudrive.features.chat.api

import com.mitsudrive.features.chat.api.model.*
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    // Список чатов
    fun observeChats(): Flow<List<ChatRoom>>
    
    // Сообщения в чате
    fun observeMessages(chatId: String): Flow<List<ChatMessage>>
    
    // Отправка сообщения
    suspend fun sendMessage(
        chatId: String,
        request: SendMessageRequest
    ): Result<ChatMessage>
    
    // Редактирование сообщения
    suspend fun editMessage(
        chatId: String,
        messageId: String,
        request: EditMessageRequest
    ): Result<ChatMessage>
    
    // Удаление сообщения
    suspend fun deleteMessage(
        chatId: String,
        messageId: String,
        forEveryone: Boolean
    ): Result<Unit>
    
    // Загрузка истории
    suspend fun loadHistory(chatId: String, before: String?)
    
    // Отметить как прочитанное
    suspend fun markAsRead(chatId: String)
    
    // Создание чата
    suspend fun createChat(
        type: ChatType,
        participantIds: List<String>,
        title: String?
    ): Result<ChatRoom>
    
    // Поиск чатов
    fun searchChats(query: String): Flow<List<ChatRoom>>
}
