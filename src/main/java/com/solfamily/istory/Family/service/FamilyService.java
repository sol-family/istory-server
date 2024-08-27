package com.solfamily.istory.Family.service;

import com.solfamily.istory.global.configure.JsonParser;
import com.solfamily.istory.global.service.ShinhanApiService;
import com.solfamily.istory.Family.db.FamilyRepository;
import com.solfamily.istory.user.db.UserRepository;
import com.solfamily.istory.Family.model.FamilyEntity;
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

    public ResponseEntity<Map<String, Object>> hasSavingsAccount(
            String userId
    ) {
        Map<String, Object> response = new HashMap<>();

        String familyKey = userRepository.findById(userId).get().getFamilyKey();
        
        String savingsAccount = familyRepository.findById(familyKey).get().getSavingsAccountNo();

        // 가족계좌가 존재하지 않는다면, 아직 가족이 확정되지 않은 상태
        if(savingsAccount == null) {
            response.put("hasFamily", true);
            response.put("inviteCode" , ""); // redis 작업 후 redis에 familyKey로 접근해서 inviteCode 받아오기
            response.put("hasSavingsAccount", false);
        } else {
            response.put("hasFamily", true);
            response.put("hasSavingsAccount", true);          
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


}
