package com.mitsudrive.features.chat.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitsudrive.core.ui.components.DriveTextField
import com.mitsudrive.core.ui.theme.*
import com.mitsudrive.features.chat.ui.components.MessageBubble
import com.mitsudrive.features.chat.ui.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    chatTitle: String,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Шапка чата
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка назад
            Text(
                text = "←",
                fontSize = 24.sp,
                color = TextPrimary,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onBack() }
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Название чата
            Column {
                Text(
                    text = chatTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "онлайн",
                    fontSize = 12.sp,
                    color = OnlineGreen
                )
            }
        }
        
        // Разделитель
        Divider(color = BorderColor)
        
        // Сообщения
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(
                items = uiState.messages,
                key = { it.id }
            ) { message ->
                MessageBubble(
                    message = message,
                    isCurrentUser = message.senderId == "current_user"
                )
            }
        }
        
        // Ошибка
        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                fontSize = 12.sp,
                color = ErrorRed,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        
        // Поле ввода
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Кнопка вложения
            Text(
                text = "📎",
                fontSize = 20.sp,
                modifier = Modifier.clickable { }
            )
            
            // Поле ввода
            DriveTextField(
                value = uiState.inputText,
                onValueChange = viewModel::onInputChange,
                placeholder = "Сообщение...",
                modifier = Modifier.weight(1f)
            )
            
            // Кнопка отправки
            FilledIconButton(
                onClick = viewModel::sendMessage,
                enabled = uiState.inputText.isNotBlank(),
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(Dimens.radius_round),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = NeonBlue,
                    contentColor = DarkBackground,
                    disabledContainerColor = NeonBlue.copy(alpha = 0.3f),
                    disabledContentColor = DarkBackground.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = "➤",
                    fontSize = 18.sp
                )
            }
        }
    }
}
