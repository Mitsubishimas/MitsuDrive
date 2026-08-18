package com.mitsudrive.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "map_events",
    indices = [
        Index(value = ["lat", "lng"]),
        Index(value = ["expiresAt"])
    ]
)
data class MapEventEntity(
    @PrimaryKey
    val id: String,
    val eventType: String, // 'accident', 'camera', 'danger', 'traffic'
    val lat: Double,
    val lng: Double,
    val description: String? = null,
    val userId: String,
    val createdAt: String,
    val expiresAt: String,
    val confirmations: Int = 0,
    val isConfirmed: Boolean = false
)
