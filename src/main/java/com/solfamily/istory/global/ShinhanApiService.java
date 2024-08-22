package com.solfamily.istory.global;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ShinhanApiService {
    @Value("${SHINHAN_API_KEY}")
    private static String apiKey;

    // 사용자 계정 생성 API
    public Map<String, Object> userJoin(
            String userId
    ) {
        Map<String, Object> userInfo = new HashMap<>();

//        log.info("userId : {}" , userId);

        HttpClient client = HttpClient.newHttpClient();

        // JSON 데이터
        String jsonInputString = "{" +
                "\"apiKey\":\"" + apiKey + "\"," +
                "\"userId\":\"" + userId + "\" " +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://finopenapi.ssafy.io/ssafy/api/v1/member")) // 요청을 보낼 URL
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

//            System.out.println("Response Code: " + response.statusCode());
//            System.out.println("Response Body: " + response.body());

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());
            String userKey = rootNode.path("userKey").asText(); // userKey 추출
//            log.info("userKey : {}" , userKey);
            userInfo.put("userKey", userKey);

            return userInfo;
        } catch (Exception e) {
            return null;
        }
    }
}

//    public static void main(String[] args) {
//        HttpClient client = HttpClient.newHttpClient();
//
//        String managerId = "pkb1998@naver.com";
//        // JSON 데이터
//        String jsonInputString = "{\"managerId\": \"" + managerId + "\"}";
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://finopenapi.ssafy.io/ssafy/api/v1/edu/app/reIssuedApiKey")) // 요청을 보낼 URL
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
//    }
//}