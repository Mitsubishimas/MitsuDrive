package com.mitsudrive.features.auth.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitsudrive.core.ui.components.*
import com.mitsudrive.core.ui.theme.*
import com.mitsudrive.features.auth.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            
            Text(
                text = "MitsuDrive",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = NeonBlue
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Регистрация",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Email
            DriveTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "Email",
                leadingIcon = { Text("📧", fontSize = 16.sp) }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Имя
            DriveTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                placeholder = "Имя пользователя",
                leadingIcon = { Text("👤", fontSize = 16.sp) }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Телефон
            DriveTextField(
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChange,
                placeholder = "Телефон (+7...)",
                leadingIcon = { Text("📱", fontSize = 16.sp) }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Пароль
            DrivePasswordField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "Пароль (мин. 6 символов)"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Подтверждение пароля
            DrivePasswordField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                placeholder = "Подтвердите пароль"
            )
            
            // SMS код (если отправлен)
            if (uiState.isSmsSent) {
                Spacer(modifier = Modifier.height(16.dp))
                
                DriveTextField(
                    value = uiState.smsCode,
                    onValueChange = viewModel::onSmsCodeChange,
                    placeholder = "Код из SMS",
                    leadingIcon = { Text("🔑", fontSize = 16.sp) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Отправить код повторно",
                        fontSize = 14.sp,
                        color = NeonBlue,
                        modifier = Modifier.clickable { viewModel.resendSms() }
                    )
                }
            }
            
            // Ошибка
            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.error!!,
                    fontSize = 14.sp,
                    color = ErrorRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Кнопка
            if (!uiState.isSmsSent) {
                NeonButton(
                    text = "Отправить SMS код",
                    onClick = viewModel::sendSms,
                    loading = uiState.isLoading
                )
            } else {
                NeonButton(
                    text = "Подтвердить и зарегистрироваться",
                    onClick = viewModel::register,
                    loading = uiState.isLoading
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            GhostButton(
                text = "Уже есть аккаунт",
                onClick = onNavigateToLogin
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Регистрируясь, вы соглашаетесь с условиями\nиспользования и политикой конфиденциальности",
                fontSize = 12.sp,
                color = TextTertiary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
