package com.mitsudrive.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Формы MitsuDrive
val MitsuDriveShapes = Shapes(
    // Маленькие элементы (кнопки, чипы)
    extraSmall = RoundedCornerShape(8.dp),
    
    // Средние элементы (карточки)
    small = RoundedCornerShape(12.dp),
    
    // Большие элементы (диалоги)
    medium = RoundedCornerShape(16.dp),
    
    // Крупные элементы (экраны)
    large = RoundedCornerShape(20.dp),
    
    // Очень крупные (модальные окна)
    extraLarge = RoundedCornerShape(24.dp)
)
