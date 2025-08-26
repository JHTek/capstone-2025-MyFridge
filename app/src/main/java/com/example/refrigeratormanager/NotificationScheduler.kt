package com.example.refrigeratormanager

import android.content.Context
import android.os.Build
import androidx.work.*
import java.util.concurrent.TimeUnit

// NotificationScheduler: 알림 Worker를 WorkManager로 스케줄링하는 헬퍼 객체
object NotificationScheduler {

    /**
     * 주기적인 알람 작업 예약
     * @param context Context
     * @param intervalHours 반복 실행할 주기 (시간 단위)
     */
    fun scheduleAlarmWithInterval(context: Context, intervalHours: Long) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // 안드로이드 8.0 (Oreo) 미만에서는
            // WorkManager의 주기적 실행이 보장되지 않을 수 있어 실행하지 않음
            return
        }

        // WorkManager 인스턴스 가져오기
        val workManager = WorkManager.getInstance(context)

        //주기적 작업 요청 생성
        val workRequest = PeriodicWorkRequestBuilder<AlarmWorker>(
            intervalHours, TimeUnit.HOURS // 몇 시간 간격으로 실행할지 지정
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED) // 네트워크 연결 필요 조건 추가
                    .build()
            )
            .build()

        // 고유한 이름("ServerExpirationAlarm")으로 작업 등록
        // 동일한 이름의 작업이 이미 있으면 교체(REPLACE) → 중복 방지
        workManager.enqueueUniquePeriodicWork(
            "ServerExpirationAlarm",               // 작업 고유 이름
            ExistingPeriodicWorkPolicy.REPLACE,    // 기존 작업 있으면 교체
            workRequest
        )
    }

    /**
     * 예약된 알람 작업 취소
     * @param context Context
     */
    fun cancelAlarm(context: Context) {
        // 고유 이름("ServerExpirationAlarm")으로 예약된 작업 취소
        WorkManager.getInstance(context).cancelUniqueWork("ServerExpirationAlarm")
    }
}
