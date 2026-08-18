package com.mitsudrive.features.map.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitsudrive.core.ui.theme.*
import com.mitsudrive.features.map.api.model.MapEvent
import com.mitsudrive.features.map.api.model.MapEventType

@Composable
fun EventMarker(
    event: MapEvent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (emoji, color) = when (event.eventType) {
        MapEventType.ACCIDENT -> "🚗" to AccentRed
        MapEventType.CAMERA -> "📸" to WarningOrange
        MapEventType.DANGER -> "⚠️" to AccentRed
        MapEventType.TRAFFIC -> "🚦" to NeonBlue
        MapEventType.ROAD_WORK -> "🚧" to WarningOrange
    }
    
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 20.sp
        )
    }
}

@Composable
fun EventInfoCard(
    event: MapEvent,
    onConfirm: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius_lg),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
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
                    text = event.eventType.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Text(
                    text = "✕",
                    fontSize = 18.sp,
                    color = TextSecondary,
                    modifier = Modifier.clickable(onClick = onClose)
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacing_sm))
            
            Text(
                text = event.description ?: "Без описания",
                fontSize = 14.sp,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(Dimens.spacing_md))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_md)
            ) {
                Text(
                    text = "👤 ${event.username}",
                    fontSize = 12.sp,
                    color = TextTertiary
                )
                Text(
                    text = "👍 ${event.confirmations}",
                    fontSize = 12.sp,
                    color = TextTertiary
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacing_md))
            
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(Dimens.radius_round),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (event.isConfirmed) CardBackground else NeonBlue,
                    contentColor = if (event.isConfirmed) TextSecondary else DarkBackground
                )
            ) {
                Text(
                    text = if (event.isConfirmed) "Подтверждено" else "Подтвердить",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
