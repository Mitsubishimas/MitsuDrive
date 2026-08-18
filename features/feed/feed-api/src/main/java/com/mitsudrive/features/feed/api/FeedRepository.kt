package com.mitsudrive.features.feed.api

import com.mitsudrive.features.feed.api.model.*
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    // Получение ленты
    fun observeFeed(): Flow<List<Post>>
    
    // Загрузка следующей страницы
    suspend fun loadNextPage()
    
    // Создание поста
    suspend fun createPost(request: CreatePostRequest): Result<Post>
    
    // Лайк поста
    suspend fun toggleLike(postId: String): Result<Post>
    
    // Получение комментариев
    fun observeComments(postId: String): Flow<List<Comment>>
    
    // Добавление комментария
    suspend fun addComment(postId: String, request: CreateCommentRequest): Result<Comment>
    
    // Удаление поста
    suspend fun deletePost(postId: String): Result<Unit>
    
    // Обновление ленты
    suspend fun refresh()
}
