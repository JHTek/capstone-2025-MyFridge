package com.example.refrigeratormanager.DTO

data class ApiResponse(
    val status: String, // 성공 여부 (예: "success", "error")
    val message: String? // 서버 메시지
)