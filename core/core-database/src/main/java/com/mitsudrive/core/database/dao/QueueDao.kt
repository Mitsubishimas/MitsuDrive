package com.mitsudrive.core.database.dao

import androidx.room.*
import com.mitsudrive.core.database.entity.OutgoingQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QueueDao {
    @Query("SELECT * FROM outgoing_queue WHERE id = :id")
    suspend fun getQueueItem(id: String): OutgoingQueueEntity?
    
    @Query("SELECT * FROM outgoing_queue WHERE nextAttemptAt IS NULL OR nextAttemptAt <= :now ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getPendingItems(now: String, limit: Int = 10): List<OutgoingQueueEntity>
    
    @Query("SELECT * FROM outgoing_queue")
    fun observeAllQueue(): Flow<List<OutgoingQueueEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: OutgoingQueueEntity)
    
    @Update
    suspend fun update(item: OutgoingQueueEntity)
    
    @Delete
    suspend fun delete(item: OutgoingQueueEntity)
    
    @Query("DELETE FROM outgoing_queue WHERE messageId = :messageId")
    suspend fun deleteByMessageId(messageId: String)
    
    @Query("UPDATE outgoing_queue SET attempts = attempts + 1, nextAttemptAt = :nextAttempt WHERE id = :id")
    suspend fun incrementAttempts(id: String, nextAttempt: String)
}
