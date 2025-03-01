package com.example.refrigeratormanager

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Edge-to-Edge 설정 추가
        window.decorView.setBackgroundColor(Color.BLACK) // 배경 검정
        setContentView(R.layout.activity_camera)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val cancelButton = findViewById<TextView>(R.id.tvCancel)

        val adapter = CameraPagerAdapter(this)
        viewPager.adapter = adapter

        // TabLayout 연결
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "바코드"
                1 -> "카메라"
                2 -> "직접 입력"
                else -> "기타"
            }
        }.attach()

        // 취소 버튼 클릭 시 액티비티 종료
        cancelButton.setOnClickListener {
            finish()
        }
    }
}
