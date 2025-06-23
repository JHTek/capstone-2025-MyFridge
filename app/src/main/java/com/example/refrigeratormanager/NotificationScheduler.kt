package com.example.refrigeratormanager.alarm

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleAlarmWithInterval(context: Context, intervalHours: Long) {
        val workManager = WorkManager.getInstance(context)

        val now = LocalDateTime.now()
        val nextMidnight = now.toLocalDate().plusDays(1).atTime(LocalTime.MIDNIGHT)
        val initialDelay = Duration.between(now, nextMidnight)

        val workRequest = PeriodicWorkRequestBuilder<AlarmWorker>(
            intervalHours, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "DailyExpirationAlarm",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }


    fun cancelAlarm(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("PeriodicExpirationAlarm")
    }
}
