package com.example.refrigeratormanager.chat

data class ChatMessage(
    val message: String,
    val isMine: Boolean // true: 내가 보낸 메시지, false: 상대방 메시지
)
