package com.mitsudrive.core.database.dao

import androidx.room.*
import com.mitsudrive.core.database.entity.MediaFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaFileDao {
    @Query("SELECT * FROM media_files WHERE id = :id")
    suspend fun getMediaById(id: String): MediaFileEntity?
    
    @Query("SELECT * FROM media_files WHERE id = :id")
    fun observeMediaById(id: String): Flow<MediaFileEntity?>
    
    @Query("SELECT * FROM media_files WHERE chatId = :chatId ORDER BY createdAt DESC")
    fun observeMediaInChat(chatId: String): Flow<List<MediaFileEntity>>
    
    @Query("SELECT * FROM media_files WHERE messageId = :messageId")
    suspend fun getMediaByMessageId(messageId: String): MediaFileEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(media: MediaFileEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(media: List<MediaFileEntity>)
    
    @Update
    suspend fun update(media: MediaFileEntity)
    
    @Delete
    suspend fun delete(media: MediaFileEntity)
    
    @Query("UPDATE media_files SET downloadStatus = :status WHERE id = :mediaId")
    suspend fun updateDownloadStatus(mediaId: String, status: String)
    
    @Query("UPDATE media_files SET downloadProgress = :progress WHERE id = :mediaId")
    suspend fun updateDownloadProgress(mediaId: String, progress: Float)
    
    @Query("UPDATE media_files SET localPath = :path, downloadStatus = 'downloaded' WHERE id = :mediaId")
    suspend fun markAsDownloaded(mediaId: String, path: String)
    
    @Query("SELECT * FROM media_files WHERE downloadStatus = 'not_downloaded' ORDER BY createdAt ASC LIMIT :limit")
    suspend fun getPendingDownloads(limit: Int = 10): List<MediaFileEntity>
}
