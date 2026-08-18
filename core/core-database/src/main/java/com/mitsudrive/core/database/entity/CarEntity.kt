package com.mitsudrive.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cars",
    indices = [
        Index(value = ["userId"])
    ]
)
data class CarEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val brand: String,
    val model: String,
    val year: Int? = null,
    val vin: String? = null,
    val mileage: Int = 0,
    val lastServiceDate: String? = null,
    val nextServiceDate: String? = null,
    val createdAt: String,
    val updatedAt: String? = null
)
