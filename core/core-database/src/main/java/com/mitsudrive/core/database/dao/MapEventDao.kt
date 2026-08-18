package com.mitsudrive.core.database.dao

import androidx.room.*
import com.mitsudrive.core.database.entity.MapEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MapEventDao {
    @Query("SELECT * FROM map_events WHERE id = :id")
    suspend fun getEventById(id: String): MapEventEntity?
    
    @Query("SELECT * FROM map_events WHERE lat BETWEEN :minLat AND :maxLat AND lng BETWEEN :minLng AND :maxLng")
    fun observeEventsInBounds(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Flow<List<MapEventEntity>>
    
    @Query("SELECT * FROM map_events WHERE expiresAt > :now")
    fun observeActiveEvents(now: String): Flow<List<MapEventEntity>>
    
    @Query("SELECT * FROM map_events WHERE eventType = :type AND expiresAt > :now")
    fun observeEventsByType(type: String, now: String): Flow<List<MapEventEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: MapEventEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<MapEventEntity>)
    
    @Update
    suspend fun update(event: MapEventEntity)
    
    @Delete
    suspend fun delete(event: MapEventEntity)
    
    @Query("DELETE FROM map_events WHERE expiresAt < :now")
    suspend fun deleteExpired(now: String)
    
    @Query("UPDATE map_events SET confirmations = confirmations + 1 WHERE id = :eventId")
    suspend fun incrementConfirmation(eventId: String)
}
