package com.solfamily.istory.global;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private static String apiName = "inquireSavingsProducts";
    private static String transmissionDate = "20240101";
    private static String transmissionTime = "121212";
    private static String institutionCode = "00100";
    private static String fintechAppNo = "001";
    private static String apiServiceCode = "inquireSavingsProducts";
    private static String institutionTransactionUniqueNo = "20240101121212123456";


    // 사용자 계정 생성
    public Map<String, Object> userJoin(
            String userId
    ) {
        Map<String, Object> userInfo = new HashMap<>();

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

//          System.out.println("Response Code: " + response.statusCode());
//          System.out.println("Response Body: " + response.body());

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());
            String userKey = rootNode.path("userKey").asText(); // userKey 추출
//          log.info("userKey : {}" , userKey);
            userInfo.put("userKey", userKey);

            return userInfo;
        } catch (Exception e) {
            return null;
        }
    }

    // 적금 상품 조회
    public ResponseEntity<String> inquireSavingsProducts() {
        HttpClient client = HttpClient.newHttpClient();

        // JSON 데이터
        String jsonInputString = "{" +
                "\"Header\": {" +
                "\"apiName\":\"" + apiName + "\"," +
                "\"transmissionDate\":\"" + transmissionDate + "\"," +
                "\"transmissionTime\":\"" + transmissionTime + "\"," +
                "\"transmissionDate\":\"" + transmissionDate + "\"," +
                "\"institutionCode\":\"" + institutionCode + "\"," +
                "\"fintechAppNo\":\"" + fintechAppNo + "\"," +
                "\"apiServiceCode\":\"" + apiServiceCode + "\"," +
                "\"institutionTransactionUniqueNo\":\"" + institutionTransactionUniqueNo + "\"," +
                "\"apiKey\":\"" + apiKey + "\"," +
                "}" +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://finopenapi.ssafy.io/ssafy/api/v1/edu/savings/inquireSavingsProducts")) // 요청을 보낼 URL
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 응답 상태 코드와 본문을 그대로 클라이언트에 반환
            return new ResponseEntity<>(response.body(), HttpStatus.valueOf(response.statusCode()));

        } catch (Exception e) {
            // 예외 발생 시 INTERNAL_SERVER_ERROR 상태 코드로 빈 응답 반환
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 적금 계좌 생성
    public ResponseEntity<String> createSavingAccount(
            String accountTypeUniqueNo, // 상품고유번호
            String depositBalance, // 가입금액
            String withdrawalAccountNo // 출금계좌번호
    ) {
        HttpClient client = HttpClient.newHttpClient();

        // JSON 데이터
        String jsonInputString = "{" +
                "\"Header\": {" +
                "\"apiName\":\"" + apiName + "\"," +
                "\"transmissionDate\":\"" + transmissionDate + "\"," +
                "\"transmissionTime\":\"" + transmissionTime + "\"," +
                "\"transmissionDate\":\"" + transmissionDate + "\"," +
                "\"institutionCode\":\"" + institutionCode + "\"," +
                "\"fintechAppNo\":\"" + fintechAppNo + "\"," +
                "\"apiServiceCode\":\"" + apiServiceCode + "\"," +
                "\"institutionTransactionUniqueNo\":\"" + institutionTransactionUniqueNo + "\"," +
                "\"apiKey\":\"" + apiKey + "\"," +
                "}," +
                "\"accountTypeUniqueNo\":\"" + accountTypeUniqueNo + "\"," +
                "\"depositBalance\":\"" + depositBalance + "\"," +
                "\"withdrawalAccountNo\":\"" + withdrawalAccountNo + "\"" +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://finopenapi.ssafy.io/ssafy/api/v1/edu/savings/createAccount")) // 요청을 보낼 URL
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 응답 상태 코드와 본문을 그대로 클라이언트에 반환
            return new ResponseEntity<>(response.body(), HttpStatus.valueOf(response.statusCode()));

        } catch (Exception e) {
            // 예외 발생 시 INTERNAL_SERVER_ERROR 상태 코드로 빈 응답 반환
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }   
}

//    API키 발급
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