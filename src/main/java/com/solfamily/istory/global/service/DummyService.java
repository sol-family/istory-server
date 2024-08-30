package com.solfamily.istory.global.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.solfamily.istory.mission.model.entity.MissionEntity;
import com.solfamily.istory.mission.service.MissionService;
import com.solfamily.istory.user.db.UserRepository;
import com.solfamily.istory.user.model.UserDto;
import com.solfamily.istory.user.model.UserEntity;
import com.solfamily.istory.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class DummyService {
    @Autowired
    private UserService userService;
    @Autowired
    private MissionService missionService;
    @Value("${SHINHAN_API_KEY}")
    private String apiKey;

    public ResponseEntity<Map<String, Object>> createDummyUsers() {
        ResponseEntity<Map<String, Object>> response = ResponseEntity.ok(Collections.singletonMap("result","false"));
        try {
            List<UserDto> users = getDummyUsers();
            for(UserDto user : users) {
                response = userService.signUp(user);
            }
            return response;
        } catch (Exception e) {
            return response;
        }
    }

    private List<UserDto> getDummyUsers() throws Exception {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

                    @Override
                    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
                        jsonWriter.value(localDateTime.format(formatter));
                    }

                    @Override
                    public LocalDateTime read(JsonReader jsonReader) throws IOException {
                        return LocalDateTime.parse(jsonReader.nextString(), formatter);
                    }
                })
                .create();

        System.setIn(new FileInputStream("src/main/resources/dummyUsers.json"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str;
        StringBuilder sb = new StringBuilder();

        while ((str = br.readLine()) != null) {
            sb.append(str);
        }

        Type type = new TypeToken<List<UserDto>>() {}.getType();
        return gson.fromJson(sb.toString(), type);
    }

    public ResponseEntity<Map> createDummyFamilyMissions(String familyKey,String date) {
        missionService.createMissionsByFamilyKey(familyKey,date);
        return ResponseEntity.ok(Collections.singletonMap("result", "success"));
    }

    public ResponseEntity<Map> shinhanTest(String userKey) {
        try {
            Map<String, Object> result = new HashMap<>();
            HttpClient client = HttpClient.newHttpClient();

            String requestUrl = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/savings/createProduct";
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
//            header.put("userKey",userKey);
            json.add("Header",gson.toJsonTree(header));
            json.add("bankCode",gson.toJsonTree("2"));
            json.add("accountName",gson.toJsonTree("istory적금"));
            json.add("accountDescription",gson.toJsonTree("365일 1년 istory적금입니다"));
            json.add("subscriptionPeriod",gson.toJsonTree("365"));
            json.add("minSubscriptionBalance",gson.toJsonTree("2"));
            json.add("maxSubscriptionBalance",gson.toJsonTree("2"));
            json.add("interestRate",gson.toJsonTree("2"));
            json.add("rateDescription",gson.toJsonTree("2"));

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
    
//    신한 적금 상품 등록

    
    
//    신한 적금 상품 조회
//    public ResponseEntity<Map> shinhanTest() {
//        try {
//            Map<String, Object> result = new HashMap<>();
//            HttpClient client = HttpClient.newHttpClient();
//
//            String requestUrl = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/savings/inquireSavingsProducts";
//            String apiName = requestUrl.split("/")[requestUrl.split("/").length-1];
//            String nowDate = LocalDate.now().toString().substring(0,10).replace("-","");
//            String nowTime = LocalTime.now().toString().replaceAll("[.:]", "").substring(0,12);
//
//            Map<String,String> header = new HashMap<>();
//            Gson gson = new Gson();
//            JsonObject json = new JsonObject();
//            header.put("apiName",apiName);
//            header.put("transmissionDate", nowDate);
//            header.put("transmissionTime", nowTime.substring(0,6));
//            header.put("institutionCode","00100");
//            header.put("fintechAppNo","001");
//            header.put("apiServiceCode",apiName);
//            header.put("institutionTransactionUniqueNo",nowDate+nowTime);
//            header.put("apiKey",apiKey);
//            json.add("Header",gson.toJsonTree(header));
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(requestUrl)) // 요청을 보낼 URL
//                    .header("Content-Type", "application/json") // 컨텐츠 타입 = JSON 타입
//                    .POST(HttpRequest.BodyPublishers.ofString(json.toString())) // POST 방식으로 request 보냄
//                    .build();
//            System.out.println(json);
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            log.info("SHINHAN_API_KEY: {}", apiKey);
//            log.info("Response Code: {}", response.statusCode());
//            log.info("Response Body: {}", response.body());
//
//            // JSON 파싱
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(response.body());
//            Iterator<Map.Entry<String,JsonNode>> list =rootNode.fields();
//            while(list.hasNext()){
//                Map.Entry<String,JsonNode> temp = list.next();
//                result.put(temp.getKey(),temp.getValue());
//            }
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            log.info("ErrorName : {}, ErrorMsg : {}" , e.getClass(), e.getMessage());
//            return null;
//        }
//    }

//    신한 수시입출금 상품 조회
    public ResponseEntity<Map> shinhanAccoutList() {
        try {
            Map<String, Object> result = new HashMap<>();
            HttpClient client = HttpClient.newHttpClient();

            String requestUrl = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/inquireDemandDepositList";
            String apiName = requestUrl.split("/")[requestUrl.split("/").length-1];
            String nowDate = LocalDate.now().toString().substring(0,10).replace("-","");
            String nowTime = LocalTime.now().toString().replaceAll("[.:]", "").substring(0,12);

            Map<String,String> header = new HashMap<>();
            Gson gson = new Gson();
            JsonObject json = new JsonObject();
            header.put("apiName",apiName);
            header.put("transmissionDate", nowDate);
            header.put("transmissionTime", nowTime.substring(0,6));
            header.put("institutionCode","00100");
            header.put("fintechAppNo","001");
            header.put("apiServiceCode",apiName);
            header.put("institutionTransactionUniqueNo",nowDate+nowTime);
            header.put("apiKey",apiKey);
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

//    신한 수시입출금계좌 조회
//    public ResponseEntity<Map> shinhanAccountCheck(String userKey) {
//        try {
//            Map<String, Object> result = new HashMap<>();
//            HttpClient client = HttpClient.newHttpClient();
//
//            String requestUrl = "https://finopenapi.ssafy.io/ssafy/api/v1/edu/demandDeposit/inquireDemandDepositAccountList";
//            String apiName = requestUrl.split("/")[requestUrl.split("/").length-1];
//            String nowDate = LocalDate.now().toString().substring(0,10).replace("-","");
//            String nowTime = LocalTime.now().toString().replaceAll("[.:]", "").substring(0,12);
//
//            Map<String,String> header = new HashMap<>();
//            Gson gson = new Gson();
//            JsonObject json = new JsonObject();
//            header.put("apiName",apiName);
//            header.put("transmissionDate", nowDate);
//            header.put("transmissionTime", nowTime.substring(0,6));
//            header.put("institutionCode","00100");
//            header.put("fintechAppNo","001");
//            header.put("apiServiceCode",apiName);
//            header.put("institutionTransactionUniqueNo",nowDate+nowTime);
//            header.put("apiKey",apiKey);
//            header.put("userKey",userKey);
//            json.add("Header",gson.toJsonTree(header));
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(requestUrl)) // 요청을 보낼 URL
//                    .header("Content-Type", "application/json") // 컨텐츠 타입 = JSON 타입
//                    .POST(HttpRequest.BodyPublishers.ofString(json.toString())) // POST 방식으로 request 보냄
//                    .build();
//            System.out.println(json);
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            log.info("SHINHAN_API_KEY: {}", apiKey);
//            log.info("Response Code: {}", response.statusCode());
//            log.info("Response Body: {}", response.body());
//
//            // JSON 파싱
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(response.body());
//            Iterator<Map.Entry<String,JsonNode>> list =rootNode.fields();
//            while(list.hasNext()){
//                Map.Entry<String,JsonNode> temp = list.next();
//                result.put(temp.getKey(),temp.getValue().asText());
//            }
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            log.info("ErrorName : {}, ErrorMsg : {}" , e.getClass(), e.getMessage());
//            return null;
//        }
//    }
}
