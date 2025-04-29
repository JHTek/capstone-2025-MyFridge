package com.example.refrigeratormanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        etID = findViewById(R.id.etID)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignUp = findViewById(R.id.tvSignUp)

        // 로그인 버튼 클릭
        btnLogin.setOnClickListener {
            val user = etID.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(user, pass)
            }
        }

        // 회원가입 버튼 클릭
        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(user: String, pass: String) {
        val apiService = ApiClient.getClient().create(UsersApi::class.java)

        // 로그인 요청 전송 (userid, password를 @Field로 전송)
        apiService.login(user, pass).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {

                    val loginResponse = response.body()//로그
                    Log.d("MainActivity", "로그인리스폰스: ${loginResponse.toString()}")
                    val token = response.headers()["Authorization"]?.replace("Bearer ", "") // JWT 토큰
                    val userId = loginResponse?.userid ?: ""
                    val nickname = loginResponse?.username ?: ""
                    saveLoginData(token,userId,nickname)

                    Log.d("MainActivity", "헤더에서 받은 토큰: $token")//로그

                    Toast.makeText(this@MainActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
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
        val editor = sharedPreferences.edit()
        editor.putString("JWT_TOKEN", token)
        editor.putString("USER_ID", userId)
        editor.putString("NICKNAME", nickname)
        editor.apply()

        Log.d("MainActivity", "Login data saved: token=$token, id=$userId, nickname=$nickname")
    }

}
