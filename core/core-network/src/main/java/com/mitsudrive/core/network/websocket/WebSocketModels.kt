package com.mitsudrive.core.network.websocket

// Входящие сообщения
sealed class WsMessage {
    abstract val type: String
    
    data class Connected(
        override val type: String = "connected",
        val userId: String,
        val timestamp: Long
    ) : WsMessage()
    
    data class Message(
        override val type: String = "message",
        val id: String,
        val chatId: String,
        val userId: String,
        val content: String?,
        val messageType: String,
        val mediaId: String?,
        val createdAt: String,
        val replyTo: String?
    ) : WsMessage()
    
    data class Presence(
        override val type: String = "presence",
        val userId: String,
        val isOnline: Boolean,
        val lastSeen: String?
    ) : WsMessage()
    
    data class Typing(
        override val type: String = "typing",
        val chatId: String,
        val userId: String,
        val isTyping: Boolean
    ) : WsMessage()
    
    data class SosAlert(
        override val type: String = "sos",
        val id: String,
        val userId: String,
        val lat: Double,
        val lng: Double,
        val message: String?,
        val timestamp: String
    ) : WsMessage()
    
    data class Event(
        override val type: String,
        val eventType: String,
        val payload: String
    ) : WsMessage()
}

// Исходящие сообщения
sealed class WsOutgoingMessage {
    abstract val type: String
    
    data class Subscribe(
        override val type: String = "subscribe",
        val channels: List<String>
    ) : WsOutgoingMessage()
    
    data class Unsubscribe(
        override val type: String = "unsubscribe",
        val channels: List<String>
    ) : WsOutgoingMessage()
    
    data class SendMessage(
        override val type: String = "send_message",
        val chatId: String,
        val content: String?,
        val messageType: String,
        val mediaId: String? = null,
        val replyTo: String? = null
    ) : WsOutgoingMessage()
    
    data class UpdatePresence(
        override val type: String = "presence",
        val isOnline: Boolean
    ) : WsOutgoingMessage()
    
    data class TypingIndicator(
        override val type: String = "typing",
        val chatId: String,
        val isTyping: Boolean
    ) : WsOutgoingMessage()
    
    data class Ping(
        override val type: String = "ping"
    ) : WsOutgoingMessage()
}
