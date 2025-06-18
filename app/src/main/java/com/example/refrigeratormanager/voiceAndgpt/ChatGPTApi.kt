package com.example.refrigeratormanager.voiceAndgpt
import com.example.refrigeratormanager.voiceAndgpt.chatGPTDTO.ChatRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatGPTApi {
    @POST("v1/chat/completions")
    suspend fun getChatResponse(
        @Header("Authorization") authHeader: String,
        @Body request: ChatRequest
    ): chatGPTDTO.ChatResponse
}