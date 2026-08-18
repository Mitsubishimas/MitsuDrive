package com.mitsudrive.core.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mitsudrive.core.database.converter.Converters
import com.mitsudrive.core.database.dao.*
import com.mitsudrive.core.database.entity.*

@Database(
    entities = [
        UserEntity::class,
        ChatEntity::class,
        MessageEntity::class,
        MediaFileEntity::class,
        MapEventEntity::class,
        CarEntity::class,
        ChatParticipantEntity::class,
        OutgoingQueueEntity::class,
        DraftEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DriveNetDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun mediaFileDao(): MediaFileDao
    abstract fun mapEventDao(): MapEventDao
    abstract fun carDao(): CarDao
    abstract fun participantDao(): ChatParticipantDao
    abstract fun queueDao(): QueueDao
    abstract fun draftDao(): DraftDao
    
    companion object {
        const val DATABASE_NAME = "drivenet.db"
    }
}
