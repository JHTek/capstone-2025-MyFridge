package com.example.refrigeratormanager

data class ChatRequest(
    val message: String, // 서버에 보낼 텍스트 메시지
    val userId: String
)
