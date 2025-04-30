package com.example.refrigeratormanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.refrigeratormanager.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: RefrigeratorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기본 Fragment 설정
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MainHomeFragment())
                .commit()
        }

        // 네비게이션 바 클릭 이벤트
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_refrigerator -> replaceFragment(HomeFragment())
                R.id.nav_mainhome -> replaceFragment(MainHomeFragment())
                R.id.nav_recipe -> replaceFragment(RecipeFragment())
                R.id.nav_mypage -> replaceFragment(MyPageFragment())
            }
            true
        }

        // 토큰 가져오기
        val ftoken = getTokenFromSharedPrefs()
        val token = "Bearer $ftoken"

        // 냉장고 목록 불러오기
        if (token != null) {
            viewModel.loadRefrigerators(token)
        } else {
            Toast.makeText(this, "로그인 정보가 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            finish() // HomeActivity 종료
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun getTokenFromSharedPrefs(): String? {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        return sharedPreferences.getString("JWT_TOKEN", null)
    }
}

