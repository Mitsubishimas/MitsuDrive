package com.mitsudrive.core.network.websocket

import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.*
import java.util.concurrent.TimeUnit

class DriveWebSocketClient(
    private val wsUrl: String = "wss://ws.mitsudrive.app/",
    private val gson: Gson = Gson()
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .build()
    
    private var webSocket: WebSocket? = null
    private var isConnected = false
    
    private val _messages = MutableSharedFlow<WsMessage>(
        replay = 0,
        extraBufferCapacity = 100,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    val messages: SharedFlow<WsMessage> = _messages.asSharedFlow()
    
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    fun connect(token: String) {
        val request = Request.Builder()
            .url("${wsUrl}?token=$token")
            .build()
        
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isConnected = true
                _connectionState.value = ConnectionState.Connected
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val message = parseMessage(text)
                    message?.let { 
                        _messages.tryEmit(it)
                    }
                } catch (e: Exception) {
                    // Ошибка парсинга
                }
            }
            
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
                _connectionState.value = ConnectionState.Disconnected
                webSocket.close(1000, null)
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
                _connectionState.value = ConnectionState.Disconnected
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                _connectionState.value = ConnectionState.Error(t.message ?: "Connection failed")
            }
        })
    }
    
    fun disconnect() {
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
        isConnected = false
        _connectionState.value = ConnectionState.Disconnected
    }
    
    fun send(message: WsOutgoingMessage) {
        if (isConnected) {
            val json = gson.toJson(message)
            webSocket?.send(json)
        }
    }
    
    fun subscribe(channels: List<String>) {
        send(WsOutgoingMessage.Subscribe(channels = channels))
    }
    
    fun unsubscribe(channels: List<String>) {
        send(WsOutgoingMessage.Unsubscribe(channels = channels))
    }
    
    private fun parseMessage(text: String): WsMessage? {
        return try {
            val jsonObject = gson.fromJson(text, com.google.gson.JsonObject::class.java)
            val type = jsonObject.get("type")?.asString ?: return null
            
            when (type) {
                "connected" -> gson.fromJson(text, WsMessage.Connected::class.java)
                "message" -> gson.fromJson(text, WsMessage.Message::class.java)
                "presence" -> gson.fromJson(text, WsMessage.Presence::class.java)
                "typing" -> gson.fromJson(text, WsMessage.Typing::class.java)
                "sos" -> gson.fromJson(text, WsMessage.SosAlert::class.java)
                else -> WsMessage.Event(type = type, eventType = type, payload = text)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    sealed class ConnectionState {
        object Connected : ConnectionState()
        object Disconnected : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }
}
