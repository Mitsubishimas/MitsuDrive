package com.mitsudrive.features.chat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsudrive.features.chat.api.ChatRepository
import com.mitsudrive.features.chat.api.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null
)

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val chatId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    init {
        observeMessages()
        loadHistory()
        markAsRead()
    }
    
    private fun observeMessages() {
        viewModelScope.launch {
            chatRepository.observeMessages(chatId).collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }
    
    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            chatRepository.loadHistory(chatId, null)
            _uiState.update { it.copy(isLoading = false) }
        }
    }
    
    private fun markAsRead() {
        viewModelScope.launch {
            chatRepository.markAsRead(chatId)
        }
    }
    
    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }
    
    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, inputText = "") }
            
            chatRepository.sendMessage(
                chatId = chatId,
                request = SendMessageRequest(
                    messageType = MessageType.TEXT,
                    content = text,
                    mediaId = null,
                    replyToMessageId = null
                )
            ).onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
            
            _uiState.update { it.copy(isSending = false) }
        }
    }
    
    fun editMessage(messageId: String, newContent: String) {
        viewModelScope.launch {
            chatRepository.editMessage(
                chatId = chatId,
                messageId = messageId,
                request = EditMessageRequest(content = newContent)
            )
        }
    }
    
    fun deleteMessage(messageId: String, forEveryone: Boolean = false) {
        viewModelScope.launch {
            chatRepository.deleteMessage(
                chatId = chatId,
                messageId = messageId,
                forEveryone = forEveryone
            )
        }
    }
}
