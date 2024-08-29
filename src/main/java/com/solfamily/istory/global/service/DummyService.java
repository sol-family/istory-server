package com.solfamily.istory.global.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DummyService {
    @Autowired
    private UserService userService;
    @Autowired
    private MissionService missionService;

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
}
