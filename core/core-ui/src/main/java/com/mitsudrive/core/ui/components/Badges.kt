package com.mitsudrive.core.ui.components

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

// ==================== БЕЙДЖИ И ИНДИКАТОРЫ ====================

// Бейдж статуса (онлайн/оффлайн)
@Composable
fun StatusBadge(
    isOnline: Boolean,
    modifier: Modifier = Modifier,
    showText: Boolean = true
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.radius_round))
            .background(
                if (isOnline) OnlineGreen.copy(alpha = 0.15f)
                else TextSecondary.copy(alpha = 0.15f)
            )
            .padding(
                horizontal = Dimens.spacing_md,
                vertical = Dimens.spacing_xs
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_xs)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(Dimens.radius_round))
                    .background(if (isOnline) OnlineGreen else TextSecondary)
            )
            
            if (showText) {
                Text(
                    text = if (isOnline) "Онлайн" else "Оффлайн",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isOnline) OnlineGreen else TextSecondary
                )
            }
        }
    }
}

// Бейдж уведомлений
@Composable
fun NotificationBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    if (count > 0) {
        Box(
            modifier = modifier
                .size(20.dp)
                .clip(RoundedCornerShape(Dimens.radius_round))
                .background(AccentRed),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

// Бейдж типа события
@Composable
fun EventTypeBadge(
    eventType: String,
    modifier: Modifier = Modifier
) {
    val (color, label) = when (eventType) {
        "accident" -> AccentRed to "ДТП"
        "camera" -> WarningOrange to "Камера"
        "danger" -> AccentRed to "Опасность"
        "traffic" -> NeonBlue to "Пробка"
        else -> TextSecondary to eventType
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.radius_sm))
            .background(color.copy(alpha = 0.15f))
            .padding(
                horizontal = Dimens.spacing_md,
                vertical = Dimens.spacing_xs
            )
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

// Индикатор загрузки
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = NeonBlue
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = color,
            strokeWidth = 2.dp
        )
    }
}
