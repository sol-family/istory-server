package com.solfamily.istory.global.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
public class ShinhanApiService {
    @Value("${SHINHAN_API_KEY}")
    private String apiKey;
    private static String apiName = "inquireSavingsProducts";
    private static String transmissionDate = "20240101";
    private static String transmissionTime = "121212";
    private static String institutionCode = "00100";
    private static String fintechAppNo = "001";
    private static String institutionTransactionUniqueNo = "20240101121212123456";

    // 사용자 계정 생성
    public Map<String, Object> signUp(
            String userId
    ) {
        try {
        Map<String, Object> userInfo = new HashMap<>();

        HttpClient client = HttpClient.newHttpClient();

        // JSON 데이터
        String jsonInputString = "{" +
                "\"apiKey\":\"" + apiKey + "\"," +
                "\"userId\":\"" + userId + "\" " +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://finopenapi.ssafy.io/ssafy/api/v1/member")) // 요청을 보낼 URL
                .header("Content-Type", "application/json") // 컨텐츠 타입 = JSON 타입
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString)) // POST 방식으로 request 보냄
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("SHINHAN_API_KEY: {}", apiKey);
            log.info("Response Code: {}", response.statusCode());
            log.info("Response Body: {}", response.body());

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());
            String userKey = rootNode.path("userKey").asText(); // userKey 추출
            userInfo.put("userKey", userKey);

            return userInfo;
        } catch (Exception e) {
            log.info("ErrorName : {}, ErrorMsg : {}" , e.getClass(), e.getMessage());
            return null;
        }
    }

    // 적금 상품 조회
    public ResponseEntity<String> inquireSavingsProducts() {
        String apiServiceCode = "inquireSavingsProducts";

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
    public String createSavingAccount(
            String userKey,
            String withdrawalAccountNo,
            String accountTypeUniqueNo,
            long depositBalance
    ) {
        try {
            Map<String, Object> result = new HashMap<>();
            HttpClient client = HttpClient.newHttpClient();

            String requestUrl = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/savings/createAccount";
            String apiName = requestUrl.split("/")[requestUrl.split("/").length-1];
            String nowDate = LocalDate.now().toString().substring(0,10).replace("-","");
            String nowTime = LocalTime.now().toString().replaceAll("[.:]", "").substring(0,12);

            Gson gson = new Gson();
            JsonObject json = new JsonObject();

            // 헤더파트
            Map<String,String> header = new HashMap<>();
            header.put("apiName",apiName);
            header.put("transmissionDate", nowDate);
            header.put("transmissionTime", nowTime.substring(0,6));
            header.put("institutionCode","00100");
            header.put("fintechAppNo","001");
            header.put("apiServiceCode",apiName);
            header.put("institutionTransactionUniqueNo",nowDate+nowTime);
            header.put("apiKey",apiKey);
            header.put("userKey",userKey);
            json.add("Header",gson.toJsonTree(header));
            json.add("withdrawalAccountNo",gson.toJsonTree(withdrawalAccountNo));
            json.add("accountTypeUniqueNo",gson.toJsonTree(accountTypeUniqueNo));
            json.add("depositBalance",gson.toJsonTree(depositBalance));


            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl)) // 요청을 보낼 URL
                    .header("Content-Type", "application/json") // 컨텐츠 타입 = JSON 타입
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString())) // POST 방식으로 request 보냄
                    .build();
            System.out.println(json);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("SHINHAN_API_KEY: {}", apiKey);
            log.info("Response Code: {}", response.statusCode());
            log.info("Response Body: {}", response.body());

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseData = objectMapper.readValue(response.body(), Map.class);

            Map<String,Object> headerDate = (Map<String, Object>) responseData.get("Header");
            if(!headerDate.get("responseCode").equals("H0000")){
                return "";
            }
            String accountNo = (String)((Map<String, Object>) responseData.get("REC")).get("accountNo");
            return accountNo;
        } catch (Exception e) {
            log.info("ErrorName : {}, ErrorMsg : {}" , e.getClass(), e.getMessage());
            return "";
        }
    }

    // 적금 계좌 단건 조회
    public ResponseEntity<String> inquireSavingsAccount(
            String accountNo // 상품고유번호
    ) {
        String apiServiceCode = "inquireAccount";

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
                "\"accountNo\":\"" + accountNo + "\"" +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://finopenapi.ssafy.io/ssafy/api/v1/edu/savings/inquireAccount")) // 요청을 보낼 URL
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

    // 적금 납입 회차 조회
    public ResponseEntity<String> inquirePayment(
            String accountNo, // 적금계좌번호
            String userKey //
    ) {
        String apiServiceCode = "inquirePayment";

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
                "\"accountNo\":\"" + accountNo + "\"" +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://finopenapi.ssafy.io/ssafy/api/v1/edu/savings/inquirePayment")) // 요청을 보낼 URL
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

    public ResponseEntity<Map> inquireDemandDepositAccountList(String userKey) {
        try {
            Map<String, Object> result = new HashMap<>();
            HttpClient client = HttpClient.newHttpClient();

            String requestUrl = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/inquireDemandDepositAccountList";
            String apiName = requestUrl.split("/")[requestUrl.split("/").length-1];
            String nowDate = LocalDate.now().toString().substring(0,10).replace("-","");
            String nowTime = LocalTime.now().toString().replaceAll("[.:]", "").substring(0,12);

            Gson gson = new Gson();
            JsonObject json = new JsonObject();

            // 헤더파트
            Map<String,String> header = new HashMap<>();
            header.put("apiName",apiName);
            header.put("transmissionDate", nowDate);
            header.put("transmissionTime", nowTime.substring(0,6));
            header.put("institutionCode","00100");
            header.put("fintechAppNo","001");
            header.put("apiServiceCode",apiName);
            header.put("institutionTransactionUniqueNo",nowDate+nowTime);
            header.put("apiKey",apiKey);
            header.put("userKey",userKey);
            json.add("Header",gson.toJsonTree(header));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl)) // 요청을 보낼 URL
                    .header("Content-Type", "application/json") // 컨텐츠 타입 = JSON 타입
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString())) // POST 방식으로 request 보냄
                    .build();
            System.out.println(json);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("SHINHAN_API_KEY: {}", apiKey);
            log.info("Response Code: {}", response.statusCode());
            log.info("Response Body: {}", response.body());

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.body());
            Iterator<Map.Entry<String,JsonNode>> list =rootNode.fields();
            while(list.hasNext()){
                Map.Entry<String,JsonNode> temp = list.next();
                result.put(temp.getKey(),temp.getValue());
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.info("ErrorName : {}, ErrorMsg : {}" , e.getClass(), e.getMessage());
            return null;
        }
    }
}

