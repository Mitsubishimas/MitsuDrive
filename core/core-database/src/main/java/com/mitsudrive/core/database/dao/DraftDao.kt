package com.mitsudrive.core.database.dao

import androidx.room.*
import com.mitsudrive.core.database.entity.DraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftDao {
    @Query("SELECT * FROM drafts WHERE chatId = :chatId")
    suspend fun getDraft(chatId: String): DraftEntity?
    
    @Query("SELECT * FROM drafts WHERE chatId = :chatId")
    fun observeDraft(chatId: String): Flow<DraftEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(draft: DraftEntity)
    
    @Update
    suspend fun update(draft: DraftEntity)
    
    @Delete
    suspend fun delete(draft: DraftEntity)
    
    @Query("DELETE FROM drafts WHERE chatId = :chatId")
    suspend fun deleteDraft(chatId: String)
}
