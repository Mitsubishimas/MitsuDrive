package com.mitsudrive.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitsudrive.core.ui.theme.*

// ==================== ПОЛЯ ВВОДА ====================

// Текстовое поле
@Composable
fun DriveTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.input_height),
            enabled = enabled,
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextSecondary.copy(alpha = 0.6f)
                )
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            shape = RoundedCornerShape(Dimens.radius_md),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonBlue,
                unfocusedBorderColor = BorderColor,
                errorBorderColor = ErrorRed,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = NeonBlue,
                focusedLeadingIconColor = NeonBlue,
                unfocusedLeadingIconColor = TextSecondary,
                focusedTrailingIconColor = NeonBlue,
                unfocusedTrailingIconColor = TextSecondary
            ),
            singleLine = true
        )
        
        if (errorText != null) {
            Spacer(modifier = Modifier.height(Dimens.spacing_xs))
            Text(
                text = errorText,
                fontSize = 12.sp,
                color = ErrorRed,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Поле пароля
@Composable
fun DrivePasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Пароль",
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    DriveTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        isError = isError,
        errorText = errorText,
        keyboardType = KeyboardType.Password,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            TextButton(
                onClick = { passwordVisible = !passwordVisible }
            ) {
                Text(
                    text = if (passwordVisible) "Скрыть" else "Показать",
                    fontSize = 12.sp,
                    color = NeonBlue
                )
            }
        }
    )
}

// Многострочное поле
@Composable
fun DriveMultilineField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    minLines: Int = 3,
    maxLines: Int = 5
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = TextSecondary.copy(alpha = 0.6f)
            )
        },
        shape = RoundedCornerShape(Dimens.radius_md),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonBlue,
            unfocusedBorderColor = BorderColor,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = NeonBlue
        ),
        minLines = minLines,
        maxLines = maxLines
    )
}

// Поисковая строка
@Composable
fun DriveSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Поиск...",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.input_height),
        placeholder = {
            Text(
                text = placeholder,
                color = TextSecondary.copy(alpha = 0.6f)
            )
        },
        leadingIcon = {
            Text(
                text = "🔍",
                fontSize = 16.sp
            )
        },
        shape = RoundedCornerShape(Dimens.radius_round),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonBlue,
            unfocusedBorderColor = BorderColor,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = NeonBlue
        ),
        singleLine = true
    )
}
