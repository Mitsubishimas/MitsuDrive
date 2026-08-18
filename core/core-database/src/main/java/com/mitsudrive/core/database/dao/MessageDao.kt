package com.mitsudrive.core.database.dao

import androidx.room.*
import com.mitsudrive.core.database.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: String): MessageEntity?
    
    @Query("SELECT * FROM messages WHERE chatId = :chatId AND isDeleted = 0 ORDER BY createdAt DESC LIMIT :limit")
    fun observeMessages(chatId: String, limit: Int = 50): Flow<List<MessageEntity>>
    
    @Query("SELECT * FROM messages WHERE chatId = :chatId AND isDeleted = 0 AND createdAt < :before ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getMessagesBefore(chatId: String, before: String, limit: Int = 50): List<MessageEntity>
    
    @Query("SELECT * FROM messages WHERE chatId = :chatId AND isDeleted = 0 AND createdAt > :after ORDER BY createdAt ASC")
    suspend fun getMessagesAfter(chatId: String, after: String): List<MessageEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<MessageEntity>)
    
    @Update
    suspend fun update(message: MessageEntity)
    
    @Delete
    suspend fun delete(message: MessageEntity)
    
    @Query("UPDATE messages SET status = :status WHERE id = :messageId")
    suspend fun updateStatus(messageId: String, status: String)
    
    @Query("UPDATE messages SET content = :content, isEdited = 1, updatedAt = :updatedAt WHERE id = :messageId")
    suspend fun updateContent(messageId: String, content: String, updatedAt: String)
    
    @Query("UPDATE messages SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :messageId")
    suspend fun softDelete(messageId: String, deletedAt: String)
    
    @Query("UPDATE messages SET deletedForEveryone = 1, isDeleted = 1, deletedAt = :deletedAt WHERE id = :messageId")
    suspend fun deleteForEveryone(messageId: String, deletedAt: String)
    
    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteAllInChat(chatId: String)
    
    @Query("DELETE FROM messages WHERE isDeleted = 1 AND deletedAt < :before")
    suspend fun deleteOldDeleted(before: String)
    
    @Query("SELECT * FROM messages WHERE syncStatus = 'pending' OR status = 'failed'")
    suspend fun getPendingMessages(): List<MessageEntity>
}
