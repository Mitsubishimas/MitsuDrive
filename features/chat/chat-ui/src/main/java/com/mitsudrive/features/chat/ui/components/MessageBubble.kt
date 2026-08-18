package com.mitsudrive.features.chat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.mitsudrive.core.ui.theme.*
import com.mitsudrive.features.chat.api.model.ChatMessage
import com.mitsudrive.features.chat.api.model.MessageStatus
import com.mitsudrive.features.chat.api.model.MessageType

@Composable
fun MessageBubble(
    message: ChatMessage,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            // Имя отправителя (для групповых чатов)
            if (!isCurrentUser) {
                Text(
                    text = message.senderName,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                )
            }
            
            // Пузырь сообщения
            Column(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                            bottomEnd = if (isCurrentUser) 4.dp else 16.dp
                        )
                    )
                    .background(
                        if (isCurrentUser) NeonBlue.copy(alpha = 0.15f)
                        else CardBackground
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Контент сообщения
                when (message.messageType) {
                    MessageType.TEXT -> {
                        Text(
                            text = message.content ?: "",
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = TextPrimary
                        )
                    }
                    
                    MessageType.LOCATION -> {
                        Text(
                            text = "📍 Локация",
                            fontSize = 14.sp,
                            color = NeonBlue
                        )
                    }
                    
                    MessageType.SYSTEM -> {
                        Text(
                            text = message.content ?: "",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                    
                    else -> {
                        Text(
                            text = "📎 Файл",
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                }
                
                // Время и статус
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = formatTime(message.createdAt),
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    
                    if (isCurrentUser) {
                        when (message.status) {
                            MessageStatus.SENDING -> Text("⏳", fontSize = 10.sp)
                            MessageStatus.SENT -> Text("✓", fontSize = 10.sp)
                            MessageStatus.DELIVERED -> Text("✓✓", fontSize = 10.sp)
                            MessageStatus.READ -> Text("✓✓", fontSize = 10.sp, color = NeonBlue)
                            MessageStatus.FAILED -> Text("⚠️", fontSize = 10.sp)
                        }
                    }
                }
            }
            
            // Пометка "изменено"
            if (message.isEdited) {
                Text(
                    text = "изменено",
                    fontSize = 10.sp,
                    color = TextTertiary,
                    modifier = Modifier.padding(top = 2.dp, start = 8.dp)
                )
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
