package com.mitsudrive.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val avatarUrl: String? = null,
    val avatarLocalPath: String? = null,
    val status: String? = null,
    val lastSeen: String? = null,
    val isOnline: Boolean = false,
    val rating: Double = 0.0,
    val carsCount: Int = 0,
    val createdAt: String,
    val updatedAt: String? = null,
    val syncStatus: String = "synced",
    val lastSyncAt: String? = null
)
