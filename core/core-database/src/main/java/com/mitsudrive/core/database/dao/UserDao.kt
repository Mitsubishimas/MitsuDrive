package com.mitsudrive.core.database.dao

import androidx.room.*
import com.mitsudrive.core.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE id = :id")
    fun observeUserById(id: String): Flow<UserEntity?>
    
    @Query("SELECT * FROM users")
    fun observeAllUsers(): Flow<List<UserEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)
    
    @Update
    suspend fun update(user: UserEntity)
    
    @Delete
    suspend fun delete(user: UserEntity)
    
    @Query("DELETE FROM users")
    suspend fun deleteAll()
    
    @Query("UPDATE users SET isOnline = :isOnline, lastSeen = :lastSeen WHERE id = :id")
    suspend fun updatePresence(id: String, isOnline: Boolean, lastSeen: String)
    
    @Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<UserEntity>>
}
