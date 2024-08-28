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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class FamilyService {

    private final UserRepository userRepository;
    private final ShinhanApiService shinhanApiService;
    private final FamilyRepository familyRepository;
    private final JwtTokenService jwtTokenService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;

    @Autowired
    public FamilyService(UserRepository userRepository,
                         ShinhanApiService shinhanApiService,
                         FamilyRepository familyRepository,
                         JwtTokenService jwtTokenService,
                         RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.shinhanApiService = shinhanApiService;
        this.familyRepository = familyRepository;
        this.jwtTokenService = jwtTokenService;
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash(); // 생성자에서 초기화
    }

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

        // 패밀리키 업데이트 하고 DB에 userEntity 저장
        UserEntity userEntity = optionalUserEntity.get();
        userEntity.setFamilyKey(familyKey);
        userRepository.save(userEntity);

        // Redis에 inviteCode를 키로, familyKey를 값으로 저장
        hashOperations.put(inviteCode, "familyKey", familyKey);

        // family 레코드 생성 -> 해당 가족의 패밀리키와 대표자 userId 저장
        FamilyEntity familyEntity = new FamilyEntity();
        familyEntity.setFamilyKey(familyKey);
        familyEntity.setRepesentiveUserId(userId);
        familyRepository.save(familyEntity);

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

        String familyKey = hashOperations.get(inviteCodeRequest.getInviteCoode(), "familyKey");
        Optional<FamilyEntity> optionalFamilyEntity = familyRepository.findById(familyKey);

        // 해당 패밀리키를 가진 가족이 아이스토리 db에 존재하지 않을때
        if(optionalFamilyEntity.isEmpty()) {
            String errorCode = "F0"; // 해당 패밀리키를 가진 가족이 아이스토리 db에 존재하지 않을때 = F0
            response.put("result", false);
            response.put("errorCode", errorCode);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        }

        // 대표자 userId를 아이스토리 db로부터 가져옴
        String representiveUserId = familyRepository.findRepresentiveIdByFamilyKey(familyKey);

        // 대표자 이름을 아이스토리 db로부터 가져옴
        String representiveName = userRepository.findUserNameByUserId(representiveUserId);

        response.put("result", true);
        response.put("representiveUserName", representiveName);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public ResponseEntity<Map<String, Object>> hasSavingsAccount(
            String userId
    ) {
        Map<String, Object> response = new HashMap<>();

        String familyKey = userRepository.findById(userId).get().getFamilyKey();

        String savingsAccount = familyRepository.findById(familyKey).get().getSavingsAccountNo();

        // 가족계좌가 존재하지 않는다면, 아직 가족이 확정되지 않은 상태
        if (savingsAccount == null) {
            response.put("hasFamily", true);
            response.put("inviteCode", ""); // redis 작업 후 redis에 familyKey로 접근해서 inviteCode 받아오기
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
