package com.mitsudrive.features.feed.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitsudrive.core.ui.components.DriveSearchField
import com.mitsudrive.core.ui.theme.*
import com.mitsudrive.features.feed.ui.components.PostCard
import com.mitsudrive.features.feed.ui.viewmodel.FeedViewModel

@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onCreatePost: () -> Unit,
    onPostClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Шапка
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Лента",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            // Кнопка создания поста
            FilledIconButton(
                onClick = onCreatePost,
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(Dimens.radius_md),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = NeonBlue,
                    contentColor = DarkBackground
                )
            ) {
                Text(
                    text = "✏️",
                    fontSize = 18.sp
                )
            }
        }
        
        // Поиск
        DriveSearchField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Список постов
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = uiState.posts.filter { post ->
                    searchQuery.isBlank() || post.content.contains(searchQuery, ignoreCase = true)
                },
                key = { it.id }
            ) { post ->
                PostCard(
                    post = post,
                    onLike = { viewModel.toggleLike(post.id) },
                    onComment = { onPostClick(post.id) }
                )
            }
            
            // Загрузка
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = NeonBlue,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
            
            // Ошибка
            if (uiState.error != null) {
                item {
                    Text(
                        text = uiState.error!!,
                        fontSize = 14.sp,
                        color = ErrorRed,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
