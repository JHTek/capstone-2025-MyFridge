package com.example.refrigeratormanager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Pattern
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var editTextId: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextRePassword: EditText
    private lateinit var editTextNick: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnCheckId: Button
    private lateinit var btnCheckNick: Button


    private var CheckId: Boolean = false
    private var CheckNick: Boolean = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editTextId = findViewById(R.id.editTextId_Reg)
        editTextPassword = findViewById(R.id.editTextPass_Reg)
        editTextRePassword = findViewById(R.id.editTextRePass_Reg)
        editTextNick = findViewById(R.id.editTextNick_Reg)
        btnRegister = findViewById(R.id.btnRegister_Reg)
        btnCheckId = findViewById(R.id.btnCheckId_Reg)
        btnCheckNick = findViewById(R.id.btnCheckNick_Reg)

        // 아이디 중복확인
        btnCheckId.setOnClickListener {
            val user = editTextId.text.toString().trim()
            val idPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{6,15}$"

            when {
                user.isEmpty() -> showToast("아이디를 입력해주세요.")
                !Pattern.matches(idPattern, user) -> showToast("아이디 형식이 올바르지 않습니다.")
                else -> {
                    val userApi: UsersApi = ApiClient.getClient().create(UsersApi::class.java)
                    val call: Call<Boolean> = userApi.checkUser(user)

                    call.enqueue(object : Callback<Boolean> {
                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                            if (response.isSuccessful) {
                                val isUserExist = response.body()

                                if (isUserExist != null) {
                                    if (isUserExist) {
                                        showToast("이미 존재하는 아이디입니다.")
                                        CheckId = false
                                    } else {
                                        showToast("사용 가능한 아이디입니다.")
                                        CheckId = true
                                    }
                                } else {
                                    showToast("아이디 중복 확인 실패: 응답 본문이 비어 있습니다.")
                                }
                            } else {
                                // HTTP 상태 코드가 성공적이지 않은 경우
                                showToast("아이디 중복 확인 실패: " + response.message())
                            }
                        }

                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            // 네트워크 오류나 서버 오류 발생 시 처리
                            showToast("아이디 중복 확인 실패: " + t.message)
                        }

                    })
                }
            }
        }

        // 닉네임 중복확인
        btnCheckNick.setOnClickListener {
            val nick = editTextNick.text.toString().trim()
            val nickPattern = "^[ㄱ-ㅣ가-힣]*$"

            when {
                nick.isEmpty() -> showToast("닉네임을 입력해주세요.")
                !Pattern.matches(nickPattern, nick) -> showToast("닉네임 형식이 올바르지 않습니다.")
            }
        }

        // 완료 버튼 클릭 시
        btnRegister.setOnClickListener {
            val user = editTextId.text.toString().trim()
            val pass = editTextPassword.text.toString().trim()
            val repass = editTextRePassword.text.toString().trim()
            val nick = editTextNick.text.toString().trim()

            val pwPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{8,15}$"
            val phonePattern = "^(\\+[0-9]+)?[0-9]{10,15}$"

            when {
                user.isEmpty() || pass.isEmpty() || repass.isEmpty() || nick.isEmpty() -> showToast("회원정보를 모두 입력해주세요.")
                !CheckId -> showToast("아이디 중복확인을 해주세요.")
                !Pattern.matches(pwPattern, pass) -> showToast("비밀번호 형식이 올바르지 않습니다.")
                pass != repass -> showToast("비밀번호가 일치하지 않습니다.")
                else -> {
                    val newUser = Users().apply {
                        username = nick
                        userid = user
                        password = pass
                    }

                    // API 호출
                    val userApi: UsersApi = ApiClient.getClient().create(UsersApi::class.java)
                    val call: Call<String> = userApi.registerUser(newUser)

                    // API 호출 결과 처리
                    call.enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            if (response.isSuccessful) {
                                showToast("회원가입 성공!")
                                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                                finish()
                            } else {
                                showToast("회원가입 실패: ${response.message()}")
                            }
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            showToast("회원가입 실패: ${t.message}")
                        }
                    })
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
