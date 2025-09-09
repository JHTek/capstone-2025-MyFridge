package com.example.refrigeratormanager.chat

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.refrigeratormanager.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Flask API:
 * POST /recipes/{id}/chat
 * req:  { "message": "양파 볶은 다음에 뭐해?" }
 * resp: { "answer": "..."} or { "error": "..." }
 *
 * - 에뮬레이터: BASE_URL = http://10.0.2.2:5000/
 * - 실제 기기: Flask 서버 로그에 나온 192.168.x.x:5000 로 교체
 * - ChatAdapter/ChatMessage 는 기존 프로젝트 클래스 사용
 */
class ChatActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    private lateinit var api: RecipeApi
    private lateinit var recipeId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 1) 레시피 ID (상세 화면에서 putExtra로 전달하는 것을 권장)
        recipeId = intent.getStringExtra("RECIPE_ID") ?: "352364"

        // 2) UI
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val input = findViewById<EditText>(R.id.messageInput)
        val send = findViewById<Button>(R.id.sendButton)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }


        chatAdapter = ChatAdapter(chatMessages)
        recyclerView.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        recyclerView.adapter = chatAdapter

        // 3) Retrofit
        api = provideRetrofit().create(RecipeApi::class.java)

        // 4) 전송 버튼
        send.setOnClickListener {
            val msg = input.text.toString().trim()
            if (msg.isNotEmpty()) {
                addMessage(ChatMessage(msg, isMine = true))
                sendMessageToServer(msg, recyclerView, send)
                input.text.clear()
            }
        }

        // 엔터로 전송
        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                send.performClick(); true
            } else false
        }
    }

    // ===== Retrofit / DTO =====

    private fun baseUrl(): String {
        val isEmulator =
            Build.FINGERPRINT.lowercase().contains("generic") ||
                    Build.MODEL.contains("Emulator") ||
                    Build.MODEL.contains("Android SDK built for x86")

        return if (isEmulator) {
            "http://10.0.2.2:5000/"
        } else {
            // 실제 기기에서 테스트 시, PC의 로컬 IP로 교체하세요 (Flask 로그에 표시된 IP)
            "http://192.168.25.58:5000/"
        }
    }

    private fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    interface RecipeApi {
        @POST("recipes/{id}/chat")
        fun chatWithRecipe(
            @Path("id") recipeId: String,
            @Body body: ChatRequest
        ): Call<ChatResponse>
    }

    data class ChatRequest(val message: String)
    data class ChatResponse(val answer: String?, val error: String? = null)

    // ===== 네트워크 호출 =====

    private fun sendMessageToServer(
        message: String,
        recyclerView: RecyclerView,
        sendBtn: Button
    ) {
        sendBtn.isEnabled = false

        api.chatWithRecipe(recipeId, ChatRequest(message)).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                sendBtn.isEnabled = true
                if (response.isSuccessful) {
                    val body = response.body()
                    val reply = body?.answer ?: body?.error ?: "응답 없음"
                    addMessage(ChatMessage(reply, isMine = false))
                    scrollToBottom(recyclerView)
                    Log.d("ChatActivity", "답변: $reply")
                } else {
                    val err = response.errorBody()?.string() ?: "에러 본문 없음"
                    val msg = "오류 코드: ${response.code()} / $err"
                    addMessage(ChatMessage("서버 오류: $msg", isMine = false))
                    scrollToBottom(recyclerView)
                    Log.e("ChatActivity", msg)
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                sendBtn.isEnabled = true
                val msg = "네트워크 실패: ${t.localizedMessage}"
                addMessage(ChatMessage(msg, isMine = false))
                scrollToBottom(recyclerView)
                Log.e("ChatActivity", msg, t)
            }
        })
    }

    private fun addMessage(m: ChatMessage) {
        chatMessages.add(m)
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
    }

    private fun scrollToBottom(rv: RecyclerView) {
        rv.post { rv.smoothScrollToPosition(chatMessages.size - 1) }
    }


}
