package com.mitsudrive.features.chat.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsudrive.features.chat.api.ChatRepository
import com.mitsudrive.features.chat.api.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatListUiState(
    val chats: List<ChatRoom> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatListViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()
    
    init {
        observeChats()
    }
    
    private fun observeChats() {
        viewModelScope.launch {
            chatRepository.observeChats().collect { chats ->
                _uiState.update { it.copy(chats = chats) }
            }
        }
    }
    
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
    
    fun markAsRead(chatId: String) {
        viewModelScope.launch {
            chatRepository.markAsRead(chatId)
        }
    }
    
    fun createPrivateChat(userId: String) {
        viewModelScope.launch {
            chatRepository.createChat(
                type = ChatType.PRIVATE,
                participantIds = listOf(userId),
                title = null
            )
        }
    }
    
    fun createGroupChat(participantIds: List<String>, title: String) {
        viewModelScope.launch {
            chatRepository.createChat(
                type = ChatType.GROUP,
                participantIds = participantIds,
                title = title
            )
        }
    }
}
