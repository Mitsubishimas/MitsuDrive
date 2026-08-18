package com.mitsudrive.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mitsudrive.app.MainActivity
import com.mitsudrive.app.R

class DriveFirebaseMessagingService : FirebaseMessagingService() {
    
    companion object {
        private const val CHANNEL_ID = "drivenet_messages"
        private const val CHANNEL_NAME = "Сообщения"
        private const val CHANNEL_DESCRIPTION = "Уведомления о сообщениях и событиях"
        
        private const val CHANNEL_ID_SOS = "drivenet_sos"
        private const val CHANNEL_NAME_SOS = "Экстренные уведомления"
        private const val CHANNEL_DESCRIPTION_SOS = "SOS-сигналы от водителей"
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Отправить токен на сервер
        // sendTokenToServer(token)
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Обработка данных
        val type = remoteMessage.data["type"] ?: "message"
        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title ?: "MitsuDrive"
        val body = remoteMessage.data["body"] ?: remoteMessage.notification?.body ?: ""
        
        when (type) {
            "sos" -> showSosNotification(title, body)
            "message" -> showMessageNotification(title, body)
            "event" -> showEventNotification(title, body)
            else -> showMessageNotification(title, body)
        }
    }
    
    private fun showMessageNotification(title: String, body: String) {
        createNotificationChannels()
        
        val notificationId = System.currentTimeMillis().toInt()
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()
        
        try {
            NotificationManagerCompat.from(this).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Нет разрешения
        }
    }
    
    private fun showSosNotification(title: String, body: String) {
        createNotificationChannels()
        
        val notificationId = System.currentTimeMillis().toInt()
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "sos")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_SOS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🆘 $title")
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setVibrate(longArrayOf(0, 500, 250, 500, 250, 500))
            .build()
        
        try {
            NotificationManagerCompat.from(this).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Нет разрешения
        }
    }
    
    private fun showEventNotification(title: String, body: String) {
        showMessageNotification(title, body)
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Канал для сообщений
            val messageChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
            }
            notificationManager.createNotificationChannel(messageChannel)
            
            // Канал для SOS
            val sosChannel = NotificationChannel(
                CHANNEL_ID_SOS,
                CHANNEL_NAME_SOS,
                NotificationManager.IMPORTANCE_MAX
            ).apply {
                description = CHANNEL_DESCRIPTION_SOS
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null)
            }
            notificationManager.createNotificationChannel(sosChannel)
        }
    }
}
