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
import com.mitsudrive.features.map.ui.components.map.OsmMapView
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
        // OpenStreetMap
        OsmMapView(
            currentLocation = uiState.currentLocation,
            events = uiState.events,
            onEventClick = { event ->
                viewModel.selectEvent(event)
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Панель выбранного события
        val selectedEvent = uiState.selectedEvent
        if (selectedEvent != null) {
            EventInfoCard(
                event = selectedEvent,
                onConfirm = { viewModel.confirmEvent(selectedEvent.id) },
                onClose = { viewModel.selectEvent(null) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
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
                    containerColor = if (uiState.isSosActive) AccentRed else CardBackground.copy(alpha = 0.9f),
                    contentColor = if (uiState.isSosActive) TextPrimary else TextSecondary
                )
            ) {
                Text("🆘", fontSize = 24.sp)
            }
            
            // Кнопка центрирования
            FilledIconButton(
                onClick = { viewModel.startLocationUpdates() },
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(Dimens.radius_round),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = CardBackground.copy(alpha = 0.9f),
                    contentColor = TextPrimary
                )
            ) {
                Text("📍", fontSize = 20.sp)
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
                Text("➕", fontSize = 24.sp)
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
                    containerColor = CardBackground.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.spacing_lg)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Добавить событие",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        
                        Text(
                            "✕",
                            fontSize = 18.sp,
                            color = TextSecondary,
                            modifier = Modifier.clickable { viewModel.toggleCreateEvent() }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(Dimens.spacing_md))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EventTypeButton("🚗", "ДТП") { 
                            viewModel.createEvent(MapEventType.ACCIDENT, "ДТП")
                        }
                        EventTypeButton("📸", "Камера") {
                            viewModel.createEvent(MapEventType.CAMERA, "Камера")
                        }
                        EventTypeButton("🚦", "Пробка") {
                            viewModel.createEvent(MapEventType.TRAFFIC, "Пробка")
                        }
                        EventTypeButton("⚠️", "Опасно") {
                            viewModel.createEvent(MapEventType.DANGER, "Опасность")
                        }
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
                    .background(
                        CardBackground.copy(alpha = 0.8f),
                        RoundedCornerShape(Dimens.radius_md)
                    )
                    .padding(12.dp)
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
        colors = CardDefaults.cardColors(containerColor = DarkBackground)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacing_md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 11.sp, color = TextSecondary)
        }
    }
}
