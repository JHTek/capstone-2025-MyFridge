package com.example.refrigeratormanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var etID: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignUp: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val token = sharedPreferences.getString("JWT_TOKEN", null)

        // 로그인 토큰이 이미 존재하면 HomeActivity로 바로 이동
        if (!token.isNullOrEmpty()) {
            startHomeActivityOnce()
            return
        }

        etID = findViewById(R.id.etID)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignUp = findViewById(R.id.tvSignUp)

        // 로그인 버튼
        btnLogin.setOnClickListener {
            val user = etID.text.toString().trim()
            val pass = etPassword.text.toString().trim()
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(user, pass)
            }
        }

        // 회원가입 버튼
        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // 앱 최초 실행 시 AlarmWorker 한 번 실행
        if (sharedPreferences.getBoolean("FIRST_ALARM_RUN", true)) {
            val workRequest = OneTimeWorkRequestBuilder<AlarmWorker>().build()
            WorkManager.getInstance(this)
                .enqueueUniqueWork(
                    "INITIAL_ALARM_WORK",
                    ExistingWorkPolicy.KEEP,
                    workRequest
                )
            sharedPreferences.edit().putBoolean("FIRST_ALARM_RUN", false).apply()
        }
    }

    private fun loginUser(user: String, pass: String) {
        val apiService = ApiClient.getClient().create(UsersApi::class.java)
        apiService.login(user, pass).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val loginResponse = response.body()
                    val token = response.headers()["Authorization"]?.replace("Bearer ", "")
                    val userId = loginResponse?.userid ?: ""
                    val nickname = loginResponse?.username ?: ""

                    saveLoginData(token, userId, nickname)

                    // 로그인 후에도 AlarmWorker 한 번 실행 (중복 등록 방지)
                    val workRequest = OneTimeWorkRequestBuilder<AlarmWorker>().build()
                    WorkManager.getInstance(this@MainActivity)
                        .enqueueUniqueWork(
                            "INITIAL_ALARM_WORK",
                            ExistingWorkPolicy.KEEP,
                            workRequest
                        )

                    startHomeActivityOnce()
                } else {
                    Toast.makeText(this@MainActivity, "아이디 또는 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "서버 연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveLoginData(token: String?, userId: String, nickname: String) {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("JWT_TOKEN", token)
            .putString("USER_ID", userId)
            .putString("NICKNAME", nickname)
            .apply()

        Log.d("MainActivity", "Login data saved: token=$token, id=$userId, nickname=$nickname")
    }

    private fun startHomeActivityOnce() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
