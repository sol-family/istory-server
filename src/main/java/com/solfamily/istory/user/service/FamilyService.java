package com.solfamily.istory.user.service;

import com.solfamily.istory.global.configure.JsonParser;
import com.solfamily.istory.global.service.ShinhanApiService;
import com.solfamily.istory.user.db.FamilyRepository;
import com.solfamily.istory.user.db.UserRepository;
import com.solfamily.istory.user.model.FamilyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private UserRepository userRepository;
    private ShinhanApiService shinhanApiService;
    private FamilyRepository familyRepository;
    private JsonParser jsonParser;

    // 대표자 계정 신한 API 회원가입
    public ResponseEntity joinToShinhanApi(
            String userId
    ) {
        var userEntity = userRepository.findById(userId).get();

        // 신한 API 연동(사용자 계정 생성)
        Map<String, Object> userInfo = shinhanApiService.userJoin(userId);

        if(userInfo.get("userKey").equals("")) {
            String msg = "내부적으로 정한 에러코드 (신한 API로부터 사용자 계정이 제대로 생성되지 않았을 때 = )";
            String json = jsonParser.toJson(msg);
            return ResponseEntity
                    .status(HttpStatus.OK) // 200 error
                    .body(json); // 내부적으로 정한 에러코드 (신한 API로부터 사용자 계정이 제대로 생성되지 않았을 때 = )
        }

        var userKey = userInfo.get("userKey"); // 신한 API로부터 userKey를 받아옴
        userEntity.setUserKey(userKey.toString()); // 받아온 userKey를 유저엔티티에 저장

        userRepository.save(userEntity);

        Map<String, Object> response = new HashMap<>();
        response.put("result", true);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 가족 확정시
    public ResponseEntity confirmFamily(
            String representivUserId // 대표자 아이디
    ) {
        joinToShinhanApi(representivUserId);

        var familyEntity = new FamilyEntity();

        String familyKey = "familyKey/" + UUID.randomUUID().toString(); // 고유한 패밀리키(식별키) 생성
        familyEntity.setFamilyKey(familyKey); // 유저엔티티에 패밀리키 저장
        familyEntity.setRepesentiveUserId(representivUserId);

        try {
            familyRepository.save(familyEntity);
        } catch (Exception e) {
        }

        Map<String, Object> response = new HashMap<>();
        response.put("result", true);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
