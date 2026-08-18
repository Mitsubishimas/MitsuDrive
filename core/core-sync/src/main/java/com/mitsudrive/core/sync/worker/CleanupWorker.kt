package com.mitsudrive.core.sync.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.mitsudrive.core.database.dao.MapEventDao
import com.mitsudrive.core.database.dao.MessageDao
import java.util.concurrent.TimeUnit

class CleanupWorker(
    context: Context,
    params: WorkerParameters,
    private val mapEventDao: MapEventDao,
    private val messageDao: MessageDao
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            val now = System.currentTimeMillis().toString()
            
            // Удаляем просроченные события на карте
            mapEventDao.deleteExpired(now)
            
            // Удаляем старые удалённые сообщения (30 дней)
            val thirtyDaysAgo = (System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000).toString()
            messageDao.deleteOldDeleted(thirtyDaysAgo)
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    
    companion object {
        private const val WORK_NAME = "cleanup_work"
        
        fun scheduleCleanup() {
            val request = PeriodicWorkRequestBuilder<CleanupWorker>(
                24, TimeUnit.HOURS
            ).build()
            
            WorkManager.getInstance()
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }
    }
}
