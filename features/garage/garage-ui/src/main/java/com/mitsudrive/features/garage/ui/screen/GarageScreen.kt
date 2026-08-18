package com.mitsudrive.features.garage.ui.screen

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
import com.mitsudrive.core.ui.components.DriveTextField
import com.mitsudrive.core.ui.components.NeonButton
import com.mitsudrive.core.ui.theme.*
import com.mitsudrive.features.garage.api.model.ServiceType
import com.mitsudrive.features.garage.ui.components.CarCard
import com.mitsudrive.features.garage.ui.viewmodel.GarageViewModel

@Composable
fun GarageScreen(
    viewModel: GarageViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Шапка
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Гараж",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            // Кнопка добавления
            FilledIconButton(
                onClick = viewModel::toggleAddCar,
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(Dimens.radius_md),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = NeonBlue,
                    contentColor = DarkBackground
                )
            ) {
                Text(
                    text = "➕",
                    fontSize = 18.sp
                )
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Автомобили
            items(
                items = uiState.cars,
                key = { it.id }
            ) { car ->
                CarCard(
                    car = car,
                    onDelete = { viewModel.deleteCar(car.id) }
                )
            }
            
            // Напоминания
            if (uiState.reminders.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Напоминания о ТО",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                
                items(uiState.reminders) { reminder ->
                    ReminderCard(reminder = reminder)
                }
            }
            
            // Форма добавления
            if (uiState.isAddingCar) {
                item {
                    AddCarForm(
                        uiState = uiState,
                        onBrandChange = viewModel::onBrandChange,
                        onModelChange = viewModel::onModelChange,
                        onYearChange = viewModel::onYearChange,
                        onMileageChange = viewModel::onMileageChange,
                        onAdd = viewModel::addCar,
                        onCancel = viewModel::toggleAddCar
                    )
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
            
            // Пустое состояние
            if (uiState.cars.isEmpty() && !uiState.isAddingCar) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🚗",
                                fontSize = 64.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Добавьте свой первый автомобиль",
                                fontSize = 16.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: com.mitsudrive.features.garage.api.model.ServiceReminder
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radius_md),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Row(
            modifier = Modifier.padding(Dimens.spacing_md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_md)
        ) {
            Text(
                text = "🔧",
                fontSize = 20.sp
            )
            Column {
                Text(
                    text = when (reminder.type) {
                        ServiceType.OIL_CHANGE -> "Замена масла"
                        ServiceType.FILTERS -> "Замена фильтров"
                        ServiceType.BRAKES -> "Тормозная система"
                        ServiceType.TIRES -> "Шины"
                        ServiceType.TIMING_BELT -> "Ремень ГРМ"
                        ServiceType.INSPECTION -> "Техосмотр"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = reminder.dueDate,
                    fontSize = 12.sp,
                    color = WarningOrange
                )
            }
        }
    }
}

@Composable
private fun AddCarForm(
    uiState: com.mitsudrive.features.garage.ui.viewmodel.GarageUiState,
    onBrandChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onMileageChange: (String) -> Unit,
    onAdd: () -> Unit,
    onCancel: () -> Unit
) {
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
                text = "Добавить автомобиль",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(Dimens.spacing_md))
            
            DriveTextField(
                value = uiState.newCarBrand,
                onValueChange = onBrandChange,
                placeholder = "Марка (например, Mitsubishi)"
            )
            
            Spacer(modifier = Modifier.height(Dimens.spacing_sm))
            
            DriveTextField(
                value = uiState.newCarModel,
                onValueChange = onModelChange,
                placeholder = "Модель (например, Outlander)"
            )
            
            Spacer(modifier = Modifier.height(Dimens.spacing_sm))
            
            DriveTextField(
                value = uiState.newCarYear,
                onValueChange = onYearChange,
                placeholder = "Год выпуска"
            )
            
            Spacer(modifier = Modifier.height(Dimens.spacing_sm))
            
            DriveTextField(
                value = uiState.newCarMileage,
                onValueChange = onMileageChange,
                placeholder = "Пробег (км)"
            )
            
            Spacer(modifier = Modifier.height(Dimens.spacing_md))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing_sm)
            ) {
                NeonButton(
                    text = "Добавить",
                    onClick = onAdd,
                    modifier = Modifier.weight(1f)
                )
                
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(Dimens.radius_round)
                ) {
                    Text(
                        text = "Отмена",
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
