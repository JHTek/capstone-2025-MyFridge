package com.example.refrigeratormanager;

public class LoginResponse {
    private String status;
    private String message;
    private String username;
    private String userid;



    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername(){
        return username;
    }
    public String getUserid(){
        return userid;
    }


    @Override
    public String toString() {
        return "LoginResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", username='" + username + '\'' +
                ", userid='" + userid + '\'' +
                '}';
    }
}


