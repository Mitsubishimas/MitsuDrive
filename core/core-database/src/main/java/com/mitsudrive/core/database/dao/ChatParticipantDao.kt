package com.mitsudrive.core.database.dao

import androidx.room.*
import com.mitsudrive.core.database.entity.ChatParticipantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatParticipantDao {
    @Query("SELECT * FROM chat_participants WHERE chatId = :chatId")
    fun observeParticipants(chatId: String): Flow<List<ChatParticipantEntity>>
    
    @Query("SELECT * FROM chat_participants WHERE userId = :userId")
    fun observeUserChats(userId: String): Flow<List<ChatParticipantEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(participant: ChatParticipantEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(participants: List<ChatParticipantEntity>)
    
    @Delete
    suspend fun delete(participant: ChatParticipantEntity)
    
    @Query("DELETE FROM chat_participants WHERE chatId = :chatId")
    suspend fun deleteAllInChat(chatId: String)
}
