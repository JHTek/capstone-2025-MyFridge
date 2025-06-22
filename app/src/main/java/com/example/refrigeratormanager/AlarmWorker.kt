package com.example.refrigeratormanager.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.refrigeratormanager.MainActivity
import com.example.refrigeratormanager.product.ProductManager
import com.example.refrigeratormanager.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AlarmWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "expiration_alert_channel"
        const val NOTIFICATION_ID = 1001
        @RequiresApi(Build.VERSION_CODES.O)
        private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // 테스트 알림 함수
        fun sendTestNotification(context: Context) {
            val channelId = CHANNEL_ID
            val channelName = "Expiration Alerts"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("테스트 알림")
                .setContentText("유통기한 임박 알림이 정상 작동합니다!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(9999, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        createNotificationChannel()

        val prefs = applicationContext.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        val alertDays = prefs.getInt("alert_days", 7) // 기본 7일 이내 임박 알림

        val today = LocalDate.now()
        val products = ProductManager.getAllProducts()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var hasNotification = false

        for (product in products) {
            try {
                val expirationDate = LocalDate.parse(product.expirationDate, dateFormatter)
                val daysLeft = expirationDate.toEpochDay() - today.toEpochDay()

                if (daysLeft in 0..alertDays.toLong()) {
                    hasNotification = true
                    val message = "${product.ingredientsName} 유통기한이 ${daysLeft}일 남았습니다."

                    // 알림 클릭 시 MainActivity로 이동
                    val intent = Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

                    val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification) // 적절한 아이콘 리소스로 교체하세요
                        .setContentTitle("유통기한 임박 알림")
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build()

                    notificationManager.notify(NOTIFICATION_ID + daysLeft.toInt(), notification)

                    // 로그 저장 (날짜 - 내용)
                    val logEntry = "${java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))} - $message"
                    val existingLog = prefs.getString("alert_log", "") ?: ""
                    val newLog = "$existingLog\n$logEntry".trim()
                    prefs.edit().putString("alert_log", newLog).apply()
                }

            } catch (e: Exception) {
                Log.e("AlarmWorker", "유통기한 파싱 실패: ${product.expirationDate}", e)
            }
        }

        return if (hasNotification) Result.success() else Result.failure()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Expiration Alert Channel"
            val descriptionText = "유통기한 임박 알림을 위한 채널"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
