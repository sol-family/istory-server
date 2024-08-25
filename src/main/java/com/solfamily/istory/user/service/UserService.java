package com.solfamily.istory.user.service;

import com.solfamily.istory.user.db.UserRepository;
import com.solfamily.istory.global.JsonParser;
import com.solfamily.istory.global.JwtTokenService;
import com.solfamily.istory.global.PasswordService;
import com.solfamily.istory.user.model.LoginRequest;
import com.solfamily.istory.user.model.UserDto;
import com.solfamily.istory.user.model.UserEntity;
import com.solfamily.istory.global.ShinhanApiService;
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
    private final JsonParser jsonParser;

    // 초대코드 없이 회원가입
    public ResponseEntity userJoin(
            UserEntity userEntity
    ) {
        String userId = userEntity.getUserId(); // 유저엔티티로부터 아이디를 받아옴
        String userPw = userEntity.getUserPw(); // 유저엔티티로부터 비밀번호를 받아옴
        
        var hashedUserPw = passwordService.hashPassword(userPw); // 유저 비밀번호 암호화
        userEntity.setUserPw(hashedUserPw); // 암호화된 비밀번호로 재저장
        
        String familyKey = "familyKey/" + UUID.randomUUID().toString(); // 고유한 패밀리키(식별키) 생성
        userEntity.setFamilyKey(familyKey); // 유저엔티티에 패밀리키 저장

        // 신한 API 연동(사용자 계정 생성)
        Map<String, Object> userInfo  = shinhanApiService.userJoin(userId);

        if(userInfo.get("userKey").equals("")) {
            String msg = "";
            String json = jsonParser.toJson(msg);
            return ResponseEntity
                    .status(HttpStatus.OK) // 200 error
                    .body(json); // 내부적으로 정한 에러코드 (신한 API로부터 사용자 계정이 제대로 생성되지 않았을 때 = )
        }
        
        var userKey = userInfo.get("userKey"); // 신한 API로부터 userKey를 받아옴
        userEntity.setUserKey(userKey.toString()); // 받아온 userKey를 유저엔티티에 저장

        var entity = userRepository.save(userEntity); // 유저 엔티티를 istory_user 테이블에 저장

        // 응답 생성
        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK
                .body(userConverterService.toDto(entity)); // entity를 Dto로 변환하여 클라이언트에게 응답 반환
    }

    // 유저 아이디로 단일 조회
    public ResponseEntity getUser(String userId) {
        var optionalUserEntity = userRepository.findById(userId); // istory_user 테이블에 유저아이디가 존재하는지 조회

        // 아이디가 존재하지 않는다면
        if(optionalUserEntity.isEmpty()) {
            String msg = "";
            String json = jsonParser.toJson(msg);
            return ResponseEntity
                    .status(HttpStatus.OK) // 200 OK
                    .body(json); // 내부적으로 정한 에러코드 (db에 userId가 존재하지 않을 때 = )
        }

        var entity = optionalUserEntity.get();

        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK
                .body(userConverterService.toDto(entity)); // entity를 Dto로 변환하여 클라이언트에게 응답 반환
    }

    // 모든 유저 조회
    public ResponseEntity getAlluser() {
        var userList= userRepository.findAll(); // istory_user 테이블에서 모든 유저 조회

        // 조회된 유저가 없다면
        if(userList.isEmpty()) {
            String msg = "";
            String json = jsonParser.toJson(msg);
            return ResponseEntity
                    .status(HttpStatus.OK) // 200 OK
                    .body(json); // 내부적으로 정한 에러코드 (db에 저장된 유저가 한 명도 없을 때 = )
        }

        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK
                .body(userList.stream().map(userConverterService::toDto).toList()); // entity를 Dto로 변환하여 List로 응답 반환
    }

    // 유저 아이디 중복체크
    public ResponseEntity<Boolean> checkId(String userId) {
        var entity = userRepository.findById(userId); // istory_user 테이블에서 유저 아이디  조회

        // id가 존재하면 true, 존재하지 않으면 false
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entity.isPresent());
    }

    // 유저 로그인
    public ResponseEntity userLogin(
            LoginRequest loginRequest
            // HttpSession session
    ) {
        var userId = loginRequest.getUserId(); // 로그인 요청으로부터 유저아이디를 가져옴
        var userPw = loginRequest.getUserPw(); // 로그인 요청으로부터 유지비밀번호를 가져옴

        // 만약 유저아이디 또는 비밀번호가 존재하지 않는다면
        if (userId == null || userPw == null) {
            String msg = "";
            String json = jsonParser.toJson(msg);
            return ResponseEntity
                    .status(HttpStatus.OK) // 200 OK
                    .body(json); // 내부적으로 정한 에러코드 (로그인 요청 시 아이디 또는 비밀번호 값이 비어있을 때 = )
        }
        var optionalEntity = userRepository.findById(userId); // istory_user 테이블로부터 유저아이디 조회

        // istory_user 테이블에 해당 아이디가 존재하지 않는다면
        if(optionalEntity.isEmpty()) {
            String msg = "";
            String json = jsonParser.toJson(msg);
            return ResponseEntity
                    .status(HttpStatus.OK) // 200 OK
                    .body(json); // 내부적으로 정한 에러코드 (DB에 요청한 userId가 존재하지 않을 때 = )
        } else { // DB에 userId가 있으면
            var entity = optionalEntity.get();

            // DB 내부에 저장된 해싱된 비밀번호와 원본 비밀번호 비교하여 같다면
            if(passwordService.checkPassword(userPw, entity.getUserPw())) {

//                // 아래는 세션을 이용해 로그인 인증하는 코드
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
                        .status(HttpStatus.OK) // 200 OK
                        .body(response);
            } else { // 비밀번호가 일치하지 않으면
                String msg = "";
                String json = jsonParser.toJson(msg);
                return ResponseEntity
                        .status(HttpStatus.OK) // 200 OK
                        .body(json); // 내부적으로 정한 에러코드 (로그인 요청 비밀번호와 DB에 존재하는 비밀번호가 일치하지 않을 때 = )
            }
        }
    }
}