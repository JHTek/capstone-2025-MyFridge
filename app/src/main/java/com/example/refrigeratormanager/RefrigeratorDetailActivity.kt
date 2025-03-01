package com.example.refrigeratormanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.refrigeratormanager.databinding.ActivityRefrigeratorDetailBinding
import com.google.android.material.tabs.TabLayoutMediator

class RefrigeratorDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRefrigeratorDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRefrigeratorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인텐트에서 냉장고 이름 가져오기 (기본값: "냉장고")
        val refrigeratorName = intent.getStringExtra("refrigerator_name") ?: "냉장고"
        binding.tvRefrigeratorName.text = refrigeratorName

        // 뷰페이저 어댑터 설정
        val adapter = RefrigeratorPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // 탭 레이아웃과 뷰페이저 연결
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "냉장"
                1 -> "냉동"
                2 -> "실온"
                else -> "기타"
            }
        }.attach()

        // 뒤로 가기 버튼 클릭 시 액티비티 종료
        binding.btnBack.setOnClickListener {
            finish()
        }

        // 검색 버튼 클릭 시 검색 화면으로 이동
        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // 카메라 버튼 클릭 시 카메라 화면으로 이동
        binding.OpenCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }
}

