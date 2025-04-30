package com.mysite.ref.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/sendMessage")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request,  HttpSession session) {
        System.out.println("🔹 요청 도착: " + request.getMessage()); // 요청이 들어왔는지 확인
        ChatResponse response = chatService.sendMessage(request, session);
        System.out.println("🔹 응답 반환: " + response.getReply()); // 응답이 정상적으로 생성되었는지 확인
        return ResponseEntity.ok(response);
    }

}
