package com.mysite.ref.chat;

public class ChatRequest {
    private String message;
    private String userId;

    public ChatRequest() {}

    public ChatRequest(String message, String userId) {
        this.message = message;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }
    
    public String getUserId() {
        return userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
}