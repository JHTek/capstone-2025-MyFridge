package com.example.refrigeratormanager

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.refrigeratormanager.NotificationScheduler
import com.example.refrigeratormanager.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    companion object {
        private const val DEFAULT_ALERT_DAYS = 7
        private const val DEFAULT_INTERVAL_HOURS = 24L
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        // 1. 알림 토글 초기 상태
        val alertEnabled = prefs.getBoolean("ALERT_ENABLED", true)
        binding.switchAlert.isChecked = alertEnabled

        // 2. 알림 기준일 초기값 (1~30일)
        val alertDays = prefs.getInt("ALERT_DAYS", DEFAULT_ALERT_DAYS)
        binding.editAlertDays.setText(alertDays.toString())
        binding.editAlertDays.inputType = InputType.TYPE_CLASS_NUMBER
        binding.editAlertDays.filters = arrayOf(InputFilter.LengthFilter(2))

        // 3. 알림 주기 (시간 단위) 초기값
        val intervalHours = prefs.getLong("ALARM_INTERVAL_HOURS", DEFAULT_INTERVAL_HOURS)
        binding.editAlertInterval.setText(intervalHours.toString())
        binding.editAlertInterval.inputType = InputType.TYPE_CLASS_NUMBER
        binding.editAlertInterval.filters = arrayOf(InputFilter.LengthFilter(3)) // 최대 3자리

        // 알림 토글 변경 이벤트
        binding.switchAlert.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("ALERT_ENABLED", isChecked).apply()
            if (isChecked) {
                // 알림 켜지면 바로 스케줄링 (기존에 저장된 주기로)
                val savedInterval = prefs.getLong("ALARM_INTERVAL_HOURS", DEFAULT_INTERVAL_HOURS)
                NotificationScheduler.scheduleAlarmWithInterval(this, savedInterval)
                Toast.makeText(this, "알림이 활성화되었습니다", Toast.LENGTH_SHORT).show()
            } else {
                NotificationScheduler.cancelAlarm(this)
                Toast.makeText(this, "알림이 비활성화되었습니다", Toast.LENGTH_SHORT).show()
            }
        }

        // 저장 버튼 클릭시 알림 기준일, 주기 저장 및 스케줄링 재등록
        binding.btnSave.setOnClickListener {
            val alertDaysInput = binding.editAlertDays.text.toString().trim()
            val alertDaysValue = alertDaysInput.toIntOrNull()

            val intervalInput = binding.editAlertInterval.text.toString().trim()
            val intervalValue = intervalInput.toLongOrNull()

            if (alertDaysValue == null || alertDaysValue !in 1..30) {
                Toast.makeText(this, "알림 기준일은 1~30 사이 숫자여야 합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (intervalValue == null || intervalValue < 1 || intervalValue > 168) {
                Toast.makeText(this, "알림 주기는 1~168 시간 사이여야 합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 저장
            prefs.edit()
                .putInt("ALERT_DAYS", alertDaysValue)
                .putLong("ALARM_INTERVAL_HOURS", intervalValue)
                .apply()

            Toast.makeText(this, "설정이 저장되었습니다", Toast.LENGTH_SHORT).show()

            // 알림이 켜져있으면 스케줄링 재등록
            if (prefs.getBoolean("ALERT_ENABLED", true)) {
                NotificationScheduler.scheduleAlarmWithInterval(this, intervalValue)
            }
        }

        // ** 테스트 알림 버튼 클릭 시 즉시 알림 보내기 **
        binding.btnTestNotification.setOnClickListener {
            sendTestNotification()
        }
    }

    private fun sendTestNotification() {
        val channelId = "expiration_alert_channel"
        val channelName = "Expiration Alerts"

        val context = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                channelName,
                android.app.NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("테스트 알림")
            .setContentText("유통기한 임박 알림이 정상 작동합니다!")
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        androidx.core.app.NotificationManagerCompat.from(context).notify(9999, notification)

        Toast.makeText(this, "테스트 알림이 전송되었습니다.", Toast.LENGTH_SHORT).show()
    }
}