package com.mitsudrive.features.chat.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitsudrive.core.ui.components.DriveSearchField
import com.mitsudrive.core.ui.components.StatusBadge
import com.mitsudrive.core.ui.theme.*
import com.mitsudrive.features.chat.api.model.ChatRoom
import com.mitsudrive.features.chat.ui.viewmodel.ChatListViewModel

@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel,
    onChatClick: (String) -> Unit,
    onCreateChat: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
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
                text = "Чаты",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            // Кнопка создания чата
            FilledIconButton(
                onClick = onCreateChat,
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
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Список чатов
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = uiState.chats.filter { chat ->
                    uiState.searchQuery.isBlank() ||
                        chat.title?.contains(uiState.searchQuery, ignoreCase = true) == true
                },
                key = { it.id }
            ) { chat ->
                ChatListItem(
                    chat = chat,
                    onClick = { onChatClick(chat.id) }
                )
            }
        }
    }
}

@Composable
private fun ChatListItem(
    chat: ChatRoom,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.radius_md),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар
            Box(
                modifier = Modifier
                    .size(Dimens.avatar_lg)
                    .clip(CircleShape)
                    .background(NeonBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = chat.title?.firstOrNull()?.toString() ?: "💬",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonBlue
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Информация
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = chat.title ?: "Чат",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    
                    if (chat.isPinned) {
                        Text("📌", fontSize = 12.sp)
                    }
                    
                    if (chat.isMuted) {
                        Text("🔇", fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = chat.lastMessage?.content ?: "Нет сообщений",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Время и счётчик
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = chat.lastMessage?.let { formatTime(it.createdAt) } ?: "",
                    fontSize = 11.sp,
                    color = TextTertiary
                )
                
                if (chat.unreadCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(NeonBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (chat.unreadCount > 99) "99+" else chat.unreadCount.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBackground
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(timestamp: String): String {
    return try {
        val time = timestamp.toLong()
        val date = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        date.format(java.util.Date(time))
    } catch (e: Exception) {
        ""
    }
}
