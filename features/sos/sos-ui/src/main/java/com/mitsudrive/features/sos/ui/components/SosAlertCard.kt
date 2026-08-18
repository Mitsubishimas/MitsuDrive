package com.mitsudrive.features.sos.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitsudrive.core.ui.theme.*
import com.mitsudrive.features.sos.api.model.SosAlert
import com.mitsudrive.features.sos.api.model.SosStatus

@Composable
fun SosAlertCard(
    alert: SosAlert,
    onRespond: () -> Unit,
    onResolve: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius_lg),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        border = BorderStroke(2.dp, AccentRed.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacing_lg)
        ) {
            // Шапка
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_md)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AccentRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🆘",
                        fontSize = 24.sp
                    )
                }
                
                Column {
                    Text(
                        text = alert.username,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    
                    val distance = alert.distance
                    if (distance != null) {
                        Text(
                            text = "${distance.toInt()} м от вас",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Статус
                val statusColor = when (alert.status) {
                    SosStatus.ACTIVE -> AccentRed
                    SosStatus.RESOLVED -> SuccessGreen
                    SosStatus.CANCELLED -> TextSecondary
                    SosStatus.EXPIRED -> TextTertiary
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(Dimens.radius_round))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (alert.status) {
                            SosStatus.ACTIVE -> "Активен"
                            SosStatus.RESOLVED -> "Решён"
                            SosStatus.CANCELLED -> "Отменён"
                            SosStatus.EXPIRED -> "Истёк"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacing_md))
            
            // Сообщение
            Text(
                text = alert.message ?: "Нужна помощь!",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(Dimens.spacing_md))
            
            // Кнопки
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_sm)
            ) {
                Button(
                    onClick = onRespond,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(Dimens.radius_round),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonBlue,
                        contentColor = DarkBackground
                    )
                ) {
                    Text(
                        text = "Помочь",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                OutlinedButton(
                    onClick = onResolve,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(Dimens.radius_round),
                    border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "Решено",
                        fontSize = 14.sp,
                        color = SuccessGreen
                    )
                }
            }
        }
    }
}
