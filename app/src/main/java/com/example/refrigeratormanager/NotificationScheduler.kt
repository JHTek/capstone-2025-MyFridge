package com.example.refrigeratormanager

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val WORK_NAME = "ServerExpirationAlarm"

    fun runImmediate(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<AlarmWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun schedulePeriodic(context: Context, intervalHours: Long) {
        val workRequest = PeriodicWorkRequestBuilder<AlarmWorker>(intervalHours, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true) // 네트워크 필요 없으면 제거 가능
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelAlarm(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
