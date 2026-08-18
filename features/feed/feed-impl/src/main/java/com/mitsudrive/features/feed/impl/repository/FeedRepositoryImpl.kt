package com.mitsudrive.features.feed.impl.repository

import com.mitsudrive.features.feed.api.FeedRepository
import com.mitsudrive.features.feed.api.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FeedRepositoryImpl : FeedRepository {
    
    // Локальный кэш постов
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    
    private val mutex = Mutex()
    private var nextCursor: String? = null
    private var isRefreshing = false
    
    override fun observeFeed(): Flow<List<Post>> {
        return _posts.asStateFlow()
    }
    
    override suspend fun loadNextPage() {
        mutex.withLock {
            if (isRefreshing) return
            isRefreshing = true
            
            try {
                // TODO: Загрузка с сервера
                // val page = feedApi.getFeed(cursor = nextCursor)
                // _posts.update { current -> current + page.posts }
                // nextCursor = page.nextCursor
                
                // Временные данные для разработки
                val mockPosts = createMockPosts()
                _posts.update { current ->
                    (current + mockPosts).distinctBy { it.id }
                }
            } finally {
                isRefreshing = false
            }
        }
    }
    
    override suspend fun createPost(request: CreatePostRequest): Result<Post> {
        return try {
            // TODO: Отправка на сервер
            val post = Post(
                id = "post_${System.currentTimeMillis()}",
                userId = "current_user",
                username = "Вы",
                userAvatarUrl = null,
                content = request.content,
                imageUrl = request.imageUrl,
                postType = request.postType,
                lat = request.lat,
                lng = request.lng,
                likesCount = 0,
                commentsCount = 0,
                createdAt = System.currentTimeMillis().toString(),
                updatedAt = null,
                isLiked = false,
                isPending = true
            )
            
            _posts.update { current -> listOf(post) + current }
            Result.success(post)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun toggleLike(postId: String): Result<Post> {
        return try {
            var updatedPost: Post? = null
            
            _posts.update { current ->
                current.map { post ->
                    if (post.id == postId) {
                        val newPost = post.copy(
                            isLiked = !post.isLiked,
                            likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
                        )
                        updatedPost = newPost
                        newPost
                    } else {
                        post
                    }
                }
            }
            
            if (updatedPost != null) {
                Result.success(updatedPost!!)
            } else {
                Result.failure(Exception("Post not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeComments(postId: String): Flow<List<Comment>> {
        return _comments
            .map { commentsMap -> commentsMap[postId] ?: emptyList() }
            .distinctUntilChanged()
    }
    
    override suspend fun addComment(
        postId: String,
        request: CreateCommentRequest
    ): Result<Comment> {
        return try {
            val comment = Comment(
                id = "comment_${System.currentTimeMillis()}",
                postId = postId,
                userId = "current_user",
                username = "Вы",
                userAvatarUrl = null,
                content = request.content,
                createdAt = System.currentTimeMillis().toString(),
                isPending = true
            )
            
            _comments.update { current ->
                val postComments = current[postId] ?: emptyList()
                current + (postId to (postComments + comment))
            }
            
            // Обновляем счётчик комментариев
            _posts.update { current ->
                current.map { post ->
                    if (post.id == postId) {
                        post.copy(commentsCount = post.commentsCount + 1)
                    } else {
                        post
                    }
                }
            }
            
            Result.success(comment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            _posts.update { current -> current.filter { it.id != postId } }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun refresh() {
        mutex.withLock {
            if (isRefreshing) return
            isRefreshing = true
            
            try {
                // TODO: Полное обновление с сервера
                nextCursor = null
                _posts.value = createMockPosts()
            } finally {
                isRefreshing = false
            }
        }
    }
    
    // Временные данные для разработки
    private fun createMockPosts(): List<Post> {
        return listOf(
            Post(
                id = "post_1",
                userId = "user_1",
                username = "Дмитрий",
                userAvatarUrl = null,
                content = "Кто-нибудь сталкивался с проблемой вариатора на Outlander XL? Появились рывки при разгоне.",
                imageUrl = null,
                postType = PostType.QUESTION,
                lat = 55.7558,
                lng = 37.6173,
                likesCount = 12,
                commentsCount = 5,
                createdAt = System.currentTimeMillis().toString(),
                updatedAt = null,
                isLiked = false
            ),
            Post(
                id = "post_2",
                userId = "user_2",
                username = "Алексей",
                userAvatarUrl = null,
                content = "На МКАДе пробка 10 км в сторону области. Объезжайте через дублёр!",
                imageUrl = null,
                postType = PostType.ACCIDENT,
                lat = 55.7158,
                lng = 37.4173,
                likesCount = 45,
                commentsCount = 8,
                createdAt = System.currentTimeMillis().toString(),
                updatedAt = null,
                isLiked = false
            ),
            Post(
                id = "post_3",
                userId = "user_3",
                username = "Сергей",
                userAvatarUrl = null,
                content = "Помогите! Застрял на трассе М4, 250 км от Москвы. Нужен трос.",
                imageUrl = null,
                postType = PostType.HELP,
                lat = 54.7158,
                lng = 38.4173,
                likesCount = 23,
                commentsCount = 15,
                createdAt = System.currentTimeMillis().toString(),
                updatedAt = null,
                isLiked = false
            )
        )
    }
}
