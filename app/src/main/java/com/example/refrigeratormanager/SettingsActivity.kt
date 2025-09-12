package com.example.refrigeratormanager

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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

        // 스위치 초기 상태
        binding.switchAlert.isChecked = prefs.getBoolean("ALERT_ENABLED", true)

        // 알림 기준일 초기값
        val alertDays = prefs.getInt("ALERT_DAYS", DEFAULT_ALERT_DAYS)
        binding.editAlertDays.setText(alertDays.toString())
        binding.editAlertDays.inputType = InputType.TYPE_CLASS_NUMBER
        binding.editAlertDays.filters = arrayOf(InputFilter.LengthFilter(2))

        // 알림 주기 초기값
        val intervalHours = prefs.getLong("ALARM_INTERVAL_HOURS", DEFAULT_INTERVAL_HOURS)
        binding.editAlertInterval.setText(intervalHours.toString())
        binding.editAlertInterval.inputType = InputType.TYPE_CLASS_NUMBER
        binding.editAlertInterval.filters = arrayOf(InputFilter.LengthFilter(3))

        // 알림 토글
        binding.switchAlert.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("ALERT_ENABLED", isChecked).apply()
            if (isChecked) {
                val savedInterval = prefs.getLong("ALARM_INTERVAL_HOURS", DEFAULT_INTERVAL_HOURS)
                NotificationScheduler.schedulePeriodic(this, savedInterval)
                Toast.makeText(this, "알림이 활성화되었습니다", Toast.LENGTH_SHORT).show()
            } else {
                NotificationScheduler.cancelAlarm(this)
                Toast.makeText(this, "알림이 비활성화되었습니다", Toast.LENGTH_SHORT).show()
            }
        }

        // 저장 버튼
        binding.btnSave.setOnClickListener {
            val alertDaysValue = binding.editAlertDays.text.toString().toIntOrNull()
            val intervalValue = binding.editAlertInterval.text.toString().toLongOrNull()

            if (alertDaysValue == null || alertDaysValue !in 1..30) {
                Toast.makeText(this, "알림 기준일은 1~30 사이여야 합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (intervalValue == null || intervalValue < 1 || intervalValue > 168) {
                Toast.makeText(this, "알림 주기는 1~168 시간 사이여야 합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit()
                .putInt("ALERT_DAYS", alertDaysValue)
                .putLong("ALARM_INTERVAL_HOURS", intervalValue)
                .apply()

            Toast.makeText(this, "설정이 저장되었습니다", Toast.LENGTH_SHORT).show()

            // 스위치 상태 기준으로 WorkManager 재스케줄
            if (binding.switchAlert.isChecked) {
                NotificationScheduler.schedulePeriodic(this, intervalValue)
            }
        }
    }
}
