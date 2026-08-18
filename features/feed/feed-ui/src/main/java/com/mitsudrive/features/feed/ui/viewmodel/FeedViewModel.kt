package com.mitsudrive.features.feed.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mitsudrive.features.feed.api.FeedRepository
import com.mitsudrive.features.feed.api.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FeedUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = true
)

class FeedViewModel(
    private val feedRepository: FeedRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()
    
    init {
        observeFeed()
        refresh()
    }
    
    private fun observeFeed() {
        viewModelScope.launch {
            feedRepository.observeFeed().collect { posts ->
                _uiState.update { it.copy(posts = posts) }
            }
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            try {
                feedRepository.refresh()
                _uiState.update { it.copy(isRefreshing = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        error = e.message ?: "Ошибка обновления"
                    )
                }
            }
        }
    }
    
    fun loadMore() {
        viewModelScope.launch {
            if (_uiState.value.isLoading || !_uiState.value.hasMore) return@launch
            
            _uiState.update { it.copy(isLoading = true) }
            try {
                feedRepository.loadNextPage()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Ошибка загрузки"
                    )
                }
            }
        }
    }
    
    fun toggleLike(postId: String) {
        viewModelScope.launch {
            feedRepository.toggleLike(postId)
        }
    }
    
    fun createPost(content: String, postType: PostType = PostType.NORMAL) {
        viewModelScope.launch {
            feedRepository.createPost(
                CreatePostRequest(
                    content = content,
                    postType = postType
                )
            )
        }
    }
    
    fun deletePost(postId: String) {
        viewModelScope.launch {
            feedRepository.deletePost(postId)
        }
    }
}
