package com.example.refrigeratormanager.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.refrigeratormanager.MainActivity
import com.example.refrigeratormanager.product.ProductManager
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale

class AlarmWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    companion object {
        private const val CHANNEL_ID = "expiration_alert_channel"
        private const val NOTIFICATION_ID = 1001
        private const val PREFS_NAME = "alarm_prefs"
        private const val KEY_ALERT_DAYS = "alert_days"
        private const val KEY_LOG = "alert_log"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val context = applicationContext
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val alertDays = prefs.getInt(KEY_ALERT_DAYS, 7)  // 기본 7일

        val products = ProductManager.getAllProducts()

        if (products.isEmpty()) {
            return Result.success()
        }

        createNotificationChannel()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val today = LocalDate.now()

        val logBuilder = StringBuilder()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        for (product in products) {
            try {
                val expireDate = LocalDate.parse(product.expirationDate, formatter)
                val daysLeft = today.until(expireDate).days

                if (daysLeft in 0..alertDays) {
                    // 알림 메시지 수정
                    val message = "${product.ingredientsName} 유통기한이 ${daysLeft}일 남았습니다."

                    //알림 클릭 시 이동
                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    val pendingIntent = PendingIntent.getActivity(
                        context, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle("유통기한 임박 알림")
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build()

                    notificationManager.notify(NOTIFICATION_ID + product.hashCode(), notification)

                    //  로그 기록 수정
                    val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val nowFormatted = dateTimeFormat.format(Date())
                    val logLine = "$nowFormatted - $message\n"
                    logBuilder.append(logLine)
                }
            } catch (e: DateTimeParseException) {
                e.printStackTrace()
            }
        }

        // 로그 저장
        if (logBuilder.isNotEmpty()) {
            val existingLog = prefs.getString(KEY_LOG, "") ?: ""
            prefs.edit().putString(KEY_LOG, existingLog + logBuilder.toString()).apply()
        }

        return Result.success()
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
