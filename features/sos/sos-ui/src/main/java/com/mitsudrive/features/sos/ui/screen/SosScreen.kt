package com.mitsudrive.features.sos.ui.screen

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
import com.mitsudrive.core.ui.components.DangerButton
import com.mitsudrive.core.ui.components.DriveMultilineField
import com.mitsudrive.core.ui.theme.*
import com.mitsudrive.features.sos.ui.components.SosAlertCard
import com.mitsudrive.features.sos.ui.viewmodel.SosViewModel

@Composable
fun SosScreen(
    viewModel: SosViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Шапка
        Text(
            text = "Экстренная помощь",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(16.dp)
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Мой активный SOS
            val mySos = uiState.myActiveSos
            if (mySos != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radius_lg),
                        colors = CardDefaults.cardColors(
                            containerColor = AccentRed.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(Dimens.spacing_lg)
                        ) {
                            Text(
                                text = "🆘 Ваш SOS активен",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentRed
                            )
                            
                            Spacer(modifier = Modifier.height(Dimens.spacing_sm))
                            
                            Text(
                                text = "Ближайшие водители уведомлены",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                            
                            Spacer(modifier = Modifier.height(Dimens.spacing_md))
                            
                            DangerButton(
                                text = "Отменить SOS",
                                onClick = viewModel::cancelSos
                            )
                        }
                    }
                }
            } else {
                // Форма отправки SOS
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radius_lg),
                        colors = CardDefaults.cardColors(
                            containerColor = CardBackground
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(Dimens.spacing_lg)
                        ) {
                            Text(
                                text = "Отправить SOS",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            
                            Spacer(modifier = Modifier.height(Dimens.spacing_sm))
                            
                            Text(
                                text = "Сообщение увидят все водители в радиусе 5 км",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            
                            Spacer(modifier = Modifier.height(Dimens.spacing_md))
                            
                            DriveMultilineField(
                                value = uiState.message,
                                onValueChange = viewModel::onMessageChange,
                                placeholder = "Опишите ситуацию (опционально)"
                            )
                            
                            Spacer(modifier = Modifier.height(Dimens.spacing_md))
                            
                            DangerButton(
                                text = "🆘 Отправить SOS",
                                onClick = viewModel::sendSos,
                                loading = uiState.isSending
                            )
                        }
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
            
            // Активные алерты
            if (uiState.activeAlerts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Активные запросы помощи",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                items(uiState.activeAlerts) { alert ->
                    SosAlertCard(
                        alert = alert,
                        onRespond = { viewModel.respondToSos(alert.id) },
                        onResolve = { viewModel.resolveSos(alert.id) }
                    )
                }
            }
        }
    }
}
