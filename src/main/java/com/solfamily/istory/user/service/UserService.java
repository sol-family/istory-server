package com.solfamily.istory.user.service;

import com.solfamily.istory.Family.db.FamilyRepository;
import com.solfamily.istory.Family.service.FamilyService;
import com.solfamily.istory.user.db.UserRepository;
import com.solfamily.istory.global.service.JwtTokenService;
import com.solfamily.istory.global.service.PasswordService;
import com.solfamily.istory.user.model.LoginRequest;
import com.solfamily.istory.user.model.UserDto;
import com.solfamily.istory.user.model.UserEntity;
import com.solfamily.istory.global.service.ShinhanApiService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class UserService {

    private final UserRepository userRepository;
    private final UserConverterService userConverterService;
    private final FamilyRepository familyRepository;
    private final FamilyService familyService;
    private final PasswordService passwordService;
    private final ShinhanApiService shinhanApiService;
    private final JwtTokenService jwtTokenService;


    // 회원가입
    public ResponseEntity<Map<String, Object>> signUp(
            UserDto userDto
    ) {
        String userId = userDto.getUserId(); // userDto로부터 아이디를 받아옴
        String userPw = userDto.getUserPw(); // userDto로부터 비밀번호를 받아옴

        ResponseEntity<Map<String, Object>> checkIdResult = checkId(userId);

        // 아이스토리 DB에 중복된 아이디가 있을 때
        if (!(Boolean) checkIdResult.getBody().get("result")) {
            return checkIdResult;
        }

        // 신한 회원가입
        ResponseEntity<Map<String, Object>> shinhanSignUpResult = signUpToShinhan(userId);

        // 신한 회원가입이 제대로 처리 되지 않았을 때
        if (!(Boolean) shinhanSignUpResult.getBody().get("result")) {
            return shinhanSignUpResult;
        }

        Map<String, Object> response = new HashMap<>();

        try {
            String hashedUserPw = passwordService.hashPassword(userPw); // 유저 비밀번호 암호화
            userDto.setUserPw(hashedUserPw); // 암호화된 비밀번호로 재저장

            UserEntity userEntity = userConverterService.toEntity(userDto); // userDto를 userEntity로 변환

            userEntity.setUserKey(shinhanSignUpResult.getBody().get("userKey").toString()); // userEntity에 userKey 저장
            userEntity.setJoinDate(LocalDateTime.now()); // 회원가입 일시 저장

            userRepository.save(userEntity); // 유저 엔티티를 아이스토리 DB에 저장

            response.put("result", true);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } catch (Exception e) { // 에러 발생시
            log.info("ErrorName : {}, ErrorMsg : {}", e.getClass(), e.getMessage());
            String errorCode = "S2";
            response.put("result", false);
            response.put("errorCode", errorCode);
            return ResponseEntity
                    .status(HttpStatus.OK) // 200 OK
                    .body(response);
        }
    }

    // 유저 아이디 중복체크
    private ResponseEntity<Map<String, Object>> checkId(String userId) {
        Map<String, Object> response = new HashMap<>();

        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId); // 아이스토리 DB에서 유저 아이디 조회

        // 조회된 아이디가 있으면
        if (optionalUserEntity.isPresent()) {
            String errorCode = "S1";
            response.put("result", false);
            response.put("errorCode", errorCode);
        } else {
            response.put("result", true);
        }

        return ResponseEntity
                .status(HttpStatus.OK) // 200 error
                .body(response); // 에러 발생시 : 내부적으로 정한 에러코드 (아이스토리 DB에 이미 가입된 아이디가 있을때 = S1)
    }

    // 신한 회원가입
    private ResponseEntity<Map<String, Object>> signUpToShinhan(
            String userId
    ) {
        Map<String, Object> response = new HashMap<>();

        // 신한 API 연동(사용자 계정 생성)
        Map<String, Object> userInfo = shinhanApiService.signUp(userId);

        if (userInfo == null || userInfo.get("userKey").equals("")) {
            String errorCode = "S0";
            response.put("result", false);
            response.put("error_code", errorCode);
        } else {
            var userKey = userInfo.get("userKey"); // 신한 API로부터 userKey를 받아옴

            response.put("result", true);
            response.put("userKey", userKey);
        }

        return ResponseEntity
                .status(HttpStatus.OK) // 200 error
                .body(response); // 에러 발생시 : 내부적으로 정한 에러코드 (신한 API로부터 사용자 계정이 제대로 생성되지 않았을 때 = S0)
    }

    // 유저 로그인
    public ResponseEntity<Map<String, Object>> userLogin(
            LoginRequest loginRequest
    ) {
        Map<String, Object> response = new HashMap<>();

        String userId = loginRequest.getUserId();
        String userPw = loginRequest.getUserPw();

        Optional<UserEntity> optionalEntity = userRepository.findById(userId); // 아이스토리 DB로부터 유저아이디 조회

        // 아이스토리 DB에 로그인 요청한 아이디가 존재하지 않는다면
        if (optionalEntity.isEmpty()) {
            String errorCode = "I0";
            log.info("userId: {}", userId);
            response.put("result", false);
            response.put("errorCode", errorCode);
            return ResponseEntity
                    .status(HttpStatus.OK) // 200 OK
                    .body(response); // 내부적으로 정한 에러코드 (DB에 요청한 userId가 존재하지 않을 때 = I0 )
        }

        // DB에 userId가 있으면
        UserEntity userEntity = optionalEntity.get();

        // DB 내부에 저장된 해싱된 비밀번호와 원본 비밀번호 비교하여 같다면
        if (passwordService.checkPassword(userPw, userEntity.getUserPw())) {

            // JWT 토큰 발급
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", userEntity.getUserId()); // 사용자 ID만 claims에 담음
            LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(30); // 토큰 만료 시간 설정 (30분)
            String jwtToken = jwtTokenService.create(claims, expiredAt); // 토큰 생성

            // 만약 패밀리키가 없다면
            if (!(Boolean) hasFamily(userEntity.getUserId()).getBody().get("hasFamily")) {
                response.put("isPresent", false); // 대표자가 아님
            } else { // 패밀리카 있다면
                String familyKey = userEntity.getFamilyKey();
                String repesentiveUserId = familyRepository.findById(familyKey).get().getRepesentiveUserId(); // 가족의 대표자 아이디를 DB에서 가져옴

                // 만약 가족의 대표자 아이디와 로그인한 유저의 아이디가 같지 않다면
                if (!repesentiveUserId.equals(userEntity.getUserId())) {
                    response.put("isRepresentor", false); // 대표자가 아님
                } else {
                    response.put("isRepresentor", true); // 같다면
                }
            }

            // response 생성
            response.put("result", true);
            response.put("userId", userEntity.getUserId());
            response.put("jwtToken", jwtToken); // 응답에 jwtToken 저장

        } else { // 비밀번호가 일치하지 않으면
            String errorCode = "P0";
            response.put("result", false);
            response.put("errorCode", errorCode);
        }

        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK
                .body(response); // 에러 발생시 : 내부적으로 정한 에러코드 (로그인 요청 비밀번호와 DB에 존재하는 비밀번호가 일치하지 않을 때 = P0 )
    }

    public ResponseEntity<Map<String, Object>> getUserStatus(
            HttpServletRequest request
    ) {
        Map<String, Object> response = new HashMap<>();

        // 클라이언트로부터 jwtToken을 받아옴
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7); // 토큰 추출

        // 토큰으로부터 userId 추출
        String userId = jwtTokenService.getUserIdByClaims(token);

        UserEntity userEntity = userRepository.findById(userId).get();

        ResponseEntity<Map<String, Object>> hasFamilyResponse = hasFamily(userId);

        // 만약 패밀리키가 없다면
        if (!(Boolean) hasFamilyResponse.getBody().get("hasFamily")) {
            response.put("hasFamily", false);
            response.put("inviteCode", "");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } else { // 패밀리키가 있다면, 가족 계좌 있는지 확인하고 결과값 반환
            return familyService.hasSavingsAccount(userId);
        }
    }

    private ResponseEntity<Map<String, Object>> hasFamily(
            String userId
    ) {
        Map<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> getUserResponse = getUser(userId);

        // 아이스토리 DB에 해당 아이디가 존재하지 않는다면
        if (!(Boolean) getUserResponse.getBody().get("result")) {
            return getUserResponse;
        }

        // userEntity 값을 받아옴
        UserEntity userEntity = (UserEntity) getUserResponse.getBody().get("userEntity");

        // userEntity 값에 저장된 패밀리키를 받아옴
        String FamilyKey = userEntity.getFamilyKey();

        // 저장된 패밀리키가 없다면
        if (FamilyKey == null) {
            response.put("result", false);
            response.put("hasFamily", false);
        } else { // 저장된 패밀리카 있다면
            response.put("result", true);
            response.put("hasFamily", true);
        }

        // 응답 반환
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // 유저 아이디로 단일 조회
    public ResponseEntity<Map<String, Object>> getUser(
            HttpServletRequest request
    ) {

        Map<String, Object> response = new HashMap<>();

        // 클라이언트로부터 jwtToken을 받아옴
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7); // 토큰 추출

        // 토큰으로부터 userId 추출
        String userId = jwtTokenService.getUserIdByClaims(token);

        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId); // 아이스토리 DB에 유저아이디가 존재하는지 조회

        // 아이스토리 db에 아이디가 존재하지 않는다면
        if (optionalUserEntity.isEmpty()) {
            String errorCode = "I0";
            response.put("result", false);
            response.put("errorCode", errorCode);
        } else { // 아이디가 존재한다면
            UserEntity userEntity = optionalUserEntity.get();

            response.put("result", true);

            // userEntity에서 userDto로 변환
            UserDto userDto = userConverterService.toDto(userEntity);
            response.put("userDto", userDto);
        }

        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK
                .body(response); // 에러 발생시 : 내부적으로 정한 에러코드 (db에 userId가 존재하지 않을 때 = I0)
    }

    // 유저 아이디로 단일 조회
    private ResponseEntity<Map<String, Object>> getUser(
            String userId
    ) {
        Map<String, Object> response = new HashMap<>();
        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId); // 아이스토리 DB에 유저아이디가 존재하는지 조회

        // 아이스토리 db에 아이디가 존재하지 않는다면
        if (optionalUserEntity.isEmpty()) {
            String errorCode = "I0";
            response.put("result", false);
            response.put("errorCode", errorCode);
        } else { // 아이디가 존재한다면
            UserEntity userEntity = optionalUserEntity.get();

            response.put("result", true);
            response.put("userEntity", userEntity);
        }

        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK
                .body(response); // 에러 발생시 : 내부적으로 정한 에러코드 (db에 userId가 존재하지 않을 때 = I0)
    }

    // 모든 유저 조회
    public ResponseEntity<Map<String, Object>> getAllUser() {
        Map<String, Object> response = new HashMap<>();

        List<UserEntity> userEntityList = userRepository.findAll(); // 아이스토리 DB에서 모든 유저 조회

        // 조회된 유저가 없다면
        if (userEntityList.isEmpty()) {
            String errorCode = "U0";
            response.put("result", false);
            response.put("errorCode", errorCode);
            return ResponseEntity
                    .status(HttpStatus.OK) // 200 OK
                    .body(response); // 내부적으로 정한 에러코드 (db에 저장된 유저가 한 명도 없을 때 = U0)
        }

        response.put("result", true);

        // userEntity를 userDto로 변환
        List<UserDto> userList = userEntityList.stream()
                .map(userConverterService::toDto).toList();

        response.put("users", userList);

        return ResponseEntity
                .status(HttpStatus.OK) // 200 OK
                .body(response);
    }
}