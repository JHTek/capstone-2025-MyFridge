package com.mysite.ref.chat;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.*;
import com.mysite.ref.chat.ChatRequest;
import com.mysite.ref.chat.ChatResponse;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import jakarta.servlet.http.HttpSession;


@Service
public class ChatService {

    private static final String PROJECT_ID = "test-nfsx"; // Google Cloud 프로젝트 ID
    private static final String CREDENTIALS_PATH = "C:\\Users\\FORYOUCOM\\Downloads\\test-nfsx-20010a9880d9.json"; // 서비스 계정 키 경로
    
    public ChatResponse sendMessage(ChatRequest request, HttpSession httpSession) {
        try {
        	 String sessionId = httpSession.getId();
        	 
            String responseText = detectIntent(PROJECT_ID, sessionId, request.getMessage(), CREDENTIALS_PATH); //session 아이디 수정필요
            return new ChatResponse(responseText);
        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse("오류 발생: " + e.getMessage());
        }
    }

    public static String detectIntent(String projectId, String sessionId, String queryText, String credentialsPath) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
        SessionsSettings sessionsSettings = SessionsSettings.newBuilder()
                .setCredentialsProvider(() -> credentials)
                .build();

        try (SessionsClient sessionsClient = SessionsClient.create(sessionsSettings)) {
            SessionName session = SessionName.of(projectId, sessionId);
            TextInput textInput = TextInput.newBuilder().setText(queryText).setLanguageCode("ko").build();
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
            DetectIntentRequest request = DetectIntentRequest.newBuilder()
                    .setSession(session.toString())
                    .setQueryInput(queryInput)
                    .build();

            DetectIntentResponse response = sessionsClient.detectIntent(request);
            return response.getQueryResult().getFulfillmentText();
        }
    }
}
