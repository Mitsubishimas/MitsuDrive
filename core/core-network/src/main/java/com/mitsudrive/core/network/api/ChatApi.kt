package com.mitsudrive.core.network.api

import com.mitsudrive.core.network.model.ApiResponse
import com.mitsudrive.core.network.model.PaginatedResponse
import retrofit2.http.*

data class ChatDto(
    val id: String,
    val chatType: String,
    val title: String?,
    val avatarUrl: String?,
    val lastMessage: MessageDto?,
    val unreadCount: Int,
    val participantsCount: Int,
    val updatedAt: String
)

data class MessageDto(
    val id: String,
    val chatId: String,
    val userId: String,
    val messageType: String,
    val content: String?,
    val mediaId: String?,
    val createdAt: String,
    val isEdited: Boolean,
    val status: String
)

interface ChatApi {
    @GET("chats")
    suspend fun getChats(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): ApiResponse<PaginatedResponse<ChatDto>>
    
    @GET("chats/{chatId}")
    suspend fun getChat(@Path("chatId") chatId: String): ApiResponse<ChatDto>
    
    @POST("chats")
    suspend fun createChat(
        @Body request: CreateChatRequest
    ): ApiResponse<ChatDto>
    
    @GET("chats/{chatId}/messages")
    suspend fun getMessages(
        @Path("chatId") chatId: String,
        @Query("before") before: String? = null,
        @Query("after") after: String? = null,
        @Query("pageSize") pageSize: Int = 50
    ): ApiResponse<PaginatedResponse<MessageDto>>
    
    @POST("chats/{chatId}/messages")
    suspend fun sendMessage(
        @Path("chatId") chatId: String,
        @Body request: SendMessageRequest
    ): ApiResponse<MessageDto>
    
    @PUT("chats/{chatId}/messages/{messageId}")
    suspend fun editMessage(
        @Path("chatId") chatId: String,
        @Path("messageId") messageId: String,
        @Body request: EditMessageRequest
    ): ApiResponse<MessageDto>
    
    @DELETE("chats/{chatId}/messages/{messageId}")
    suspend fun deleteMessage(
        @Path("chatId") chatId: String,
        @Path("messageId") messageId: String,
        @Query("forEveryone") forEveryone: Boolean = false
    ): ApiResponse<Unit>
}

data class CreateChatRequest(
    val chatType: String,
    val participantIds: List<String>,
    val title: String? = null
)

data class SendMessageRequest(
    val messageType: String,
    val content: String?,
    val mediaId: String?,
    val replyTo: String?
)

data class EditMessageRequest(
    val content: String
)
