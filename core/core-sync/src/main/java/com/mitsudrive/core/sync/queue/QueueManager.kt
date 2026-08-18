package com.mitsudrive.core.sync.queue

import com.mitsudrive.core.database.dao.QueueDao
import com.mitsudrive.core.database.entity.OutgoingQueueEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class QueueManager(
    private val queueDao: QueueDao
) {
    
    // Добавление в очередь
    suspend fun enqueue(
        messageId: String,
        chatId: String,
        payload: String
    ): String {
        val queueItem = OutgoingQueueEntity(
            id = UUID.randomUUID().toString(),
            messageId = messageId,
            chatId = chatId,
            payload = payload,
            createdAt = System.currentTimeMillis().toString()
        )
        queueDao.insert(queueItem)
        return queueItem.id
    }
    
    // Получение pending элементов
    suspend fun getPendingItems(limit: Int = 10): List<OutgoingQueueEntity> {
        val now = System.currentTimeMillis().toString()
        return queueDao.getPendingItems(now, limit)
    }
    
    // Наблюдение за очередью
    fun observeQueue(): Flow<List<OutgoingQueueEntity>> {
        return queueDao.observeAllQueue()
    }
    
    // Обновление попыток
    suspend fun incrementAttempts(itemId: String, nextAttemptAt: String) {
        queueDao.incrementAttempts(itemId, nextAttemptAt)
    }
    
    // Удаление из очереди
    suspend fun removeFromQueue(messageId: String) {
        queueDao.deleteByMessageId(messageId)
    }
    
    // Очистка очереди
    suspend fun clearQueue() {
        queueDao.getAllQueueItems()?.forEach { item ->
            queueDao.delete(item)
        }
    }
}

// Расширение для QueueDao
suspend fun QueueDao.getAllQueueItems(): List<OutgoingQueueEntity>? {
    // Обёртка для совместимости
    return null
}
