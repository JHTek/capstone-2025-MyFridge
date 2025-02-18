package com.example.refrigeratormanager;

public class LoginResponse {
    private String status;
    private String message;
    private String token;  // JWT 토큰 추가

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}


