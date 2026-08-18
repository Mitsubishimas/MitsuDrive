package com.mitsudrive.core.database.dao

import androidx.room.*
import com.mitsudrive.core.database.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats WHERE id = :id")
    suspend fun getChatById(id: String): ChatEntity?
    
    @Query("SELECT * FROM chats WHERE id = :id")
    fun observeChatById(id: String): Flow<ChatEntity?>
    
    @Query("SELECT * FROM chats WHERE isArchived = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun observeAllChats(): Flow<List<ChatEntity>>
    
    @Query("SELECT * FROM chats WHERE isArchived = 1 ORDER BY updatedAt DESC")
    fun observeArchivedChats(): Flow<List<ChatEntity>>
    
    @Query("SELECT * FROM chats WHERE chatType = :type ORDER BY updatedAt DESC")
    fun observeChatsByType(type: String): Flow<List<ChatEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: ChatEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chats: List<ChatEntity>)
    
    @Update
    suspend fun update(chat: ChatEntity)
    
    @Delete
    suspend fun delete(chat: ChatEntity)
    
    @Query("DELETE FROM chats")
    suspend fun deleteAll()
    
    @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
    suspend fun markAsRead(chatId: String)
    
    @Query("UPDATE chats SET isPinned = :isPinned WHERE id = :chatId")
    suspend fun updatePinned(chatId: String, isPinned: Boolean)
    
    @Query("UPDATE chats SET isArchived = :isArchived WHERE id = :chatId")
    suspend fun updateArchived(chatId: String, isArchived: Boolean)
    
    @Query("UPDATE chats SET isMuted = :isMuted WHERE id = :chatId")
    suspend fun updateMuted(chatId: String, isMuted: Boolean)
    
    @Query("UPDATE chats SET lastMessageId = :messageId, lastMessageText = :text, lastMessageTime = :time, updatedAt = :time WHERE id = :chatId")
    suspend fun updateLastMessage(chatId: String, messageId: String, text: String, time: String)
}
