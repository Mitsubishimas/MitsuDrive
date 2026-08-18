package com.mitsudrive.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mitsudrive.core.ui.theme.*

// ==================== КНОПКИ ====================

// Неоновая кнопка (основная)
@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: @Composable (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(Dimens.button_height)
            .fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(Dimens.radius_round),
        colors = ButtonDefaults.buttonColors(
            containerColor = NeonBlue,
            contentColor = DarkBackground,
            disabledContainerColor = NeonBlue.copy(alpha = 0.3f),
            disabledContentColor = DarkBackground.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = DarkBackground,
                strokeWidth = 2.dp
            )
        } else {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(Dimens.spacing_sm))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// Призрачная кнопка (вторичная)
@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(Dimens.button_height)
            .fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(Dimens.radius_round),
        border = BorderStroke(2.dp, BorderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextPrimary,
            disabledContentColor = TextSecondary.copy(alpha = 0.5f)
        )
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(Dimens.spacing_sm))
        }
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 0.5.sp
        )
    }
}

// Опасная кнопка (для удаления, SOS)
@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(Dimens.button_height)
            .fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(Dimens.radius_round),
        colors = ButtonDefaults.buttonColors(
            containerColor = AccentRed,
            contentColor = TextPrimary,
            disabledContainerColor = AccentRed.copy(alpha = 0.3f),
            disabledContentColor = TextPrimary.copy(alpha = 0.5f)
        )
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(Dimens.spacing_sm))
        }
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 0.5.sp
        )
    }
}

// Иконка-кнопка
@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(Dimens.icon_xl),
        enabled = enabled,
        shape = RoundedCornerShape(Dimens.radius_md),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = CardBackground,
            contentColor = TextPrimary
        )
    ) {
        icon()
    }
}
