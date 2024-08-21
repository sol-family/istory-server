package com.solfamily.istory.service.shinhanapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class ShinhanAPI {
    private static String apiKey = "2f512367df5a448ebda0aa264aeba0da";

    // 사용자 계정 생성 API
    public Map<String, Object> userJoin(
            String userId
    ) {
        Map<String, Object> userInfo = new HashMap<>();

        HttpClient client = HttpClient.newHttpClient();

        // JSON 데이터
        String jsonInputString = "{" +
                "\"apiKey\": \"" + apiKey + "\"," +
                "\"userId\": \"" + userId + "\" " +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://finopenapi.ssafy.io/ssafy/api/v1/member")) // 요청을 보낼 URL
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());
            String userKey = rootNode.path("userKey").asText(); // userKey 추출
            userInfo.put("userKey", userKey);

            return userInfo;
        } catch (Exception e) {
            return null;
        }
    }
}

// 아래는 API키 발급 코드
//        HttpClient client = HttpClient.newHttpClient();
//
//        String managerId = "pkb1998@naver.com";
//        // JSON 데이터
//        String jsonInputString = "{\"managerId\": \"" + managerId + "\"}";
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://finopenapi.ssafy.io/ssafy/api/v1/edu/app/IssuedApiKey")) // 요청을 보낼 URL
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
//                .build();
//
//        try {
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println("Response Code: " + response.statusCode());
//            System.out.println("Response Body: " + response.body());
//
//            // JSON 파싱
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(response.body());
//            String apiKey = rootNode.path("apiKey").asText(); // apiKey 추출
//            System.out.println("Extracted apiKey: " + apiKey);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }