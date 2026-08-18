package com.mitsudrive.features.garage.api.model

data class Car(
    val id: String,
    val userId: String,
    val brand: String,
    val model: String,
    val year: Int?,
    val vin: String?,
    val mileage: Int,
    val lastServiceDate: String?,
    val nextServiceDate: String?,
    val isPrimary: Boolean = false
)

data class CreateCarRequest(
    val brand: String,
    val model: String,
    val year: Int?,
    val vin: String?,
    val mileage: Int = 0
)

data class UpdateCarRequest(
    val brand: String?,
    val model: String?,
    val year: Int?,
    val vin: String?,
    val mileage: Int?,
    val lastServiceDate: String?,
    val nextServiceDate: String?
)

data class ServiceReminder(
    val carId: String,
    val type: ServiceType,
    val dueDate: String,
    val mileageDue: Int?
)

enum class ServiceType {
    OIL_CHANGE,
    FILTERS,
    BRAKES,
    TIRES,
    TIMING_BELT,
    INSPECTION
}
