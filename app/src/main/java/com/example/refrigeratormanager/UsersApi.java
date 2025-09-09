package com.example.refrigeratormanager;

import com.example.refrigeratormanager.chat.ChatRequest;
import com.example.refrigeratormanager.chat.ChatResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UsersApi {
    @POST("user/register")
    Call<String> registerUser(@Body Users user);

    @GET("user/checkUser")
    Call<Boolean> checkUser(@Query("userid") String userid);

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(@Field("userid") String userid, @Field("password") String password);

    @POST("chat/sendMessage") // 실제 엔드포인트 확인 필요
    Call<ChatResponse> sendMessage(@Body ChatRequest request);
}
