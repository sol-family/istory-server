package com.solfamily.istory.service.user;

import com.solfamily.istory.db.user.UserInviteRepository;
import com.solfamily.istory.db.user.UserRepository;
import com.solfamily.istory.global.PasswordService;
import com.solfamily.istory.model.user.UserEntity;
import com.solfamily.istory.model.user.UserInviteCodeEntity;
import com.solfamily.istory.global.ShinhanApiService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class UserInviteService {

    private final UserRepository userRepository;
    private final UserInviteRepository userInviteRepository;
    private final UserConverterService userConverterService;
    private final PasswordService passwordService;
    private final ShinhanApiService shinhanApiService;

    // 초대코드로 회원가입
    public ResponseEntity userJoinByInvite(
            UserEntity userEntity
    ) {
        // 유효한 초대코드인지 확인
        String inviteCode = userEntity.getInviteCode();
        String userId = userEntity.getUserId();
        String userPw = userEntity.getUserPw();

        // 유저 비밀번호 암호화
        var hashedUserPw = passwordService.hashPassword(userPw);
        userEntity.setUserPw(hashedUserPw); // 암호화된 비밀번호로 재저장

        if(inviteCode.equals("")) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Not Exist InviteCode : 초대코드가 존재하지 않습니다.");
        }

        var OptionalUserInviteEntity = userInviteRepository.findByInviteCodeAndIsUsedFalseAndExpiryDateAfter(inviteCode, LocalDateTime.now());

        // 유효하지 않은 초대코드라면
        if(OptionalUserInviteEntity.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("Invalid InviteCode : 유효하지 않은 초대코드입니다.");
        }

        // DB에서 초대코드에 저장된 패밀키 받아와서 저장
        String familyKey = OptionalUserInviteEntity.get().getFamilyKey();
        userEntity.setFamilyKey(familyKey);

        // 신한 API 연동(사용자 계정 생성)
        Map<String, Object> userInfo  = shinhanApiService.userJoin(userId);

        if(userInfo.get("userKey").equals("")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body("There is some error about API : 신한 API와의 연동과정에서 문제가 발생했습니다.");
        }

        // 신한 API에서 받아온 사용자키 userEntity에 저장
        var userKey = userInfo.get("userKey");
//        log.info("userKey : {}", userKey);
        userEntity.setUserKey(userKey.toString());

        var entity = userRepository.save(userEntity);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userConverterService.toDto(entity));
    }

    // 초대코드 발급
    public String userInvite(String familyKey) {
        // 랜덤한 초대코드 생성
        String inviteCode = UUID.randomUUID().toString();

        // DB에 생성된 inviteCode 관련 정보 저장
        UserInviteCodeEntity userInviteCodeEntity = new UserInviteCodeEntity();
        userInviteCodeEntity.setInviteCode(inviteCode);
        userInviteCodeEntity.setFamilyKey(familyKey);
        userInviteCodeEntity.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // 초대코드 유효시간 30분
        userInviteCodeEntity.setUsed(false);
        userInviteCodeEntity.setCreatedDate(LocalDateTime.now());
        userInviteRepository.save(userInviteCodeEntity);

        // 초대 코드 반환
        return inviteCode;
    }

    // 생성된지 30분이 지난 초대코드는 30분 주기로 사용된 초대코드로 변경
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30분마다 실행
    @Transactional
    public void updateInviteCode() {
        userInviteRepository.checkExpiredInvitesAsUsed(
                LocalDateTime.now()
        );
    }

    // 사용된 초대코드는 30분 주기로 삭제
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30분마다 실행
    @Transactional
    public void deleteInviteCode() {
        userInviteRepository.deleteByisUsedTrue();
    }
}
