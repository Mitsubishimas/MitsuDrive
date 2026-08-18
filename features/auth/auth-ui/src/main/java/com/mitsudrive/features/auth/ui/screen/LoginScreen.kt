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
import com.mitsudrive.core.ui.components.DrivePasswordField
import com.mitsudrive.core.ui.components.DriveTextField
import com.mitsudrive.core.ui.components.GhostButton
import com.mitsudrive.core.ui.components.NeonButton
import com.mitsudrive.core.ui.theme.*
import com.mitsudrive.features.auth.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginSuccess()
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
            Spacer(modifier = Modifier.height(60.dp))
            
            Text(
                text = "MitsuDrive",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = NeonBlue
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Вход в аккаунт",
                fontSize = 16.sp,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            DriveTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "Email",
                leadingIcon = { Text("📧", fontSize = 16.sp) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DrivePasswordField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "Пароль"
            )
            
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Забыли пароль?",
                fontSize = 14.sp,
                color = NeonBlue,
                modifier = Modifier
                    .clickable { onNavigateToForgotPassword() }
                    .padding(4.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            NeonButton(
                text = "Войти",
                onClick = viewModel::login,
                loading = uiState.isLoading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            GhostButton(
                text = "Создать аккаунт",
                onClick = onNavigateToRegister
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Входя в аккаунт, вы соглашаетесь с условиями\nиспользования и политикой конфиденциальности",
                fontSize = 12.sp,
                color = TextTertiary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
