package com.example.refrigeratormanager

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.refrigeratormanager.alarm.NotificationScheduler
import com.example.refrigeratormanager.databinding.ActivitySettingBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        // 알림 토글 초기 상태 설정
        val alertEnabled = prefs.getBoolean("ALERT_ENABLED", true)
        binding.switchAlert.isChecked = alertEnabled

        // 알림 기준일 EditText 초기값 설정
        val alertDays = prefs.getInt("ALERT_DAYS", 7)
        binding.editAlertDays.setText(alertDays.toString())
        binding.editAlertDays.inputType = InputType.TYPE_CLASS_NUMBER
        binding.editAlertDays.filters = arrayOf(InputFilter.LengthFilter(2)) // 최대 2자리 제한

        // 토글 변경 이벤트 처리
        binding.switchAlert.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("ALERT_ENABLED", isChecked).apply()
            if (isChecked) {
                NotificationScheduler.scheduleDailyAlarm(this)
                Toast.makeText(this, "알림이 활성화되었습니다", Toast.LENGTH_SHORT).show()
            } else {
                NotificationScheduler.cancelAlarm(this)
                Toast.makeText(this, "알림이 비활성화되었습니다", Toast.LENGTH_SHORT).show()
            }
        }

        // 저장 버튼 이벤트 처리
        binding.btnSave.setOnClickListener {
            val inputText = binding.editAlertDays.text.toString().trim()
            val days = inputText.toIntOrNull()

            if (days != null && days in 1..30) {
                prefs.edit().putInt("ALERT_DAYS", days).apply()
                Toast.makeText(this, "알림 기준일이 ${days}일로 설정되었습니다", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "1~30 사이의 숫자를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}