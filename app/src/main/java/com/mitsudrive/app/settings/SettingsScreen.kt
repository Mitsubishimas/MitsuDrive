package com.mitsudrive.app.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.mitsudrive.core.ui.components.DriveTextField
import com.mitsudrive.core.ui.components.NeonButton
import com.mitsudrive.core.ui.theme.*

data class UserProfile(
    val username: String = "Водитель",
    val phone: String = "+7 (XXX) XXX-XX-XX",
    val carModel: String = "Mitsubishi Outlander",
    val city: String = "Москва",
    val about: String = "Люблю свой Mitsubishi и помогаю другим водителям!"
)

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    var profile by remember { mutableStateOf(UserProfile()) }
    var isEditing by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Шапка
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                fontSize = 24.sp,
                color = TextPrimary,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onBack() }
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Настройки",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Профиль
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(NeonBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.username.firstOrNull()?.toString() ?: "?",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonBlue
                        )
                    }
                    
                    Column {
                        Text(
                            text = profile.username,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = profile.phone,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
            
            // Кнопка редактирования
            item {
                NeonButton(
                    text = if (isEditing) "Сохранить" else "Редактировать профиль",
                    onClick = { isEditing = !isEditing }
                )
            }
            
            // Поля редактирования
            if (isEditing) {
                item {
                    DriveTextField(
                        value = profile.username,
                        onValueChange = { profile = profile.copy(username = it) },
                        placeholder = "Имя пользователя"
                    )
                }
                
                item {
                    DriveTextField(
                        value = profile.phone,
                        onValueChange = { profile = profile.copy(phone = it) },
                        placeholder = "Телефон"
                    )
                }
                
                item {
                    DriveTextField(
                        value = profile.carModel,
                        onValueChange = { profile = profile.copy(carModel = it) },
                        placeholder = "Автомобиль"
                    )
                }
                
                item {
                    DriveTextField(
                        value = profile.city,
                        onValueChange = { profile = profile.copy(city = it) },
                        placeholder = "Город"
                    )
                }
            }
            
            // Разделы
            item {
                SettingsSection(title = "Аккаунт") {
                    SettingsItem("🔑", "Изменить пароль") { }
                    SettingsItem("📱", "Сменить номер") { }
                    SettingsItem("🚪", "Выйти") { }
                }
            }
            
            item {
                SettingsSection(title = "Уведомления") {
                    SettingsItem("💬", "Сообщения") { }
                    SettingsItem("🆘", "SOS-сигналы") { }
                    SettingsItem("🗺️", "События на карте") { }
                }
            }
            
            item {
                SettingsSection(title = "О приложении") {
                    SettingsItem("📋", "Версия 0.1.0") { }
                    SettingsItem("📄", "Условия использования") { }
                    SettingsItem("🔒", "Политика конфиденциальности") { }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.radius_md),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    emoji: String,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Text(
            text = text,
            fontSize = 14.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "›",
            fontSize = 20.sp,
            color = TextSecondary
        )
    }
}
