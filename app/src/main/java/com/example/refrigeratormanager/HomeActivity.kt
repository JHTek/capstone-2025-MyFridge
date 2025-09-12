package com.example.refrigeratormanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.refrigeratormanager.databinding.ActivityHomeBinding
import com.example.refrigeratormanager.recipe.RecipeFragment
import com.example.refrigeratormanager.refrigerator.RefrigeratorListFragment
import com.example.refrigeratormanager.refrigerator.RefrigeratorViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: RefrigeratorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MainHomeFragment())
                .commit()
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_refrigerator -> replaceFragment(RefrigeratorListFragment())
                R.id.nav_mainhome -> replaceFragment(MainHomeFragment())
                R.id.nav_recipe -> replaceFragment(RecipeFragment())
                R.id.nav_mypage -> replaceFragment(MyPageFragment())
            }
            true
        }

        val ftoken = getTokenFromSharedPrefs()
        val token = "Bearer $ftoken"

        if (token != null) {
            viewModel.loadRefrigerators(token)
        } else {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 앱 진입 즉시 알림 + 주기 알림
        NotificationScheduler.runImmediate(this)
        val prefs = getSharedPreferences("app_preferences", MODE_PRIVATE)
        if (prefs.getBoolean("ALERT_ENABLED", true)) {
            val interval = prefs.getLong("ALARM_INTERVAL_HOURS", 6L)
            NotificationScheduler.schedulePeriodic(this, interval)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun getTokenFromSharedPrefs(): String? {
        return getSharedPreferences("app_preferences", MODE_PRIVATE).getString("JWT_TOKEN", null)
    }
}
