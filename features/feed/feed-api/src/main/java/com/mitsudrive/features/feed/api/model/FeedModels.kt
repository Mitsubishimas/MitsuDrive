package com.mitsudrive.features.feed.api.model

data class Post(
    val id: String,
    val userId: String,
    val username: String,
    val userAvatarUrl: String?,
    val content: String,
    val imageUrl: String?,
    val postType: PostType,
    val lat: Double?,
    val lng: Double?,
    val likesCount: Int,
    val commentsCount: Int,
    val createdAt: String,
    val updatedAt: String?,
    val isLiked: Boolean,
    val isPending: Boolean = false
)

enum class PostType {
    NORMAL,
    ACCIDENT,
    HELP,
    QUESTION
}

data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val username: String,
    val userAvatarUrl: String?,
    val content: String,
    val createdAt: String,
    val isPending: Boolean = false
)

data class CreatePostRequest(
    val content: String,
    val postType: PostType,
    val imageUrl: String? = null,
    val lat: Double? = null,
    val lng: Double? = null
)

data class CreateCommentRequest(
    val content: String
)

data class FeedPage(
    val posts: List<Post>,
    val hasMore: Boolean,
    val nextCursor: String?
)
