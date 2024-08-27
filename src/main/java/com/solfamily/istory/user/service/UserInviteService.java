//package com.solfamily.istory.user.service;
//
//import com.solfamily.istory.user.db.UserInviteRepository;
//import com.solfamily.istory.user.db.UserRepository;
//import com.solfamily.istory.global.service.PasswordService;
//import com.solfamily.istory.user.model.UserEntity;
//import com.solfamily.istory.user.model.UserInviteCodeEntity;
//import com.solfamily.istory.global.service.ShinhanApiService;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Map;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@EnableScheduling
//public class UserInviteService {
//    private final UserRepository userRepository;
//    private final UserInviteRepository userInviteRepository;
//    private final UserConverterService userConverterService;
//    private final PasswordService passwordService;
//    private final ShinhanApiService shinhanApiService;
//
//    // 초대코드 발급
//    public ResponseEntity<Map<String, Object>> getInviteCode(
//
//    ) {
//        // 랜덤한 초대코드 생성
//        String inviteCode = UUID.randomUUID().toString();
//
//        // istory_user_invite 테이블에 생성된 inviteCode 관련 정보 저장
//        UserInviteCodeEntity userInviteCodeEntity = new UserInviteCodeEntity(); // userInviteCodeEntity 객체 생성
//        userInviteCodeEntity.setInviteCode(inviteCode); // 초대코드 저장
//        userInviteCodeEntity.setFamilyKey(familyKey); // 패밀리키 저장
//        userInviteCodeEntity.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // 초대코드 유효시간은 생성시간으로부터 30분
//        userInviteCodeEntity.setUsed(false); // 사용되지 않은 초대코드
//        userInviteCodeEntity.setCreatedDate(LocalDateTime.now()); // 초대코드가 생성된 시간
//
//        userInviteRepository.save(userInviteCodeEntity); // istory_user_invite 테이블에 생성된 inviteCode 관련 정보 저장
//
//        return ResponseEntity
//                .status(HttpStatus.OK) // 200 OK
//                .body(inviteCode); // 초대 코드 반환
//    }
//
//    // 초대코드 유효성 확인
//    public ResponseEntity checkInviteCode(
//            String inviteCode
//    ) {
//        // 클라이언트로부터 받아온 초대코드가 없다면 (예외처리)
//        if(inviteCode.equals("")) {
//            return ResponseEntity
//                    .status(HttpStatus.OK) // 200 OK
//                    .body(""); // 내부적으로 정한 에러코드 (클라이언트로부터 받아온 초대코드가 없을때 = )
//        }
//
//        // istory_user_invite 테이블에서 클라이언트로부터 받은 초대코드와 같은 유효한 초대코드가 있는지 확인하고 가져옴
//        var OptionalUserInviteEntity = userInviteRepository.findByInviteCodeAndIsUsedFalseAndExpiryDateAfter(inviteCode, LocalDateTime.now());
//
//        // 만약 istory_user_invite 테이블에 저장된 유효한 초대코드가 없다면 (예외처리)
//        if(OptionalUserInviteEntity.isEmpty()) {
//            return ResponseEntity
//                    .status(HttpStatus.OK) // 200 OK
//                    .body(""); // 내부적으로 정한 에러코드 ( DB에 저장된 유효한 초대코드가 없을때 = )
//        }
//
//        return ResponseEntity
//                .status(HttpStatus.OK) // 200 OK
//                .body(true); // 초대코드가 유효하다는 것을 true로 반환
//    }
//
//    // 생성된지 30분이 지난 초대코드는 30분 주기로 사용된 초대코드로 변경
//    @Scheduled(fixedRate = 30 * 60 * 1000) // 30분마다 실행
//    @Transactional
//    public void updateInviteCode() {
//        userInviteRepository.checkExpiredInvitesAsUsed(
//                LocalDateTime.now()
//        );
//    }
//
//    // 사용된 초대코드는 30분 주기로 삭제
//    @Scheduled(fixedRate = 30 * 60 * 1000) // 30분마다 실행
//    @Transactional
//    public void deleteInviteCode() {
//        userInviteRepository.deleteByisUsedTrue();
//    }
//}
