package com.mitsudrive.features.map.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitsudrive.core.ui.theme.*
import com.mitsudrive.features.map.api.model.MapEventType
import com.mitsudrive.features.map.ui.components.EventInfoCard
import com.mitsudrive.features.map.ui.viewmodel.MapViewModel

@Composable
fun MapScreen(
    viewModel: MapViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Заглушка карты (позже заменим на Google Maps)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🗺️",
                fontSize = 80.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Карта событий",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Интерактивная карта будет добавлена\nв следующей версии",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
        
        // События (временная сетка)
        if (uiState.events.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.events.take(3).forEach { event ->
                    EventInfoCard(
                        event = event,
                        onConfirm = { viewModel.confirmEvent(event.id) },
                        onClose = { viewModel.selectEvent(null) }
                    )
                }
            }
        }
        
        // Кнопки управления
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // SOS кнопка
            FilledIconButton(
                onClick = viewModel::toggleSos,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(Dimens.radius_round),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (uiState.isSosActive) AccentRed else CardBackground,
                    contentColor = if (uiState.isSosActive) TextPrimary else TextSecondary
                )
            ) {
                Text(
                    text = "🆘",
                    fontSize = 24.sp
                )
            }
            
            // Кнопка добавления события
            FilledIconButton(
                onClick = viewModel::toggleCreateEvent,
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(Dimens.radius_round),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = NeonBlue,
                    contentColor = DarkBackground
                )
            ) {
                Text(
                    text = "➕",
                    fontSize = 24.sp
                )
            }
        }
        
        // Панель создания события
        if (uiState.isCreatingEvent) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = RoundedCornerShape(Dimens.radius_lg),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                )
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.spacing_lg)
                ) {
                    Text(
                        text = "Добавить событие",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(Dimens.spacing_md))
                    
                    // Кнопки типов событий
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EventTypeButton(
                            emoji = "🚗",
                            label = "ДТП",
                            onClick = { viewModel.createEvent(MapEventType.ACCIDENT, "ДТП") }
                        )
                        EventTypeButton(
                            emoji = "📸",
                            label = "Камера",
                            onClick = { viewModel.createEvent(MapEventType.CAMERA, "Камера") }
                        )
                        EventTypeButton(
                            emoji = "🚦",
                            label = "Пробка",
                            onClick = { viewModel.createEvent(MapEventType.TRAFFIC, "Пробка") }
                        )
                        EventTypeButton(
                            emoji = "⚠️",
                            label = "Опасно",
                            onClick = { viewModel.createEvent(MapEventType.DANGER, "Опасность") }
                        )
                    }
                }
            }
        }
        
        // Ошибка
        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                fontSize = 14.sp,
                color = ErrorRed,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun EventTypeButton(
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(Dimens.radius_md),
        colors = CardDefaults.cardColors(
            containerColor = DarkBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacing_md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}
