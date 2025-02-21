package com.example.refrigeratormanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.refrigeratormanager.databinding.ActivityRefrigeratorDetailBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RefrigeratorDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRefrigeratorDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRefrigeratorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val refrigeratorName = intent.getStringExtra("refrigerator_name") ?: "냉장고"

        binding.tvRefrigeratorName.text = refrigeratorName

        val adapter = RefrigeratorPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "냉장"
                1 -> "냉동"
                2 -> "실온"
                else -> "기타"
            }
        }.attach()
    }
}