package com.solfamily.istory.service.user;

import com.solfamily.istory.db.user.UserRepository;
import com.solfamily.istory.model.user.LoginRequest;
import com.solfamily.istory.model.user.UserDto;
import com.solfamily.istory.model.user.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    // 초대코드 없이 회원가입
    public UserDto userJoin(UserEntity userEntity) {
        // 고유한 패밀리키(식별키) 생성
        String familyKey = "familyKey/" + UUID.randomUUID().toString();
        userEntity.setFamilyKey(familyKey);

        var entity = userRepository.save(userEntity);

        return userConverter.toDto(userEntity);
    }

    // 유저 한 명 유저 아이디로 조회
    public UserDto getUser(String userId) {
        var optionalUserEntity = userRepository.findById(userId);

        if(optionalUserEntity.isEmpty()) {
            throw new RuntimeException("userId Not Found");
        }

        return userConverter.toDto(optionalUserEntity.get());
    }

    // 모든 유저 조회
    public List<UserDto> getAlluser() {
        var userEntities = userRepository.findAll();

        return userEntities.stream().map(userConverter::toDto).toList();
    }

    // 유저 아이디 중북체크
    public boolean checkId(String userId) {
        var entity = userRepository.findById(userId);

        // id가 존재하면 true, 존재하지 않으면 false
        return entity.isPresent();
    }

    // 유저 로그인
    public ResponseEntity<> userLogin(
            LoginRequest loginRequest,
            HttpSession session
    ) {

        var id = loginRequest.getUserId();
        var pw = loginRequest.getUserPw();

        var optionalEntity = userRepository.findById(id);

        // id가 없으면
        if(optionalEntity.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Not Exist Id : 아이디가 존재하지 않습니다.");
        } else { // id가 있으면
            UserEntity entity = optionalEntity.get();

            // 원본 비밀번호 DB 비밀번호를 비교하기 위한
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            // DB 내부에 저장된 해싱된 비밀번호와 원본 비밀번호 비교
            if(passwordEncoder.matches(pw, entity.getUserPw())) {

//                // 사용자 정보를 세션에 저장
//                session.setAttribute("user", userConverter.toDto(entity));
//
//                // 세션 유효 시간 설정 (30분)
//                session.setMaxInactiveInterval(30 * 60);

                // JWT 토큰 발급
                Map<String, Object> claims = new HashMap<>();
                claims.put("userId", entity.getUserId()); // 사용자 ID만 담기

                LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(30); // 토큰 만료 시간 설정 (30분)
                String jwtToken = JwtTokenService.create(claims, expiredAt); // 토큰 생성

                // 응답 구성
                Map<String, Object> response = new HashMap<>();
                response.put("jwtToken", jwtToken);

                // 사용자 정보는 별도로 제공
                UserDto userDto = userConverter.toDto(entity);
                response.put("userDto", userDto);

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
}