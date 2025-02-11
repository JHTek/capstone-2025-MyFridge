package com.example.refrigeratormanager

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private lateinit var DB: DBHelper
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

        DB = DBHelper(this)
        editTextId = findViewById(R.id.editTextId_Reg)
        editTextPassword = findViewById(R.id.editTextPass_Reg)
        editTextRePassword = findViewById(R.id.editTextRePass_Reg)
        editTextNick = findViewById(R.id.editTextNick_Reg)
        editTextPhone = findViewById(R.id.editTextPhone_Reg)
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
                    if (!DB.checkUser(user)) {
                        CheckId = true
                        showToast("사용 가능한 아이디입니다.")
                    } else {
                        showToast("이미 존재하는 아이디입니다.")
                    }
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
                else -> {
                    if (!DB.checkNick(nick)) {
                        CheckNick = true
                        showToast("사용 가능한 닉네임입니다.")
                    } else {
                        showToast("이미 존재하는 닉네임입니다.")
                    }
                }
            }
        }

        // 완료 버튼 클릭 시
        btnRegister.setOnClickListener {
            val user = editTextId.text.toString().trim()
            val pass = editTextPassword.text.toString().trim()
            val repass = editTextRePassword.text.toString().trim()
            val nick = editTextNick.text.toString().trim()
            val phone = editTextPhone.text.toString().trim()

            val pwPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{8,15}$"
            val phonePattern = "^(\\+[0-9]+)?[0-9]{10,15}$"

            when {
                user.isEmpty() || pass.isEmpty() || repass.isEmpty() || nick.isEmpty() || phone.isEmpty() -> showToast("회원정보를 모두 입력해주세요.")
                !CheckId -> showToast("아이디 중복확인을 해주세요.")
                !Pattern.matches(pwPattern, pass) -> showToast("비밀번호 형식이 올바르지 않습니다.")
                pass != repass -> showToast("비밀번호가 일치하지 않습니다.")
                !CheckNick -> showToast("닉네임 중복확인을 해주세요.")
                !Pattern.matches(phonePattern, phone) -> showToast("전화번호 형식이 올바르지 않습니다.")
                else -> {
                    val insert = DB.insertData(user, pass, nick, phone)
                    if (insert) {
                        showToast("가입되었습니다.")
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        showToast("가입 실패하였습니다.")
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
