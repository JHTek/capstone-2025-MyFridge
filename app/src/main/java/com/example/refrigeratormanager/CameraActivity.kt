package com.example.refrigeratormanager

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.refrigeratormanager.databinding.ActivityCameraBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CameraActivity : AppCompatActivity(), ManualInputFragment.ManualInputListener {

    private lateinit var binding: ActivityCameraBinding
    private var lastSelectedTab: Int = -1 // 마지막으로 선택된 탭 위치 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewPager2 설정
        val adapter = CameraPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // TabLayout과 ViewPager 연결
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "바코드"
                1 -> "카메라"
                2 -> "직접 입력"
                else -> "기타"
            }
        }.attach()

        // 취소 버튼 클릭 이벤트 설정
        binding.tvCancel.setOnClickListener {
            finish() // 현재 Activity 종료
        }

        // 탭 선택 리스너 설정
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 2 && lastSelectedTab != 2) { // 직접 입력 탭으로 이동할 때 실행
                    showManualInputDialog()
                }
                lastSelectedTab = position
            }
        })
    }

    private fun showManualInputDialog() {
        if (!supportFragmentManager.isStateSaved) { // Activity 상태가 저장되지 않았을 때만 실행
            val existingDialog = supportFragmentManager.findFragmentByTag("ManualInputFragment")
            if (existingDialog == null) {
                val dialog = ManualInputFragment()
                dialog.show(supportFragmentManager, "ManualInputFragment")
            }
        }
    }

    // ManualInputFragment에서 상품명을 입력받으면 ProductUploadFragment 실행
    override fun onProductNameEntered(productName: String) {
        val tokens = productName.trim().split("\\s+".toRegex()) // 공백 기준 split
        val name: String
        val quantity: String

        if (tokens.size >= 2) {
            quantity = tokens.last()
            name = tokens.dropLast(1).joinToString(" ") // 마지막 제외 나머지를 이름으로
        } else {
            name = tokens.firstOrNull() ?: "알 수 없음"
            quantity = "1" // 기본값
        }

        Log.d("CameraActivity", "파싱된 이름: $name, 수량: $quantity")

        val dialog = ProductUploadFragment()
        val args = Bundle().apply {
            putString("productName", name)
            putString("quantity", quantity)
        }
        dialog.arguments = args
        dialog.show(supportFragmentManager, "ProductUploadFragment")
    }




    private fun showProductUploadDialog(productName: String) {
        val dialog = ProductUploadFragment()
        val args = Bundle()
        args.putString("productName", productName)
        dialog.arguments = args
        dialog.show(supportFragmentManager, "ProductUploadFragment")
    }
}
