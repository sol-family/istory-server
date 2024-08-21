package com.solfamily.istory.service.user;

import com.solfamily.istory.db.user.UserRepository;
import com.solfamily.istory.model.user.LoginRequest;
import com.solfamily.istory.model.user.UserDto;
import com.solfamily.istory.model.user.UserEntity;
import com.solfamily.istory.service.shinhanapi.ShinhanApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class UserService {

    private final UserRepository userRepository;
    private final UserConverterService userConverterService;
    private final PasswordService passwordService;
    private final ShinhanApiService shinhanApiService;

    // 초대코드 없이 회원가입
    public ResponseEntity userJoin(
            UserEntity userEntity
    ) {
        String userId = userEntity.getUserId();
        String userPw = userEntity.getUserPw();

        // 유저 비밀번호 암호화
        var hashedUserPw = passwordService.hashPassword(userPw);
        userEntity.setUserPw(hashedUserPw); // 암호화된 비밀번호로 재저장

        // 고유한 패밀리키(식별키) 생성
        String familyKey = "familyKey/" + UUID.randomUUID().toString();
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

    // 유저 한 명 유저 아이디로 조회
    public ResponseEntity getUser(String userId) {
        var optionalUserEntity = userRepository.findById(userId);

        if(optionalUserEntity.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Not Exist User : 존재하지 않는 회원입니다.");
        }

        var entity = optionalUserEntity.get();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userConverterService.toDto(entity));
    }

    // 모든 유저 조회
    public ResponseEntity getAlluser() {
        var optionalUserEntities = userRepository.findAll();

        if(optionalUserEntities.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Not Exist Users : 존재하는 회원이 없습니다.");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(optionalUserEntities.stream().map(userConverterService::toDto).toList());
    }

    // 유저 아이디 중복체크
    public boolean checkId(String userId) {
        var entity = userRepository.findById(userId);

        // id가 존재하면 true, 존재하지 않으면 false
        return entity.isPresent();
    }

    // 유저 로그인
    public ResponseEntity userLogin(
            LoginRequest loginRequest
            // HttpSession session
    ) {

        var userId = loginRequest.getUserId();
        var userPw = loginRequest.getUserPw();

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Not Exist Id : 아이디 값이 존재하지 않습니다.");
        }
        var optionalEntity = userRepository.findById(userId);

        // id가 없으면
        if(optionalEntity.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Not Exist Id : 아이디가 존재하지 않습니다.");
        } else { // id가 있으면
            UserEntity entity = optionalEntity.get();

            // DB 내부에 저장된 해싱된 비밀번호와 원본 비밀번호 비교
            if(passwordService.checkPassword(userPw, entity.getUserPw())) {

//                // 사용자 정보를 세션에 저장
//                session.setAttribute("user", userConverter.toDto(entity));
//
//                // 세션 유효 시간 설정 (30분)
//                session.setMaxInactiveInterval(30 * 60);

                // JWT 토큰 발급
                Map<String, Object> claims = new HashMap<>();
                claims.put("userId", entity.getUserId()); // 사용자 ID만 claims에 담음

                LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(30); // 토큰 만료 시간 설정 (30분)
                String jwtToken = JwtTokenService.create(claims, expiredAt); // 토큰 생성

                // 응답 구성
                Map<String, Object> response = new HashMap<>();
                response.put("jwtToken", jwtToken); // 응답에 jwtToken 저장

                // 사용자 정보는 별도로 제공
                UserDto userDto = userConverterService.toDto(entity);
                response.put("userDto", userDto); // 응답에 userDto 저장

                // 상태코드와 JWT Token, 유저정보가 담긴 Response 반환
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(response);
            } else { // 비밀번호가 일치하지 않으면
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid Password : 비밀번호가 일치하지 않습니다.");
            }
        }
    }

    // 유저Key 조회
    public String getUserKey(
            String userId
    ) {
        return userRepository.findUserKeyByUserId(userId);
    }
}