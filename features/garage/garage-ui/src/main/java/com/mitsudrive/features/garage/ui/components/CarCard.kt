package com.mitsudrive.features.garage.ui.components

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
import com.mitsudrive.features.garage.api.model.Car

@Composable
fun CarCard(
    car: Car,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius_lg),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacing_lg)
        ) {
            // Шапка
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_md)
                ) {
                    // Иконка
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(NeonBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🚗",
                            fontSize = 24.sp
                        )
                    }
                    
                    Column {
                        Text(
                            text = "${car.brand} ${car.model}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        
                        val year = car.year
                        if (year != null) {
                            Text(
                                text = "$year год",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
                
                // Кнопка удаления
                Text(
                    text = "🗑️",
                    fontSize = 18.sp,
                    modifier = Modifier.clickable(onClick = onDelete)
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacing_md))
            
            // Информация
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_xl),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Пробег",
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                    Text(
                        text = "${car.mileage} км",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                
                val nextServiceDate = car.nextServiceDate
                if (nextServiceDate != null) {
                    Column {
                        Text(
                            text = "Следующее ТО",
                            fontSize = 11.sp,
                            color = TextTertiary
                        )
                        Text(
                            text = nextServiceDate,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WarningOrange
                        )
                    }
                }
            }
        }
    }
}
