package com.solfamily.istory.Family.service;

import com.solfamily.istory.Family.model.FamilyEntity;
import com.solfamily.istory.Family.model.InviteCodeRequest;
import com.solfamily.istory.global.service.JwtTokenService;
import com.solfamily.istory.global.service.ShinhanApiService;
import com.solfamily.istory.Family.db.FamilyRepository;
import com.solfamily.istory.user.db.UserRepository;
import com.solfamily.istory.user.model.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final UserRepository userRepository;
    private final ShinhanApiService shinhanApiService;
    private final FamilyRepository familyRepository;
    private final JwtTokenService jwtTokenService;
    private final HashOperations<String, String, String> hashOperations; // Redis의 HashOperations 빈 주입

    public ResponseEntity<Map<String, Object>> getInviteCode(
            HttpServletRequest request
    ) {
        Map<String, Object> response = new HashMap<>();

        // 클라이언트로부터 jwtToken을 받아옴
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7); // 토큰 추출

        // 토큰으로부터 userId 추출
        String userId = jwtTokenService.getUserIdByClaims(token);

        // userId의 해시값을 16진수로 변환하여 초대코드 생성 -> 중복 초대코드 발생 예방
        int hash = userId.hashCode();
        String inviteCode = String.valueOf(Integer.toHexString(hash));

        // 랜덤 값 + 초대코드를 합쳐 패밀리키 중복 발생 예방
        String familyKey = UUID.randomUUID() + inviteCode;

        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);

        // 아이스토리 DB에서 조회된 유저가 없을때
        if (optionalUserEntity.isEmpty()) {
            String errorCode = "U0";
            response.put("result", false);
            response.put("errorCode", errorCode);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        }

        // Redis에 inviteCode를 키로, familyKey와 대표자 id를 저장 (초대코드를 발급하는 사람이 대표자)
        hashOperations.put(inviteCode, "familyKey", familyKey);
        hashOperations.put(inviteCode, "representiveId", userId);

        hashOperations.put(userId, "inviteCode", inviteCode);

        // 응답 생성
        response.put("result", true);
        response.put("inviteCode", inviteCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public ResponseEntity<Map<String, Object>> getRepresentiveName (
        InviteCodeRequest inviteCodeRequest
    ) {
        Map<String, Object> response = new HashMap<>();

        // redis에서 대표자 아이디 가져옴
        String representiveUserId = hashOperations.get(inviteCodeRequest.getInviteCoode(), "representiveId");

        // 대표자 이름을 아이스토리 db로부터 가져옴
        String representiveName = userRepository.findUserNameByUserId(representiveUserId);

        response.put("result", true);
        response.put("representiveUserName", representiveName);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public ResponseEntity<Map<String, Object>> hasInviteCode(
            String userId
    ) {
        Map<String, Object> response = new HashMap<>();

        String familyKey = userRepository.findFamilyKeyByUserId(userId);

        // 패밀리키가 존재하지 않는다면, 아직 가족이 확정되지 않은 상태
        if(familyKey == null) {
            String inviteCode = hashOperations.get(userId, "inviteCode");

            response.put("hasFamily", false);
            // inviteCode가 없으면 초대코드 페이지로, 있으면 가족구성준비 페이지로
            response.put("inviteCode", inviteCode == null ? "" : inviteCode); // redis에 userId로 접근해서 inviteCode 받아오기
        } else {
            // 패밀리키가 존재한다면 가족구성완료
            response.put("hasFamily", true);
            response.put("inviteCode", "");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }



}
