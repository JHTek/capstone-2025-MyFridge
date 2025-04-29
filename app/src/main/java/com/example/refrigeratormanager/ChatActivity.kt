package com.example.refrigeratormanager

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val editText = findViewById<EditText>(R.id.messageInput)
        val sendButton = findViewById<Button>(R.id.sendButton)

        Log.d("DEBUG", "RecyclerView: $recyclerView")
        Log.d("DEBUG", "EditText: $editText")
        Log.d("DEBUG", "SendButton: $sendButton")


        // RecyclerView 설정
        chatAdapter = ChatAdapter(chatMessages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        sendButton.setOnClickListener {
            val message = editText.text.toString().trim()

            if (message.isNotEmpty()) {
                addMessage(ChatMessage(message, isMine = true)) // 내가 보낸 메시지 추가
                sendMessageToServer(message)
                editText.text.clear()
            }
        }
    }

    private fun sendMessageToServer(message: String) {
        val apiService = ApiClient.getClient().create(UsersApi::class.java)
        val request = ChatRequest(message)

        apiService.sendMessage(request).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful) {
                    val reply = response.body()?.reply ?: "응답 없음"
                    addMessage(ChatMessage(reply, isMine = false)) // 상대방 응답 추가
                    Log.d("API_RESPONSE", "서버 응답: $reply")
                } else {
                    Log.e("API_ERROR", "오류 코드: ${response.code()}") // 응답 코드 추가
                    Log.e("API_ERROR", "오류 메시지: ${response.errorBody()?.string() ?: "에러 본문 없음"}") // 응답 내용 출력
                }
            }


            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                Log.e("API_ERROR", "네트워크 실패: ${t.localizedMessage}")
                t.printStackTrace() // 전체 스택 트레이스를 출력
            }

        })
    }

    private fun addMessage(message: ChatMessage) {
        chatMessages.add(message)
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
    }
}
