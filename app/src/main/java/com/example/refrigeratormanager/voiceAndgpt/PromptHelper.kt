package com.example.refrigeratormanager.voiceAndgpt
import com.example.refrigeratormanager.voiceAndgpt.chatGPTDTO.Message


object PromptHelper {
    fun getIngredientParsingPrompt(userText: String): List<Message> {
        return listOf(
            Message(
                "system",
                "너는 식재료 등록을 도와주는 어시스턴트야. 사용자의 문장에서 식재료명과 갯수를 JSON 형태로 추출해줘. 예: '감자 3개랑 당근 2개 샀어' → {\"감자\": 3, \"당근\": 2}. 다른 말은 절대 하지 마."
            ),
            Message("user", userText)
        )
    }
}
