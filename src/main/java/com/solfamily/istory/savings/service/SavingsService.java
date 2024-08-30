package com.solfamily.istory.savings.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.solfamily.istory.global.service.JwtTokenService;
import com.solfamily.istory.global.service.ShinhanApiService;
import com.solfamily.istory.user.db.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsService {
    private final ShinhanApiService shinhanApiService;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    @Value("${SHINHAN_API_KEY}")
    private String apiKey;
    @Value("${SAVINGS_PRODUCT_CODE}")
    private String accountTypeUniqueNo;

    // 적금 상품 조회
    public ResponseEntity<String> inquireSavingsProducts() {
        return shinhanApiService.inquireSavingsProducts();
    }

    // 적금 계좌 생성
    public ResponseEntity<Map<String,String>> createSavingsAccount(
            HttpServletRequest request,
            String withdrawalAccountNo,
            long depositBalance
    ) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7); // 토큰 추출
        String userId = jwtTokenService.getUserIdByClaims(token);
        String userKey = userRepository.findById(userId).get().getUserKey();
        return shinhanApiService.createSavingAccount(userKey,
                withdrawalAccountNo,accountTypeUniqueNo, depositBalance);
    }

    public ResponseEntity<Map> createWithdrawalAccount(String userKey,String accountTypeUniqueNo){
        try {
            Map<String, Object> result = new HashMap<>();
            HttpClient client = HttpClient.newHttpClient();

            String requestUrl = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/createDemandDepositAccount";
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
            json.add("accountTypeUniqueNo",gson.toJsonTree(accountTypeUniqueNo));

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

//             JSON 파싱
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
            return ResponseEntity.ok(Collections.singletonMap("errorCode","createWithdrawalAccountError"));
        }
    }

    public void insertCashByAccountNo(String userKey,String accountNo){
        try {
            Map<String, Object> result = new HashMap<>();
            HttpClient client = HttpClient.newHttpClient();

            String requestUrl = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/updateDemandDepositAccountDeposit";
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
            json.add("accountNo",gson.toJsonTree(accountNo));
            json.add("transactionBalance",gson.toJsonTree(10000000));

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
        } catch (Exception e) {
            log.info("ErrorName : {}, ErrorMsg : {}" , e.getClass(), e.getMessage());
        }
    }
}
