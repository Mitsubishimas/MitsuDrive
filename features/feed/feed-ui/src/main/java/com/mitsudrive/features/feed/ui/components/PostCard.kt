package com.mitsudrive.features.feed.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitsudrive.core.ui.components.EventTypeBadge
import com.mitsudrive.core.ui.theme.*
import com.mitsudrive.features.feed.api.model.Post
import com.mitsudrive.features.feed.api.model.PostType

@Composable
fun PostCard(
    post: Post,
    onLike: () -> Unit,
    onComment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onComment),
        shape = RoundedCornerShape(Dimens.radius_lg),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacing_lg)
        ) {
            // Шапка поста
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_sm)
            ) {
                // Аватар
                Box(
                    modifier = Modifier
                        .size(Dimens.avatar_md)
                        .clip(CircleShape)
                        .background(NeonBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.username.firstOrNull()?.toString() ?: "?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonBlue
                    )
                }
                
                Column {
                    Text(
                        text = post.username,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = formatTime(post.createdAt),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Тип поста
                when (post.postType) {
                    PostType.ACCIDENT -> EventTypeBadge("accident")
                    PostType.HELP -> EventTypeBadge("danger")
                    PostType.QUESTION -> EventTypeBadge("traffic")
                    else -> {}
                }
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacing_md))
            
            // Контент
            Text(
                text = post.content,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = TextPrimary
            )
            
            // Изображение (если есть)
            if (post.imageUrl != null) {
                Spacer(modifier = Modifier.height(Dimens.spacing_md))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(Dimens.radius_md))
                        .background(CardBackground)
                ) {
                    // TODO: Загрузка изображения через Coil
                    Text(
                        text = "📷",
                        fontSize = 48.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacing_md))
            
            // Действия
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_xl),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Лайк
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_xs),
                    modifier = Modifier.clickable(onClick = onLike)
                ) {
                    Text(
                        text = if (post.isLiked) "❤️" else "🤍",
                        fontSize = 18.sp
                    )
                    Text(
                        text = post.likesCount.toString(),
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
                
                // Комментарии
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_xs)
                ) {
                    Text(
                        text = "💬",
                        fontSize = 16.sp
                    )
                    Text(
                        text = post.commentsCount.toString(),
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Поделиться
                Text(
                    text = "📤",
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { }
                )
            }
        }
    }
}

private fun formatTime(timestamp: String): String {
    return try {
        val time = timestamp.toLong()
        val diff = System.currentTimeMillis() - time
        when {
            diff < 60_000 -> "Только что"
            diff < 3_600_000 -> "${diff / 60_000} мин назад"
            diff < 86_400_000 -> "${diff / 3_600_000} ч назад"
            else -> "${diff / 86_400_000} дн назад"
        }
    } catch (e: Exception) {
        timestamp
    }
}
