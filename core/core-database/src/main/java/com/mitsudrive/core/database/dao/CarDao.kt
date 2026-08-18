package com.mitsudrive.core.database.dao

import androidx.room.*
import com.mitsudrive.core.database.entity.CarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {
    @Query("SELECT * FROM cars WHERE id = :id")
    suspend fun getCarById(id: String): CarEntity?
    
    @Query("SELECT * FROM cars WHERE userId = :userId")
    fun observeUserCars(userId: String): Flow<List<CarEntity>>
    
    @Query("SELECT * FROM cars WHERE userId = :userId")
    suspend fun getUserCars(userId: String): List<CarEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(car: CarEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cars: List<CarEntity>)
    
    @Update
    suspend fun update(car: CarEntity)
    
    @Delete
    suspend fun delete(car: CarEntity)
    
    @Query("DELETE FROM cars WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}
